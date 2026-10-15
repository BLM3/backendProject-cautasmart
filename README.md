Cautasmart.ro — Affiliate Platform & Automated Buying Guides

> Platformă web modernă de afiliere și ghiduri de cumpărături SEO-optimizate ("Top 5 produse"), integrată cu furnizori de top (ex. SpringFarma.ro) și structurată pentru performanță ridicată pe infrastructură cloud/serverless.


Tehnologii & Arhitectură

-Backend
Java 17 / Spring Boot 3: REST API cu arhitectură stratificată (Controller, Service, Repository).
Spring Data JPA & Hibernate: ORM pentru gestiunea eficientă a modelelor relaționale.
HikariCP: Tuning avansat al pool-ului de conexiuni optimizat pentru medii Serverless (`keepalive-time`, `connection-test-query`).

-Database
Neon PostgreSQL (Serverless): Bază de date PostgreSQL găzduită în cloud, configurată cu scripturi SQL idempotente pentru menținerea integrității datelor (`guides`, `guide_items`, `guide_item_pros`, `guide_item_cons`).

-Frontend
React: Interfață de utilizator dinamică, responsivă și optimizată pentru SEO și conversii.

-DevOps & Hosting
Render: Găzduire containerizată pentru backend-ul Spring Boot.
Cron-job Automation: Strategie de ping-uri sintetice automate la fiecare 5 minute pentru atenuarea stărilor de *cold start* pe Render și Neon.

---

Caracteristici Principale

1. Ghiduri de Cumpărături Interactive: Generare dinamică de topuri („Top 5 cele mai bune suplimente de colagen”, „Top 5 Camere de Supraveghere Raport Calitate-Preț” etc.).
2. Structură de Date Relațională: Clasificare clară a produselor cu atribute dedicate (`rank`, `badge_text`, `review_summary`, `pros`, `cons`).
3. Optimizare Cold Start: Mecanisme automate de mentenanță a conexiunii la baza de date și menținerea instanței de backend active.
4. Idempotență și Scripts SQL: Scripting SQL curat pentru popularea și actualizarea conținutului fără duplicate sau erori de Cheie Primară / Externă.

---

Baza de Date — Schema Relațională

`guides`: Reține detaliile ghidului (`id`, `title`, `slug`, `category`, `buying_advice`, `banner_image_url`).
`guide_items`: Produsele asociate fiecărui ghid, ordonate după `rank`.
`guide_item_pros` & `guide_item_cons`: Punctele forte și dezavantajele specifice fiecărui produs, legate prin `FOREIGN KEY`.

---

Configurare & Rulare Locală

1. Backend (Spring Boot)
```bash
# Clonare repository
git clone [https://github.com/utilizator/backendProject-cautasmart.git](https://github.com/utilizator/backendProject-cautasmart.git)

# Configurare mediu (.env sau application.yml)
# DB_URL, DB_USERNAME, DB_PASSWORD (Neon PostgreSQL)

# Rulare proiect
mvn spring-boot:run
