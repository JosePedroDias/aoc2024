import java.util.Objects
import kotlin.time.measureTime

/*
   robot -> empty              move robot
   robot -> box+ -> empty      move robot and boxes
   robot -> wall               no op
   robot -> box+ -> wall       no op
*/

private const val ROBOT = '@'
private const val WALL = '#'
private const val BOX = 'O'

private data class State(val m: Matrix6, val moves: List<Dir3>) {
    override fun toString(): String {
        return m.toString()
    }
}

private data class Obstacle( var p: Pos4, val movable: Boolean, val repr: String )

private fun parse(lines: List<String>, scale: Int = 1): State {
    var inMoves = false
    val m = Matrix6(scale)
    val moves = mutableListOf<Dir3>()
    var robotP: Pos4? = null
    lines.forEachIndexed { y, l ->
        if (l.isEmpty()) {
            m.updateBounds()
            inMoves = true
        } else if (!inMoves) {
            l.forEachIndexed { x, ch ->
                val p = Pos4(x*scale, y)
                val obs: Obstacle? = when (ch) {
                    WALL -> Obstacle(p, false, if (scale == 2) "##" else "#")
                    BOX -> Obstacle(p, true,  if (scale == 2) "[]" else "O")
                    ROBOT -> { robotP = p; null }
                    else -> null
                }
                if (obs != null) {
                    m[p] = obs
                    m.move(listOf(obs), Dir3.NONE)
                }
            }
        } else {
            val chars = l.toCharArray().map { ch ->
                when(ch) {
                    '^' -> Dir3.U
                    '>' -> Dir3.R
                    'v' -> Dir3.D
                    '<' -> Dir3.L
                    else -> throw Error("unexpected input")
                }
            }
            moves += chars
        }
    }
    m.robotPos = robotP!!
    return State(m, moves)
}

private enum class Dir3 { U, R, D, L, NONE }

private data class Pos4(var x: Int, var y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }

    operator fun plusAssign(d: Dir3) {
        when (d) {
            Dir3.U -> y -= 1
            Dir3.R -> x += 1
            Dir3.D -> y += 1
            Dir3.L -> x -= 1
            else -> {}
        }
    }

    fun move(d: Dir3): Pos4 {
        return when (d) {
            Dir3.U -> Pos4(x, y - 1)
            Dir3.R -> Pos4(x + 1, y)
            Dir3.D -> Pos4(x, y + 1)
            Dir3.L -> Pos4(x - 1, y)
            else -> Pos4(x, y)
        }
    }

    fun toGps(): Int {
        return x + 100 * y
    }
}

private class Matrix6(val scale: Int) {
    private val m: MutableMap<Pos4, Obstacle> = mutableMapOf()
    private var ranges = arrayOf(0..< 1, 0..< 1)
    var showRobot:Boolean = true
    var robotPos:Pos4 = Pos4(0, 0)

    operator fun set(p: Pos4, obs: Obstacle) {
        m[p] = obs
    }

    operator fun get(p: Pos4): Obstacle? {
        return m.getOrDefault(p, null)
    }

    fun canItMove(obs: Obstacle, d: Dir3, visited: MutableSet<Obstacle> = mutableSetOf()): Pair<Boolean, Set<Obstacle>> {
        visited.add(obs)
        val len = obs.repr.length
        val found = mutableSetOf<Obstacle>()
        for (dx in 0..< len) {
            val p = Pos4(obs.p.x + dx, obs.p.y).move(d)
            val other = m[p]
            if (other != null && !visited.contains(other)) {
                if (!other.movable) return Pair(false, setOf())
                val (b, s) = canItMove(other, d, visited)
                if (!b) return Pair(false, setOf())
                found.add(other)
                for (o in s) found.add(o)
            }
        }
        return Pair(true, found)
    }

    fun move(movables: List<Obstacle>, d: Dir3) {
        if (movables.isEmpty()) return
        val len = movables[0].repr.length
        for (dx in 0..< len) {
            for (obs in movables) m.remove(Pos4(obs.p.x + dx, obs.p.y))
        }

        for (obs in movables) obs.p += d

        for (dx in 0..< scale) {
            for (obs in movables) m[Pos4(obs.p.x + dx, obs.p.y)] = obs
        }
    }

    fun updateBounds() {
        var xi = Int.MAX_VALUE
        var xf = Int.MIN_VALUE
        var yi = Int.MAX_VALUE
        var yf = Int.MIN_VALUE
        for ((x, y) in m.keys) {
            if (x < xi) xi = x
            if (x > xf) xf = x
            if (y < yi) yi = y
            if (y > yf) yf = y
        }
        ranges[0] = IntRange(xi, xf - 1 + scale)
        ranges[1] = IntRange(yi, yf)
    }

    fun getBoxes(): Set<Obstacle> {
        val res = mutableSetOf<Obstacle>()
        for (obs in m.values) {
            if (obs.movable) res.add(obs)
        }
        return res
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in ranges[1]) {
            var skipNext = false
            for (x in ranges[0]) {
                val p = Pos4(x, y)
                if (showRobot && p == robotPos) {
                    sb.append(ROBOT)
                } else {
                    if (skipNext) {
                        skipNext = false
                        continue
                    }
                    val obs = this[p]
                    if (obs != null) {
                        sb.append(obs.repr)
                        if (obs.repr.length == 2) skipNext = true
                    } else {
                        sb.append('.')
                    }
                }
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

private fun toGps(p: Pos4): Int {
    return p.x + 100 * p.y
}

private fun part1(st: State, debug: Boolean = false): Int {
    val (m, moves) = st
    if (debug) println(m)
    val p = m.robotPos

    moves.forEachIndexed { nth, d ->
        var isNoop = false
        val toMove = mutableSetOf<Obstacle>()
        var pt = p.move(d)

        while (true) {
            val obs = m[pt] ?: break
            if (obs.movable) {
                val (b, s) = m.canItMove(obs, d)
                if (b) {
                    toMove.add(obs)
                    for (o in s) toMove.add(o)
                    pt = pt.move(d)
                    continue
                }
            }
            isNoop = true
            toMove.clear()
            break
        }

        if (isNoop) {
            if (debug) println("#$nth: $d -> stuck: noop")
        } else {
            val countBefore = m.getBoxes().count()
            if (debug) {
                if (toMove.size > 0) println("#$nth: $d -> robot drags ${toMove.size} boxes")
                else println("#$nth: $d -> robot moves")
            }
            m.move(toMove.toList(), d)
            p += d
            val countAfter = m.getBoxes().count()
            check(countAfter == countBefore, { "$countAfter != $countBefore" })
        }
        if (debug) println(m)
    }

    val sum = m.getBoxes().sumOf { it.p.toGps() }
    //println("sum: $sum")
    return sum
}

fun main() {
    val dt = measureTime {
        val st1 = parse(readInput("15t1"))
        val res1 = part1(st1, false)
        check(res1 == 2028)

        val st2 = parse(readInput("15t2"))
        val res2 = part1(st2, false)
        check(res2 == 10092)

        val s = parse(readInput("15"))
        val res = part1(s, false)
        println("Answer to part 1: $res")

        val st3b = parse(readInput("15t3"), 2)
        val res1b = part1(st3b, false)
        check(res1b == 618)

        val st2b = parse(readInput("15t2"), 2)
        val res2b = part1(st2b, false)
        check(res2b == 9021)

        val sb = parse(readInput("15"), 2)
        val resB = part1(sb, false)
        println("Answer to part 2: $resB") // 618
    }
    println(dt)
}
