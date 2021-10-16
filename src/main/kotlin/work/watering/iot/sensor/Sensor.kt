package work.watering.iot.sensor

import work.watering.iot.Publisher

abstract class Sensor(
    protected val publisher: Publisher
){
    open fun publishData() {}
    open fun getData() {}
}
