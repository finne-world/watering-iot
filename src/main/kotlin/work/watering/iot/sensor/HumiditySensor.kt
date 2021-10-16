package work.watering.iot.sensor

import work.watering.iot.Publisher
import work.watering.iot.message.HumidityHistoryMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class HumiditySensor(
    private var humidity: Float,//後で消す
    publisher: Publisher
): Sensor(publisher = publisher) {
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
