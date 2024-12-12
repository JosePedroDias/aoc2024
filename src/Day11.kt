import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.measureTime

/*
0 -> 1
even nr of digits -> left half (more significant) | right half (less significant)
else -> 2024 x

nr of stones after 25 iterations?

#0     0     1  10    99  999                                                                          [ 5]
#1     1  2024   1     0    9      9  2021976                                                          [ 7]
#2  2024    20  24  2024    1  18216    18216  4092479424                                              [ 8]
#3    20    24   2     0    2      4       20          24  2024  36869184  36869184  40924  79424      [13]
*/

class Histogram {
    private val m : MutableMap<Long, Long> = mutableMapOf()

    fun change(n: Long, delta: Long) {
        val vOld = m.getOrDefault(n, 0)
        val vNew = vOld + delta
        if (vNew < 0) {
            throw Error("negative histogram value!")
        }
        else if (vNew == 0L) { m.remove(n) }
        else { m[n] = vNew }
    }

    fun inc(n: Long) {
        change(n, 1)
    }

    fun entries(): List<Pair<Long, Long>> {
        val res = mutableListOf<Pair<Long, Long>>()
        for ((k, v) in m.entries) {
            res.add(Pair(k, v))
        }
        return res
    }

    fun sumAllValues(): Long {
        return m.values.map { it.toLong() }.sum()
    }

    override fun toString(): String {
        return m.toString()
    }
}

private typealias RuleSet = MutableMap<Long, Pair<Long, Long?>>

private fun parse(line: String): Histogram {
    val hg = Histogram()
    line.split(" ").forEach { hg.inc(it.toLong()) }
    return hg
}

private fun nrDigits(n: Long): Int {
    return if (n == 0L) 0 else log10(n.toDouble()).toInt() + 1
}

private fun splitEvenlySizedNumber(n: Long): Pair<Long, Long> {
    val nrDi = nrDigits(n)
    val order = 10.0.pow(nrDi/2).toInt()
    val a = n / order
    val b = n - a * order
    return Pair(a, b)
}

private fun blinkNTimes(line: String, times: Int): Long {
    val rules: RuleSet = mutableMapOf()
    val histogram = parse(line)

    var t = 0
    repeat(times) {
        ++t
        //println("#$t")
        //println("histogram: $histogram")

        histogram.entries().toList().forEach { (n, amount) ->
            var res: Pair<Long, Long?>? = rules[n]
            if (res == null) {
                if (n == 0L) {
                    res = Pair(1, null)
                } else if (nrDigits(n) % 2 == 0) {
                    res = splitEvenlySizedNumber(n)
                } else {
                    res = Pair(n * 2024, null)
                }
                rules[n] = res
                //println("new rule: $n -> $res")
            } else {
                //println("old rule: $n -> $res")
            }

            val (a, b) = res
            histogram.change(n, -amount)
            histogram.change(a, amount)
            if (b != null) { histogram.change(b, amount) }
            //println("histogram: $histogram")
        }
    }

    return histogram.sumAllValues()
}

fun main() {
    val dt = measureTime {
        check(nrDigits(2314L) == 4)
        check(splitEvenlySizedNumber(2314L) == Pair(23L, 14L))

        check(22L == blinkNTimes("125 17", 6))

        check(55312L == blinkNTimes(readInputAsString("11_test"), 25))

        println("part 1 answer: ${blinkNTimes(readInputAsString("11"), 25)}")
        println("part 2 answer: ${blinkNTimes(readInputAsString("11"), 75)}")
    }
    println(dt)
}