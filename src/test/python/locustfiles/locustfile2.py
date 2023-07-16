"""
See the README.md file for instructions to get this running for you.
This link explains how to write tests: https://docs.locust.io/en/stable/quickstart.html
"""
from locust import task, constant_throughput, between
from bs4 import BeautifulSoup

from utils.RandomUser import RandomNewLoggedInUser
from utils.common import *


class BiddingRandomNewLoggedInUser(RandomNewLoggedInUser):
    wait_time = constant_throughput(0.1)
    # wait_time = constant_pacing(0.1)
    # wait_time = between(1, 2)  # Makes this user wait between x and y seconds between tasks.
    bid_increment = 5

    def __init__(self, environment):
        super().__init__(environment)

    @task
    def view_listing_and_bid(self):
        """
         locust will randomly select methods annotated with @task to run.
         """
        self.client.get("/auth/landing-page")
        self.client.get("/auth/listing/listing-all")
        with self.client.get(f"/auth/listing/view?listing_id={ZINGER_BOX_ID}") as response:
            soup = BeautifulSoup(response.text, "html.parser")
            viewed_price = self.get_highest_bid(soup)
            self.bid(viewed_price + BiddingRandomNewLoggedInUser.bid_increment)

    @staticmethod
    def get_highest_bid(soup):
        # First try to find the highest bid.
        if soup.h4:
            string = soup.h4.text
            elems = string.split(" ")
            for elem in elems:
                if elem.startswith("$"):
                    return float(elem[1:])
        else:
            # If there's no bids, find the start price.
            for thing in soup.find_all("p"):
                if thing:
                    print(f"thing={thing.text}")
                    for elem in thing.text.split(" "):
                        if elem.startswith("$"):
                            return float(elem[1:])
            raise Exception("No price listed on page.")

    def bid(self, amount):
        self.client.post(
            url=f"/auth/listing/view?listing_id={ZINGER_BOX_ID}",
            data={"value": f"{amount}"}
        )
