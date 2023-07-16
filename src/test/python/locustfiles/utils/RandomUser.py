"""
See the README.md file for instructions to get this running for you.
This link explains how to write tests: https://docs.locust.io/en/stable/quickstart.html
"""
import secrets
import string

from locust import FastHttpUser


class RandomNewLoggedInUser(FastHttpUser):
    """
    This user creates themselves by registering a new account, then logs in as themselves.
    """
    abstract = True

    def __init__(self, environment):
        super().__init__(environment)
        self.USER_DETAIL_LEN = 10
        self.email = None
        self.address = None
        self.pwd = None
        self.uname = None

    def on_start(self):
        """
        Runs once when this user is created.
        Creates a new user and logs them in.
        """
        self.client.post("/register", data=self.create_random_credentials())
        creds = {
            "username": f"{self.uname}",
            "password": f"{self.pwd}"
        }
        self.client.post("/login", data=creds)

    def on_stop(self):
        """
        Runs once when this user is stopped.
        """
        self.client.post("/logout", data={})

    def create_random_credentials(self):
        self.uname = f"rando_user_{self.random_string()}"
        self.pwd = "password"
        self.address = f"address_{self.uname}"
        self.email = f"{self.uname}@dodgy.com"

        return {
            "email": self.email,
            "uname": self.uname,
            "pwd": self.pwd,
            "address": self.address
        }

    def random_string(self):
        """
        https://stackoverflow.com/questions/2257441/random-string-generation-with-upper-case-letters-and-digits
        """
        return ''.join(secrets.choice(string.ascii_uppercase + string.digits) for _ in range(self.USER_DETAIL_LEN))
