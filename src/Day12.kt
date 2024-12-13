import kotlin.time.measureTime

private data class Pos3(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }
}

private data class MutablePair(var first: Int, var second: Int)

private data class Island(val s: Set<Pos3>, var ch: Char = 'O') {
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
        var sum = 0
        for (p in s) {
            sum += 4 - neighbors(p).count()
        }
        return sum
    }

    // -x, +x, -y, +y
    private fun countContinuousSegments(l: List<Pos3>, attr: (Pos3) -> Int, groupAttr: (Pos3) -> Int): Int {
        if (l.isEmpty()) return 0
        if (l.size == 1) return 1

        var count = 0

        val dims = l.groupBy { groupAttr(it) }

        for (segs in dims.values) {
            var prev: Pos3? = null
            for (p in segs) {
                if (prev != null) {
                    if (attr(p) - attr(prev) != 1) {
                        ++count
                        prev = null
                    } else {
                        prev = p
                    }
                } else {
                    ++count
                    prev = p
                }
            }
        }

        return count
    }

    val sides: Int
    get() {
        val segmentsPerDirection: Array<MutableList<Pos3>> = arrayOf(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
        for (p in s) {
            for ((idx, pp) in neighbors2(p)) {
                segmentsPerDirection[idx].add(pp)
            }
        }

        var count = 0

        val xSel = fun(p: Pos3): Int { return p.x }
        val ySel = fun(p: Pos3): Int { return p.y }

        val leftSegments = segmentsPerDirection[0]
        leftSegments.sortBy { it.y }
        leftSegments.sortBy { it.x }
        count += countContinuousSegments(leftSegments, ySel, xSel)

        val rightSegments = segmentsPerDirection[1]
        rightSegments.sortBy { it.y }
        rightSegments.sortBy { it.x }
        count += countContinuousSegments(rightSegments, ySel, xSel)

        val upSegments = segmentsPerDirection[2]
        upSegments.sortBy { it.x }
        upSegments.sortBy { it.y }
        count += countContinuousSegments(upSegments, xSel, ySel)

        val downSegments = segmentsPerDirection[3]
        downSegments.sortBy { it.x }
        downSegments.sortBy { it.y }
        count += countContinuousSegments(downSegments, xSel, ySel)

        return count
    }

    val price: Int
        get() = area * perimeter

    val discountPrice: Int
        get() = area * sides

    private fun neighbors(p: Pos3) = sequence {
        listOf(
            Pos3(p.x - 1, p.y),
            Pos3(p.x + 1, p.y),
            Pos3(p.x, p.y - 1),
            Pos3(p.x, p.y + 1),
        ).filter { s.contains(it) }
            .forEach { yield(it) }
    }

    private fun neighbors2(p: Pos3) = sequence {
        listOf(
            Pos3(p.x - 1, p.y), // 0: -x
            Pos3(p.x + 1, p.y), // 1: +x
            Pos3(p.x, p.y - 1), // 2: -y
            Pos3(p.x, p.y + 1), // 3: +y
        ).forEachIndexed { idx, pp ->
            if (!s.contains(pp)) {
                yield(Pair(idx, pp))
            }
        }
    }

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
    //println("totalPrice: $totalPrice")

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
    println("totalDiscountPrice: $totalDiscountPrice")

    return totalDiscountPrice
}

fun main() {
    val dt = measureTime {
        val mt1 = parse(readInput("12_test"))
        val mt2 = parse(readInput("12_test2"))
        val mt3 = parse(readInput("12_test3"))
        val mt4 = parse(readInput("12_test4"))
        val mt5 = parse(readInput("12_test5"))
        val m = parse(readInput("12"))

        check(part1(mt1) == 140)
        check(part1(mt2) == 772)
        check(part1(mt3) == 1930)
        println("Answer to part 1: ${part1(m)}")

        //check(part2(mt1) == 80)
        //check(part2(mt2) == 436)
        //check(part2(mt4) == 236)
        //check(part2(mt5) == 368)
        check(part2(mt3, true) == 1206) // 1255
        println("Answer to part 2: ${part2(m)}")
    }
    println(dt)
}
