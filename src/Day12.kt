import kotlin.streams.toList
import kotlin.time.measureTime

private typealias Pox = Pair<Int, Int>

private fun parse(lines: List<String>): List<List<Int>> {
    return lines.map {
        it.chars().toList()
    }
}

val adj4 = listOf(
    Pair(-1,  0),
    Pair( 1,  0),
    Pair( 0, -1),
    Pair( 0,  1),
)

val dirs2 = listOf(
    Pair(1, 0),
    Pair(0, 1),
)

private fun connected(m: List<List<Int>>): List<List<Pox>> {
    val w = m[0].size
    val h = m.size

    var nextId = 0
    val pos2Island = mutableMapOf<Pox, Int>()

    fun dfs(x: Int, y: Int, v: Int, id: Int) {
        if (x in 0..< w && y in 0..< h) {
            val p = Pair(x, y)
            if (pos2Island.containsKey(p)) return
            if (m[y][x] == v) {
                pos2Island[p] = id
                for ((dx, dy) in adj4) {
                    dfs(x+dx, y+dy, v, id)
                }
            }
        }
    }

    for (y in 0..< h) {
        for (x in 0..< w) {
            if (!pos2Island.containsKey(Pair(x, y))) {
                dfs(x, y, m[y][x], nextId++)
            }
        }
    }

    val islands = mutableMapOf<Int, MutableList<Pox>>()

    for ((p, id) in pos2Island) {
        val bag = islands.getOrDefault(id, mutableListOf())
        if (bag.size == 0) { islands[id] = bag }
        bag.add(p)
    }

    return islands.values.toList()
}

private fun part1(m: List<List<Int>>): Int {
    val w = m[0].size
    val h = m.size
    val islands = connected(m)

    val res = islands.sumOf { isl ->
        var perim = 0
        for (p in isl) {
            for ((dx, dy) in adj4) {
                val x = p.first + dx
                val y = p.second + dy
                if (x !in 0 ..< w || y !in 0..< h || !isl.contains(Pair(x, y))) {
                    ++perim
                }
            }
        }
        isl.size * perim
    }
    return res
}

private fun part2(m: List<List<Int>>): Int {
    val w = m[0].size
    val h = m.size
    val islands = connected(m)

    val res = islands.sumOf { isl ->
        // perim all boundary segments
        val perim = mutableSetOf<Pair<Pox, Pox>>()
        for (p in isl) {
            for ((dx, dy) in adj4) {
                val x = p.first + dx
                val y = p.second + dy
                val p2 = Pair(x, y)
                if (x !in 0 ..< w || y !in 0..< h || !isl.contains(p2)) {
                    perim.add(Pair(p, p2))
                }
            }
        }

        // reduce to last in each dir
        var sides = 0
        for ((p1, p2) in perim) {
            var keep = true
            for ((dx, dy) in dirs2) {
                val p1n = Pair(p1.first + dx, p1.second + dy)
                val p2n = Pair(p2.first + dx, p2.second + dy)
                if (Pair(p1n, p2n) in perim) keep = false
            }
            if (keep) ++sides
        }

        isl.size * sides
    }
    return res
}

fun main() {
    val dt = measureTime {
        val it1 = parse(readInput("12t1"))
        val it2 = parse(readInput("12t2"))
        val it3 = parse(readInput("12t3"))
        val it4 = parse(readInput("12t4"))
        val it5 = parse(readInput("12t5"))
        val p = parse(readInput("12"))

        check(part1(it1) == 140)
        check(part1(it2) == 772)
        check(part1(it3) == 1930)
        println("Answer to part 1: ${part1(p)}")

        check(part2(it1) == 80)
        check(part2(it2) == 436)
        check(part2(it4) == 236)
        check(part2(it5) == 368)
        check(part2(it3) == 1206) // 1255
        println("Answer to part 2: ${part2(p)}")
    }
    println(dt)
}
