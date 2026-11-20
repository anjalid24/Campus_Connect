package com.ljuniversity.companion.util;

/** Explicit opt-in only. The default desktop demo never opens JDBC connections. */
public final class AppConfig {
    private AppConfig() {}
    public static String databaseUrl() { return System.getenv().getOrDefault("CAMPUS_DB_URL", "jdbc:mysql://localhost:3306/campus_connect?connectTimeout=5000&socketTimeout=10000"); }
    public static String databaseUser() { return System.getenv().getOrDefault("CAMPUS_DB_USER", ""); }
    public static String databasePassword() { return System.getenv().getOrDefault("CAMPUS_DB_PASSWORD", ""); }
    public static boolean databaseEnabled() { return "true".equalsIgnoreCase(System.getenv("CAMPUS_ENABLE_DATABASE")); }
    public static void requireDatabase() {
        if (!databaseEnabled() || databaseUser().isBlank()) throw new IllegalStateException("Database mode is disabled. Set CAMPUS_ENABLE_DATABASE=true and private CAMPUS_DB_* settings first.");
    }
}
