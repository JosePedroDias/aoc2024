import kotlin.time.measureTime

private fun signals(n: Int) = sequence {
    val topNum = 1 shl n
    for (i in 0..< topNum) {
        val arr = List(n) { idx -> (i shr idx) and 1 == 1 }
        yield(arr)
    }
}

fun main() {
    fun part1(input: List<String>): Long {
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
                    val isMult = it[i - 1]
                    val andNum = nums[i]
                    //sb.append( if (isMult) " * " else " + " )
                    //sb.append(andNum)
                    if (isMult) {
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

    val dt = measureTime {
        check(3749L == part1(readInput("07_test")))
        println("part 1 answer: ${part1(readInput("07"))}")

        //check(6L == part2(readInput("07_test")))
        //println("part 2 answer: ${part2(readInput("07"))}")
    }
    println(dt) // 4.6s to 1.1s after parallelism kicks in
}