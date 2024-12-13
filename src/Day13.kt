import kotlin.time.measureTime

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
    var res = 0
    var caseI = 0
    for ((a, b, p) in seq) {
        println("** case #$caseI **")
        ++caseI
        var aNeededPresses: Int? = null
        var bNeededPresses: Int? = null
        println("a:$a, b:$b, p:$p")

        for ((numAs, numBs) in combinations(100)) {
            val claw = Pos5()

        }

        val clawA = Pos5()
        for (i in 0 .. MAX_BUTTON_PRESSES) {
            //println("#$i: $clawA")
            if (clawA == p) { aNeededPresses = i; break }
            clawA += a
        }
        println("last clawA: $clawA")

        val clawB = Pos5()
        for (i in 0 .. MAX_BUTTON_PRESSES) {
            //println("#$i: $clawB")
            if (clawB == p) { bNeededPresses = i; break }
            clawB += b
        }
        println("last clawB: $clawB")

        // TODO combinations?
        println("aNeededPresses: $aNeededPresses, bNeededPresses: $bNeededPresses")
        if (aNeededPresses != null && bNeededPresses != null && aNeededPresses + bNeededPresses <= MAX_BUTTON_PRESSES) {
            val aCost = BUTTON_PRESS_COST[0] * aNeededPresses
            val bCost = BUTTON_PRESS_COST[1] * bNeededPresses
            val bothCost = aCost + bCost
            println("aCost: $aCost, bCost: $bCost, bothCost: $bothCost")
        } else if (aNeededPresses != null) {
            val aCost = BUTTON_PRESS_COST[0] * aNeededPresses
            println("aCost: $aCost")
        } else if (bNeededPresses != null) {
            val bCost = BUTTON_PRESS_COST[1] * bNeededPresses
            println("bCost: $bCost")
        } else {
            println("nope")
        }
    }
    return res
}

fun main() {
    val dt = measureTime {
        val test = parse("13_test") // 480
        //val prob = parse("13")

        //part1(test)
        check(part1(test) == 480)
        //println("Answer to part 1: ${part1(test)}")

        //check(part2(prob) == 140) // TODO
        //println("Answer to part 2: ${part2(prob)}")
    }
    println(dt)
}