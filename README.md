# Real-Time Multi-Room Chat Application

A real-time, multi-room chat application built with **Java, Spring Boot, WebSockets (STOMP), Spring Data JPA, and the Twilio API**. Users can join different chat rooms, see message history, chat live, and optionally receive an SMS via Twilio when they receive a message while offline.

## Features

- Real-time bidirectional messaging using WebSockets (STOMP protocol over SockJS)
- **Multiple chat rooms** — messages are isolated per room
- **Persistent message history** (H2 embedded database via Spring Data JPA) — join a room and see past messages
- **Online presence tracking** — thread-safe tracking of who's currently in each room
- **Twilio SMS integration** — sends an SMS notification to offline users when they get a new message (toggle on/off via config)
- Clean layered architecture: config / controller / service / repository / model
- Unit tests with JUnit 5

## Tech Stack

- **Backend:** Java 17, Spring Boot 3, Spring WebSocket, Spring Data JPA
- **Database:** H2 (in-memory, swappable for MySQL/Postgres)
- **External API:** Twilio SDK (SMS notifications)
- **Frontend:** HTML, CSS, vanilla JavaScript, SockJS + Stomp.js
- **Build tool:** Maven
- **Testing:** JUnit 5

## Architecture

```
Browser  <--WebSocket(SockJS)-->  Spring Boot
                                      |
                                      |-- ChatController: handles send/join events per room
                                      |-- ChatMessageRepository: persists messages (JPA -> H2)
                                      |-- PresenceService: tracks who's online, per room
                                      |-- NotificationService: sends SMS via Twilio if recipient offline
                                      |
                          REST: GET /api/rooms/{roomId}/messages -> message history
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Steps
```bash
git clone <your-repo-url>
cd chat-app-v2
mvn spring-boot:run
```

Open `http://localhost:8080` in multiple tabs, pick a room, chat. Refresh the page and rejoin the same room — your message history loads back from the database.

### Enabling Twilio SMS notifications (optional)
1. Sign up for a free trial at https://www.twilio.com/try-twilio
2. Get your Account SID, Auth Token, and a Twilio phone number from the console
3. In `application.properties`, set:
   ```
   twilio.enabled=true
   twilio.account-sid=<your-sid>
   twilio.auth-token=<your-token>
   twilio.from-number=<your-twilio-number>
   ```
4. Register a recipient's phone number by calling `ChatController.registerPhoneNumber(username, phoneNumber)` (currently exposed as a Java method — wiring it to a REST endpoint is a good next exercise)

### Running tests
```bash
mvn test
```

## Possible Extensions
- Swap H2 for MySQL/PostgreSQL for real persistence across restarts
- Move presence tracking to Redis so it works across multiple server instances
- Add JWT-based authentication instead of free-text usernames
- Add a REST endpoint to register/update a user's phone number for SMS alerts
- Rate-limit message sending to prevent spam

---

