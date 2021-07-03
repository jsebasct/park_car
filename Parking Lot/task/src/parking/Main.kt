package parking

import java.util.*

data class Car (val registrationNumber: String, var color: String) {
    override fun toString(): String {
        return "$registrationNumber $color"
    }
}

data class ParkLot (var car: Car?, var taken: Boolean = false) {
    override fun toString(): String {
        return if (taken) car.toString() else ""
    }
}

class ParkingLot(size: Int) {

    private var parkingLots: List<ParkLot> = List(size) { ParkLot(null) }

    /**
     * @return the HUMAN index at which was inserted the new car
     */
    fun park(registrationNumber: String, color: String): String {
        if (registrationNumber.contains("\\s+")) {
            throw IllegalArgumentException("register number must not contain spaces")
        }

        val indexFreeParkLot = parkingLots.indexOfFirst { !it.taken }
        if (indexFreeParkLot == -1) {
            return "Sorry, the parking lot is full."

        } else {
            parkingLots[indexFreeParkLot].car = Car(registrationNumber, color)
            parkingLots[indexFreeParkLot].taken = true
        }

        val spot = indexFreeParkLot+1
        return "$color car parked in spot ${spot}."
    }

    fun leave(humanIndex: Int): String {
        val parkIndex = humanIndex - 1

        return if (parkingLots[parkIndex].taken) {
            parkingLots[parkIndex].car = null
            parkingLots[parkIndex].taken = false
            "Spot $humanIndex is free."
        } else {
            "There is no car in spot $humanIndex."
        }
    }

    fun getStatus(): String {
        return if (parkingLots.all { !it.taken }) "Parking lot is empty."
        else {
            
            var res = ""
            for ((index, parkLot) in parkingLots.withIndex()) {
                if (parkLot.taken) {
                    res += "${index+1} $parkLot\n"
                }
            }
            res.dropLast(1)
        }
    }
}

fun main() {
    val msgNotCreated = "Sorry, a parking lot has not been created."
    var parkingLot: ParkingLot? = null//ParkingLot(20)
    val scanner = Scanner(System.`in`)

    var input = scanner.nextLine()!!
    var tokens = input.split(" ")

    var keepOn = tokens[0] != "exit"
    while (keepOn) {
        val commandResult = when (tokens[0]) {
            "park" -> {
                parkingLot?.park(tokens[1], tokens[2]) ?: msgNotCreated
            }
            "leave" -> {
                parkingLot?.leave(tokens[1].toInt()) ?: msgNotCreated
            }
            "create" -> {
                parkingLot = ParkingLot(tokens[1].toInt())
                "Created a parking lot with ${tokens[1]} spots."
            }

            "status" -> {
                parkingLot?.getStatus() ?: msgNotCreated
            }
            else -> throw Exception("Unknown Command")
        }

        println(commandResult)

        input = scanner.nextLine()!!
        tokens = input.split(" ")
        keepOn = tokens[0] != "exit"
    }

}
