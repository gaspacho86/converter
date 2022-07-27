@file:OptIn(ExperimentalStdlibApi::class)

package converter

import java.math.*
import kotlin.math.abs
import kotlin.math.pow

const val SCALE = 5
var srcBase: Int = 0
var targetBase: Int = 0

fun main() = setBase("")

fun setBase (separator: String) {
    print("${separator}Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
    val userInput = readLine()!!
    if (userInput == "/exit") return
    srcBase = userInput.split(" ").toList()[0].toInt()
    targetBase = userInput.split(" ").toList()[1].toInt()
    setNumber()
}

fun setNumber() {
    print("Enter number in base $srcBase to convert to base $targetBase (To go back type /back) ")
    val userInput = readLine()!!
    when (userInput) {
        "/back" -> setBase("\n")
        else -> {
            printConversionResult(userInput.toUpperCase())
            setNumber()
        }
    }
}

fun printConversionResult(userInput: String) {
    print("Conversion result: ")
    if (srcBase == targetBase) {
        print("$userInput\n\n")
    } else if (userInput.contains(".")) {
        val (integerPart, fractionalPart) = userInput.split(".").map { it.toUpperCase() }
        print(intPartToTarget(srcToDecimal(integerPart, false)))
        print(fractionPartToTarget(srcToDecimal(fractionalPart, true)) + "\n\n")
    } else {
        println(intPartToTarget(srcToDecimal(userInput, false)))
        println()
    }
}

fun srcToDecimal(num: String, isFractionalPart: Boolean): BigDecimal {
    var result = BigDecimal.ZERO
    for (i in num.lastIndex downTo 0) {
        val digit = if (num[i] >= 'A') num[i] - '7' else num[i].digitToInt()
        val power = if (isFractionalPart) -(i + 1) else abs(i - num.lastIndex)
//        result += (digit.toBigDecimal() * srcBase.toDouble().pow(power).toBigDecimal()).setScale(0)
        result += (digit.toBigDecimal() * srcBase.toBigDecimal().pow(power, MathContext.DECIMAL64))
    }
    return result
}

fun intPartToTarget(part: BigDecimal): String {
    return part.toBigInteger().toString(targetBase.toInt())
    //var num = part
    //var result = ""

    //while (num > BigDecimal.ZERO) {
    //    val (quotient, remainder) = num.divideAndRemainder(targetBase.toBigDecimal())
    //    num = quotient
    //    result += if (remainder >= BigDecimal.TEN) '7' + remainder.toInt() else remainder
    //}
    //return if (result.isEmpty()) "0" else result.reversed()
}

fun fractionPartToTarget(part: BigDecimal): String {
    var num = part
    val result = MutableList(SCALE) { "0" }

    for (i in result.indices) {
        num *= targetBase.toBigDecimal()
        val integerPart = num.setScale(0, RoundingMode.DOWN)
        result[i] = if (integerPart >= BigDecimal.TEN) ('7' + integerPart.toInt()).toString() else integerPart.toString()
        num -= integerPart
    }
    return ".${result.joinToString("")}"
}