package work.watering.iot.sensor

import com.pi4j.io.gpio.*
import work.watering.iot.Publisher
import work.watering.iot.message.WateringHistoryMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class WateringSensor (
    var amount: Int,//後で消す
    publisher: Publisher
): Sensor(publisher = publisher) {
    var gpio: GpioController = GpioFactory.getInstance();
    var pin: GpioPinDigitalOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "pomp", PinState.LOW)

    override fun getData(){
        //GPIOからamountデータを取得する
    }

    fun watering(amount: Int = 100) {
        //水やりする
        pin.setShutdownOptions(true, PinState.LOW)
        pin.high()
        Thread.sleep(3000)
        pin.low()
    }

    override fun publishData(){
        this.publisher.publish(
            WateringHistoryMessage(
                serial = UUID.fromString("d2a7a6ab-f9ef-46f0-a956-cb16597d1f42"),
                amount = this.amount,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            )
        )
    }
    fun run() {
        watering(amount)
        publishData()
    }
}
