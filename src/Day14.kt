import kotlin.time.measureTime

private const val STEPS = 100

// https://kotlinlang.org/docs/operator-overloading.html
private data class Pos6(var x:Int = 0, var y:Int = 0) {
    override fun toString(): String {
        return "($x, $y)"
    }

    operator fun plus(v: Pos6) {
        x += v.x
        y += v.y
    }

    operator fun plusAssign(v: Pos6) {
        x += v.x
        y += v.y
    }

    fun keepInBounds(ranges: Array<IntRange>) {
        val (xr, yr) = ranges
        if (x < xr.first) { x += xr.last - xr.first + 1 }
        if (y < yr.first) { y += yr.last - yr.first + 1 }
        if (x > xr.last) { x -= xr.last - xr.first + 1 }
        if (y > yr.last) { y -= yr.last - yr.first + 1 }
    }
}

private data class Robot(val p: Pos6, val v:Pos6)

private data class Matrix5(val w: Int, val h: Int) {
    var ranges = arrayOf(0..< w, 0..< h)
    var middles = arrayOf(ranges[0].last / 2, ranges[1].last / 2)
    var drawQuadrants: Boolean = false
    val robots = mutableListOf<Robot>()

    init {
        check(w % 2 == 1)
        check(h % 2 == 1)
    }

    fun addRobot(r: Robot) {
        robots.add(r)
    }

    fun moveTick() {
        for (r in robots) {
            r.p += r.v
            r.p.keepInBounds(ranges)
        }
    }

    fun quadrants() = sequence {
        val (middleX, middleY) = middles

        val rx0 = 0 ..< middleX
        val rx1 = middleX+1 .. ranges[0].last

        val ry0 = 0 ..< middleY
        val ry1 = middleY+1 .. ranges[1].last

        yield(Pair(rx0, ry0))
        yield(Pair(rx1, ry0))
        yield(Pair(rx0, ry1))
        yield(Pair(rx1, ry1))
    }

    fun answer(): Long {
        //drawQuadrants = true; println(this); drawQuadrants = false

        val qs = quadrants().toList()
        val qCounts = qs.map { (rx, ry) ->
            var sum = 0L
            for (y in ry.first..ry.last) {
                for (x in rx.first..rx.last) {
                    sum += countRobotsAt(Pos6(x, y))
                }
            }
            sum
        }
        println("qCounts: $qCounts")

        val answer = qCounts.reduce { acc, v -> acc * v }
        println("answer: $answer")
        return answer
    }

    fun countRobotsAt(p: Pos6): Int {
        return robots.count { it.p == p }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0..ranges[1].last) {
            for (x in 0..ranges[0].last) {
                if (drawQuadrants && (x == middles[0] || y == middles[1])) {
                    sb.append(' ')
                } else {
                    val amount = countRobotsAt(Pos6(x, y))
                    if (amount > 0) sb.append(amount)
                    else sb.append('.')
                }
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

private val lineRgx = Regex("""p=(\d+),(\d+) v=(-?\d+),(-?\d+)""")

private fun parse(lines: List<String>, dims: Pair<Int, Int>): Matrix5 {
    val m = Matrix5(dims.first, dims.second)
    for (l in lines) {
        val valsS = lineRgx.matchEntire(l)?.groupValues?.drop(1)?.toList()
        check(valsS != null && valsS.size == 4)
        val vals = valsS.map { it.toInt() }
        m.addRobot(Robot(Pos6(vals[0], vals[1]), Pos6(vals[2], vals[3])))
    }
    return m
}

private fun simulate(m: Matrix5) {
    var t = 0
    //println("t=${t++}\n$m\n")
    repeat(100) {
        m.moveTick()
        //println("t=${t++}\n$m\n")
    }
}

fun main() {
    val dt = measureTime {
        val ranges = arrayOf(0..< 4, 0..< 3)
        val p = Pos6(1, 1)
        p += Pos6(-1, 0); p.keepInBounds(ranges)
        check(p == Pos6(0, 1))
        p += Pos6(-1, 0); p.keepInBounds(ranges)
        println(p)
        check(p == Pos6(3, 1))
        p += Pos6(0, 2); p.keepInBounds(ranges)
        check(p == Pos6(3, 0))

        val mt = parse(readInput("14t1"), Pair(11, 7))
        simulate(mt)
        println("Answer to part 0: ${mt.answer()}")

        val m = parse(readInput("14"), Pair(101, 103))
        simulate(m)
        println("Answer to part 1: ${m.answer()}")
    }
    println(dt)
}
