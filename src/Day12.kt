import kotlin.time.measureTime

/*
the perimeter of an island is any cell of that island which has
less than the 4 cardinal direction neighbors

the number of sides of an island is equal to the number of corners
it has, ie, the number of turns one has to do over the perimeter,
granted one the left (or right) vacant while moving forward, turning right each time
that's not possible, stopping once we loop back to the initial position/direction.
*/

private enum class Dir2 { U, R, D, L }
private fun Dir2.turnRight() = Dir2.entries[(ordinal + 1) % Dir2.entries.size]
private fun Dir2.turnLeft() = Dir2.entries[(ordinal - 1 + Dir2.entries.size) % Dir2.entries.size]

private typealias PosDir = Pair<Pos3, Dir2>

private data class Cursor(val members: Set<Pos3>) {
    var pos: Pos3
    var dir: Dir2
    init {
        pos = members.find { isSideCell(it) }!!
        dir = Dir2.U
        while (true) {
            if (!canMoveForward(pos, dir.turnLeft())) { break }
            dir = dir.turnLeft()
        }
    }

    fun moveForward(p: Pos3, d: Dir2): Pos3 = when (d) {
        Dir2.U -> Pos3(p.x,     p.y - 1)
        Dir2.R -> Pos3(p.x + 1, p.y)
        Dir2.D -> Pos3(p.x,     p.y + 1)
        Dir2.L -> Pos3(p.x - 1, p.y)
    }

    fun canMoveForward(p: Pos3, d: Dir2): Boolean {
        return members.contains(moveForward(p, d))
    }

    fun get4Neighbors(p: Pos3): List<Pos3> {
        return listOf(
            Pos3(p.x,     p.y - 1),
            Pos3(p.x + 1, p.y),
            Pos3(p.x,     p.y + 1),
            Pos3(p.x - 1, p.y),
        ).filter { members.contains(it) }
    }

    fun get4NeighborsPairs(p: Pos3): List<PosDir> {
        return listOf(
            Pair(Pos3(p.x,     p.y - 1), Dir2.U),
            Pair(Pos3(p.x + 1, p.y), Dir2.R),
            Pair(Pos3(p.x,     p.y + 1) , Dir2.D),
            Pair(Pos3(p.x - 1, p.y), Dir2.L),
        ).filter { !members.contains(it.first) }
    }

    fun get8Neighbors(p: Pos3): List<Pos3> {
        return listOf(
            Pos3(p.x - 1, p.y - 1),
            Pos3(p.x,        p.y - 1),
            Pos3(p.x + 1, p.y - 1),
            Pos3(p.x - 1, p.y),
            Pos3(p.x + 1, p.y),
            Pos3(p.x - 1, p.y + 1),
            Pos3(p.x,        p.y + 1),
            Pos3(p.x + 1, p.y + 1),
        ).filter { members.contains(it) }
    }

    fun isPerimeterCell(p: Pos3): Boolean {
        return get8Neighbors(p).count() < 8
    }

    fun isSideCell(p: Pos3): Boolean {
        return get4Neighbors(p).count() < 4
    }

    fun getPerimeterCellPairs(): List<PosDir> {
        return members
            .filter { isPerimeterCell(it) }
            .flatMap { p -> get4NeighborsPairs(p) }
    }

    fun getSideCount(): Int {
        val found = mutableListOf<PosDir>(Pair(pos, dir))
        while (true) {
            if (canMoveForward(pos, dir)) {
                pos = moveForward(pos, dir)
                if (found.contains(Pair(pos, dir))) break
                found.add(Pair(pos, dir))
            } else {
                dir = dir.turnRight()
                if (found.contains(Pair(pos, dir))) break
                found.add(Pair(pos, dir))
            }
        }

        val dirs = found.map { it.second }.toMutableList()
        dirs.add(found.first().second)
        val sideCount = dirs.zipWithNext().fold(0) {
            acc, (a, b) -> if (a != b) acc + 1 else acc
        }
        return sideCount
    }
}


// this way to find neighbors and perimeter is
// heavily inspired from
// https://github.com/jakubgwozdz/advent-of-code-2024/blob/main/aoc2024/src/main/kotlin/day12/Day12.kt

private data class Pos3(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }
}

private data class MutablePair(var first: Int, var second: Int)

private data class Island(val s: Set<Pos3>, var ch: Char = 'O') {
    val cursor = Cursor(s)

    fun getBoundary(): Pair<IntRange, IntRange> {
        val xLimits = MutablePair(Int.MAX_VALUE, Int.MIN_VALUE)
        val yLimits = MutablePair(Int.MAX_VALUE, Int.MIN_VALUE)
        for ((x, y) in s) {
            if (x < xLimits.first) xLimits.first = x
            if (x > xLimits.second) xLimits.second = x
            if (y < yLimits.first) yLimits.first = y
            if (y > yLimits.second) yLimits.second = y
        }
        return Pair(
            xLimits.first..xLimits.second,
            yLimits.first .. yLimits.second,
        )
    }

    val area: Int
    get() = s.size

    val perimeter: Int
    get() {
        val r = cursor.getPerimeterCellPairs().size
        return r
    }

    val sides: Int
    get() {
        val r = cursor.getSideCount()
        return r
    }

    val price: Int
        get() = area * perimeter

    val discountPrice: Int
        get() = area * sides

    fun getMatrix(): Matrix4 {
        val bounds = getBoundary()
        val m = Matrix4(
            bounds.first.last - bounds.first.first + 1,
            bounds.second.last - bounds.second.first + 1,
        )
        for (p in s) {
            val p0 = Pos3(
                p.x - bounds.first.first,
                p.y - bounds.second.first,
            )
            m.s(p0, ch)
        }
        return m
    }
}

private data class Matrix4(val w: Int, val h: Int) {
    val m: MutableMap<Pos3, Char> = mutableMapOf()
    val m2: MutableMap<Char, MutableList<Pos3>> = mutableMapOf()
    var xRange = 0..< w
    var yRange = 0..< h

    fun s(p: Pos3, ch: Char) {
        m[p] = ch
        var l = m2[ch]
        if (l == null) {
            l = mutableListOf()
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

    private fun neighbors(p: Pos3) = sequence {
        listOf(
            Pos3(p.x - 1, p.y),
            Pos3(p.x + 1, p.y),
            Pos3(p.x, p.y - 1),
            Pos3(p.x, p.y + 1),
        ).filter { inBounds(it) }
        .forEach { yield(it) }
    }

    private fun neighborsWithValue(p: Pos3, ch: Char) = sequence {
        neighbors(p).filter { g(it) == ch }
        .forEach { yield(it) }
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
            yield(Island(island, ch))
            island = mutableSetOf()
        }
    }

    fun findIslands() = sequence {
        for (ch in chars()) {
            for (island in findIslands(ch)) {
                yield(island)
            }
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

private fun part1(m: Matrix4, debug: Boolean = false): Int {
    if (debug) {
        for (island in m.findIslands()) {
            println(island.getMatrix())
            println("area: ${island.area}")
            println("perimeter: ${island.perimeter}")
            println("price: ${island.price}")
            println()
        }
    }

    val totalPrice = m.findIslands().fold(0) { sum, island -> sum + island.price }
    if (debug) println("totalPrice: $totalPrice")

    return totalPrice
}

private fun part2(m: Matrix4, debug: Boolean = false): Int {
    if (debug) {
        for (island in m.findIslands()) {
            println(island.getMatrix())
            println("area: ${island.area}")
            println("sides: ${island.sides}")
            println("discountPrice: ${island.discountPrice}")
            println("\n---\n")
        }
    }

    val totalDiscountPrice = m.findIslands().fold(0) { sum, island -> sum + island.discountPrice }
    if (debug) println("totalDiscountPrice: $totalDiscountPrice")

    return totalDiscountPrice
}

fun main() {
    val dt = measureTime {
        val mt1 = parse(readInput("12t1"))
        val mt2 = parse(readInput("12t2"))
        val mt3 = parse(readInput("12t3"))
        val mt4 = parse(readInput("12t4"))
        val mt5 = parse(readInput("12t5"))
        val m = parse(readInput("12"))

        check(part1(mt1, true) == 140)
        check(part1(mt2) == 772)
        check(part1(mt3) == 1930)
        println("Answer to part 1: ${part1(m)}")

        check(part2(mt1, true) == 80)
        check(part2(mt2) == 436)
        check(part2(mt4) == 236)
        check(part2(mt5) == 368)
        check(part2(mt3, true) == 1206) // 1255
        println("Answer to part 2: ${part2(m)}")
    }
    println(dt)
}
