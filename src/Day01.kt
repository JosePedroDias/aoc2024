fun main() {
    // PART 1

    fun numbersFromLine(line: String): Pair<Int, Int> {
        val parts = line.split("   ")
        val leftNum = parts[0].toInt()
        val rightNum = parts[1].toInt()
        return Pair(leftNum, rightNum)
    }
    check(Pair(24, 113) == numbersFromLine("24   113"))

    fun part1(input: List<String>): Int {
        val ll = mutableListOf<Int>()
        val lr = mutableListOf<Int>()

        input.forEach {
            val (l, r) = numbersFromLine(it);
            ll.add(l)
            lr.add(r)
        }
        ll.sort()
        lr.sort()

        var diff = 0
        for ((l, r) in ll.zip(lr)) {
            //println("($l, $r) > $r - $l = ${r - l}")
            diff += Math.abs(r - l)
        }
        return diff
    }
    check(11 == part1(readInput("01_test")))
    println("part 1 answer: ${part1(readInput("01"))}")

    fun part2(input: List<String>): Int {
        val ll = mutableListOf<Int>()
        val lr = mutableListOf<Int>()

        input.forEach {
            val (l, r) = numbersFromLine(it);
            ll.add(l)
            lr.add(r)
        }

        var res = 0
        for (n in ll) {
            val hitsOnRight = lr.count { it == n }
            //println("n = $n ($hitsOnRight times) ~ ${n * hitsOnRight}")
            res += n * hitsOnRight
        }
        return res
    }
    check(31 == part2(readInput("01_test")))
    println("part 2 answer: ${part2(readInput("01"))}")
}
