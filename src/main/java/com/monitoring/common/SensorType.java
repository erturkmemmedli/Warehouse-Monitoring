package com.monitoring.common;

public enum SensorType {
    TEMPERATURE(3344, 35),
    HUMIDITY(3355, 50),
    CO2(3366, 1000);

    private final int defaultPort;
    private final double defaultThreshold;

    SensorType(int defaultPort, double defaultThreshold) {
        this.defaultPort = defaultPort;
        this.defaultThreshold = defaultThreshold;
    }

    public int port() {
        return Env.getInt(name() + "_PORT", defaultPort);
    }

    public double threshold() {
        return Env.getDouble(name() + "_THRESHOLD", defaultThreshold);
    }
}