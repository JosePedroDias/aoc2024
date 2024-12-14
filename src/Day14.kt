import kotlin.time.measureTime

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
    val robots = mutableListOf<Robot>()

    fun addRobot(r: Robot) {
        robots.add(r)
    }

    fun moveTick() {
        for (r in robots) {
            r.p += r.v
            r.p.keepInBounds(ranges)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0..ranges[1].last) {
            for (x in 0..ranges[0].last) {
                val p = Pos6(x, y)
                val amount = robots.count { it.p == p }
                if (amount > 0) {
                    sb.append(amount)
                } else {
                    sb.append('.')
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
    println("t=${t++}\n$m\n")
    repeat(100) {
        m.moveTick()
        println("t=${t++}\n$m\n")
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

        //val m = parse(readInput("14"), Pair(101, 103))
        //simulate(m)

        val res1 = 0
        println("Answer to part 1: $res1")
    }
    println(dt)
}
