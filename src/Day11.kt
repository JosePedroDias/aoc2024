import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.measureTime

/*
0 -> 1
even nr of digits -> left half (more significant) | right half (less significant)
else -> 2024 x

nr of stones after 25 iterations?

0 1 10 99 999
1 2024 1 0 9 9 2021976
 */

private fun parse(line: String): List<Int> {
    return line.split(" ").map { it.toInt() }
}

private fun nrDigits(n: Int): Int {
    return n.toString().length
}

private fun nrDigits2(n: Int): Int {
    return if (n == 0) 0 else log10(n.toDouble()).toInt() + 1
}

private fun splitEvenlySizedNumber(n: Int): List<Int> {
    val s = n.toString()
    val i = s.length / 2
    val a = s.substring(0, i)
    val b = s.substring(i)
    return listOf(a.toInt(), b.toInt())
}

private fun splitEvenlySizedNumber2(n: Int): List<Int> {
    val nrDi = nrDigits(n)
    val order = 10.0.pow(nrDi/2).toInt()
    val a = n / order
    val b = n - a * order
    return listOf(a, b)
}

private fun step(stones: List<Int>): List<Int> {
    return stones.flatMap {
        when {
            it == 0 -> listOf(1)
            nrDigits2(it) % 2 == 0 -> splitEvenlySizedNumber2(it)
            else -> listOf(2024 * it)
        }
    }
}

private fun blinkNTimes(line: String, times: Int): Int {
    var stones = parse(line)
    var i = 0
    repeat(times) {
        ++i
        println("time: $i")
        System.gc()
        stones = step(stones)
    }
    return stones.size
}

fun main() {
    val dt = measureTime {
        check(nrDigits(2314) == 4)
        check(nrDigits2(2314) == 4)

        check(splitEvenlySizedNumber(2314)[0] == 23)
        check(splitEvenlySizedNumber(2314)[1] == 14)
        check(splitEvenlySizedNumber2(2314)[0] == 23)
        check(splitEvenlySizedNumber2(2314)[1] == 14)

        check(55312 == blinkNTimes(readInputAsString("11_test"), 25))

        println("part 1 answer: ${blinkNTimes(readInputAsString("11"), 25)}")
        println("part 2 answer: ${blinkNTimes(readInputAsString("11"), 75)}")
    }
    println(dt)
}