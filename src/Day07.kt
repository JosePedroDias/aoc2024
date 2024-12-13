import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.measureTime

private fun signals(n: Int) = sequence {
    val topNum = 1 shl n
    for (i in 0..< topNum) {
        val arr = List(n) { idx -> (i shr idx) and 1 == 1 }
        yield(arr)
    }
}

private fun signalsTernary(n: Int) = sequence {
    val topNum = 3.0.pow(n).toInt()
    for (i in 0 until topNum) {
        val arr = List(n) { idx -> (i / 3.0.pow(idx).toInt()) % 3 }
        yield(arr)
    }
}

private fun concatOp(a: Long, b: Long): Long {
    val digitsB = if (b == 0L) 0 else log10(b.toDouble()).toInt() + 1
    return (10.0.pow(digitsB) * a + b).toLong()
}

private fun part1(input: List<String>): Long {
    var total = 0L
    for (line in input) {
        val (a, b) = line.split(": ")
        val target = a.toLong()
        val nums = b.split(" ").map { it.toLong() }
        //println("t: $target | nums: $nums")
        val outcome = signals(nums.size - 1).any {
            //val sb = StringBuilder()
            var num = nums[0]
            //sb.append(num)
            for (i in 1..< nums.size) {
                val isMul = it[i - 1]
                val andNum = nums[i]
                //sb.append( if (isMul) " * " else " + " )
                //sb.append(andNum)
                if (isMul) {
                    num *= andNum
                } else {
                    num += andNum
                }
            }
            val worked = num == target
            //val emoji = if (worked) "✅" else "❌"
            //println("  $target $emoji $sb ($num)")
            worked
        }
        if (outcome) {
            total += target
        }
    }
    return total
}

private fun part2(input: List<String>): Long {
    var total = 0L
    for (line in input) {
        val (a, b) = line.split(": ")
        val target = a.toLong()
        val nums = b.split(" ").map { it.toLong() }
        //println("t: $target | nums: $nums")
        val outcome = signalsTernary(nums.size - 1).any {
            //val sb = StringBuilder()
            var num = nums[0]
            //sb.append(num)
            for (i in 1..< nums.size) {
                val opIdx = it[i - 1]
                val andNum = nums[i]
                when (opIdx) {
                    0 -> { num += andNum; /*sb.append(" + ")*/ }
                    1 -> { num *= andNum; /*sb.append(" * ")*/ }
                    else -> { num = concatOp(num, andNum); /*sb.append(" || ")*/ }
                }
                //sb.append(andNum)
            }
            val worked = num == target
            //val emoji = if (worked) "✅" else "❌"
            //println("  $target $emoji $sb ($num)")
            worked
        }
        if (outcome) {
            total += target
        }
    }
    return total
}

fun main() {
    check(concatOp(2L, 5L) == 25L)
    check(concatOp(23L, 5L) == 235L)
    check(concatOp(2L, 35L) == 235L)

    //println(signals(3).toList())
    //println(signalsTernary(3).toList())

    val dt = measureTime {
        check(3749L == part1(readInput("07_test")))
        println("part 1 answer: ${part1(readInput("07"))}")

        check(11387L == part2(readInput("07_test")))
        println("part 2 answer: ${part2(readInput("07"))}")
    }
    println(dt) // 4.6s to 1.1s after parallelism kicks in
}
