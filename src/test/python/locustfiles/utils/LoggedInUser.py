"""
See the README.md file for instructions to get this running for you.
This link explains how to write tests: https://docs.locust.io/en/stable/quickstart.html
"""
from locust import FastHttpUser


class LoggedInUser(FastHttpUser):
    abstract = True

    def __init__(self, environment):
        super().__init__(environment)

    def on_start(self):
        """
        Runs once when this user is created.
        """
        self.client.post("/login", data=self.get_creds())

    def on_stop(self):
        """
        Runs once when this user is stopped.
        """
        self.client.post("/logout", data={})

    def get_creds(self):
        """
        Implement this for different user credentials.
        """
        pass
