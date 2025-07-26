# Distributed Dictionary System

## 1. Introduction

This project implements a distributed dictionary management system based on a TCP client-server architecture. The server handles multiple clients concurrently, enabling real-time query, addition, deletion, and update of dictionary entries. The system demonstrates mid-level concurrency and Java socket programming using a **thread-per-connection** model.

Both the client and server provide GUI interfaces to enhance usability and transparency.

---

## 2. System Design

### 2.1 Components Overview

| Component            | Description                                             | Implementing Classes                                     |
|----------------------|---------------------------------------------------------|-----------------------------------------------------------|
| Client Component     | Sends requests and processes responses via GUI         | `Client`, `ClientUI`, `ActionHandler`, `ResponseHandler`  |
| Server Component     | Accepts connections and delegates processing           | `Server`, `ServerGUI`                                     |
| Handler Component    | Handles each client in a separate thread               | `RequestHandler`                                          |
| Dictionary Logic     | Manages dictionary entries and operations              | `DictionaryManager`                                       |
| Communication Layer  | Handles TCP sockets and JSON parsing                   | `BufferedReader`, `BufferedWriter`, `ObjectMapper`        |

### 2.2 Client-Server Architecture

- **Client** connects to the **Server** using TCP sockets.
- Server spawns a dedicated thread per connection (`RequestHandler`).
- Communication is persistent and bidirectional using **JSON messages**.

---

## 3. Implementation Details

### 3.1 Communication Protocol

- All interactions are performed using JSON over TCP sockets.
- For each operation (add, query, delete, update):
    - The client sends a structured JSON request.
    - The server parses, validates, and responds with a JSON result.

### 3.2 Threading and Concurrency

- Server uses **thread-per-connection**: one thread handles all requests from one client.
- Client uses `BlockingQueue` to asynchronously handle responses.
- Server uses `ConcurrentHashMap` to store dictionary data safely under concurrency.

---

## 4. Critical Analysis

### 4.1 Concurrency Strategy Comparison

| Model                 | Pros                                   | Cons                                      |
|----------------------|----------------------------------------|-------------------------------------------|
| Thread-per-Request   | Fine-grained control                   | High overhead, risk of thread explosion   |
| Thread Pool          | Efficient, reusable threads            | Requires queue management, fairness logic |
| **Thread-per-Connection** | Simple, isolated, GUI-friendly       | Suitable for medium-load applications     |

This project adopts **thread-per-connection** for session-level simplicity, error containment, and easier logging.

### 4.2 Operational Observability (ServerGUI)

- GUI tracks:
    - Connected clients
    - Connection count
    - Per-thread logs of client actions
- Maps each `Socket` to a client ID using `ConcurrentHashMap<Socket, String>`
- Reinforces session visibility, aids debugging and auditing

---

## 5. Conclusion

This distributed dictionary system demonstrates a clean, maintainable, and scalable architecture:

- Concurrent, session-isolated request handling
- Persistent TCP communication using JSON
- Real-time GUI monitoring for debugging and transparency
- Thread-safe design with Java concurrency primitives

It provides a robust foundation for real-time multi-user systems and showcases practical application of concurrent programming principles in distributed environments.
