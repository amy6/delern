import webapp2

from google.appengine.api import app_identity
from google.appengine.api import urlfetch

class ProxyToFunctions(webapp2.RequestHandler):

    def get(self, *unused_args):
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

        self.response.status = result.status_code
        #self.response.write(result.content)


app = webapp2.WSGIApplication([
    webapp2.Route(r'/<:.*>', handler=ProxyToFunctions)
], debug=True)

# vim: ft=python
