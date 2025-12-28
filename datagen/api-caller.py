import random
import time
import uuid
import requests
from faker import Faker
from collections import deque

BASE_URL = "http://localhost:8080/users"
SLEEP_SECONDS = 0.2
MAX_USERS_IN_MEMORY = 50

fake = Faker()

# Bounded deque (keeps only last 50 users)
created_user_ids = deque(maxlen=MAX_USERS_IN_MEMORY)

write_count = 0
read_count = 0


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

    if response.status_code in (200, 201):
        created_user_ids.append(user["id"])  # auto-evicts old IDs
        write_count += 1
        print(f"[WRITE] Created user {user['id']} (tracked={len(created_user_ids)})")
    else:
        print(f"[WRITE] Failed: {response.status_code} - {response.text}")


def read_user():
    global read_count
    if not created_user_ids:
        print("[READ] No users available, skipping")
        return

    # deque supports indexing, but convert once for random choice
    user_id = random.choice(tuple(created_user_ids))
    response = requests.get(f"{BASE_URL}/{user_id}")

    if response.status_code == 200:
        read_count += 1
        print(f"[READ] Fetched user {user_id}")
    else:
        print(f"[READ] Failed for {user_id}: {response.status_code}")


def main():
    print("Starting load generator. Press Ctrl+C to stop.")
    try:
        while True:
            # 60% reads, 40% writes
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
