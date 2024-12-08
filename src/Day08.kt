import kotlin.time.measureTime

private data class Vec2 (var x: Int, var y: Int)

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

    fun allHaving(c: T): List<Vec2> {
        val all = mutableListOf<Vec2>()
        for (y in 0..< h) {
            for (x in 0..< w) {
                val p = Vec2(x, y)
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
    println(mAll)

    val frequencies = mAll.m.values.toSet()
    println(frequencies)

    val mFreqs = mutableMapOf<Char, Matrix2<Boolean>>()
    for (f in frequencies) {
        val m = Matrix2<Boolean>(w, h)
        for (p in mAll.allHaving(f)) {
            m.s(p, true)
        }
        mFreqs[f] = m
    }

    println(mFreqs)

    return 0
}

private fun part2(input: List<String>): Int {
    return 0
}

fun main() {
    val dt = measureTime {
        part0(readInput("08_test"))
        part0(readInput("08"))

        check(14 == part1(readInput("08_test")))
        //println("part 1 answer: ${part1(readInput("08"))}")

        //check(11387L == part2(readInput("08_test")))
        //println("part 2 answer: ${part2(readInput("08"))}")
    }
    println(dt) // 4.6s to 1.1s after parallelism kicks in
}