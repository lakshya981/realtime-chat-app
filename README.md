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

## Before You Put This on Your Resume or Talk About It in an Interview

This project uses several concepts that are very likely to come up as follow-up questions. Make sure you can explain, in your own words:

1. **Why WebSockets instead of regular HTTP requests?** (Hint: persistent connection vs. request/response, no repeated polling)
2. **What STOMP is** and why it sits on top of the raw WebSocket protocol (structured pub/sub messaging with destinations, like `/topic/room.general`)
3. **Why `PresenceService` uses `ConcurrentHashMap`** instead of a regular `HashMap` (multiple WebSocket sessions run on different threads — a plain HashMap isn't thread-safe under concurrent writes)
4. **How the message flow works end to end**: browser sends to `/app/chat.sendMessage/{roomId}` → `@MessageMapping` in `ChatController` handles it → message is saved via `ChatMessageRepository` → `@SendTo` broadcasts it to everyone subscribed to `/topic/room.{roomId}`
5. **What happens when a user disconnects** (`WebSocketEventListener` catches `SessionDisconnectEvent`, updates presence, and broadcasts a "left" message)
6. **Why the Twilio integration is guarded by a config flag** (`twilio.enabled`) instead of always running (so the app is demoable without needing a paid/trial account, and so a third-party API failure never breaks the core chat feature — see the try/catch in `NotificationService`)

If you can walk through these points confidently, this project will hold up well under interview questions. If any of them feel shaky, spend 20–30 minutes re-reading the relevant file before you list this project — that's a much better use of time than hoping it doesn't come up.
