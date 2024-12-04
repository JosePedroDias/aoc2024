typealias CharMatrix = Array<CharArray>

fun main() {
    val WORD = listOf('X', 'M', 'A', 'S')
    val WORD2 = listOf('M', 'A', 'S')

    fun fillMatrix(input: List<String>): CharMatrix {
        val W = input[0].length
        val H = input.size
        //println("W:$W, H:$H")
        val m = Array(H) { CharArray(W) }
        input.forEachIndexed() {
            y, line ->
            val row = m[y]
            line.forEachIndexed { x, ch -> row[x] = ch }
        }
        return m
    }

    fun matrixToString(m: CharMatrix): String {
        val W = m[0].size
        val H = m.size
        val lines = mutableListOf<String>()
        for (y in 0 until H) {
            val chars = mutableListOf<Char>()
            for (x in 0 until W) {
                chars.add(m[y][x])
            }
            lines.add(chars.joinToString(""))
        }
        return lines.joinToString("\n")
    }

    fun fillMatrix(m: CharMatrix, f: (x: Int, y: Int) -> Char): CharMatrix {
        val W = m[0].size
        val H = m.size
        val m2 = Array(H) { CharArray(W) }
        for (y in 0 until H) {
            for (x in 0 until W) {
                m2[y][x] = f(x, y)
            }
        }
        return m2
    }

    fun part1(input: List<String>, debug: Boolean): Int {
        val m = fillMatrix(input)
        val W = m[0].size
        val H = m.size
        val WS = WORD.size
        var hits = 0

        val cases = listOf(
            Pair( Pair(0,   0  ), Pair( 1,  0) ), // +X
            Pair( Pair(W-1, 0  ), Pair(-1,  0) ), // -X
            Pair( Pair(0,   0  ), Pair( 0,  1) ), // +Y
            Pair( Pair(0,   H-1), Pair( 0, -1) ), // +Y
            Pair( Pair(0,   0  ), Pair( 1,  1) ), // ++DIAGONAL
            Pair( Pair(W-1, H-1), Pair(-1, -1) ), // --DIAGONAL
            Pair( Pair(W-1, 0  ), Pair(-1,  1) ), // -+DIAGONAL
            Pair( Pair(0,   H-1), Pair( 1, -1) ), // +-DIAGONAL
        )

        for ((from, dir) in cases) {
            //println("case: from:$from dir:$dir")
            var (x0, y0) = from
            val (dx, dy) = dir
            //var x1 = if (dx == 0) W - 1 else x0 + dx * (WS-1)
            //var y1 = if (dy == 0) H - 1 else y0 + dy * (WS-1)
            var x1 = if (x0 == 0) W - 1 else 0
            var y1 = if (y0 == 0) H - 1 else 0

            // swap ranges if necessary
            if (x0 > x1) { x0 = x1.also { x1 = x0 } }
            if (y0 > y1) { y0 = y1.also { y1 = y0 } }
            //println("  x0:$x0, dx:$dx, x1:$x1 | y0:$y0, dy:$dy, y1:$y1")

            for (y in y0..y1) {
                for (x in x0..x1) {
                    val found = WORD.indices.all {
                        val xx = x + dx * it
                        val yy = y + dy * it
                        try {
                            m[yy][xx] == WORD[it]
                        } catch (_: Throwable) {
                            false
                        }
                    }
                    if (found) {
                        ++hits
                        if (debug) {
                            //println("HIT dir:$dir pos0:($x,$y)")
                            println("HIT #$hits:")
                            val m2 = fillMatrix(m, fun(xi: Int, yi: Int): Char {
                                for (i in 0 until WS) {
                                    if (xi == x + i * dx && yi == y + i * dy) {
                                        return WORD[i]
                                    }
                                }
                                return '.'
                            })
                            println(matrixToString(m2))
                        }
                    }
                }
            }
        }
        return hits
    }
    check(18 == part1(readInput("04_test"), false))
    println("part 1 answer: ${part1(readInput("04"), false)}")

    fun part2(input: List<String>, debug: Boolean): Int {
        val m = fillMatrix(input)
        val W = m[0].size
        val H = m.size
        var hits = 0

        val cases = listOf(
            Pair( 1,  1), // ++DIAGONAL
            Pair(-1, -1), // --DIAGONAL
            Pair(-1,  1), // -+DIAGONAL
            Pair( 1, -1), // +-DIAGONAL
        )

        for (y in 1 until H-1) {
            for (x in 1 until W-1) {
                var numFound = 0
                for ((dx, dy) in cases) {
                    val x0 = x - dx
                    val y0 = y - dy
                    val found = WORD2.indices.all {
                        val xx = x0 + it * dx
                        val yy = y0 + it * dy
                        WORD2[it] == m[yy][xx]
                    }
                    if (found) {
                        ++numFound
                    }
                }
                if (numFound > 1) {
                    ++hits
                }
            }
        }
        
        return hits
    }
    check(9 == part2(readInput("04_test"), false))
    println("part 2 answer: ${part2(readInput("04"), false)}")
}
