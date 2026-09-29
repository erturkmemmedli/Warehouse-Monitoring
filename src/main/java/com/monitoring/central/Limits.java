package com.monitoring.central;

public record Limits(double min, double max) {

    public boolean isOutside(double value) {
        return value < min || value > max;
    }
}