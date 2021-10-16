package work.watering.iot

import work.watering.iot.schedule.ScheduledWateringTask
import work.watering.iot.sensor.HumiditySensor
import work.watering.iot.sensor.TemperatureSensor
import work.watering.iot.sensor.WateringSensor
import java.text.SimpleDateFormat
import java.util.*

class EventDispatcher(
    private val wateringSensor: WateringSensor,
    private val humiditySensor: HumiditySensor,
    private val temperatureSensor: TemperatureSensor,
) {
    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy/mm/dd hh:mm:ss")
    private lateinit var swtask: ScheduledWateringTask

    fun interpretMessage(payload: String){
        val ary: List<String> = payload.split(", ",": ")
        println(ary[typenum])
        when(ary[typenum]) {
            "\"watering\"" -> {
                this.wateringSensor.run()
                println("watering success")
            }
            "\"humidity\"" -> {
                this.humiditySensor.publishData()
                println("humidity success")
            }
            "\"temperature\"" -> {
                this.temperatureSensor.publishData()
                println("temperature success")
            }
            "\"execution\"" -> {
                Timer().cancel()
                this.swtask = ScheduledWateringTask(this.wateringSensor,Integer.toUnsignedLong(Integer.parseInt(ary[5])), status = true)
                Timer().schedule(swtask, sdf.parse("2021/09/14 23:45:00"))
                println("success")
            }
            "\"stop-execution\"" -> {
                Timer().cancel()
                println("stoped")
            }
            else -> {
                print("other process")
            }
        }
    }
}
