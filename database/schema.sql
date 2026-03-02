-- Ocean View Resort - PostgreSQL Schema
-- Galle Beachside Hotel Management System


-- ==================== ROLES & USERS ====================
CREATE TABLE roles (
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_roles_code ON roles(code);

INSERT INTO roles (code, name, description) VALUES
('ADMIN', 'Administrator', 'Full system access'),
('MANAGER', 'Manager', 'Branch and staff management'),
('RECEPTIONIST', 'Receptionist', 'Check-in/out, reservations'),
('CUSTOMER', 'Customer', 'Portal guest');

-- Branches (Multi-Branch / Composite hierarchy)
CREATE TABLE branches (
    id          SERIAL PRIMARY KEY,
    parent_id   INTEGER REFERENCES branches(id),
    name        VARCHAR(200) NOT NULL,
    code        VARCHAR(50) NOT NULL UNIQUE,
    address     TEXT,
    city        VARCHAR(100),
    country     VARCHAR(100) DEFAULT 'Sri Lanka',
    phone       VARCHAR(50),
    email       VARCHAR(255),
    is_active   BOOLEAN DEFAULT true,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_branches_parent ON branches(parent_id);
CREATE INDEX idx_branches_code ON branches(code);

INSERT INTO branches (name, code, city, address) VALUES
('Ocean View Resort - Galle', 'GALLE-MAIN', 'Galle', 'Beach Road, Galle Fort');

CREATE TABLE users (
    id              SERIAL PRIMARY KEY,
    role_id         INTEGER NOT NULL REFERENCES roles(id),
    branch_id       INTEGER REFERENCES branches(id),
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(50),
    is_locked       BOOLEAN DEFAULT false,
    locked_until    TIMESTAMP,
    lock_reason     TEXT,
    failed_attempts INTEGER DEFAULT 0,
    last_login_at   TIMESTAMP,
    is_active       BOOLEAN DEFAULT true,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_users_branch ON users(branch_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

CREATE TABLE activity_logs (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id   VARCHAR(100),
    details     JSONB,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activity_logs_user ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_created ON activity_logs(created_at);

-- ==================== ROOM CATEGORIES & ROOMS ====================
CREATE TABLE room_categories (
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    base_price      DECIMAL(12,2) NOT NULL,
    max_occupancy   INTEGER NOT NULL DEFAULT 2,
    size_sqm        DECIMAL(8,2),
    amenities       JSONB,
    image_url       VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_room_categories_code ON room_categories(code);

CREATE TABLE room_status (
    code        VARCHAR(50) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

INSERT INTO room_status (code, name, description) VALUES
('AVAILABLE', 'Available', 'Ready for booking'),
('OCCUPIED', 'Occupied', 'Guest checked in'),
('MAINTENANCE', 'Maintenance', 'Under repair'),
('CLEANING', 'Cleaning', 'Housekeeping in progress'),
('OUT_OF_ORDER', 'Out of Order', 'Not available');

CREATE TABLE rooms (
    id          SERIAL PRIMARY KEY,
    branch_id   INTEGER NOT NULL REFERENCES branches(id),
    category_id INTEGER NOT NULL REFERENCES room_categories(id),
    room_number VARCHAR(20) NOT NULL,
    floor       INTEGER NOT NULL,
    status      VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE' REFERENCES room_status(code),
    view_type   VARCHAR(50),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(branch_id, room_number)
);

CREATE INDEX idx_rooms_branch ON rooms(branch_id);
CREATE INDEX idx_rooms_category ON rooms(category_id);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_floor ON rooms(floor);

-- ==================== GUESTS ====================
CREATE TABLE guests (
    id              SERIAL PRIMARY KEY,
    user_id         INTEGER REFERENCES users(id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    phone           VARCHAR(50),
    id_type         VARCHAR(50),
    id_number       VARCHAR(100),
    nationality     VARCHAR(100),
    date_of_birth   DATE,
    loyalty_points  INTEGER DEFAULT 0,
    is_blacklisted  BOOLEAN DEFAULT false,
    blacklist_reason TEXT,
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_guests_user ON guests(user_id);
CREATE INDEX idx_guests_email ON guests(email);
CREATE INDEX idx_guests_blacklist ON guests(is_blacklisted);

CREATE TABLE guest_documents (
    id          SERIAL PRIMARY KEY,
    guest_id    INTEGER NOT NULL REFERENCES guests(id) ON DELETE CASCADE,
    doc_type    VARCHAR(50) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    file_name   VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_guest_documents_guest ON guest_documents(guest_id);

-- ==================== RESERVATIONS ====================
CREATE TABLE reservation_status (
    code        VARCHAR(50) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL
);

INSERT INTO reservation_status (code, name) VALUES
('PENDING_APPROVAL', 'Pending Approval'),
('CONFIRMED', 'Confirmed'),
('CHECKED_IN', 'Checked In'),
('CHECKED_OUT', 'Checked Out'),
('CANCELLED', 'Cancelled'),
('NO_SHOW', 'No Show');

CREATE TABLE reservations (
    id                  SERIAL PRIMARY KEY,
    branch_id           INTEGER NOT NULL REFERENCES branches(id),
    guest_id            INTEGER NOT NULL REFERENCES guests(id),
    room_id             INTEGER REFERENCES rooms(id),
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL' REFERENCES reservation_status(code),
    check_in_date       DATE NOT NULL,
    check_out_date      DATE NOT NULL,
    adults              INTEGER NOT NULL DEFAULT 1,
    children            INTEGER DEFAULT 0,
    special_requests    TEXT,
    deposit_amount      DECIMAL(12,2) DEFAULT 0,
    deposit_paid_at     TIMESTAMP,
    approved_by         INTEGER REFERENCES users(id),
    approved_at         TIMESTAMP,
    cancelled_at        TIMESTAMP,
    cancel_reason       TEXT,
    created_by          INTEGER REFERENCES users(id),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date)
);

CREATE INDEX idx_reservations_branch ON reservations(branch_id);
CREATE INDEX idx_reservations_guest ON reservations(guest_id);
CREATE INDEX idx_reservations_room ON reservations(room_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_dates ON reservations(check_in_date, check_out_date);

CREATE TABLE reservation_history (
    id              SERIAL PRIMARY KEY,
    reservation_id  INTEGER NOT NULL REFERENCES reservations(id) ON DELETE CASCADE,
    previous_status VARCHAR(50),
    new_status      VARCHAR(50) NOT NULL,
    changed_by      INTEGER REFERENCES users(id),
    snapshot        JSONB,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservation_history_res ON reservation_history(reservation_id);

CREATE TABLE waiting_list (
    id              SERIAL PRIMARY KEY,
    branch_id       INTEGER NOT NULL REFERENCES branches(id),
    guest_id        INTEGER NOT NULL REFERENCES guests(id),
    category_id     INTEGER REFERENCES room_categories(id),
    desired_check_in DATE NOT NULL,
    desired_check_out DATE NOT NULL,
    status          VARCHAR(50) DEFAULT 'WAITING',
    notified_at     TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_waiting_list_branch ON waiting_list(branch_id);
CREATE INDEX idx_waiting_list_dates ON waiting_list(desired_check_in, desired_check_out);

-- ==================== BILLING ====================
CREATE TABLE payment_status (
    code        VARCHAR(50) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL
);

INSERT INTO payment_status (code, name) VALUES
('PENDING', 'Pending'),
('PARTIAL', 'Partial'),
('PAID', 'Paid'),
('REFUNDED', 'Refunded'),
('FAILED', 'Failed');

CREATE TABLE payment_types (
    code        VARCHAR(50) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL
);

INSERT INTO payment_types (code, name) VALUES
('CASH', 'Cash'),
('CARD', 'Credit/Debit Card'),
('BANK_TRANSFER', 'Bank Transfer'),
('ONLINE', 'Online Payment'),
('VOUCHER', 'Voucher');

CREATE TABLE seasonal_pricing (
    id          SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL REFERENCES room_categories(id),
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    multiplier  DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    name        VARCHAR(100),
    CONSTRAINT chk_season_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_seasonal_pricing_category ON seasonal_pricing(category_id);
CREATE INDEX idx_seasonal_pricing_dates ON seasonal_pricing(start_date, end_date);

CREATE TABLE invoices (
    id              SERIAL PRIMARY KEY,
    reservation_id  INTEGER NOT NULL REFERENCES reservations(id),
    invoice_number  VARCHAR(50) NOT NULL UNIQUE,
    subtotal        DECIMAL(12,2) NOT NULL,
    tax_rate        DECIMAL(5,2) DEFAULT 0,
    tax_amount      DECIMAL(12,2) DEFAULT 0,
    discount_amount DECIMAL(12,2) DEFAULT 0,
    total_amount    DECIMAL(12,2) NOT NULL,
    status          VARCHAR(50) DEFAULT 'PENDING',
    due_date        DATE,
    paid_at         TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoices_reservation ON invoices(reservation_id);
CREATE INDEX idx_invoices_number ON invoices(invoice_number);

CREATE TABLE invoice_line_items (
    id          SERIAL PRIMARY KEY,
    invoice_id  INTEGER NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    quantity    DECIMAL(10,2) DEFAULT 1,
    unit_price  DECIMAL(12,2) NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    line_type   VARCHAR(50)
);

CREATE INDEX idx_invoice_lines_invoice ON invoice_line_items(invoice_id);

CREATE TABLE payments (
    id              SERIAL PRIMARY KEY,
    invoice_id      INTEGER REFERENCES invoices(id),
    reservation_id  INTEGER REFERENCES reservations(id),
    amount          DECIMAL(12,2) NOT NULL,
    payment_type    VARCHAR(50) NOT NULL REFERENCES payment_types(code),
    reference       VARCHAR(255),
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING' REFERENCES payment_status(code),
    processed_at    TIMESTAMP,
    created_by      INTEGER REFERENCES users(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_reservation ON payments(reservation_id);

CREATE TABLE extra_services (
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    price       DECIMAL(12,2) NOT NULL,
    tax_inclusive BOOLEAN DEFAULT false,
    is_active   BOOLEAN DEFAULT true
);

CREATE TABLE reservation_extras (
    id          SERIAL PRIMARY KEY,
    reservation_id INTEGER NOT NULL REFERENCES reservations(id),
    extra_id    INTEGER NOT NULL REFERENCES extra_services(id),
    quantity    INTEGER DEFAULT 1,
    unit_price  DECIMAL(12,2) NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== PROMOTIONS ====================
CREATE TABLE promotions (
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    discount_type   VARCHAR(50) NOT NULL,
    discount_value  DECIMAL(12,2) NOT NULL,
    min_stay_nights INTEGER,
    valid_from      DATE NOT NULL,
    valid_to        DATE NOT NULL,
    max_uses        INTEGER,
    used_count      INTEGER DEFAULT 0,
    is_active       BOOLEAN DEFAULT true,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_promo_dates CHECK (valid_to >= valid_from)
);

CREATE INDEX idx_promotions_code ON promotions(code);
CREATE INDEX idx_promotions_dates ON promotions(valid_from, valid_to);

-- ==================== NOTIFICATIONS ====================
CREATE TABLE notifications (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER REFERENCES users(id),
    guest_id    INTEGER REFERENCES guests(id),
    type        VARCHAR(50) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    body        TEXT,
    entity_type VARCHAR(100),
    entity_id   VARCHAR(100),
    is_read     BOOLEAN DEFAULT false,
    read_at     TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_guest ON notifications(guest_id);
CREATE INDEX idx_notifications_type ON notifications(type);

-- ==================== MAINTENANCE ====================
CREATE TABLE maintenance_requests (
    id          SERIAL PRIMARY KEY,
    room_id     INTEGER NOT NULL REFERENCES rooms(id),
    reported_by INTEGER REFERENCES users(id),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    priority    VARCHAR(50) DEFAULT 'NORMAL',
    status      VARCHAR(50) DEFAULT 'OPEN',
    assigned_to INTEGER REFERENCES users(id),
    completed_at TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_maintenance_room ON maintenance_requests(room_id);
CREATE INDEX idx_maintenance_status ON maintenance_requests(status);

-- ==================== INVENTORY ====================
CREATE TABLE inventory_items (
    id          SERIAL PRIMARY KEY,
    branch_id   INTEGER NOT NULL REFERENCES branches(id),
    sku         VARCHAR(50) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    category    VARCHAR(100),
    quantity    INTEGER NOT NULL DEFAULT 0,
    unit        VARCHAR(50) DEFAULT 'UNIT',
    min_level   INTEGER DEFAULT 0,
    reorder_level INTEGER,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(branch_id, sku)
);

CREATE INDEX idx_inventory_branch ON inventory_items(branch_id);

CREATE TABLE inventory_transactions (
    id          SERIAL PRIMARY KEY,
    item_id     INTEGER NOT NULL REFERENCES inventory_items(id),
    quantity    INTEGER NOT NULL,
    type        VARCHAR(50) NOT NULL,
    reference   VARCHAR(255),
    created_by  INTEGER REFERENCES users(id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== HOUSEKEEPING ====================
CREATE TABLE housekeeping_tasks (
    id          SERIAL PRIMARY KEY,
    room_id     INTEGER NOT NULL REFERENCES rooms(id),
    assigned_to INTEGER REFERENCES users(id),
    task_type   VARCHAR(50) NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    status      VARCHAR(50) DEFAULT 'PENDING',
    notes       TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_housekeeping_room ON housekeeping_tasks(room_id);
CREATE INDEX idx_housekeeping_scheduled ON housekeeping_tasks(scheduled_at);

-- ==================== FEEDBACK ====================
CREATE TABLE feedback (
    id          SERIAL PRIMARY KEY,
    guest_id    INTEGER REFERENCES guests(id),
    reservation_id INTEGER REFERENCES reservations(id),
    rating      INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment     TEXT,
    category    VARCHAR(100),
    is_public   BOOLEAN DEFAULT true,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_feedback_guest ON feedback(guest_id);
CREATE INDEX idx_feedback_rating ON feedback(rating);

-- ==================== SERVICE REQUESTS ====================
CREATE TABLE service_requests (
    id              SERIAL PRIMARY KEY,
    reservation_id  INTEGER REFERENCES reservations(id),
    guest_id        INTEGER REFERENCES guests(id),
    request_type    VARCHAR(50) NOT NULL,
    description     TEXT,
    status          VARCHAR(50) DEFAULT 'PENDING',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_service_requests_guest ON service_requests(guest_id);
CREATE INDEX idx_service_requests_reservation ON service_requests(reservation_id);
CREATE INDEX idx_service_requests_status ON service_requests(status);

-- ==================== SESSIONS (optional server-side session store) ====================
CREATE TABLE user_sessions (
    id          VARCHAR(255) PRIMARY KEY,
    user_id     INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP NOT NULL,
    ip_address  VARCHAR(45),
    user_agent  TEXT
);

CREATE INDEX idx_sessions_user ON user_sessions(user_id);
CREATE INDEX idx_sessions_expires ON user_sessions(expires_at);

-- ==================== SEED DATA ====================
-- Default admin (password: Admin@123)
-- Hash generated with BCrypt equivalent - replace with actual hash from app
INSERT INTO users (role_id, branch_id, username, email, password_hash, first_name, last_name)
SELECT 1, 1, 'admin', 'admin@oceanview.lk',
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
       'System', 'Administrator'
FROM roles WHERE code = 'ADMIN' LIMIT 1;

-- Additional staff & customer users (passwords use same demo hash as admin: Admin@123)
INSERT INTO users (role_id, branch_id, username, email, password_hash, first_name, last_name, phone)
VALUES
((SELECT id FROM roles WHERE code = 'MANAGER'), 1, 'manager1', 'manager1@oceanview.lk',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amal', 'Fernando', '+94 71 123 4567'),
((SELECT id FROM roles WHERE code = 'RECEPTIONIST'), 1, 'reception1', 'reception1@oceanview.lk',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nimali', 'Perera', '+94 77 987 6543'),
((SELECT id FROM roles WHERE code = 'CUSTOMER'), 1, 'guest1', 'guest1@oceanview.lk',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lahiru', 'Jayasinghe', '+94 76 555 8899');

-- Room categories and sample rooms
INSERT INTO room_categories (code, name, description, base_price, max_occupancy, size_sqm) VALUES
('STD', 'Standard Ocean View', 'Comfortable room with ocean view', 15000.00, 2, 28.00),
('DLX', 'Deluxe Sea View', 'Spacious deluxe with balcony', 25000.00, 3, 38.00),
('STE', 'Suite', 'Premium suite with living area', 45000.00, 4, 55.00);

INSERT INTO rooms (branch_id, category_id, room_number, floor, status, view_type) VALUES
(1, 1, '101', 1, 'AVAILABLE', 'OCEAN'),
(1, 1, '102', 1, 'AVAILABLE', 'OCEAN'),
(1, 2, '201', 2, 'AVAILABLE', 'OCEAN'),
(1, 2, '202', 2, 'AVAILABLE', 'OCEAN'),
(1, 3, '301', 3, 'AVAILABLE', 'OCEAN');

-- Extra Services
INSERT INTO extra_services (code, name, price, tax_inclusive, is_active) VALUES
('DJ_NIGHT', 'DJ Night Party', 35000.00, false, true),
('CAMP_BBQ', 'Camp Night with BBQ', 28000.00, false, true),
('POOL_1HR', 'Swimming Pool Reservation (1 Hour)', 5000.00, false, true),
('SUNSET_CRUISE', 'Sunset Cruise Experience', 18000.00, false, true),
('SPA_WELLNESS', 'Spa & Wellness Package', 12000.00, false, true),
('SURF_LESSON', 'Surfing Lesson (2 Hours)', 8500.00, false, true),
('BEACH_BONFIRE', 'Beach Bonfire Evening', 15000.00, false, true),
('YOGA_SESSION', 'Beachside Yoga Session', 3500.00, false, true),
('SNORKELING', 'Snorkeling Adventure', 7500.00, false, true),
('KAYAK_TOUR', 'Kayak Tour (1 Hour)', 6000.00, false, true);

-- Guests linked to customer user
INSERT INTO guests (user_id, first_name, last_name, email, phone, nationality, loyalty_points)
VALUES
((SELECT id FROM users WHERE username = 'guest1'),
 'Lahiru', 'Jayasinghe', 'guest1@oceanview.lk', '+94 76 555 8899', 'Sri Lanka', 120);

-- Sample reservations for reporting & receptionist workflows
INSERT INTO reservations (branch_id, guest_id, room_id, status, check_in_date, check_out_date, adults, children, special_requests, deposit_amount, created_by)
VALUES
(
  1,
  (SELECT id FROM guests WHERE email = 'guest1@oceanview.lk'),
  (SELECT id FROM rooms WHERE room_number = '101'),
  'CONFIRMED',
  CURRENT_DATE + INTERVAL '1 day',
  CURRENT_DATE + INTERVAL '3 days',
  2,
  0,
  'High floor if available',
  10000.00,
  (SELECT id FROM users WHERE username = 'reception1')
),
(
  1,
  (SELECT id FROM guests WHERE email = 'guest1@oceanview.lk'),
  (SELECT id FROM rooms WHERE room_number = '201'),
  'CHECKED_IN',
  CURRENT_DATE - INTERVAL '1 day',
  CURRENT_DATE + INTERVAL '1 day',
  2,
  1,
  'Baby cot required',
  15000.00,
  (SELECT id FROM users WHERE username = 'reception1')
);

-- Reservation history mock data
INSERT INTO reservation_history (reservation_id, previous_status, new_status, changed_by, snapshot)
SELECT id, 'PENDING_APPROVAL', status, (SELECT id FROM users WHERE username = 'manager1'),
       jsonb_build_object('note', 'Auto-approved for demo data')
FROM reservations
WHERE status IN ('CONFIRMED','CHECKED_IN');

-- Waiting list mock entries
INSERT INTO waiting_list (branch_id, guest_id, category_id, desired_check_in, desired_check_out, status)
VALUES
(1,
 (SELECT id FROM guests WHERE email = 'guest1@oceanview.lk'),
 (SELECT id FROM room_categories WHERE code = 'DLX'),
 CURRENT_DATE + INTERVAL '7 days',
 CURRENT_DATE + INTERVAL '10 days',
 'WAITING');

-- Seasonal pricing examples
INSERT INTO seasonal_pricing (category_id, start_date, end_date, multiplier, name)
VALUES
((SELECT id FROM room_categories WHERE code = 'STD'),
 CURRENT_DATE + INTERVAL '30 days',
 CURRENT_DATE + INTERVAL '60 days',
 1.25,
 'Peak Season – Standard'),
((SELECT id FROM room_categories WHERE code = 'DLX'),
 CURRENT_DATE + INTERVAL '30 days',
 CURRENT_DATE + INTERVAL '60 days',
 1.40,
 'Peak Season – Deluxe');

-- Promotions used on the welcome page (manager-configured offers)
INSERT INTO promotions (code, name, description, discount_type, discount_value, min_stay_nights, valid_from, valid_to, max_uses, is_active)
VALUES
('OCEANESCAPE',
 'Ocean Escape – 20% off suites',
 'Stay 3 nights or more in a Suite and enjoy 20% off the room rate plus complimentary breakfast.',
 'PERCENT',
 20.00,
 3,
 CURRENT_DATE - INTERVAL '5 days',
 CURRENT_DATE + INTERVAL '25 days',
 200,
 true),
('SUNSETGETAWAY',
 'Sunset Getaway – LKR 8,000 off',
 'Weekend stays (Fri–Sun) with a flat LKR 8,000 discount on Deluxe Sea View rooms.',
 'AMOUNT',
 8000.00,
 2,
 CURRENT_DATE - INTERVAL '1 day',
 CURRENT_DATE + INTERVAL '40 days',
 NULL,
 true);

-- Sample invoices and line items for reporting
INSERT INTO invoices (reservation_id, invoice_number, subtotal, tax_rate, tax_amount, discount_amount, total_amount, status, due_date, paid_at)
VALUES
(
  (SELECT id FROM reservations ORDER BY id LIMIT 1),
  'INV-OV-1001',
  60000.00,
  10.00,
  6000.00,
  0.00,
  66000.00,
  'PAID',
  CURRENT_DATE,
  CURRENT_TIMESTAMP
);

INSERT INTO invoice_line_items (invoice_id, description, quantity, unit_price, amount, line_type)
VALUES
(
  (SELECT id FROM invoices WHERE invoice_number = 'INV-OV-1001'),
  'Room charge – Standard Ocean View (2 nights)',
  2,
  30000.00,
  60000.00,
  'ROOM'
);

-- Payments example
INSERT INTO payments (invoice_id, reservation_id, amount, payment_type, reference, status, processed_at, created_by)
VALUES
(
  (SELECT id FROM invoices WHERE invoice_number = 'INV-OV-1001'),
  (SELECT reservation_id FROM invoices WHERE invoice_number = 'INV-OV-1001'),
  66000.00,
  'CARD',
  'VISA-XXXX-8842',
  'PAID',
  CURRENT_TIMESTAMP,
  (SELECT id FROM users WHERE username = 'reception1')
);

-- Reservation extras usage
INSERT INTO reservation_extras (reservation_id, extra_id, quantity, unit_price, amount)
VALUES
(
  (SELECT id FROM reservations ORDER BY id DESC LIMIT 1),
  (SELECT id FROM extra_services WHERE code = 'SPA_WELLNESS'),
  1,
  12000.00,
  12000.00
);

-- Notifications for guests & staff
INSERT INTO notifications (user_id, guest_id, type, title, body, entity_type, entity_id)
VALUES
(
  (SELECT id FROM users WHERE username = 'manager1'),
  NULL,
  'SYSTEM',
  'Daily summary ready',
  'Your daily occupancy and revenue summary is available in the dashboard.',
  'REPORT',
  'SUMMARY'
),
(
  NULL,
  (SELECT id FROM guests WHERE email = 'guest1@oceanview.lk'),
  'RESERVATION',
  'Reservation confirmed',
  'Your upcoming stay at OceanView has been confirmed. We look forward to welcoming you.',
  'RESERVATION',
  (SELECT id::text FROM reservations ORDER BY id LIMIT 1)
);

-- Maintenance request sample
INSERT INTO maintenance_requests (room_id, reported_by, title, description, priority, status, assigned_to)
VALUES
(
  (SELECT id FROM rooms WHERE room_number = '201'),
  (SELECT id FROM users WHERE username = 'reception1'),
  'Air conditioner service',
  'Guest reported that the air conditioner is not cooling properly.',
  'HIGH',
  'OPEN',
  (SELECT id FROM users WHERE username = 'manager1')
);

-- Inventory and transactions sample data
INSERT INTO inventory_items (branch_id, sku, name, category, quantity, unit, min_level, reorder_level)
VALUES
(1, 'LIN-001', 'King-size bed linen set', 'Housekeeping', 50, 'SET', 10, 20),
(1, 'FNB-001', 'Mineral water 1L', 'Food & Beverage', 200, 'BOTTLE', 50, 100);

INSERT INTO inventory_transactions (item_id, quantity, type, reference, created_by)
VALUES
(
  (SELECT id FROM inventory_items WHERE sku = 'LIN-001'),
  -3,
  'ISSUE',
  'Room preparation',
  (SELECT id FROM users WHERE username = 'reception1')
),
(
  (SELECT id FROM inventory_items WHERE sku = 'FNB-001'),
  -6,
  'ISSUE',
  'Welcome amenities',
  (SELECT id FROM users WHERE username = 'reception1')
);

-- Housekeeping tasks
INSERT INTO housekeeping_tasks (room_id, assigned_to, task_type, scheduled_at, status, notes)
VALUES
(
  (SELECT id FROM rooms WHERE room_number = '101'),
  (SELECT id FROM users WHERE username = 'reception1'),
  'TURN_DOWN',
  CURRENT_TIMESTAMP + INTERVAL '6 hours',
  'PENDING',
  'Evening turndown service with honeymoon decoration.'
);

-- Feedback examples
INSERT INTO feedback (guest_id, reservation_id, rating, comment, category, is_public)
VALUES
(
  (SELECT id FROM guests WHERE email = 'guest1@oceanview.lk'),
  (SELECT id FROM reservations ORDER BY id LIMIT 1),
  5,
  'Amazing stay with stunning ocean views and very friendly staff.',
  'GENERAL',
  true
);

-- Service requests examples
INSERT INTO service_requests (reservation_id, guest_id, request_type, description, status)
VALUES
(
  (SELECT id FROM reservations ORDER BY id DESC LIMIT 1),
  (SELECT id FROM guests WHERE email = 'guest1@oceanview.lk'),
  'ROOM_SERVICE',
  'Order for two fresh juices and a fruit platter.',
  'PENDING'
);

-- Activity log example
INSERT INTO activity_logs (user_id, action, entity_type, entity_id, details, ip_address)
VALUES
(
  (SELECT id FROM users WHERE username = 'manager1'),
  'LOGIN',
  'USER',
  (SELECT id::text FROM users WHERE username = 'manager1'),
  jsonb_build_object('note', 'Manager logged in for morning shift'),
  '127.0.0.1'
);

-- Demo user session (for debugging only)
INSERT INTO user_sessions (id, user_id, created_at, expires_at, ip_address, user_agent)
VALUES
(
  'demo-session-admin',
  (SELECT id FROM users WHERE username = 'admin'),
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP + INTERVAL '30 minutes',
  '127.0.0.1',
  'Demo seed data'
);
