package com.ljuniversity.companion.util;

import java.security.*;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Versioned password storage for new local legacy-mode accounts. */
public final class Passwords {
    private static final int ITERATIONS = 210_000;
    private Passwords() {}
    public static String hash(String password) {
        if (password == null || password.length() < 8 || password.length() > 256) throw new IllegalArgumentException("Use 8 to 256 characters.");
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        return "pbkdf2-sha256$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
    }
    public static boolean verify(String password, String stored) {
        if (password == null || password.length() > 256 || stored == null) return false;
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4 || !parts[0].equals("pbkdf2-sha256")) return false;
            int count = Integer.parseInt(parts[1]);
            if (count < 100_000 || count > 1_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]), expected = Base64.getDecoder().decode(parts[3]);
            if (salt.length != 16 || expected.length != 32) return false;
            return MessageDigest.isEqual(expected, derive(password, salt, count));
        } catch (RuntimeException e) { return false; }
    }
    private static byte[] derive(String password, byte[] salt, int count) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, count, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException e) { throw new IllegalStateException("Password hashing unavailable", e); }
        finally { spec.clearPassword(); }
    }
}
