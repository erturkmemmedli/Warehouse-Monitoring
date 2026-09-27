# Warehouse Monitoring

The warehouse service receives temperature and humidity readings over UDP and publishes them to an MQTT broker (Mosquitto).
The central service reads them from the broker and logs an alarm when a value is above the threshold.

Defaults given in the requirements:
- Temperature: UDP port 3344, threshold 35
- Humidity: UDP port 3355, threshold 50

Used teck stack:
- Java 21
- Maven
- Reactor Netty
- HiveMQ MQTT client

## How to Run

Followings are needed to exist in local:
- Java 21
- Maven
- Docker

1. Start the broker and build the jar

```bash
docker compose up -d
mvn package
```

2. Start the central service in one terminal

```bash
java -cp target/warehouse-monitoring.jar com.monitoring.central.CentralApp
```

3. Start the warehouse service in another terminal

```bash
java -cp target/warehouse-monitoring.jar com.monitoring.warehouse.WarehouseApp
```

4. Send some readings in another terminal

```bash
echo "sensor_id=t1; value=30" | nc -u -w1 127.0.0.1 3344   # OK
echo "sensor_id=t1; value=40" | nc -u -w1 127.0.0.1 3344   # ALARM
echo "sensor_id=h1; value=45" | nc -u -w1 127.0.0.1 3355   # OK
echo "sensor_id=h1; value=65" | nc -u -w1 127.0.0.1 3355   # ALARM
echo "hello" | nc -u -w1 127.0.0.1 3344                    # Warning -> Skipped
```

## How to Stop

1. Stop the apps with Ctrl+C 
2. Stop the broker with `docker compose down`

## Settings

Everything has a default value, so nothing needs to be set.

To change something, set an environment variable:
- `WAREHOUSE_ID`, `TEMPERATURE_PORT`, `HUMIDITY_PORT` for the warehouse service
- `TEMPERATURE_THRESHOLD`, `HUMIDITY_THRESHOLD` for the central service
- `MQTT_HOST`, `MQTT_PORT` for both

For example, in another therminal on the same machine, a second warehouse can be created with following command:

```bash
WAREHOUSE_ID=warehouse-2 TEMPERATURE_PORT=4344 HUMIDITY_PORT=4355 java -cp target/warehouse-monitoring.jar com.monitoring.warehouse.WarehouseApp
```

## How to Test

```bash
mvn test
```
