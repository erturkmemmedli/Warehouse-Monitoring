package com.monitoring.central;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAlarmNotifier implements AlarmNotifier {

    private static final Logger log = LoggerFactory.getLogger(LogAlarmNotifier.class);

    @Override
    public void notify(Alarm alarm) {
        log.warn(
            "ALARM! {} {} in {} is {} (threshold {})",
            alarm.measurement().type(),
            alarm.measurement().sensorId(),
            alarm.measurement().warehouseId(),
            alarm.measurement().value(),
            alarm.threshold()
        );
    }
}