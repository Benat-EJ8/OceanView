# Ocean View Resort – API Endpoints

Base URL: `http://localhost:8080` (or `http://localhost:8080/oceanview` if deployed with context `/oceanview`).  
All JSON request/response unless noted. Session-based auth (cookie) for protected endpoints.

---

## Auth (public where noted)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/login` | No | Login. Body: `{ "username", "password" }`. Returns `{ success, data: UserDTO }`. |
| POST | `/api/auth/logout` | Yes | Invalidate session. |
| GET | `/api/auth/me` | Yes | Current user. Returns `{ success, data: UserDTO }`. |
| POST | `/api/auth/register` | No | Customer registration. Body: `{ username, password, email, firstName, lastName }`. |

---

## Users

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/users` | Yes | List users. Query: `role=RECEPTIONIST` (optional). |
| POST | `/api/users` | Yes | Create user (Manager/Admin). Body: User entity; query: `password=...`. |

---

## Room categories (public for listing)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/room-categories` | No | List room categories. |

---

## Rooms

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/rooms?branchId=1` | Yes | List rooms for branch. |
| GET | `/api/rooms?branchId=1&available=true&checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD` | Yes | List available rooms for date range. |

---

## Reservations

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/reservations?branchId=1` | Yes | List reservations for branch. |
| GET | `/api/reservations?guestId=1` | Yes | List reservations for guest. |
| GET | `/api/reservations?pending=true` | Yes | List pending-approval reservations. |
| GET | `/api/reservations?id=1` | Yes | Get one reservation. |
| POST | `/api/reservations` | Yes | Create reservation. Body: `branchId, guestId, roomId?, checkInDate, checkOutDate, adults, children?, specialRequests?, depositAmount?`. |
| PUT | `/api/reservations?id=1&action=approve` | Yes | Approve reservation. |
| PUT | `/api/reservations?id=1&action=cancel&reason=...` | Yes | Cancel reservation. |

---

## Reports (Manager / Admin)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/reports?type=occupancy&from=YYYY-MM-DD&to=YYYY-MM-DD` | Yes | Occupancy report. |
| GET | `/api/reports?type=revenue&from=&to=` | Yes | Revenue report. |
| GET | `/api/reports?type=bookings&from=&to=` | Yes | Booking stats. |
| GET | `/api/reports?type=staff&from=&to=` | Yes | Staff performance. |

---

## Response format

- Success: `{ "success": true, "data": ... }`
- Error: `{ "success": false, "message": "..." }`
- 401: Not authenticated (for protected routes).

---

## Session

- Session-based auth via `HttpSession`; frontend uses `credentials: 'include'` so cookies are sent.
- Session timeout is configurable (e.g. 30 minutes in `application.properties`).
