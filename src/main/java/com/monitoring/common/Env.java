package com.monitoring.common;

public final class Env {

    private Env() {}

    public static String get(String name, String defaultVal) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultVal : value;
    }

    public static int getInt(String name, int defaultVal) {
        return Integer.parseInt(get(name, String.valueOf(defaultVal)));
    }

    public static double getDouble(String name, double defaultVal) {
        return Double.parseDouble(get(name, String.valueOf(defaultVal)));
    }
}