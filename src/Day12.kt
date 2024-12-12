import kotlin.time.measureTime

private data class Pos3(val x: Int, val y: Int)
//private data class Rect(val x: Int, val y: Int, val w: Int, val h: Int)
private typealias Island = Set<Pos3>

private data class MutablePair(var first: Int, var second: Int)

private data class Matrix4(val w: Int, val h: Int) {
    val m: MutableMap<Pos3, Char> = mutableMapOf()
    val m2: MutableMap<Char, MutableList<Pos3>> = mutableMapOf()
    var xRange = 0..< w
    var yRange = 0..< h

    fun s(p: Pos3, ch: Char) {
        m[p] = ch
        var l = m2[ch]
        if (l == null) {
            l = mutableListOf<Pos3>()
            m2[ch] = l
        }
        l.add(p)
    }

    fun g(p: Pos3): Char {
        return m.getOrDefault(p, '.')
    }

    fun inBounds(p: Pos3): Boolean {
        return p.x in xRange && p.y in yRange
    }

    fun chars(): Set<Char> {
        return m.values.toSet()
    }

    fun positionsHaving(ch: Char):MutableList<Pos3> {
        return m2[ch]!!.toMutableList()
    }

    fun neighbors(p: Pos3) = sequence {
        listOf(
            Pos3(p.x - 1, p.y),
            Pos3(p.x + 1, p.y),
            Pos3(p.x, p.y - 1),
            Pos3(p.x, p.y + 1),
        ).filter { inBounds(it) }
        .forEach { yield(it) }
    }

    fun neighborsWithValue(p: Pos3, ch: Char) = sequence {
        neighbors(p).filter { g(it) == ch }
        .forEach { yield(it) }
    }

    fun getBoundary(cells: Collection<Pos3>): Pair<IntRange, IntRange> {
        val xLims = MutablePair(Int.MAX_VALUE, Int.MIN_VALUE)
        val yLims = MutablePair(Int.MAX_VALUE, Int.MIN_VALUE)
        for ((x, y) in cells) {
            if (x < xLims.first) xLims.first = x
            if (x > xLims.second) xLims.second = x
            if (y < yLims.first) yLims.first = y
            if (y > yLims.second) yLims.second = y
        }
        return Pair(
            xLims.first..xLims.second,
            yLims.first .. yLims.second,
        )
    }

    fun findIslands(ch: Char) = sequence {
        val visited = mutableSetOf<Pos3>()
        val toVisit = mutableSetOf<Pos3>()
        val positionsWithThisValue = positionsHaving(ch)
        var island = mutableSetOf<Pos3>()
        while (positionsWithThisValue.size > 0) {
            val p0 = positionsWithThisValue[0]
            positionsWithThisValue.remove(p0)
            toVisit.add(p0)
            do {
                for (p in toVisit.toList()) {
                    toVisit.remove(p)
                    neighborsWithValue(p, ch).forEach {
                        if (!visited.contains(it)) toVisit.add(it)
                    }
                    island.add(p)
                    visited.add(p)
                    positionsWithThisValue.remove(p)
                }
            } while (toVisit.size > 0)
            yield(island)
            island = mutableSetOf()
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in yRange) {
            for (x in xRange) {
                sb.append(g(Pos3(x, y)))
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

private fun parse(lines: List<String>): Matrix4 {
    val w = lines[0].length
    val h = lines.size
    val m = Matrix4(w, h)
    lines.forEachIndexed { y, row ->
        row.forEachIndexed { x, ch ->
            m.s(Pos3(x, y), ch)
        }
    }
    return m
}

fun main() {
    val dt = measureTime {
        val lines = readInput("12_test")
        //val lines = readInput("12_test2")
        //val lines = readInput("12_test3")
        //val lines = readInput("12")
        val m = parse(lines)
        println(m)
        val chars = m.chars().toMutableList()
        for (ch in chars) {
            //println("\nislands with $ch...")
            for (island in m.findIslands(ch)) {
                //println(island)
                val bounds = m.getBoundary(island)
                val m2 = Matrix4(
                    bounds.first.last - bounds.first.first + 1,
                    bounds.second.last - bounds.second.first + 1,
                )
                for (p in island) {
                    val p0 = Pos3(
                        p.x - bounds.first.first,
                        p.y - bounds.second.first,
                    )
                    m2.s(p0, ch)
                }
                println(m2)
                println()
            }
            //println( m.findIslands(ch).toList() )
        }
    }
    println(dt)
}
