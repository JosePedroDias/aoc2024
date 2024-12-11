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

private fun parse(line: String): List<Long> {
    return line.split(" ").map { it.toLong() }
}

private fun nrDigits(n: Long): Int {
    return n.toString().length
}

private fun nrDigits2(n: Long): Int {
    return if (n == 0L) 0 else log10(n.toDouble()).toInt() + 1
}

private fun splitEvenlySizedNumber(n: Long): List<Long> {
    val s = n.toString()
    val i = s.length / 2
    val a = s.substring(0, i)
    val b = s.substring(i)
    return listOf(a.toLong(), b.toLong())
}

private fun splitEvenlySizedNumber2(n: Long): List<Long> {
    val nrDi = nrDigits(n)
    val order = 10.0.pow(nrDi/2).toLong()
    val a = n / order
    val b = n - a * order
    return listOf(a, b)
}

private fun step(stones: List<Long>): List<Long> {
    return stones.flatMap {
        when {
            it == 0L -> listOf(1L)
            nrDigits2(it) % 2 == 0 -> splitEvenlySizedNumber2(it)
            else -> listOf(2024L * it)
        }
    }
}

private fun blinkNTimes(line: String, times: Int): Int {
    var stones = parse(line)
    repeat(times) { stones = step(stones) }
    return stones.size
}

fun main() {
    val dt = measureTime {
        check(nrDigits(2314L) == 4)
        check(nrDigits2(2314L) == 4)

        check(splitEvenlySizedNumber(2314L)[0] == 23L)
        check(splitEvenlySizedNumber(2314L)[1] == 14L)
        check(splitEvenlySizedNumber2(2314L)[0] == 23L)
        check(splitEvenlySizedNumber2(2314L)[1] == 14L)

        check(55312 == blinkNTimes(readInputAsString("11_test"), 25))

        println("part 1 answer: ${blinkNTimes(readInputAsString("11"), 25)}")
        println("part 2 answer: ${blinkNTimes(readInputAsString("11"), 75)}")
    }
    println(dt)
}