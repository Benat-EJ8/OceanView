# Ocean View Resort – Hotel Management System

Beachside hotel management web application for **Ocean View Resort, Galle**.  
Backend: **Java Servlets (Tomcat)** + **JDBC/PostgreSQL**.  
Frontend: **Vite + React** with role-based routing.

---

## 1. Prerequisites

- **Java 17** (JDK 17)
- **Apache Tomcat 9** (e.g. `C:\Program Files\Apache Software Foundation\Tomcat 9.0`)
- **PostgreSQL** (create database `oceanview`)
- **Node.js 18+** (for frontend)
- **Maven** (for backend build)

---

## 2. Database setup

1. Create a PostgreSQL database named `oceanview`.
2. Run the schema:

```bash
psql -U postgres -d oceanview -f database/schema.sql
```

3. Update backend JDBC settings in `backend/src/main/resources/application.properties`:

```properties
jdbc.url=jdbc:postgresql://localhost:5432/oceanview
jdbc.username=postgres
jdbc.password=YOUR_PASSWORD
```

4. Default admin user is created by the schema (username: `admin`). Set a known password: use the app’s password reset or re-run a one-off update with a BCrypt hash.

---

## 3. Backend (Tomcat)

### Run from IntelliJ IDEA

1. Open the **OceanView** project folder in IntelliJ.
2. Follow **[INTELLIJ_SETUP.md](INTELLIJ_SETUP.md)** to add a Tomcat Server run configuration and set **Application context** to **`/oceanview`**.
3. Run the **Tomcat OceanView** configuration. The API will be at **http://localhost:8080/oceanview/** (welcome page) and **http://localhost:8080/oceanview/api/...** (endpoints).

### Build WAR (command line)

```bash
cd backend
mvn clean package
```

Output: `backend/target/oceanview.war`.

### Deploy in IntelliJ IDEA

1. **Run → Edit Configurations → + → Tomcat Server → Local**
2. Set **Application server** to your Tomcat 9 directory.
3. **Deployment** tab → **+ → Artifact** → choose `oceanview:war exploded` (or `oceanview.war`).
4. Set **Application context** to `/` (or leave as `/oceanview` and adjust frontend proxy/base URL).
5. **Apply** and **Run**.

### Deploy manually

1. Copy `oceanview.war` to `Tomcat 9.0/webapps/`.
2. Start Tomcat (`bin/startup.bat` on Windows).
3. Backend base URL: `http://localhost:8080/` (or `http://localhost:8080/oceanview/` if context is `/oceanview`).

---

## 4. Frontend (Vite + React)

```bash
cd frontend
npm install
npm run dev
```

Frontend: **http://localhost:5173**

Vite proxy forwards `/api` to `http://localhost:8080`. If your Tomcat context is `/oceanview`, set in `frontend/vite.config.js`:

```js
proxy: {
  '/api': { target: 'http://localhost:8080/oceanview', changeOrigin: true },
},
```

---

## 5. Run order

1. Start **PostgreSQL**.
2. Start **Tomcat** (with `oceanview` deployed).
3. Start **frontend**: `cd frontend && npm run dev`.
4. Open **http://localhost:5173**.

---

## 6. Roles and flows

- **Customer**: Register → Login → View rooms → Submit booking request (pending approval).
- **Receptionist / Manager**: Login → Approve/cancel bookings → View reservations.
- **Manager**: Reports (occupancy, revenue, bookings, staff) and Staff management (receptionists).

Default roles in DB: `ADMIN`, `MANAGER`, `RECEPTIONIST`, `CUSTOMER`.  
Create users and assign roles via the app or directly in the database.

---

## 7. Project layout

```
OceanView/
├── backend/                    # Java Servlet app (WAR)
│   └── src/main/
│       ├── java/com/oceanview/resort/
│       │   ├── controller/    # Servlets, CORS
│       │   ├── service/
│       │   ├── repository/
│       │   ├── domain/
│       │   ├── dto/
│       │   ├── mapper/
│       │   ├── security/
│       │   ├── patterns/      # Strategy, Factory, Observer, etc.
│       │   └── util/
│       └── webapp/WEB-INF/
│           └── web.xml
├── frontend/                   # Vite + React
│   └── src/
│       ├── api.js
│       ├── context/
│       ├── components/
│       └── pages/
├── database/
│   └── schema.sql
├── API.md                      # API endpoints list
└── README.md
```

---

## 8. API overview

See **API.md** for the full list of endpoints (auth, users, rooms, room-categories, reservations, reports).

---

## 9. Design patterns (backend)

- **User**: Strategy (role permissions), State (account lock), Observer (activity logging).
- **Room**: Factory (room creation), State (status transitions), Flyweight (category cache).
- **Reservation**: Chain of Responsibility (validation), Command (cancel), Observer (notifications), Memento (history).
- **Billing**: Strategy (payment types), Decorator (extra charges), Template Method (invoice).
- **Search**: Specification (room filters).
- **Notifications**: Observer.
- **External**: Adapter (Email/SMS gateway).
- **Reports**: Facade.
- **Multi-branch**: Composite (branch hierarchy).

---

## 10. Default admin

The schema inserts one admin user. To set a known password, use your app’s password reset flow or update `users.password_hash` with a BCrypt hash for the admin row.
