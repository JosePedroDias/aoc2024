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

//private typealias Num = Int
private const val N = 1000000

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

private fun splitEvenlySizedNumber2(n: Int): Pair<Int, Int> {
    val nrDi = nrDigits(n)
    val order = 10.0.pow(nrDi/2).toInt()
    val a = n / order
    val b = n - a * order
    return Pair(a, b)
}

private fun stonesToString(stones: IntArray, num: Int): String {
    val sb = StringBuilder()
    for (i in 0 until num) {
        sb.append(stones[i])
        sb.append(' ')
    }
    sb.append('(')
    sb.append(num)
    sb.append(')')
    return sb.toString()
}

private fun step(stones: IntArray, num_: Int, times: Int): Int {
    var num = num_
    var t = -1
    repeat(times) {
        ++t
        println("** #$t ($num) **")
        //println(stonesToString(stones, num))
        var i = 0
        while (i < num) {
            var v = stones[i]
            if (v == 0) {
                //println("  i:$i  A) 0 -> 1")
                stones[i] = 1
            } else if (nrDigits2(v) % 2 == 0) {
                val (v1, v2) = splitEvenlySizedNumber2(v)
                //println("  i:$i  B) $v1|$v2")

                // shift the cells to the right of these
                ++num
                var j = num - 1
                while (j > i + 1) {
                    stones[j] = stones[j - 1]
                    --j
                }
                //println("  MOVED ${stonesToString(stones, num)}")

                stones[i] = v1
                stones[++i] = v2
            } else {
                v *= 2024
                //println("  i:$i  C) *2024 = $v")
                stones[i] = v
            }
            //println("  ${stonesToString(stones, num)}")
            ++i
        }
    }
    println("result: $num")
    return num
}

private fun blinkNTimes(line: String, times: Int): Int {
    val stones = IntArray(N)
    val stonesList = parse(line)
    stonesList.forEachIndexed { idx, v -> stones[idx] = v }
    return step(stones, stonesList.size, times)
}

fun main() {
    val dt = measureTime {

        /*
        check(nrDigits(2314) == 4)
        check(nrDigits2(2314) == 4)
        check(splitEvenlySizedNumber(2314)[0] == 23)
        check(splitEvenlySizedNumber(2314)[1] == 14)
        check(splitEvenlySizedNumber2(2314)[0] == 23)
        check(splitEvenlySizedNumber2(2314)[1] == 14)
        */

        //check(22 == blinkNTimes("125 17", 6))

        //check(55312 == blinkNTimes(readInputAsString("11_test"), 25))

        println("part 1 answer: ${blinkNTimes(readInputAsString("11"), 25)}")
        println("part 2 answer: ${blinkNTimes(readInputAsString("11"), 75)}")
    }
    println(dt)
}