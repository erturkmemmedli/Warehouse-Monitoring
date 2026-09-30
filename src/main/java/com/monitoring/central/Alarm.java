package com.monitoring.central;

import com.monitoring.common.Measurement;

public record Alarm(Measurement measurement, double threshold) {}