"""
See the README.md file for instructions to get this running for you.
This link explains how to write tests: https://docs.locust.io/en/stable/quickstart.html
"""
from locust import task, constant_pacing

from utils.RandomUser import RandomNewLoggedInUser


class ManyConnectionsRandomNewLoggedInUser(RandomNewLoggedInUser):
    wait_time = constant_pacing(0.5)

    def __init__(self, environment):
        super().__init__(environment)

    @task
    def example_task(self):
        """
        These are all read-only operations.
        The aim of this test is to stress-test the connection pool by quickly performing many read-only operations
         which each require a new database connection.
        """
        self.client.get("/auth/landing-page")
        self.client.get(f"/auth/user/user-details?uname={self.uname}")
        self.client.get("/auth/landing-page")
        self.client.get("/auth/listing/auctions")
        self.client.get("/auth/landing-page")
        self.client.get("/auth/sellergroup/seller-group-home")
        self.client.get("/auth/landing-page")
        self.client.get("/auth/listing/listing-all")
