package work.watering.iot

import software.amazon.awssdk.crt.mqtt.MqttMessage
import work.watering.iot.sensor.HumiditySensor
import work.watering.iot.sensor.TemperatureSensor
import work.watering.iot.sensor.WateringSensor
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch


fun main() {
    val pwd = System.getProperty("user.dir")
    println(pwd)

    Main().also {
        it.start()
    }
}

const val hTopic: String = "/develop/histories/humidity"
const val tTopic: String = "/develop/histories/temperature"
const val wTopic: String = "/develop/histories/watering"
const val subTopic: String = "topic"
const val testTopic: String = "topic"
const val typenum: Int = 3

class Main {
    private val subscriber: Subscriber = Subscriber(subTopic)
    private val humiditySensor: HumiditySensor = HumiditySensor(0.35f, Publisher(hTopic))
    private val temperatureSensor: TemperatureSensor = TemperatureSensor(35.0f, Publisher(tTopic))
    private val wateringSensor: WateringSensor = WateringSensor(350, Publisher(wTopic))

    fun start() {
        print("Start")
        val countDownLatch = CountDownLatch(1)
        //subscribe

        this.subscriber.subscribe { message: MqttMessage ->
            val payload: String = String(message.payload, StandardCharsets.UTF_8)
            println("Subscribed-Message: $payload")
            EventDispatcher(
                this.wateringSensor,
                this.humiditySensor,
                this.temperatureSensor,
            ).interpretMessage(payload)

            countDownLatch.countDown()
        }
        countDownLatch.await()
    }
}
