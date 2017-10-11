import webapp2
import pubsub_utils

class PushToPubSub(webapp2.RequestHandler):
    def get(self, topic):
        if 'X-Appengine-Cron' not in self.request.headers:
            # AppEngine strips "X-" headers from external requests,
            # and AppEngine Cron always adds "X-Appengine-Cron: true".
            self.response.status = '403'
            return
        pubsub_utils.publish_to_topic('cron-' + topic, 'cron')

app = webapp2.WSGIApplication([
    webapp2.Route(r'/cron/<topic>', handler=PushToPubSub)
], debug=True)

# vim: ft=python
