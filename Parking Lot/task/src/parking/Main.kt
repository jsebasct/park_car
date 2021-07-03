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

abstract class Command {

    open abstract fun executeParkCommand(
        parkingLot: ParkingLot?,
        tokens: List<String>
    ) : ParkingLot?

    fun getMessageNotCreated() = "Sorry, a parking lot has not been created."
}

class ParkCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        val commandResult = parkingLot?.park(tokens[1], tokens[2]) ?: getMessageNotCreated()
        println(commandResult)
        return parkingLot
    }
}

class LeaveCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        val commandResult = parkingLot?.leave(tokens[1].toInt()) ?: getMessageNotCreated()
        println(commandResult)
        return parkingLot
    }
}

class CreateCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        val res = ParkingLot(tokens[1].toInt())
        val commandResult = "Created a parking lot with ${tokens[1]} spots."
        println(commandResult)
        return res
    }
}

class StatusCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        val commandResult = parkingLot?.getStatus() ?: getMessageNotCreated()
        println(commandResult)
        return parkingLot
    }
}

fun getCommandType(tokens: List<String>) = tokens[0]

class ParkingManager {

    private var parkingLot: ParkingLot? = null

    fun startParking() {
        val scanner = Scanner(System.`in`)

        var input = scanner.nextLine()!!
        var tokens = input.split(" ")

        var keepOn = getCommandType(tokens) != "exit"
        while (keepOn) {
            val xCommand = getCommand(getCommandType(tokens))
            parkingLot = xCommand.executeParkCommand(parkingLot, tokens)

            input = scanner.nextLine()!!
            tokens = input.split(" ")
            keepOn = getCommandType(tokens) != "exit"
        }
    }

    private fun getCommand(type: String) =
        when (type) {
            "park" -> ParkCommand()
            "leave" -> LeaveCommand()
            "create" -> CreateCommand()
            "status" -> StatusCommand()
            else -> throw Exception("Unknown Command")
        }
}

fun main() {
    val p = ParkingManager()
    p.startParking()
}
