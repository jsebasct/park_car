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

class ParkCommand() {

    public fun executeParkCommand(
        parkingLot: ParkingLot?,
        tokens: List<String>,
        msgNotCreated: String
    ) = when (getCommandType(tokens)) {
        "park" -> {
            val commandResult = parkingLot?.park(tokens[1], tokens[2]) ?: msgNotCreated
            println(commandResult)
            parkingLot
        }
        "leave" -> {
            val commandResult = parkingLot?.leave(tokens[1].toInt()) ?: msgNotCreated
            println(commandResult)
            parkingLot
        }
        "create" -> {
            val res = ParkingLot(tokens[1].toInt())
            val commandResult = "Created a parking lot with ${tokens[1]} spots."
            println(commandResult)
            res
        }

        "status" -> {
            val commandResult = parkingLot?.getStatus() ?: msgNotCreated
            println(commandResult)
            parkingLot
        }
        else ->  {
            throw Exception("Unknown Command")
        }
    }

}

fun getCommandType(tokens: List<String>) = tokens[0]

class ParkingManager {

    private var parkingLot: ParkingLot? = null
    private var parkCommand = ParkCommand()

    fun startParking() {
        val msgNotCreated = "Sorry, a parking lot has not been created."
        val scanner = Scanner(System.`in`)

        var input = scanner.nextLine()!!
        var tokens = input.split(" ")

        var keepOn = getCommandType(tokens) != "exit"
        while (keepOn) {
            parkingLot = parkCommand.executeParkCommand(parkingLot, tokens, msgNotCreated)

            input = scanner.nextLine()!!
            tokens = input.split(" ")
            keepOn = getCommandType(tokens) != "exit"
        }
    }

}

fun main() {
    val p = ParkingManager()
    p.startParking()
}
