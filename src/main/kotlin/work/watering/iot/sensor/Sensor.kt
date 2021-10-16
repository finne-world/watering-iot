package work.watering.iot.sensor

import com.pi4j.io.gpio.GpioController
import com.pi4j.io.gpio.GpioPinDigitalOutput
import work.watering.iot.Publisher

interface Sensor {
    val publisher: Publisher
    var pin: GpioPinDigitalOutput//後で確定
    val gpio: GpioController

    fun publishData() {}
    fun getData() {}
}
