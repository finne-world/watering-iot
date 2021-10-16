package work.watering.iot.sensor

import com.pi4j.io.gpio.*
import work.watering.iot.Publisher
import work.watering.iot.message.WateringHistoryMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


class WateringSensor (
    private var amount: Int,//後で消す
    override val publisher: Publisher
): Sensor {
    override val gpio: GpioController = GpioFactory.getInstance()
    override var pin: GpioPinDigitalOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "pomp", PinState.LOW)

    override fun getData(){
        //GPIOから水量を計算する
    }

    fun watering() {
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
        watering()
        publishData()
    }
}
