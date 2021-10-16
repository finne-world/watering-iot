package work.watering.iot.sensor

import com.pi4j.io.gpio.*
import work.watering.iot.Publisher
import work.watering.iot.message.HumidityHistoryMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class HumiditySensor(
    private var humidity: Float,//後で消す
    override val publisher: Publisher
): Sensor {
    override val gpio: GpioController = GpioFactory.getInstance()

    //TODO 土壌湿度センサーを後で指定
    override var pin: GpioPinDigitalOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW)
    override fun getData(){
        //GPIOからhumidityデータを取得する
    }
    override fun publishData(){
        this.publisher.publish(
            HumidityHistoryMessage(
                serial = UUID.fromString("7dc962ba-a611-4cc1-8b18-4e19fef4ab02"),
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                value = humidity
            )
        )
    }
}
