from twitter import *

import os.path
import sys
import time

def main():
    keys_file = sys.argv[1]
    root_user = sys.argv[2]
    users_dir = sys.argv[3]

    separator = "<*>(<*>)<*>"

    with open(keys_file) as f:
        lines = f.read().splitlines()

        keys = {
            "access_key": lines[0],
            "access_secret": lines[1],
            "consumer_key": lines[2],
            "consumer_secret": lines[3]
        }


    t = Twitter(
        auth=OAuth(
            consumer_key=keys["consumer_key"],
            consumer_secret=keys["consumer_secret"],
            token=keys["access_key"],
            token_secret=keys["access_secret"]))

    users = [f["screen_name"] for f in t.friends.list(screen_name=root_user, count=200)["users"]]

    print(users)

    ending = ".txt"

    for u in users:
        tweets = get_tweets(t, u)
        print(u)

        with open(os.path.join(users_dir, u + ending), "w") as f:
            f.write(separator.join(tweets))

        time.sleep(2)

def get_tweets(t, username):
    tweets = t.statuses.user_timeline(screen_name=username, count=200)
    return [tw["text"] for tw in tweets]

if __name__ == "__main__":
    main()
