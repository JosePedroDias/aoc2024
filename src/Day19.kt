import kotlin.time.measureTime

private typealias Puzzle19 = Pair<Set<String>, List<String>>

private fun parse(lines: List<String>): Puzzle19 {
    val alphabet = lines[0].split(", ").toSet()
    val candidates = lines.takeLast(lines.size - 2)
    return Pair(alphabet, candidates)
}

private fun part1(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    val maxWordSize = alphabet.maxOf { it.length }

    val cache = mutableMapOf<String, Boolean>()
    fun canObtain(design: String): Boolean {
        if (cache.containsKey(design)) return cache[design]!!
        if (design.isEmpty()) {
            cache[design] = true
            return true
        }
        for (i in 0..minOf(design.length, maxWordSize)) {
            if (alphabet.contains(design.substring(0, i)) && canObtain(design.substring(i))) {
                cache[design] = true
                return true
            }
        }
        cache[design] = false
        return false
    }

    return candidates.count { canObtain(it) }
}

private fun part2(puzzle: Puzzle19): Long {
    val (alphabet, candidates) = puzzle
    val maxWordSize = alphabet.maxOf { it.length }

    val cache = mutableMapOf<String, Long>()
    fun countHits(design: String): Long {
        if (cache.containsKey(design)) return cache[design]!!
        if (design.isEmpty()) {
            cache[design] = 1L
            return 1L
        }
        var hits = 0L
        for (i in 0..minOf(design.length, maxWordSize)) {
            if (alphabet.contains(design.substring(0, i))) {
                hits += countHits(design.substring(i))
            }
        }
        cache[design] = hits
        return hits
    }

    return candidates.fold(0L, { acc, c -> countHits(c) + acc })
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("19t1"))
        val oT1 = part1(iT1)
        check(oT1 == 6)

        val iP = parse(readInput("19"))
        val oP = part1(iP)
        println("Answer to part 1: $oP")

        val oT1b = part2(iT1)
        check(oT1b == 16L, { "$oT1b != 16" })

        val oP2 = part2(iP)
        println("Answer to part 2: $oP2")
    }
    println(dt)
}
