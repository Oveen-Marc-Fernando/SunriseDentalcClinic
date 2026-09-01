-- =============================================================================
-- Sunrise Dental System — database schema
--
-- Mirrors the model/ classes 1:1 (User, DentistModel, PatientModel,
-- BillingModel, InventoryModel, SupplyRequestModel, LeaveRequestModel) plus
-- one appointments table the app doesn't yet have a formal Model class for
-- (D_APP_Grid / OS_AM_Grid currently hold appointment rows as raw
-- Object[][] sample data instead).
--
-- How to run this:
--   1. Start MySQL in XAMPP.
--   2. Open http://localhost/phpmyadmin
--   3. Click "SQL" tab (or "Import" and pick this file) and run this whole
--      script — it creates the database, every table, and seeds each one
--      with the same sample data already hardcoded into the app's
--      controllers/grids, so you have real rows to test against immediately.
--   4. db/db.properties already points at db_url=...sunrise_dental — no
--      Java-side config changes needed once this has run.
--
-- Note on current app inconsistencies (so the seed data below isn't a
-- surprise): different screens currently seed their own independent sample
-- data rather than sharing one source — e.g. D_RS_Grid's dentist-facing
-- "Inventory" browse view and SupplyRequestController's own product catalog
-- use IDs (I0001-I0003) that don't match the real Inventory Management
-- module's IDs (P1001-P1003) seeded here; OS_AM_Grid / OS_APM_Grid similarly
-- have their own separate appointment samples not seeded below. This schema
-- seeds the "canonical" version of each entity (the one with a real
-- Model class); unifying every screen onto these tables via DAOs is the
-- natural next step once this schema is in place.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS sunrise_dental
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sunrise_dental;

-- =============================================================================
-- users — authentication (mirrors model.User / model.LoginModel)
-- =============================================================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    username    VARCHAR(50)  NOT NULL PRIMARY KEY,
    -- Plain text here only because LoginModel.java currently stores plain
    -- text too (it's a stub/demo auth store). Before any real deployment,
    -- switch this to a hashed value (e.g. BCrypt) and hash on the Java side
    -- before it ever reaches this column.
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    -- Collected at self-registration (Register.java); pre-fills this
    -- account's own profile email/NIC/contact number later (see
    -- PatientProfileController) so they don't have to be typed twice.
    email           VARCHAR(150) NULL,
    nic             VARCHAR(30)  NULL,
    contact_number  VARCHAR(20)  NULL,
    role        ENUM('OFFICE_STAFF', 'DENTIST', 'ADMINISTRATION', 'PATIENT', 'UNKNOWN') NOT NULL,
    -- New self-registrations (Register.java) start PENDING and can't log in
    -- until an Administrator approves them (AD_APR_UserLogins). Every
    -- account seeded below defaults to APPROVED so existing logins keep
    -- working unchanged.
    status         ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'APPROVED',
    approved_date  DATE NULL,
    -- Stable link from a PATIENT-role login to its own row in "patients" —
    -- set the first time that login's My Profile wizard saves anything
    -- (PatientProfileController). Before this existed, "which patient
    -- record is this login" was resolved by matching full_name every time,
    -- which broke (creating a duplicate orphaned patient record) the
    -- moment someone edited their own Full Name in that wizard. The FK
    -- constraint itself is added further down (as an ALTER TABLE, once
    -- "patients" actually exists — this table is created before it).
    patient_id     VARCHAR(20) NULL,
    -- Same idea, for a DENTIST-role login and its own row in "dentists"
    -- (DentistProfileController) — same bug class, same fix.
    dentist_id      VARCHAR(30) NULL,
    -- True only for accounts Office Staff creates on someone else's behalf
    -- with a system-generated temporary password (Dentist Management's
    -- "Add"/"Create Login" — see DentistManagementController). Forces the
    -- Edit Profile popup open on that person's very first dashboard visit
    -- so the temp password never stays in place long-term. Self-registered
    -- accounts (Register.java) pick their own password up front, so this
    -- stays FALSE for them.
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

INSERT INTO users (username, password, full_name, role, status, approved_date) VALUES
    ('officestaff', 'staff123', 'Office Staff',  'OFFICE_STAFF',   'APPROVED', '2026-01-05'),
    ('dentist',     'dent456',  'Dr. Smith',     'DENTIST',        'APPROVED', '2026-01-05'),
    ('admin',       'admin789', 'Administrator', 'ADMINISTRATION', 'APPROVED', '2026-01-05'),
    ('patient',     'pat000',   'John Doe',      'PATIENT',        'APPROVED', '2026-01-05'),
    ('adminlegacy', '1234',     'Legacy Admin',  'ADMINISTRATION', 'APPROVED', '2026-01-05');

-- =============================================================================
-- dentists — mirrors model.DentistModel (all 5 wizard steps' fields)
-- =============================================================================
DROP TABLE IF EXISTS dentists;
CREATE TABLE dentists (
    dentist_id           VARCHAR(30)  NOT NULL PRIMARY KEY,
    title                VARCHAR(10),
    gender               VARCHAR(10),
    full_name            VARCHAR(100) NOT NULL UNIQUE,
    dob                  DATE,
    nic                  VARCHAR(20),
    slmc_no              VARCHAR(30),
    qualification        VARCHAR(150),
    university           VARCHAR(150),
    graduation_year      YEAR,
    specialization       VARCHAR(150),
    experience           VARCHAR(50),
    license_status       ENUM('Active', 'Inactive'),
    mobile_no            VARCHAR(20),
    email                VARCHAR(100),
    address               VARCHAR(255),
    emergency_no         VARCHAR(20),
    joined_date          DATE,
    employment_type      VARCHAR(50), -- Full Time / Part Time / Contract
    consultation_fee     DECIMAL(10,2),
    employment_status    VARCHAR(50), -- General Dentist / Specialist / Consultant / Resident
    working_days         VARCHAR(100), -- e.g. "Monday, Wednesday, Friday"
    start_time           TIME,
    end_time             TIME,
    break_time           VARCHAR(20), -- stored "HH:mm-HH:mm" to match DentistModel's own string format
    room_no              VARCHAR(30)
) ENGINE=InnoDB;

-- Dentist ID follows the app-wide "D101, D102, ..." scheme (DentistDAO.nextDentistId()).
INSERT INTO dentists (dentist_id, title, full_name, slmc_no, mobile_no, email, consultation_fee,
        license_status, working_days, start_time, end_time, break_time, room_no, employment_type) VALUES
    ('D101', 'Dr', 'Dr. Smith', 'SL45210', '071-2345678', 'smith@sunrisedental.com', 3000.00,
        'Active',   'Monday, Tuesday, Wednesday, Thursday, Friday', '09:00', '17:00', '12:45-13:15', 'Room 102', 'Consultant'),
    ('D102', 'Dr', 'Dr. Oveen', 'SL12756', '070-1568774', 'marc@gmail.com',        2500.00,
        'Active',   'Monday, Wednesday, Friday',                    '09:00', '17:00', '12:00-13:00', 'Room 101', 'Full-Time'),
    ('D103', 'Dr', 'Dr. Ashan', 'SL76451', '077-8945212', 'asha2@gmail.com',       3250.00,
        'Inactive', 'Tuesday, Thursday, Saturday',                  '10:00', '16:00', '13:00-14:00', 'Room 103', 'Part-Time');

-- "dentists" now exists, so "users".dentist_id (declared up above) can finally get its real FK.
ALTER TABLE users
    ADD CONSTRAINT fk_users_dentist
    FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id)
    ON UPDATE CASCADE ON DELETE SET NULL;

-- =============================================================================
-- patients — mirrors model.PatientModel
-- Only the columns OS_PM_Grid's sample data currently populates (name,
-- patient_id, dob, email) are seeded — the rest exist for when the full
-- Patient Management wizard (all of PatientModel's fields) is wired to a DAO.
-- =============================================================================
DROP TABLE IF EXISTS patients;
CREATE TABLE patients (
    patient_id             VARCHAR(30)  NOT NULL PRIMARY KEY,
    title                   VARCHAR(10),
    gender                  VARCHAR(10),
    full_name               VARCHAR(100) NOT NULL,
    dob                      DATE,
    age                      VARCHAR(10),
    nic                     VARCHAR(30),
    address_line1           VARCHAR(150),
    address_line2           VARCHAR(150),
    city                     VARCHAR(80),
    mobile_no               VARCHAR(20),
    landline_no             VARCHAR(20),
    email                    VARCHAR(100),
    blood_group             VARCHAR(10),
    allergies                TEXT,
    medical_conditions      TEXT,
    current_medications     TEXT,
    previous_surgeries      TEXT,
    general_medical_notes   TEXT,
    last_dental_visit       DATE,
    dental_history           TEXT,
    dental_problems          TEXT,
    oral_hygiene             VARCHAR(10), -- Good / Fair / Poor
    dental_medical_notes    TEXT
) ENGINE=InnoDB;

-- Patient ID follows the app-wide "P101, P102, ..." scheme (PatientDAO.nextPatientId()).
INSERT INTO patients (patient_id, full_name, dob, address_line1, city, mobile_no, email, last_dental_visit) VALUES
    ('P101', 'Thejaan',  '2020-08-03', '45 Lotus Road', 'Colombo', '077-1234567', 'marc@gmail.com',     '2026-06-12'),
    ('P102', 'Asini',    '2023-11-17', '12 Palm Grove', 'Kandy',   '071-9876543', 'asha2@gmail.com',    '2026-05-28'),
    ('P103', 'Kaveesha', '2024-01-05', '8 Ocean View',  'Galle',   '076-5551212', 'kaveesha@gmail.com', '2026-07-03');

-- "patients" now exists, so "users".patient_id (declared up above) can finally get its real FK.
ALTER TABLE users
    ADD CONSTRAINT fk_users_patient
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
    ON UPDATE CASCADE ON DELETE SET NULL;

-- =============================================================================
-- appointments — no formal Model class yet in the app (D_APP_Grid holds
-- these as raw Object[][] sample rows); this is the canonical shape.
-- =============================================================================
DROP TABLE IF EXISTS appointments;
CREATE TABLE appointments (
    appointment_id   VARCHAR(20)  NOT NULL PRIMARY KEY,
    patient_name      VARCHAR(100) NOT NULL,
    dentist_name      VARCHAR(100) NOT NULL,
    treatment_type    VARCHAR(150),
    appointment_date DATE         NOT NULL,
    appointment_time TIME         NOT NULL,
    status            ENUM('Pending', 'Completed', 'Rejected') NOT NULL DEFAULT 'Pending',
    -- Snapshot of the patient's address/contact number at the moment this
    -- appointment was booked (OS_AM_1 auto-fills both from the
    -- patient's own record, then this table keeps its own permanent copy —
    -- deliberately not re-joined live from "patients" on every read, so an
    -- old appointment/receipt still shows what was true when it was booked).
    address           VARCHAR(255),
    contact_no        VARCHAR(20),
    CONSTRAINT fk_appointments_dentist
        FOREIGN KEY (dentist_name) REFERENCES dentists(full_name)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Appointment ID follows the app-wide "APO101, APO102, ..." scheme (AppointmentDAO.nextAppointmentId()).
INSERT INTO appointments (appointment_id, patient_name, dentist_name, treatment_type, appointment_date, appointment_time, status) VALUES
    ('APO101', 'Thejaan',  'Dr. Smith', 'Dental Crown',          '2026-08-17', '09:00:00', 'Completed'),
    ('APO102', 'Asini',    'Dr. Smith', 'Root Canal Treatment',  '2026-08-19', '11:00:00', 'Pending'),
    ('APO103', 'Kaveesha', 'Dr. Smith', 'Teeth Whitening',       '2026-08-21', '14:00:00', 'Pending');

-- =============================================================================
-- billings — mirrors model.BillingModel
-- =============================================================================
DROP TABLE IF EXISTS billings;
CREATE TABLE billings (
    billing_id           VARCHAR(20)  NOT NULL PRIMARY KEY,
    appointment_id        VARCHAR(20),
    patient_id            VARCHAR(30),
    dentist_name          VARCHAR(100),
    patient_name          VARCHAR(100) NOT NULL,
    appointment_date      DATE,
    appointment_charges   DECIMAL(10,2) DEFAULT 0,
    clinical_total        DECIMAL(10,2) DEFAULT 0,
    medicine_total        DECIMAL(10,2) DEFAULT 0,
    total_bill_amount     DECIMAL(10,2) DEFAULT 0,
    -- Incremented each time the receipt PDF is successfully emailed
    -- (BillPreviewDialog's Email button) — AD_OP_Billings' "Sent Email Count" column.
    email_sent_count      INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_billings_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_billings_patient
        FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Billing ID follows the app-wide "B101, B102, ..." scheme (BillingDAO.nextBillingId()).
-- Linked to a real row in "appointments" below (appointment_id/dentist_name/
-- appointment_date), and appointment_charges mirrors total_bill_amount since
-- these 3 predate the clinical/medicine cost breakdown — keeps the PDF
-- receipt's "Appointment Charges" line item consistent with its total.
INSERT INTO billings (billing_id, appointment_id, patient_id, dentist_name, patient_name, appointment_date, appointment_charges, total_bill_amount) VALUES
    ('B101', 'APO101', 'P101', 'Dr. Smith', 'Mr. Thejaan',  '2026-08-17', 24500.00, 24500.00),
    ('B102', 'APO102', 'P102', 'Dr. Smith', 'Ms. Asini',    '2026-08-19', 42150.00, 42150.00),
    ('B103', 'APO103', 'P103', 'Dr. Smith', 'Mr. Kaveesha', '2026-08-21', 58400.00, 58400.00);

-- =============================================================================
-- inventory — mirrors model.InventoryModel (the canonical Inventory
-- Management module, OS_IM_Grid). Product ID follows the app-wide
-- "I101, I102, ..." scheme (InventoryDAO.nextProductId()); supply_requests
-- below refers to this table's IDs.
-- =============================================================================
DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
    product_id        VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_name       VARCHAR(150) NOT NULL,
    product_type       VARCHAR(100),
    quantity            INT DEFAULT 0,
    manufacture_date   DATE,
    expire_date        DATE,
    description         VARCHAR(255),
    supplier_name      VARCHAR(150),
    buying_price       DECIMAL(10,2),
    contact_number     VARCHAR(20),
    selling_price      DECIMAL(10,2),
    company_name       VARCHAR(150),
    -- AD_OP_Inventory's "Publish Status" column — whether this product is
    -- currently visible to staff (Billing's Medicine Charges list, Request
    -- Supplies catalog, ...). Defaults to visible for every existing row.
    published          TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- Medication-type rows double as Billing's "Medicine Charges" catalog
-- (BillingManagementController reads inventory WHERE product_type =
-- 'Medication') — billing a medicine deducts real stock via
-- InventoryDAO.deductStock(), instead of a disconnected static price list.
INSERT INTO inventory (product_id, product_name, product_type, quantity, manufacture_date, expire_date, supplier_name, contact_number, selling_price, description) VALUES
    ('I101', 'Dental Gloves',       'Consumables', 500, '2026-01-15', '2028-01-15', 'MedSupply Co.',   '070-1234567', NULL,   'Powder-free latex examination gloves, box of 100'),
    ('I102', 'Dental Chair',        'Equipment',     3, '2025-06-10', '2030-06-10', 'DentalTech Ltd.', '077-9876543', NULL,   'Hydraulic reclining dental treatment chair with LED light'),
    ('I103', 'Anesthesia Gel',      'Medication',  120, '2026-03-01', '2027-03-01', 'PharmaPlus',      '071-5551234', 500.00, 'Topical anesthesia gel for minor procedures'),
    ('I104', 'Pain Killers',        'Medication',  300, '2026-01-01', '2028-01-01', 'PharmaPlus',      '071-5551234',  25.00, 'Post-treatment pain relief tablets, blister pack'),
    ('I105', 'Antibiotics',         'Medication',  250, '2026-01-01', '2028-01-01', 'PharmaPlus',      '071-5551234',  45.00, 'Broad-spectrum antibiotic capsules for dental infections'),
    ('I106', 'Mouthwash',           'Medication',   80, '2026-01-01', '2028-01-01', 'PharmaPlus',      '071-5551234', 350.00, 'Antiseptic oral rinse, 500ml bottle'),
    ('I107', 'Vitamin Supplements', 'Medication',  120, '2026-01-01', '2028-01-01', 'PharmaPlus',      '071-5551234', 150.00, 'Multivitamin supplement tablets for post-op recovery'),
    ('I108', 'Antiseptic Gel',      'Medication',   90, '2026-01-01', '2028-01-01', 'PharmaPlus',      '071-5551234', 220.00, 'Topical antiseptic gel for minor oral wounds');

-- =============================================================================
-- supply_requests — mirrors model.SupplyRequestModel
-- =============================================================================
DROP TABLE IF EXISTS supply_requests;
CREATE TABLE supply_requests (
    tracking_id     VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_id       VARCHAR(20),
    product_type     VARCHAR(100),
    product_name     VARCHAR(150) NOT NULL,
    description       VARCHAR(255),
    quantity          INT DEFAULT 0,
    expiry_date      DATE,
    manufacture_date DATE,
    -- Pending until Administration decides via AD_APR_SupplyRequest (same
    -- Pending -> Approved/Rejected shape as leave_requests.status).
    status            VARCHAR(20) NOT NULL DEFAULT 'Pending',
    -- Which dentist submitted this — NULL for legacy rows created before
    -- this column existed (nobody recorded who asked). Same ON UPDATE
    -- CASCADE pattern as leave_requests.dentist_name, so a dentist renaming
    -- themselves in My Profile doesn't orphan their own request history.
    dentist_name      VARCHAR(100) NULL,
    CONSTRAINT fk_supply_requests_product
        FOREIGN KEY (product_id) REFERENCES inventory(product_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_supply_requests_dentist
        FOREIGN KEY (dentist_name) REFERENCES dentists(full_name)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Legacy seed rows predate per-dentist attribution — dentist_name left NULL
-- (unattributed) rather than guessed; still visible to Administration's
-- unscoped approval screen, just not claimed by any one dentist's own view.
INSERT INTO supply_requests (tracking_id, product_id, product_type, product_name, description, quantity, expiry_date, manufacture_date, status) VALUES
    ('SR-1001', 'I101', 'Consumables', 'Dental Gloves',  'Box of disposable exam gloves', 5,  '2027-05-10', '2026-01-15', 'Pending'),
    ('SR-1002', 'I103', 'Medication',  'Anesthesia Gel', 'Topical anesthetic gel',        10, '2027-03-01', '2026-03-01', 'Pending');

-- =============================================================================
-- leave_requests — mirrors model.LeaveRequestModel
-- =============================================================================
DROP TABLE IF EXISTS leave_requests;
CREATE TABLE leave_requests (
    leave_request_id INT AUTO_INCREMENT PRIMARY KEY,
    dentist_name   VARCHAR(100) NOT NULL,
    leave_date     DATE NOT NULL,
    status         ENUM('Pending', 'Approved', 'Rejected') NOT NULL DEFAULT 'Pending',
    CONSTRAINT fk_leave_requests_dentist
        FOREIGN KEY (dentist_name) REFERENCES dentists(full_name)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO leave_requests (dentist_name, leave_date, status) VALUES
    ('Dr. Oveen', '2026-09-12', 'Approved');

-- =============================================================================
-- services — Billing's master service price list (Clinical Charges step).
-- Previously a static in-memory Map inside BillingManagementController
-- (edits made live via OS_BM_Service were lost on every app restart); now
-- a real table, so price changes persist. Medicine Charges has no separate
-- table of its own — it reads Medication-type rows straight out of
-- "inventory" above, so billing a medicine deducts real stock.
-- =============================================================================
DROP TABLE IF EXISTS services;
CREATE TABLE services (
    service_name   VARCHAR(150)  NOT NULL PRIMARY KEY,
    price           DECIMAL(10,2) NOT NULL DEFAULT 0
) ENGINE=InnoDB;

INSERT INTO services (service_name, price) VALUES
    ('Appointment charges',       2200.00),
    ('Aligners',                  7000.00),
    ('Teeth Whitening',           5000.00),
    ('Cleaning & Polishing',      8000.00),
    ('Dental Implants',          25200.00),
    ('Smile Design & Cosmetics', 45200.00),
    ('Root Dental Treatment',     2200.00),
    ('Tooth Colored Fillings',    2200.00),
    ('Dental Bridges',            2200.00),
    ('Dental Crowns',             2200.00),
    ('Dentures',                  2200.00);

-- =============================================================================
-- approvals — mirrors model.ApprovalModel (Office Staff's Approval
-- Management grid + Add Approval form). Office staff can only view status
-- here; approving/declining is Administration-only, done elsewhere.
-- =============================================================================
DROP TABLE IF EXISTS approvals;
CREATE TABLE approvals (
    approval_id     VARCHAR(20)   NOT NULL PRIMARY KEY,
    description      VARCHAR(255)  NOT NULL,
    remarks           VARCHAR(255),
    approval_date    DATE,
    amount            DECIMAL(10,2) DEFAULT 0,
    status            ENUM('Approved', 'Pending', 'Declined') NOT NULL DEFAULT 'Pending',
    -- Which Office Staff login submitted this request (OS_APM_Add) — links
    -- back to "users" so an approval is never an orphan record.
    submitted_by     VARCHAR(50),
    CONSTRAINT fk_approvals_submitted_by
        FOREIGN KEY (submitted_by) REFERENCES users(username)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Approval ID follows the app-wide "APR101, APR102, ..." scheme (ApprovalDAO.nextApprovalId()).
INSERT INTO approvals (approval_id, description, remarks, approval_date, amount, status) VALUES
    ('APR101', 'Xray checkup',         'No issue found',   '2026-08-08', 0, 'Approved'),
    ('APR102', 'Root Canal Treatment', 'Pending review',   '2026-08-07', 0, 'Pending'),
    ('APR103', 'Teeth Whitening',      'Client request',   '2026-08-06', 0, 'Approved'),
    ('APR104', 'Crown fitting',        'Dentist signoff',  '2026-08-05', 0, 'Approved'),
    ('APR105', 'Filling & Cleaning',   'Regular checkup',  '2026-08-04', 0, 'Declined');
