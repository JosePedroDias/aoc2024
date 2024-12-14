import kotlin.time.measureTime

private data class Vec2 (var x: Int, var y: Int) {
    fun add(v: Vec2): Vec2 {
        return Vec2(x + v.x, y + v.y)
    }

    fun sub(v: Vec2): Vec2 {
        return Vec2(x - v.x, y - v.y)
    }

    fun mulS(n: Int): Vec2 {
        return Vec2(n * x, n * y)
    }

    fun clone(): Vec2 {
        return Vec2(x, y)
    }
}

private class Matrix2<T>(val w: Int, val h: Int) {
    val m = mutableMapOf<Vec2, T>()

    fun inBounds(p: Vec2): Boolean {
        return p.x in 0..< w && p.y in 0..< h
    }

    fun g(p: Vec2): T? {
        return m[p]
    }

    fun s(p:Vec2, c:T) {
        if (!inBounds(p)) {
            throw Error("pos is out of bounds")
        }
        m[p] = c
    }

    fun allHaving(c: T) = sequence {
        for ((k, v) in m.entries) {
            if (v == c) {
                yield(k)
            }
        }
    }

    fun clone(): Matrix2<T> {
        val mc = Matrix2<T>(w, h)
        for ((k, v) in m) {
            mc.s(k, v)
        }
        return mc
    }

    override fun toString(): String {
        return StringBuilder().apply {
            for (y in 0..< h) {
                for (x in 0..< w) {
                    val v = g(Vec2(x, y))
                    if (v is Boolean) {
                        append(if (v) 'T' else 'F')
                    } else {
                        append(v ?: '.')
                    }

                }
                append('\n')
            }
        }.toString()
    }
}

private fun part0(lines: List<String>) {
    val m = mutableMapOf<Char, Int>()
    lines.forEachIndexed {
            y, line ->
        line.forEachIndexed fe@{ x, ch ->
            if (ch != '.') {
                var count = 0
                if (m.containsKey(ch)) {
                    count = m[ch]!!
                }
                ++count
                m[ch] = count
            }
        }
    }
    println("frequencies: $m")
    println("amount of frequencies: ${m.keys.size}")
    println("max frequency amount: ${m.values.max()}")
}

private fun part1(lines: List<String>): Int {
    val w = lines[0].length
    val h = lines.size
    val mAll = Matrix2<Char>(w, h)
    lines.forEachIndexed {
            y, line ->
        line.forEachIndexed fe@{ x, ch ->
            if (ch != '.') {
                mAll.s(Vec2(x, y), ch)
            }
        }
    }

    val frequencies = mAll.m.values.toSet()
    val antiNodes = mutableSetOf<Vec2>()

    for (f in frequencies) {
        val positions = mAll.allHaving(f).toList()
        //val m = Matrix2<Char>(w, h); for (p in positions) { m.s(p, f) }
        for ((iA, iB) in combinations(positions.size)) {
            val pA = positions[iA]
            val pB = positions[iB]
            val an1 = pB.sub(pA).add(pB)
            val an2 = pA.sub(pB).add(pA)
            if (mAll.inBounds(an1) && !positions.contains(an1)) {
                antiNodes.add(an1)
                //m.s(an1, '#')
            }
            if (mAll.inBounds(an2) && !positions.contains(an2)) {
                antiNodes.add(an2)
                //m.s(an2, '#')
            }
        }
        //println(m)
    }

    return antiNodes.size
}

private fun part2(lines: List<String>): Int {
    val w = lines[0].length
    val h = lines.size
    val mAll = Matrix2<Char>(w, h)
    val antiNodes = mutableSetOf<Vec2>()
    lines.forEachIndexed {
            y, line ->
        line.forEachIndexed fe@{ x, ch ->
            if (ch != '.') {
                val p = Vec2(x, y)
                mAll.s(p, ch)
                antiNodes.add(p)
            }
        }
    }
    val frequencies = mAll.m.values.toSet()
    for (f in frequencies) {
        val positions = mAll.allHaving(f).toList()
        //val m = Matrix2<Char>(w, h); for (p in positions) { m.s(p, f) }
        for ((iA, iB) in combinations(positions.size)) {
            val pA = positions[iA]
            val pB = positions[iB]
            for (i in -50..50) {
                val an1 = pB.sub(pA).mulS(i).add(pB)
                val an2 = pA.sub(pB).mulS(i).add(pA)
                if (mAll.inBounds(an1)) {
                    antiNodes.add(an1)
                    //if (!m.m.containsKey(an1)) { m.s(an1, '#') }
                }
                if (mAll.inBounds(an2)) {
                    antiNodes.add(an2)
                    //if (!m.m.containsKey(an2)) { m.s(an2, '#') }
                }
            }
        }
        //println(m)
    }

    return antiNodes.size
}

fun main() {
    val dt = measureTime {
        //part0(readInput("08_test"))
        //part0(readInput("08"))

        //check(2 == part1(readInput("08_test2")))
        check(14 == part1(readInput("08t1")))
        println("part 1 answer: ${part1(readInput("08"))}")

        //check(9 == part2(readInput("08_test3")))
        check(34 == part2(readInput("08t1")))
        println("part 2 answer: ${part2(readInput("08"))}")
    }
    println(dt)
}