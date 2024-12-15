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
private const val EMPTY = '.'

private typealias State = Triple<Matrix6, List<Dir3>, Pos4>

private fun parse(lines: List<String>): State {
    var inMoves = false
    val m = Matrix6()
    val moves = mutableListOf<Dir3>()
    lines.forEachIndexed { y, l ->
        if (l.isEmpty()) {
            m.updateBounds()
            inMoves = true
        } else if (!inMoves) {
            l.forEachIndexed { x, ch -> m[Pos4(x, y)] = ch }
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
    val p = m.find(ROBOT)!!
    m[p] = EMPTY
    return Triple(m, moves, p)
}

private enum class Dir3 { U, R, D, L }

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
        }
    }

    fun move(d: Dir3): Pos4 {
        return when (d) {
            Dir3.U -> Pos4(x, y - 1)
            Dir3.R -> Pos4(x + 1, y)
            Dir3.D -> Pos4(x, y + 1)
            Dir3.L -> Pos4(x - 1, y)
        }
    }
}

private class Matrix6 {
    private val m: MutableMap<Pos4, Char> = mutableMapOf()
    private var ranges = arrayOf(0..< 1, 0..< 1)

    operator fun set(p: Pos4, ch: Char) {
        m[p] = ch
    }

    operator fun get(p: Pos4): Char {
        return m.getOrDefault(p, '.')
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
        ranges[0] = IntRange(xi, xf)
        ranges[1] = IntRange(yi, yf)
    }

    fun inBounds(p: Pos4): Boolean {
        return p.x in ranges[0] && p.y in ranges[1]
    }

    fun find(chTarget: Char): Pos4? {
        for ((p, ch) in m.entries) {
            if (ch == chTarget) {
                return p
            }
        }
        return null
    }

    fun findAll(chTarget: Char) = sequence {
        for ((p, ch) in m.entries) {
            if (ch == chTarget) {
                yield(p)
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in ranges[1]) {
            for (x in ranges[0]) { sb.append(this[Pos4(x, y)]) }
            sb.append('\n')
        }
        return sb.toString()
    }

    fun toStringWithRobot(p: Pos4): String {
        m[p] = ROBOT
        val s = toString()
        m[p] = EMPTY
        return s
    }
}

private fun toGps(p: Pos4): Int {
    return p.x + 100 * p.y
}

private fun part1(st: State, debug: Boolean = false): Int {
    val (m, moves, p) = st
    if (debug) println(m.toStringWithRobot(p))
    for (d in moves) {
        var isNoop = false
        val positionsToMove = mutableListOf<Pos4>()
        var pt = p.move(d)

        while (true) {
            when (m[pt]) {
                EMPTY -> break
                WALL -> { isNoop = true; break }
                BOX -> positionsToMove.add(pt)
                else -> throw Error("unexpected")
            }
            pt = pt.move(d)
        }

        if (isNoop) {
            if (debug) println("$d -> stuck: noop")
        } else {
            if (debug) {
                if (positionsToMove.size > 0) println("$d -> robot drags ${positionsToMove.size} boxes")
                else println("$d -> robot moves")
            }
            p += d
            for (pb in positionsToMove) { m[pb] = EMPTY }
            positionsToMove.map { it.move(d) } .forEach { m[it] = BOX }
        }
        if (debug) println(m.toStringWithRobot(p))
    }

    var sum = 0
    for (pB in m.findAll(BOX)) { sum += toGps(pB) }
    println("sum: $sum")
    return sum
}

fun main() {
    val dt = measureTime {
        val st2 = parse(readInput("15t2"))
        val res2 = part1(st2)
        check(res2 == 2028)

        val st1 = parse(readInput("15t1"))
        val res1 = part1(st1, true)
        check(res1 == 10092)

        val s = parse(readInput("15"))
        val resp1 = part1(s)
        println("Answer to part 1: $resp1")
    }
    println(dt)
}
