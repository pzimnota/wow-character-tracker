# 🧙‍♂️ WoW Character Tracker

Backend API for tracking World of Warcraft characters.  
Built with **Java 21**, **Spring Boot**, and **PostgreSQL**.  
Integrates with the official **Blizzard Game Data API**.

---

## 🚧 Project status
This project is **in progress** — core modules are being developed.  
Currently implemented:
- Realm endpoint (`GET /realms?region=EU`)
- Realm caching with Caffeine
- Basic validation layer
- Blizzard OAuth2 token handling (partial, no live auth yet)

Next steps:
- Add real Blizzard API authorization
- Implement character management (`POST /characters`)
- Extend unit/integration tests

---

## 🧰 Tech stack
- Java 21  
- Spring Boot 3  
- Spring Data JPA / Hibernate  
- PostgreSQL  
- Caffeine Cache  
- JUnit 5 / Mockito  

---
