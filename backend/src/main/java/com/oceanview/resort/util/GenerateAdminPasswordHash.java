package com.oceanview.resort.util;

import com.oceanview.resort.security.PasswordHasher;

/**
 * Run this class once to generate a BCrypt hash for the admin password.
 * Then run the SQL in database/set-admin-password.sql with the printed hash.
 */
public class GenerateAdminPasswordHash {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "Admin@123";
        String hash = PasswordHasher.hash(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt hash (use in SQL):");
        System.out.println(hash);
        System.out.println();
        System.out.println("Run this SQL in PostgreSQL (replace YOUR_HASH with the hash above):");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE username = 'admin';");
    }
}
