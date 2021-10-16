package work.watering.iot.schedule

import work.watering.iot.sensor.WateringSensor
import java.util.TimerTask
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScheduledWateringTask(
    private val wateringSensor: WateringSensor,
    private val delayAtHours: Long = 1,
    private val status: Boolean
): TimerTask() {
    private fun periodicWatering(interval: Long){
        val service: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        service.scheduleAtFixedRate({ wateringSensor.run() }, 0,interval, TimeUnit.SECONDS)
    }
    override fun run(){
        if(status) {
            this.periodicWatering(delayAtHours)
        } else {
            this.wateringSensor.run()
        }
    }
}
