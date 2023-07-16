"""
See the README.md file for instructions to get this running for you.
This link explains how to write tests: https://docs.locust.io/en/stable/quickstart.html
"""
from locust import task, constant_throughput, between

from utils.RandomUser import RandomNewLoggedInUser


class InactiveRandomNewLoggedInUser(RandomNewLoggedInUser):
    wait_time = constant_throughput(0.1)
    # wait_time = constant_pacing(0.1)
    # wait_time = between(1, 2)  # Makes this user wait between x and y seconds between tasks.
    bid_increment = 5

    def __init__(self, environment):
        super().__init__(environment)

    @task
    def login_and_do_nothing(self):
        print("Finished the task")
