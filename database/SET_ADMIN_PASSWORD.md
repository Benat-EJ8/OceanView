# Set admin password

The default admin user in the schema has a placeholder password hash that doesn’t match `Admin@123` or `password`. Use one of the methods below to set a known password.

---

## Option A: Generate hash in Java, then update DB (recommended)

### Step 1: Generate a BCrypt hash

**From IntelliJ:**

1. Open `backend/src/main/java/com/oceanview/resort/util/GenerateAdminPasswordHash.java`.
2. Right‑click the file or the class name → **Run 'GenerateAdminPasswordHash.main()'**.
3. In the **Run** tool window you’ll see something like:
   ```text
   Password: Admin@123
   BCrypt hash (use in SQL):
   $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

To use a different password (e.g. `MySecret123`), run with a program argument:  
**Run → Edit Configurations → GenerateAdminPasswordHash → Program arguments:** `MySecret123` → **Apply** → **Run**.

**From command line:**

```bash
cd backend
mvn -q exec:java -Dexec.mainClass="com.oceanview.resort.util.GenerateAdminPasswordHash" -Dexec.args="Admin@123"
```

To generate a hash for a different password, change the last argument, e.g. `-Dexec.args="MyPassword123"`.

### Step 2: Update the database

1. Connect to your `oceanview` database (psql, pgAdmin, DBeaver, etc.).
2. Run (replace the hash with the one from Step 1):

```sql
UPDATE users SET password_hash = '$2a$10$...paste.the.full.hash.here...' WHERE username = 'admin';
```

3. Confirm one row updated: `SELECT 1 FROM users WHERE username = 'admin';`

### Step 3: Log in

- **URL:** http://localhost:5173 (or your frontend URL)  
- **Username:** `admin`  
- **Password:** the one you used in Step 1 (e.g. `Admin@123`).

---

## Option B: Generate hash online, then update DB

If you prefer not to run the Java class:

1. Open a BCrypt generator (e.g. https://www.bcrypt-generator.com/ ). Rounds: **10**.
2. Enter password: **Admin@123** (or your choice) and generate the hash.
3. In PostgreSQL run (paste the hash you got):

```sql
UPDATE users SET password_hash = 'PASTE_THE_HASH_HERE' WHERE username = 'admin';
```

4. Log in with **username:** `admin`, **password:** whatever you hashed (e.g. `Admin@123`).
