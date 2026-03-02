# OceanView Hotel Management System

> **Enterprise Hotel Management System** for Ocean View Resort, Galle, Sri Lanka  
> Java Servlets · JDBC · PostgreSQL · React (Vite) · Apache Tomcat · No Spring

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Architecture](#architecture)
4. [Repository Structure](#repository-structure)
5. [Design Patterns](#design-patterns)
6. [Setup & Deployment](#setup--deployment)
7. [Default Accounts](#default-accounts)
8. [API Overview](#api-overview)
9. [Database](#database)
10. [Security](#security)
11. [CI/CD](#cicd)
12. [Branching Strategy](#branching-strategy)
13. [License](#license)

---

## Project Overview

OceanView is a full-stack, enterprise-grade hotel management system built entirely with **raw Java Servlets and JDBC** — no Spring, no Jakarta EE frameworks — following strict Clean Layered Architecture and 13 applied GoF design patterns.

### Role Hierarchy

```
Admin
└── Manager
    └── Receptionist
        └── Customer (public portal)
```

### Modules

| Module | Key Features |
|---|---|
| User Management | RBAC, account lock, password reset, activity logs |
| Customer Portal | Public registration, login, room browsing, booking requests |
| Room Management | CRUD, categories, status lifecycle, floor views, availability |
| Reservation | Approval workflow, modify/cancel, waiting list, group bookings, deposits |
| Billing | Seasonal pricing, tax, discounts, extra services, invoice, payment states |
| Guest | History, loyalty points, blacklist, document storage |
| Reports | Occupancy, revenue, utilization, booking stats, staff performance |
| Check-In/Out | Early/late handling, room state updates |
| Maintenance | Request logging, assignment, tracking |
| Housekeeping | Cleaning scheduling |
| Notifications | Booking alerts, payment reminders, maintenance alerts |
| Promotions | Promo codes, discount packages |
| Inventory | Supply tracking |
| Search | Multi-criteria Specification-based filtering |
| Security | Password hashing, session timeout, encryption, CAPTCHA placeholder, 2FA hooks |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18 (Vite), React Router, Recharts |
| Backend | Java 17, Java Servlets (javax), JDBC |
| Database | PostgreSQL 14+ |
| Server | Apache Tomcat 10.x |
| Build | Maven 3.8+ (backend), npm (frontend) |
| CI/CD | GitHub Actions |

---

## Architecture

```
┌──────────────────────────────────────────────────────┐
│           React Frontend (Vite)                      │  Presentation
│  Landing · Customer Portal · Manager · Receptionist  │
│  Role-based routing · Protected routes · Charts      │
└──────────────────┬───────────────────────────────────┘
                   │ HTTP / JSON
┌──────────────────▼───────────────────────────────────┐
│           Java Servlet Controllers                   │  HTTP Layer
│  Request parsing → DTO binding → delegate to service │
└──────────────────┬───────────────────────────────────┘
                   │ Constructor-injected services
┌──────────────────▼───────────────────────────────────┐
│           Service Layer                              │  Business Logic
│  Workflows · Pattern orchestration · Validation      │
└──────────────────┬───────────────────────────────────┘
                   │ Repository interfaces
┌──────────────────▼───────────────────────────────────┐
│           Repository Layer (DAO)                     │  Data Access
│  Raw JDBC · PreparedStatements · ResultSets          │
└──────────────────┬───────────────────────────────────┘
                   │ JDBC connection pool (Singleton)
┌──────────────────▼───────────────────────────────────┐
│           PostgreSQL                                 │  Persistence
└──────────────────────────────────────────────────────┘
```

### Key Architectural Rules

- **No Spring / No Jakarta EE.** All dependency injection is done manually via constructor injection. Services are instantiated in controllers' `init()` method.
- **DTOs at the boundary.** Controllers receive and return DTOs only. Domain entities never leak into the HTTP layer.
- **Patterns live in the service layer.** No pattern code in controllers or repositories.

---

## Repository Structure

```
OceanView/
├── .github/workflows/          # GitHub Actions CI/CD
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/oceanview/
│       ├── controller/         # HttpServlet subclasses (HTTP layer only)
│       ├── service/            # Business logic
│       ├── repository/         # JDBC DAOs
│       ├── domain/             # Entity POJOs
│       ├── dto/                # Data Transfer Objects
│       ├── mapper/             # Domain ↔ DTO mapping
│       ├── patterns/           # All GoF pattern implementations
│       │   ├── strategy/
│       │   ├── factory/
│       │   ├── observer/
│       │   ├── state/
│       │   ├── command/
│       │   ├── decorator/
│       │   ├── specification/
│       │   ├── adapter/
│       │   ├── facade/
│       │   ├── builder/
│       │   ├── chain/
│       │   ├── memento/
│       │   ├── composite/
│       │   └── flyweight/
│       ├── security/           # AuthFilter, RoleGuard, PasswordUtil, SessionManager
│       └── util/               # DatabaseConfig (Singleton), JsonUtil, ValidationUtil
├── frontend/
│   └── src/
│       ├── pages/              # Landing, Login, Register, Dashboards, etc.
│       ├── components/
│       ├── context/            # AuthContext, NotificationContext
│       ├── services/api.js
│       └── hooks/
├── database/
│   ├── schema.sql
│   └── seed.sql
├── docs/
└── LICENSE
```

---

## Design Patterns

This project applies **13 GoF design patterns**:

| # | Pattern | Applied To |
|---|---|---|
| 1 | **Strategy** | Role permissions & payment types (Cash/Card/Transfer) |
| 2 | **Factory** | Room creation by category (Standard, Deluxe, Suite) |
| 3 | **Observer** | Notifications, activity logging, report cache invalidation |
| 4 | **State** | Booking lifecycle, room status, account lock state machine |
| 5 | **Command** | Reservation modify/cancel with undo support |
| 6 | **Decorator** | Extra billing charges (Tax, Breakfast, Airport Transfer, Discount) |
| 7 | **Specification** | Dynamic multi-criteria room/guest/booking search filters |
| 8 | **Adapter** | Email/SMS external gateway (e.g., SendGrid) |
| 9 | **Facade** | Reporting API — single entry point for multiple report services |
| 10 | **Builder** | Domain object construction (Booking, Room, Invoice) |
| 11 | **Chain of Responsibility** | Booking validation pipeline (dates → availability → blacklist → deposit → group size) |
| 12 | **Memento** | Reservation history / undo — snapshots stored in DB as JSONB |
| 13 | **Composite** | Multi-branch hotel hierarchy for aggregated reporting |
| + | **Flyweight** | Shared room category data to avoid duplicate objects in memory |

---

## Setup & Deployment

### Prerequisites

| Tool | Version |
|---|---|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| PostgreSQL | 14+ |
| Apache Tomcat | 10.x |

### Step 1 — Database

```bash
psql -U postgres
CREATE DATABASE oceanview;
CREATE USER oceanview_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE oceanview TO oceanview_user;
\q

psql -U oceanview_user -d oceanview -f database/schema.sql
psql -U oceanview_user -d oceanview -f database/seed.sql
```

### Step 2 — Backend

Edit `backend/src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/oceanview
db.username=oceanview_user
db.password=your_secure_password
db.pool.maxSize=10
session.timeout=1800
```

Build:

```bash
cd backend
mvn clean package
```

Deploy via IntelliJ: Run → Edit Configurations → Tomcat Server (Local) → Add Artifact `oceanview:war exploded` → Application context: `/`

### Step 3 — Frontend

```bash
cd frontend
npm install

# Development (proxies /api/* → http://localhost:8080)
npm run dev

# Production build
npm run build
# Output: frontend/dist/
```

---

## Default Accounts

> ⚠️ **Change all passwords immediately after first login.**

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `Admin@123` |
| Manager | `manager` | `Manager@123` |
| Receptionist | `receptionist` | `Reception@123` |

---

## API Overview

All endpoints return `application/json`. Protected routes require an active `JSESSIONID` session cookie.

### Auth

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| POST | `/api/auth/logout` | Auth |
| GET | `/api/auth/me` | Auth |
| POST | `/api/auth/reset-password` | Auth |

### Rooms

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/rooms` | Auth |
| GET | `/api/rooms/available?checkIn=&checkOut=` | Auth |
| POST | `/api/rooms` | Manager+ |
| PUT | `/api/rooms/:id` | Manager+ |
| DELETE | `/api/rooms/:id` | Admin |

### Reservations

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/bookings` | Customer+ |
| GET | `/api/bookings/:id` | Auth |
| PUT | `/api/bookings/:id` | Staff |
| POST | `/api/bookings/:id/approve` | Receptionist+ |
| POST | `/api/bookings/:id/checkin` | Receptionist+ |
| POST | `/api/bookings/:id/checkout` | Receptionist+ |
| POST | `/api/bookings/:id/cancel` | Auth |
| GET | `/api/bookings/:id/history` | Staff |

### Billing

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/billing/:bookingId` | Staff |
| POST | `/api/billing/:bookingId/pay` | Staff |
| POST | `/api/billing/apply-promo` | Staff |

### Reports (Manager+ only)

| Endpoint | Description |
|---|---|
| `/api/reports/dashboard` | Full facade report |
| `/api/reports/occupancy` | Occupancy stats |
| `/api/reports/revenue` | Revenue summary |
| `/api/reports/utilization` | Room utilization % |
| `/api/reports/staff` | Staff performance |

### Search

| Endpoint | Query Params |
|---|---|
| `/api/search/rooms` | `category`, `maxPrice`, `checkIn`, `checkOut`, `floor` |
| `/api/search/guests` | `name`, `email`, `phone` |
| `/api/search/bookings` | `status`, `from`, `to`, `guestId` |

---

## Database

The schema contains **13 tables**:

`roles` · `users` · `room_categories` · `rooms` · `guests` · `reservations` · `reservation_history` · `invoices` · `payments` · `notifications` · `maintenance_requests` · `promotions` · `inventory` · `housekeeping_tasks` · `feedback` · `audit_log`

---

## Security

- **Password Hashing:** BCrypt via `PasswordUtil`
- **Session Timeout:** 30-minute inactivity via `HttpSession.setMaxInactiveInterval(1800)`
- **Account Lock:** After N failed logins — Observer triggers State transition to LOCKED
- **SQL Injection Prevention:** All JDBC queries use `PreparedStatement`
- **Encryption:** `EncryptionUtil` for sensitive stored data (e.g., ID document paths)
- **CAPTCHA Placeholder:** Front-end hook ready for Google reCAPTCHA
- **2FA Hook:** `AuthService.initiate2FA()` stub present for future OTP integration

### RBAC Permissions

| Permission | Admin | Manager | Receptionist | Customer |
|---|---|---|---|---|
| Manage users | ✅ | ❌ | ❌ | ❌ |
| Manage receptionists | ✅ | ✅ | ❌ | ❌ |
| View all reports | ✅ | ✅ | ❌ | ❌ |
| Approve bookings | ✅ | ✅ | ✅ | ❌ |
| Create booking (own) | ✅ | ✅ | ✅ | ✅ |
| Billing / Payments | ✅ | ✅ | ✅ | ❌ |
| Maintenance | ✅ | ✅ | ✅ | ❌ |
| View own bookings | ✅ | ✅ | ✅ | ✅ |

---

## CI/CD

GitHub Actions runs on push to `main`/`develop` and on PRs to `main`:

- **Backend:** `mvn clean package && mvn test` (Java 17, Temurin)
- **Frontend:** `npm ci && npm run build` (Node 18)

---

## Branching Strategy

| Branch | Purpose |
|---|---|
| `main` | Production-ready. Protected; requires PR + CI pass |
| `develop` | Integration branch — features merge here first |
| `feature/*` | One branch per feature, branched from `develop` |
| `hotfix/*` | Emergency fixes branched from `main`, merged to both `main` and `develop` |

---

## License

MIT — see `LICENSE` in the repository root.

---

*OceanView Resort, Galle · [github.com/Benat-EJ8/OceanView](https://github.com/Benat-EJ8/OceanView)*