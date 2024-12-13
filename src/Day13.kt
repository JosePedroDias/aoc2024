import kotlin.time.measureTime

/*
0-100a  +   0b (100)
0- 99a  +   1b ( 99)
0- 90a  +  10b ( 90)
0 -60a  +  40b ( 60)
0 -10a  +  90b ( 10)
0 - 1a  +  99b (  2)
0       + 100b (  1)
*/

private fun aBUpTo(maxAmount: Int) = sequence<Pair<Int, Int>> {
    for (aPlusB in 0..maxAmount) {
        for (a in 0..aPlusB) {
            yield(Pair(a, maxAmount-aPlusB))
        }
    }
}

private fun abTo(maxAmount: Int) = sequence<Pair<Int, Int>> {
    for (b in 0..maxAmount) {
        for (a in 0..maxAmount) {
            yield(Pair(a, b))
        }
    }
}

private data class Pos5(var x:Int = 0, var y:Int = 0) {
    override fun toString(): String {
        return "($x, $y)"
    }
    operator fun plus(p:Pos5) {
        x += p.x
        y += p.y
    }
    operator fun plusAssign(p:Pos5) {
        x += p.x
        y += p.y
    }
    fun addTimes(times: Int, p:Pos5) {
        x += times * p.x
        y += times * p.y
    }
    override fun equals(p:Any?): Boolean {
        if (p is Pos5) {
            return x == p.x && y == p.y
        }
        return false
    }
}

private typealias Case = List<Pos5>

private val BUTTON_PRESS_COST = arrayOf(3, 1)
private val MAX_BUTTON_PRESSES = 100

private fun parse(path: String): Sequence<Case> {
    val rgx1 = Regex("""Button .: X([+-])(\d+), Y([+-])(\d+)""")
    val rgx2 = Regex("""Prize: X=(\d+), Y=(\d+)""")

    fun parseButton(line: String): Pos5 {
        val (_, xs, xn, ys, yn) = rgx1.matchAt(line, 0)!!.groupValues
        val x = xn.toInt() * if (xs == "-") -1 else 1
        val y = yn.toInt() * if (ys == "-") -1 else 1
        return Pos5(x, y)
    }

    fun parsePrize(line: String): Pos5 {
        val (_, xn, yn) = rgx2.matchAt(line, 0)!!.groupValues
        val x = xn.toInt()
        val y = yn.toInt()
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

fun main() {
    val dt = measureTime {
        //aBUpTo(3).forEach { (a, b) -> println("$a,$b") }
        abTo(3).forEach { (a, b) -> println("$a,$b") }

        val p = Pos5()
        p.addTimes(80, Pos5(94, 34))
        p.addTimes(40, Pos5(22, 67))
        val cost = 80 * BUTTON_PRESS_COST[0] + 40 * BUTTON_PRESS_COST[1]
        check(p == Pos5(8400, 5400))
        check(cost == 280)

        val test = parse("13_test") // 480
        val prob = parse("13")

        check(part1(test) == 480)
        println("Answer to part 1: ${part1(prob)}")

        //check(part2(prob) == 140) // TODO
        //println("Answer to part 2: ${part2(prob)}")
    }
    println(dt)
}