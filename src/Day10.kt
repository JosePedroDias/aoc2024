import kotlin.time.measureTime

private const val DOT_HEIGHT = -10

private typealias Pos2 = Pair<Int, Int>

private class Matrix3(lines: List<String>) {
    val w: Int = lines[0].length;
    val h: Int = lines.size;
    val xr = 0..< w;
    val yr = 0..< h;
    val m = Array(h) { IntArray(w) }

    init {
        lines.forEachIndexed() {
                y, line ->
            val row = m[y]
            line.forEachIndexed { x, ch ->
                val v = if (ch == '.') DOT_HEIGHT else ch.digitToInt()
                row[x] = v
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in 0 until h) {
            for (x in 0 until w) {
                val v = m[y][x]
                sb.append(if (v == DOT_HEIGHT) '.' else v)
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    fun g(x: Int, y: Int): Int {
        return m[y][x]
    }

    fun s(x: Int, y: Int, v: Int) {
        m[y][x] = v
    }

    fun allHaving(v: Int) = sequence {
        for (y in 0 until h) {
            for (x in 0 until w) {
                val vv = g(x, y)
                if (vv == v) { yield(Pair(x, y)) }
            }
        }
    }

    fun isInBounds(x: Int, y: Int): Boolean {
        return x in xr && y in yr
    }

    fun neighbors(x: Int, y: Int): List<Pos2> {
        return listOf(
            Pair(x-1, y),
            Pair(x+1, y),
            Pair(x,   y-1),
            Pair(x,   y+1),
        ).filter { (x, y) -> isInBounds(x, y) }
    }
}

private fun part1(lines: List<String>): Int {
    val m = Matrix3(lines)
    //println(m)

    val nineMap = mutableMapOf<Pos2, MutableSet<Pos2>>()

    fun step(p: Pos2, pPrev: Pos2?, p0: Pos2?) {
        val (x, y) = p
        val v = m.g(x, y)
        //if (p0 != null) { println("$pPrev -> $p ($v)") }
        if (v == 9) {
            if (!nineMap.containsKey(p0)) {
                nineMap[p0!!] = mutableSetOf()
            }
            nineMap[p0]!!.add(p)
            return
        }
        val neighs = m.neighbors(x, y).filter { (x1, y1) -> m.g(x1, y1) == v + 1 }
        for (p2 in neighs) { step(p2, p, p0 ?: p) }
    }

    val zeroes = m.allHaving(0).toList()
    for (p in zeroes) { step(p, null, null) }

    //println("# trailheads: ${nineMap.keys.size}")
    for (pz in zeroes) {
        val pzNines = nineMap[pz]
        //println("- ${pz}: $pzNines (score: ${pzNines!!.size})")
    }

    return nineMap.values.sumOf { it.count() }
}

private fun part2(lines: List<String>): Int {
    val m = Matrix3(lines)
    //println(m)

    val nineMap = mutableMapOf<Pos2, MutableList<List<Pos2>>>()

    fun step(p: Pos2, way: List<Pos2>, p0: Pos2?) {
        val (x, y) = p
        val newWay = way + p
        val v = m.g(x, y)
        //if (p0 != null) { println("$pPrev -> $p ($v)") }
        if (v == 9) {
            if (!nineMap.containsKey(p0)) {
                nineMap[p0!!] = mutableListOf()
            }
            nineMap[p0]!!.add(newWay)
            return
        }
        val neighs = m.neighbors(x, y).filter { (x1, y1) -> m.g(x1, y1) == v + 1 }
        for (p2 in neighs) { step(p2, newWay, p0 ?: p) }
    }

    val zeroes = m.allHaving(0).toList()
    for (p in zeroes) { step(p, listOf(), null) }

    /*
    println("# trailheads: ${nineMap.keys.size}")
    for (pz in zeroes) {
        val pzNines = nineMap[pz]
        println("- ${pz}: (rating ${pzNines!!.size})")
        for (way in pzNines) {
            println("  - $way")
        }
    }
    */

    return nineMap.values.sumOf { it.count() }
}

fun main() {
    val dt = measureTime {
        //check(4 == part1(readInput("10_test1")))
        //check(3 == part1(readInput("10_test2")))
        check(36 == part1(readInput("10_test")))
        println("part 1 answer: ${part1(readInput("10"))}")

        //check(3 == part2(readInput("10_test3")))
        check(81 == part2(readInput("10_test")))
        println("part 2 answer: ${part2(readInput("10"))}")
    }
    println(dt)
}
