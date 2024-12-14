import kotlin.time.measureTime

private typealias Num = Long

private val BUTTON_PRESS_COST = arrayOf(3, 1)
private val MAX_BUTTON_PRESSES = 100
private val MAX_BUTTON_PRESSES2 = 10000
const val INCREMENT = 10000000000000L

private data class Pos5(var x:Num = 0, var y:Num = 0) {
    override fun toString(): String {
        return "($x, $y)"
    }
    fun addTimes(times: Int, p:Pos5) {
        x += times * p.x
        y += times * p.y
    }
    fun bump() {
        x += INCREMENT
        y += INCREMENT
    }
}

private typealias Case = List<Pos5>

private fun parse(path: String): Sequence<Case> {
    val rgx1 = Regex("""Button .: X([+-])(\d+), Y([+-])(\d+)""")
    val rgx2 = Regex("""Prize: X=(\d+), Y=(\d+)""")

    fun parseButton(line: String): Pos5 {
        val (_, xs, xn, ys, yn) = rgx1.matchAt(line, 0)!!.groupValues
        val x = xn.toLong() * if (xs == "-") -1 else 1
        val y = yn.toLong() * if (ys == "-") -1 else 1
        return Pos5(x, y)
    }

    fun parsePrize(line: String): Pos5 {
        val (_, xn, yn) = rgx2.matchAt(line, 0)!!.groupValues
        val x = xn.toLong()
        val y = yn.toLong()
        return Pos5(x, y)
    }

    val lines = readInput(path)
    return lines.windowed(4, 4, true) { (a, b, p) ->
        val bA = parseButton(a)
        val bB = parseButton(b)
        val pr = parsePrize(p)
        listOf(bA, bB, pr)
    }.asSequence()
}

private fun part1(seq: Sequence<Case>): Int {
    var totalCost = 0
    var caseI = 0
    for ((aVec, bVec, goalVec) in seq) {
        println("** case #$caseI | aVec:$aVec, b:$bVec, goalVec:$goalVec **")
        ++caseI

        // aTimes, bTimes, costTokens
        val validTrios = mutableListOf<Triple<Int, Int, Int>>()

        for ((aTimes, bTimes) in abTo(MAX_BUTTON_PRESSES)) {
            val clawPos = Pos5()
            clawPos.addTimes(aTimes, aVec)
            clawPos.addTimes(bTimes, bVec)
            //println("$aTimes * $aVec + $bTimes * $bVec == $clawPos (goal: $goalVec)")
            if (clawPos == goalVec) {
                val cost = aTimes * BUTTON_PRESS_COST[0] + bTimes * BUTTON_PRESS_COST[1]
                //println("-> $aTimes a, $bTimes b (cost: $cost)")
                validTrios.add(Triple(aTimes, bTimes, cost))
            }
        }

        if (validTrios.size > 0) {
            validTrios.sortBy { it.third }
            val (bestATimes, bestBTimes, bestCost) = validTrios[0]
            totalCost += bestCost
            println(">>> best: a:$bestATimes, b:$bestBTimes, cost:$bestCost of ${validTrios.size}")
        } else {
            println(">>> no combination found!")
        }
    }
    return totalCost
}

private fun part2(seq: Sequence<Case>): Long {
    var totalCost = 0L
    var caseI = 0
    for ((aVec, bVec, goalVec) in seq) {
        println("** case #$caseI | aVec:$aVec, b:$bVec, goalVec:$goalVec **")
        ++caseI

        val result = solveEq(Pair(aVec.x, aVec.y), Pair(bVec.x, bVec.y), Pair(goalVec.x + INCREMENT, goalVec.y + INCREMENT))
        if (result != null) {
            val (aTimes, bTimes) = result
            println("FOUND: a=$aTimes, b=$bTimes")
            val cost = aTimes * BUTTON_PRESS_COST[0] + bTimes * BUTTON_PRESS_COST[1]
            totalCost += cost
        } else {
            println("NOT FOUND")
        }
    }
    return totalCost
}

fun main() {
    val dt = measureTime {
        val p = Pos5()
        p.addTimes(80, Pos5(94, 34))
        p.addTimes(40, Pos5(22, 67))
        val cost = 80 * BUTTON_PRESS_COST[0] + 40 * BUTTON_PRESS_COST[1]
        check(p == Pos5(8400, 5400))
        check(cost == 280)

        val test = parse("13_test") // 480
        val prob = parse("13")

        //check(part1(test) == 480)
        //println("Answer to part 1: ${part1(prob)}")

        //check(part2(prob) == 140L)
        println("Answer to part 2: ${part2(prob)}")
    }
    println(dt)
}