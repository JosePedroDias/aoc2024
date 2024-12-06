private data class Pos (var x: Int, var y: Int)

private enum class Dir(val x:Int, val y:Int) {
    N( 0, -1),
    S( 0,  1),
    W(-1,  0),
    E( 1,  0);

    fun turnRight(): Dir {
        return when (this) {
            N -> E
            E -> S
            S -> W
            W -> N
        }
    }

    fun moveForward(p: Pos): Pos {
        return Pos(p.x + x, p.y + y)
    }
}

private const val EMPTY = '.'
private const val OBSTACLE = '#'

private fun guardCharFromDir(d: Dir): Char {
    return when(d) {
        Dir.N -> '^'
        Dir.E -> '>'
        Dir.S -> 'v'
        Dir.W -> '<'
    }
}

private class Matrix(lines: List<String>) {
    val w: Int = lines[0].length
    val h: Int = lines.size
    val m = Array(h) { CharArray(w) }
    var dir: Dir = Dir.N
    var pos: Pos = Pos(0, 0)

    init {
        lines.forEachIndexed() {
                y, line ->
            val row = m[y]
            line.forEachIndexed { x, ch ->
                if (ch == '^') {
                    dir = Dir.N
                    pos = Pos(x, y)
                } else if (ch == '>') {
                    dir = Dir.E
                    pos = Pos(x, y)
                } else if (ch == 'v') {
                    dir = Dir.S
                    pos = Pos(x, y)
                } else if (ch == '<') {
                    dir = Dir.W
                    pos = Pos(x, y)
                }
                row[x] = ch
            }
        }
    }

    fun inBounds(p: Pos): Boolean {
        return p.x in 0..< w && p.y in 0..< h
    }

    fun g(p: Pos): Char {
        return m[p.y][p.x]
    }

    fun s(p:Pos, c:Char) {
        if (!inBounds(p)) {
            throw Error("pos is out of bounds")
        }
        m[p.y][p.x] = c
    }

    fun allHaving(c: Char): List<Pos> {
        val all = mutableListOf<Pos>()
        for (y in 0..< h) {
            for (x in 0..< w) {
                val p = Pos(x, y)
                if (g(p) == c) {
                    all.add(p)
                }
            }
        }
        return all
    }

    override fun toString(): String {
        return StringBuilder().apply {
            for (y in 0..< h) {
                for (x in 0..< w) {
                    append(g(Pos(x, y)))
                }
                append('\n')
            }
        }.toString()
    }
}

private fun posDirToString(p: Pos, d: Dir): String {
    return "${p.x} ${p.y} ${d.x} ${d.y}"
}

fun main() {
    fun part1(input: List<String>): Int {
        val m = Matrix(input)
        // println("pos:${m.pos} | dir:${m.dir} ${m.dir.x},${m.dir.y}"); println(m)
        val positions = mutableSetOf(m.pos)
        while (true) {
            val pos2 = m.dir.moveForward(m.pos)
            try {
                val charAtPos2 = m.g(pos2)
                if (charAtPos2 == OBSTACLE) {
                    m.dir = m.dir.turnRight()
                    // println("OBSTACLE: TURN RIGHT TO ${m.dir}"); println(m)
                } else {
                    m.s(m.pos, EMPTY)
                    m.pos = pos2
                    m.s(m.pos, guardCharFromDir(m.dir))
                    positions.add(m.pos)
                    // println("EMPTY: MOVE FORWARD ${m.dir} TO ${m.pos}"); println(m)
                }
            } catch (e: Throwable) {
                // println("OUT OF BOUNDS: ALL DONE"); println(positions)
                return positions.size
            }
        }
    }
    check(41 == part1(readInput("06_test")))
    println("part 1 answer: ${part1(readInput("06"))}")

    fun part2(input: List<String>): Int {
        val m0 = Matrix(input)
        val candidates = m0.allHaving(EMPTY)
        var loopsFound = 0
        for (p in candidates) {
            val m = Matrix(input) // TODO kinda dumb
            m.s(p, OBSTACLE)
            val positions = mutableSetOf(posDirToString(m.pos, m.dir))
            ite@
            while (true) {
                val pos2 = m.dir.moveForward(m.pos)
                try {
                    val charAtPos2 = m.g(pos2)
                    if (charAtPos2 == OBSTACLE) {
                        m.dir = m.dir.turnRight()
                    } else {
                        m.s(m.pos, EMPTY)
                        m.pos = pos2
                        m.s(m.pos, guardCharFromDir(m.dir))
                        val pd = posDirToString(m.pos, m.dir)
                        if (positions.contains(pd)) {
                            ++loopsFound
                            break@ite
                        }
                        positions.add(pd)
                    }
                } catch (e: Throwable) {
                    break@ite
                }
            }
        }
        return loopsFound
    }
    check(6 == part2(readInput("06_test")))
    println("part 2 answer: ${part2(readInput("06"))}")
}