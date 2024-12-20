import kotlin.time.measureTime

private typealias Puzzle19 = Pair<List<String>, List<String>>

private fun parse(lines: List<String>): Puzzle19 {
    val alphabet = lines[0].split(", ")
    val candidates = lines.takeLast(lines.size - 2)
    return Pair(alphabet, candidates)
}

private fun possible(pattern: String, alphabet: List<String>): Boolean {
    val usefulParts = mutableSetOf<String>()
    for (size in 1..pattern.length) {
        for (j in 0..pattern.length - size) {
            usefulParts.add(pattern.substring(j, j + size))
        }
    }
    val filteredAlphabet = alphabet.filter { usefulParts.contains(it) }
    //println("$pattern from: $filteredAlphabet ?")
    val rgx = Regex("""(?:${filteredAlphabet.joinToString("|")})+""")
    val m = rgx.matchEntire(pattern)
    return m != null
}

private fun part1(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.count { possible(it, alphabet) }
}

fun main() {
    val dt = measureTime {
        val t1 = parse(readInput("19t1"))
        val r1 = part1(t1)
        check(r1 == 6)

        val t = parse(readInput("19"))
        val r = part1(t)
        println("Answer to part 1: $r")
    }
    println(dt)
}
