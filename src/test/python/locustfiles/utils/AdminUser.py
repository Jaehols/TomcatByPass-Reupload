"""
See the README.md file for instructions to get this running for you.
This link explains how to write tests: https://docs.locust.io/en/stable/quickstart.html
"""
from LoggedInUser import LoggedInUser
from common import ADMIN_USER_CREDS


class AdminUser(LoggedInUser):
    abstract = True

    def __init__(self, environment):
        super().__init__(environment)

    def get_creds(self):
        return ADMIN_USER_CREDS
