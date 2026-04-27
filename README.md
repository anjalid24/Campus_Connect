# 🎓 Campus Connect

Campus Connect is a **Java-based desktop application** built using **Java Swing** for LJ University.  
It provides students with an integrated platform for **campus activities, academic resources, event management, task tracking, career guidance, faculty feedback, and gamified engagement**.

---

## 🎯 Project Objective

The goal of Campus Connect is to centralize multiple student-centric services into a single desktop application while demonstrating:
- Object-Oriented Programming in Java
- GUI development using Swing
- Use of Data Structures
- Modular project architecture
- Integration with external AI services

---

## 🛠️ Technology Stack

- **Language:** Java  
- **UI Framework:** Java Swing  
- **Database:** MySQL (mysql-connector-j-9.3.0)  
- **JSON Processing:** Gson 2.13.1  
- **IDE:** IntelliJ IDEA  



## 📂 Project Structure

<img width="497" height="563" alt="image" src="https://github.com/user-attachments/assets/8366ce70-2bb0-4750-bfe0-63b6cc68e577" />


---

## 🧩 Module Description

### 🔹 Core Entry Point
- **Main.java** – Launches the application and initializes the Swing UI using the Event Dispatch Thread.

---

### 🔹 Model Package (`model`)
Contains data models and structures used throughout the application:
- `StudentInfo` – Student profile data
- `Message` – Messaging/chat model
- `Task` – Task and assignment model
- `Entry` – Generic data entry abstraction
- `BST` & `BSTNode` – Binary Search Tree implementation for efficient operations

---

### 🔹 UI Package (`ui`)
Handles all user interface screens:
- Login system
- Homepage dashboard
- Event management
- Task manager
- Academic resources
- Career guidance
- Faculty feedback
- Submission box

#### UI Components
Reusable neon-styled components such as:
- `NeonButton`
- `NeonPanel`
- `NeonGradientPanel`
- `EventCard`
- `LeaderboardRow`

---

### 🔹 Utility Package (`util`)
Helper and service classes:
- `GeminiClient` – AI integration using Gemini API
- `ChatbotConsole` – AI chatbot support
- `LeaderboardConsole` – Gamified leaderboard logic
- `GradientPanel` – UI styling utilities

---

## ⭐ Key Features

- Secure login system
- Event browsing and management
- Task and assignment tracking
- Academic resource access
- Career guidance support
- Faculty feedback system
- AI chatbot integration (Gemini API)
- Leaderboard for student engagement
- Neon-themed modern UI

---

## 🧠 Architecture

The application follows an **MVC-style modular architecture**:
- **Model** → Data & structures  
- **View (UI)** → Swing interfaces  
- **Controller / Utils** → Logic & helpers  

This ensures clean separation of concerns and maintainability.

---

## ▶️ How to Run

### Option 1 — Pre-built JAR (Easiest)
Download `CampusConnect.jar` from the [Releases](https://github.com/anjalid24/Campus_Connect/releases) page (or directly from the repo), then run:

```bash
java -jar CampusConnect.jar
```

> **Requirement:** Java 21+ must be installed. Download from [java.com](https://www.java.com).  
> The `lib/` folder must be in the same directory as the JAR.

---

### Option 2 — Build from Source (Command Line)

1. Clone the repository:
   ```bash
   git clone https://github.com/anjalid24/Campus_Connect.git
   cd Campus_Connect
   ```

2. Compile all sources:
   ```powershell
   javac -d out -cp "lib\mysql-connector-j-9.7.0.jar;lib\gson-2.13.1.jar" -sourcepath src (Get-ChildItem -Recurse -Filter "*.java" src | ForEach-Object { $_.FullName })
   ```

3. Run the application:
   ```powershell
   java -cp "out;lib\mysql-connector-j-9.7.0.jar;lib\gson-2.13.1.jar;src" com.ljuniversity.companion.Main
   ```

---

### Option 3 — IntelliJ IDEA

1. Open the project in **IntelliJ IDEA**
2. Add `lib/mysql-connector-j-9.7.0.jar` and `lib/gson-2.13.1.jar` as project libraries
3. Run `Main.java`

---

### 🗄️ Database Setup (Optional)

The app runs without a database — modules like Career Guidance and Events work fully offline.  
To enable login, signup, and the leaderboard, set up MySQL:

```sql
CREATE DATABASE `campus-connect`;
USE `campus-connect`;

CREATE TABLE loginPage (
  enrollment_no VARCHAR(14) PRIMARY KEY,
  username      VARCHAR(100) NOT NULL,
  password      VARCHAR(255) NOT NULL
);
```

Default DB config (editable in `LoginPageSwing.java`):
- Host: `localhost:3306`
- User: `root`
- Password: *(empty)*

---

## 📌 Highlights

- Uses **Binary Search Tree** for efficient data handling
- Implements **custom Swing components**
- Integrates **AI assistance via Gemini**
- Designed as a **student-centric digital companion**

---

## ✅ Conclusion

Campus Connect is a comprehensive desktop application that demonstrates advanced Java concepts, GUI design, data structures, and AI integration in a real-world academic use case.



