# PulseDesk — Comment-to-Ticket Triage

A Spring Boot backend application that collects user comments and uses AI (Hugging Face) to automatically decide whether a comment should become a support ticket — and if so, generates structured ticket data including a title, category, priority, and summary.

Built as part of the IBM Internship Technical Challenge.

---

## Live Demo

http://pulsedesk.onrender.com

---

## How It Works

1. A user submits a comment via `POST /comments`
2. The comment is saved to an in-memory H2 database
3. The app calls the Hugging Face Inference API (Qwen3-8B model) to decide if the comment describes a real issue
4. If yes, a second AI call generates a structured ticket with title, category, priority, and summary
5. The ticket is saved and available via `GET /tickets`

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- H2 (in-memory embedded database)
- Hugging Face Inference API (`Qwen/Qwen3-8B` via Novita provider)

---

## AI Model

This project uses **`Qwen/Qwen3-8B`** via the Hugging Face Inference Router.

> `Qwen/Qwen3-8B` is fully open, ungated, and available on the free tier — anyone can run this project with just a standard HuggingFace token.

---

## UI
A simple web interface is available at the root URL:
- Submit comments
- View all comments
- View all generated tickets in real time

---

## Setup Instructions

### Prerequisites

- Java 17+
- A free [Hugging Face](https://huggingface.co) account

### 1. Clone the repository

```bash
git clone https://github.com/DeimanteDav/pulsedesk.git
cd pulsedesk
```

### 2. Create a Hugging Face token

1. Go to [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)
2. Click **"Create new token"**
3. Select type **"Fine-grained"**
4. Enable **"Make calls to Inference Providers"**
5. Copy the token

### 3. Configure the application

Set your token as an environment variable:

```bash
# Windows
set HF_TOKEN=hf_xxxxxxxxxxxxxxxx

# Mac/Linux
export HF_TOKEN=hf_xxxxxxxxxxxxxxxx
```

Or set it directly in IntelliJ: **Run → Edit Configurations → Environment Variables**

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The app starts at `http://localhost:8080`

---

## API Endpoints

### Submit a comment
```
POST /comments
Content-Type: application/json

{
  "text": "The app crashes every time I try to log in"
}
```

### Get all comments
```
GET /comments
```

### Get all tickets
```
GET /tickets
```

### Get a ticket by ID
```
GET /tickets/{id}
```

---

## Example

**Request:**
```bash
curl -X POST http://localhost:8080/comments \
  -H "Content-Type: application/json" \
  -d '{"text": "App crashes when clicking login"}'
```

**Auto-generated ticket (from `GET /tickets`):**
```json
{
  "id": 1,
  "title": "App crashes on login click",
  "category": "bug",
  "priority": "high",
  "summary": "The application crashes when the user attempts to click the login button."
}
```

**No ticket created for compliments:**
```bash
curl -X POST http://localhost:8080/comments \
  -H "Content-Type: application/json" \
  -d '{"text": "Great app, love the new design!"}'
```

---

## H2 Database Console

You can inspect the in-memory database at:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave blank)
```

---

## Project Structure

```
src/main/java/com/deimante/pulsedesk/
├── PulseDeskApplication.java
├── controller/
│   ├── CommentController.java   # POST /comments, GET /comments
│   └── TicketController.java    # GET /tickets, GET /tickets/{id}
├── model/
│   ├── Comment.java
│   └── Ticket.java
├── repository/
│   ├── CommentRepository.java
│   └── TicketRepository.java
└── service/
    ├── CommentService.java
    └── HuggingFaceService.java   # AI API calls (triage + ticket generation)
```
