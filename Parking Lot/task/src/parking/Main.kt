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

    fun registeredByColor(color: String): List<String> {
        return parkingLots
            .filter { it.taken && it.car!!.color.equals(color, ignoreCase = true) }
            .map { it.car!!.registrationNumber }
    }

    fun spotByColor(color: String): MutableList<Int> {
        val res = mutableListOf<Int>()
        parkingLots.forEachIndexed { index, parkLot ->
            if (parkLot.taken
                && parkLot.car!!.color.equals(color, ignoreCase = true)) {
                res.add(index+1)
            }
        }
        return res
    }

    fun spotByRegistrationNumber(regNumber: String): Int {
        var res = -1
        parkingLots.forEachIndexed { index, parkLot ->
            if (parkLot.taken
                && parkLot.car!!.registrationNumber.equals(regNumber, ignoreCase = true)) {
                res = index+1
            }
        }
        return res
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

class RegisterByNumberCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        if (parkingLot == null) {
            println(getMessageNotCreated())
        } else {
            val registrationNumbers = parkingLot.registeredByColor(tokens[1])
            if (registrationNumbers.isEmpty()) {
                println("No cars with color ${tokens[1]} were found.")
            } else {
                println(registrationNumbers.joinToString())
            }
        }

        return parkingLot
    }
}

class SpotByColorCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        if (parkingLot == null) {
            println(getMessageNotCreated())
        } else {
            val spotByColors = parkingLot.spotByColor(tokens[1])
            if (spotByColors.isEmpty()) {
                println("No cars with color ${tokens[1]} were found.")
            } else {
                println(spotByColors.joinToString())
            }
        }

        return parkingLot
    }
}

class SpotByRegistrationCommand: Command() {
    override fun executeParkCommand(parkingLot: ParkingLot?, tokens: List<String>): ParkingLot? {
        if (parkingLot == null) {
            println(getMessageNotCreated())
        } else {
            val spotByRegNumber = parkingLot.spotByRegistrationNumber(tokens[1])
            if (spotByRegNumber == -1) {
                println("No cars with registration number ${tokens[1]} were found.")
            } else {
                println(spotByRegNumber)
            }
        }

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
            "reg_by_color" -> RegisterByNumberCommand()
            "spot_by_color" -> SpotByColorCommand()
            "spot_by_reg" -> SpotByRegistrationCommand()
            else -> throw Exception("Unknown Command")
        }
}

fun main() {
    val p = ParkingManager()
    p.startParking()
}
