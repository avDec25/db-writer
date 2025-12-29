import random
import time
import uuid
import requests
from faker import Faker
from collections import deque
import json

BASE_URL = "http://localhost:8080/users"
# BASE_URL = "http://localhost:60600/users"
SLEEP_SECONDS = 0.05
MAX_USERS_IN_MEMORY = 50
REQUEST_TIMEOUT = 2  # seconds
MAX_RETRIES = 5
RETRY_BACKOFF = 0.4  # seconds

fake = Faker()
created_user_ids = deque(maxlen=MAX_USERS_IN_MEMORY)

write_count = 0
read_count = 0


def pretty_response(response):
    if response is None:
        return "<no response>"
    try:
        return json.dumps(response.json(), indent=2)
    except ValueError:
        return response.text


def generate_user():
    return {
        "id": f"user::{uuid.uuid4()}",
        "name": fake.name(),
        "age": random.randint(18, 80)
    }


def request_with_retry(method, url, **kwargs):
    """
    Generic request wrapper with retry logic
    """
    for attempt in range(1, MAX_RETRIES + 1):
        try:
            response = requests.request(
                method,
                url,
                timeout=REQUEST_TIMEOUT,
                **kwargs
            )

            # Retry on server errors
            if response.status_code >= 500:
                raise requests.exceptions.HTTPError(
                    f"Server error: {response.status_code}"
                )

            return response

        except requests.exceptions.RequestException as e:
            print(f"[RETRY {attempt}/{MAX_RETRIES}] {method.upper()} {url} failed: {e}")

            if attempt < MAX_RETRIES:
                time.sleep(RETRY_BACKOFF * attempt)  # linear backoff
            else:
                print(f"[FAILED] {method.upper()} {url} after {MAX_RETRIES} attempts")
                return None


def write_user():
    global write_count
    user = generate_user()

    response = request_with_retry(
        "post",
        BASE_URL,
        json=user
    )

    print("\n[WRITE]")
    print(f"Response:\n{pretty_response(response)}")

    if response and response.status_code in (200, 201):
        created_user_ids.append(user["id"])
        write_count += 1
        print(f"[WRITE] Created user {user['id']} (tracked={len(created_user_ids)})")
    else:
        print("[WRITE] Giving up on this user")


def read_user():
    global read_count

    if not created_user_ids:
        print("[READ] No users available, skipping")
        return

    user_id = random.choice(tuple(created_user_ids))

    response = request_with_retry(
        "get",
        f"{BASE_URL}/{user_id}"
    )

    print("\n[READ]")
    print(f"Response:\n{pretty_response(response)}")

    if response and response.status_code == 200:
        read_count += 1
        print(f"[READ] Fetched user {user_id}")
    else:
        print(f"[READ] Giving up on {user_id}")


def main():
    print("Starting load generator. Press Ctrl+C to stop.")
    try:
        while True:
            if random.random() < 0.6:
                read_user()
            else:
                write_user()

            time.sleep(SLEEP_SECONDS)

    except KeyboardInterrupt:
        print("\nStopping script...")
        print(f"Total writes: {write_count}")
        print(f"Total reads : {read_count}")
        print(f"Users kept in memory: {len(created_user_ids)}")
        print("Exited cleanly.")


if __name__ == "__main__":
    main()
