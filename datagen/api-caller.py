import random
import time
import uuid
import requests
from faker import Faker
from collections import deque
import json

BASE_URL = "http://localhost:8080/users"
SLEEP_SECONDS = 2.0
MAX_USERS_IN_MEMORY = 50

fake = Faker()

created_user_ids = deque(maxlen=MAX_USERS_IN_MEMORY)

write_count = 0
read_count = 0


def pretty_response(response):
    """Safely print response body"""
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


def write_user():
    global write_count
    user = generate_user()
    response = requests.post(BASE_URL, json=user)

    print(f"\n[WRITE] Status: {response.status_code}")
    print(f"[WRITE] Response:\n{pretty_response(response)}")

    if response.status_code in (200, 201):
        created_user_ids.append(user["id"])
        write_count += 1
        print(f"[WRITE] Created user {user['id']} (tracked={len(created_user_ids)})")
    else:
        print("[WRITE] Failed to create user")


def read_user():
    global read_count
    if not created_user_ids:
        print("[READ] No users available, skipping")
        return

    user_id = random.choice(tuple(created_user_ids))
    response = requests.get(f"{BASE_URL}/{user_id}")

    print(f"\n[READ] Status: {response.status_code}")
    print(f"[READ] Response:\n{pretty_response(response)}")

    if response.status_code == 200:
        read_count += 1
        print(f"[READ] Fetched user {user_id}")
    else:
        print(f"[READ] Failed for {user_id}")


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
