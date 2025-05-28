# Online Chat Application

## Overview
This Java-based chat application allows multiple clients to connect to a central server (`ChatServer`), send messages, and receive broadcasts of messages from all other connected clients.

## Components
1. **ChatServer.java**
   - Listens on port **12345**.
   - Assigns each client a unique ID (`User1`, `User2`, ...).
   - Manages client connections with a thread-safe set.
   - Broadcasts incoming messages to all clients.
   - Supports `/quit` command for graceful disconnect.

2. **ChatClient.java**
   - Connects to `localhost:12345`.
   - Sends user messages to the server.
   - Displays messages broadcast by the server.
   - Type `/quit` to exit the chat.

## Requirements
- Java 8 or higher
- No external libraries required

## How to Compile & Run
1. **Compile**  
   ```bash
   javac ChatServer.java ChatClient.java
