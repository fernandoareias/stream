# Kafka Streaming Project

This project leverages Docker Compose to spin up a set of Kafka Streams applications that interact with Apache Kafka clusters. Each service is designed to handle specific events, such as "Proposta Criada", "Cartão Criado", and "Client Created". The architecture is highly modular, allowing you to scale and extend individual components as necessary.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Setup and Installation](#setup-and-installation)
- [Services](#services)
- [Configuration](#configuration)
- [License](#license)

## Overview

This project sets up multiple services that read from and produce to Kafka topics in real-time. It enables streaming data processing for various events. The components are integrated with Apache Kafka and the Confluent Schema Registry, ensuring data consistency across streams.

## Architecture

The architecture consists of the following components:

- **Kafka Brokers:** A set of Kafka brokers that manage the streaming data.
- **Schema Registry:** Provides a centralized service to store and manage schema versions for Kafka data.
- **Kafka Streams Apps:** Each app subscribes to specific Kafka topics and processes data in real-time.

Services run in a Docker network, enabling seamless communication between components.

## Setup and Installation

### Prerequisites

- Docker (version 3.9 or higher)
- Docker Compose

### Clone the repository

```bash
git clone <repository-url>
cd <repository-folder> 
```


# Build and start the services
```bash
docker-compose up --build
```

# Verify the running services
```bash
docker ps
```

You should see the services streaming-proposta-criada-app, streaming-cartao-criado-app, client-created-app, and others up and running.

## Create a topic with a specific numbers partitions

CDC credit-card-created-event.public.credit_card
CDC proposal-created-event.public.proposal
Evento credit-card-created-event
Evento proposal-created-event
## Services

### 1. **streaming-proposta-criada-app**
- **Description:** Consumes and processes events related to proposals being created.
- **Kafka Topics:** proposta-criada
- **Dependencies:** Kafka brokers (`kafka1`, `kafka2`, `kafka3`)

### 2. **streaming-cartao-criado-app**
- **Description:** Consumes and processes events related to the creation of credit cards.
- **Kafka Topics:** cartao-criado
- **Dependencies:** Kafka brokers (`kafka1`, `kafka2`, `kafka3`)

### 3. **client-created-app**
- **Description:** Consumes and processes events related to client creation.
- **Kafka Topics:** client-created
- **Dependencies:** Kafka brokers (`kafka1`, `kafka2`, `kafka3`)

### 4. **client-by-product-app**
- **Description:** Processes events related to clients filtered by their associated products.
- **Kafka Topics:** client-by-product
- **Dependencies:** Kafka brokers (`kafka1`, `kafka2`, `kafka3`)

## Configuration

### Environment Variables

- **KAFKA_BROKER:** The list of Kafka broker addresses (e.g., `host.docker.internal:19090`, `host.docker.internal:19091`, `host.docker.internal:19092`).
- **SCHEMA_REGISTRY_URL:** The URL of the schema registry (e.g., `http://host.docker.internal:8081`).
- **JOB_NAME:** The job name used for each Kafka Streams application (e.g., `streaming-proposta-criada-event`).

### Kafka Cluster

This project assumes that you are running a Kafka cluster with three brokers (`kafka1`, `kafka2`, `kafka3`) and a schema registry running at `http://host.docker.internal:8081`.


## License

This project is licensed under the MIT License 