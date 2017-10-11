# https://cloud.google.com/appengine/docs/standard/python/tools/using-libraries-python-27#installing_a_third-party_library

import os
from google.appengine.ext import vendor

vendor.add(os.path.join(os.path.dirname(os.path.realpath(__file__)), 'lib'))
