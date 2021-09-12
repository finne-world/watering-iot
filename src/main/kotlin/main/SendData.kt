package main

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.fasterxml.jackson.databind.ObjectMapper;

class DataToSend {
    var DeviceID = "deviceID"
    var upTimeForDevice = "dt"
    var waterAmount = "water"
}

fun makeData(){
    var data: DataToSend = DataToSend()
    val data.De = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy年 MM月 dd日 HH時 mm分 ss秒"))

    val mapper: ObjectMapper = ObjectMapper()
    val json: String = mapper.writeValueAsString()
}