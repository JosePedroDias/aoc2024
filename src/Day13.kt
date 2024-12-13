import kotlin.time.measureTime

private typealias Case = List<Pair<Int, Int>>

private fun parse(path: String): Sequence<Case> {
    val rgx1 = Regex("""Button .: X([+-])(\d+), Y([+-])(\d+)""")
    val rgx2 = Regex("""Prize: X=(\d+), Y=(\d+)""")

    fun parseButton(line: String): Pair<Int, Int> {
        val (_, xs, xn, ys, yn) = rgx1.matchAt(line, 0)!!.groupValues
        val x = xn.toInt() * if (xs == "-") -1 else 1
        val y = yn.toInt() * if (ys == "-") -1 else 1
        return Pair(x, y)
    }

    fun parsePrize(line: String): Pair<Int, Int> {
        val (_, xn, yn) = rgx2.matchAt(line, 0)!!.groupValues
        val x = xn.toInt()
        val y = yn.toInt()
        return Pair(x, y)
    }

    val lines = readInput(path)
    return lines.windowed(4, 4, true) { (a, b, p) ->
        val bA = parseButton(a)
        val bB = parseButton(b)
        val pr = parsePrize(p)
        listOf(bA, bB, pr)
    }.asSequence()
}

private fun part1(seq: Sequence<Case>) {
    for ((a, b, p) in seq) {
        println("a:$a, b:$b, p:$p")
    }
}

fun main() {
    val dt = measureTime {
        val test = parse("13_test") // 480
        //val prob = parse("13")

        //println(test.toList())
        part1(test)

        // You estimate that each button would need to be pressed no more than 100 times to win a prize.

        //check(part1(test) == 140)
        //println("Answer to part 1: ${part1(test)}")

        //check(part2(prob) == 140)
        //println("Answer to part 2: ${part2(prob)}")
    }
    println(dt)
}