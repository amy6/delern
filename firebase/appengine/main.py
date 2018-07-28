"""AppEngine application entry point."""

import logging

# pylint: disable=import-error
import webapp2
from google.appengine.api import app_identity, urlfetch

class ProxyToFunctions(webapp2.RequestHandler):
    """HTTP handler proxying requests to Cloud Functions."""

    def get(self, *args):
        """HTTP 'GET' method handler for incoming requests."""

        if self.request.path.startswith('/_ah/'):
            # Ignore /_ah/start, /_ah/stop requests.
            self.response.status = '200 OK'
            return

        del args
        url = 'https://us-central1-{}.cloudfunctions.net{}'.format(
            app_identity.get_application_id(),
            self.request.path_qs)
        auth_token, _ = app_identity.get_access_token(
            'https://www.googleapis.com/auth/cloud-platform')

        # "result" is of type _URLFetchResult,
        # https://cloud.google.com/appengine/docs/standard/python/refdocs/modules/google/appengine/api/urlfetch
        result = urlfetch.fetch(url, headers={
            'Authorization': 'Bearer {}'.format(auth_token),
        }, deadline=180, validate_certificate=True)

        logging.info('Got status %s with text: %s', result.status_code,
                     result.content)
        if result.status_code != 200:
            logging.error('Request failed: %s', result.status_code)

        try:
            self.response.status = result.status_code
        except KeyError:
            # response.status setter tries to look up a status message in its
            # predefined dictionary (which is very small). When the message
            # is not found, and status is not of '429 Quota Exceeded' form,
            # it raises an exception.
            # https://github.com/GoogleCloudPlatform/webapp2/blob/deb34447ef8927c940bed2d80c7eec75f9f01be8/webapp2.py#L451
            self.response.status = '{} Unknown Error'.format(result.status_code)

        #self.response.write(result.content)


# pylint: disable=invalid-name
app = webapp2.WSGIApplication([
    webapp2.Route(r'/<:.*>', handler=ProxyToFunctions)
], debug=True)
