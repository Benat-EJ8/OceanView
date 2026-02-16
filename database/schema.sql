-- Ocean View Resort - PostgreSQL Schema
-- Galle Beachside Hotel Management System

-- Extensions (optional, for UUID if desired)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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
