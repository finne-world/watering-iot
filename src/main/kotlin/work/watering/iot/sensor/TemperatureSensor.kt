package work.watering.iot.sensor

import com.pi4j.io.gpio.*
import software.amazon.awssdk.crt.mqtt.MqttClient
import work.watering.iot.Publisher
import work.watering.iot.message.TemperatureHistoryMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class TemperatureSensor(
    private val temperature: Float,//後で消す
    override val publisher: Publisher
): Sensor {
    override val gpio: GpioController = GpioFactory.getInstance()

    //TODO 気温センサーを後で指定
    override var pin: GpioPinDigitalOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW)

    override fun getData(){
        //GPIOからtemperatureデータを取得する
    }

    override fun publishData(){
        this.publisher.publish(
            TemperatureHistoryMessage(
                serial = UUID.fromString("7dc962ba-a611-4cc1-8b18-4e19fef4ab02"),
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                value = temperature
            )
        )
    }
}
