import psycopg2
import random
import time
import json
from faker import Faker
import threading
from queue import Queue

conn = psycopg2.connect(
    dbname='credit',
    user='postgres',
    password='postgres',
    host='host.docker.internal',
    port='5432'
)
cursor = conn.cursor()

faker = Faker()

def generate_cpf(): 
    base_cpf = [random.randint(0, 9) for _ in range(9)]
    
    def calculate_digit(cpf_base, position):
        total = sum([cpf_base[i] * (position + 1 - i) for i in range(position, 9)])
        remainder = total % 11
        return 0 if remainder < 2 else 11 - remainder
    
    first_digit = calculate_digit(base_cpf, 0)
    second_digit = calculate_digit(base_cpf, 1)
    
    cpf = base_cpf + [first_digit, second_digit]
    return ''.join(map(str, cpf))

def generate_proposal_event():
    return {
        "key": faker.uuid4(),
        "proposalNumber": faker.random_number(digits=6),
        "proponentDocument": generate_cpf(),
        "product": random.choice(['consignado', 'private'])
    }

def generate_credit_card_event(proponent_document):
    return {
        "key": faker.uuid4(),
        "cardNumber": faker.credit_card_number(),
        "portadorDocument": proponent_document
    }

def insert_proposal_event(data):
    cursor.execute("""
        INSERT INTO proposal (key, proposalNumber, proponentDocument, product, status)
        VALUES (%s, %s, %s, %s, 'CREATED')
    """, (data["key"], data["proposalNumber"], data["proponentDocument"], data["product"]))
    print(f"Inserted proposal: {data}")

def insert_credit_card_event(data):
    cursor.execute("""
        INSERT INTO credit_card (key, cardNumber, portadorDocument, status)
        VALUES (%s, %s, %s, 'CREATED')
    """, (data["key"], data["cardNumber"], data["portadorDocument"]))
    print(f"Inserted credit card: {data}")

def worker(queue):
    while not queue.empty():
        proposal_event = generate_proposal_event()
        credit_card_event = generate_credit_card_event(proposal_event["proponentDocument"])

        insert_proposal_event(proposal_event)
        insert_credit_card_event(credit_card_event)

        conn.commit()
        queue.task_done()

queue = Queue()

num_threads = 10

for _ in range(10000): 
    queue.put(1)

threads = []
for i in range(num_threads):
    thread = threading.Thread(target=worker, args=(queue,))
    threads.append(thread)
    thread.start()

for thread in threads:
    thread.join()

print("All events have been processed and inserted.")
