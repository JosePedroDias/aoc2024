import java.util.*
import kotlin.time.measureTime

private typealias Puzzle19 = Pair<List<String>, List<String>>

private fun parse(lines: List<String>): Puzzle19 {
    val alphabet = lines[0].split(", ")
    val candidates = lines.takeLast(lines.size - 2)
    return Pair(alphabet, candidates)
}

// abc -> [a, b, c, ab, bc, abc]
private fun subParts(word: String): Set<String> {
    val usefulParts = mutableSetOf<String>()
    for (size in 1..word.length) {
        for (j in 0..word.length - size) {
            usefulParts.add(word.substring(j, j + size))
        }
    }
    return usefulParts
}

private fun possible(pattern: String, alphabet: List<String>): Boolean {
    val usefulParts = subParts(pattern)
    val filteredAlphabet = alphabet.filter { usefulParts.contains(it) }
    val rgx = Regex("""(?:${filteredAlphabet.joinToString("|")})+""")
    val m = rgx.matchEntire(pattern)
    return m != null
}

private fun possibilities0(goal: String, alphabet: List<String>, soFar: Int = 0): Int {
    if (soFar == goal.length) return 1
    var sum = 0
    for (al in alphabet) {
        val nxt = soFar + al.length
        if (nxt <= goal.length && goal.substring(soFar, nxt) == al) {
            //println(goal.substring(0, nxt))
            sum += possibilities0(goal, alphabet, nxt)
        }
    }
    return sum
}

private fun possibilities1(goal: String, alphabet: List<String>): Int {
    var hitCount = 0
    val goalSize = goal.length
    val soFars = ArrayDeque(listOf(0))
    while (soFars.size > 0) {
        val soFar = soFars.poll()!!
        for (al in alphabet) {
            //print("\nsoFar:$soFar, al:$al")
            val nxt = soFar + al.length
            if (nxt > goalSize) continue
            val testStr = goal.substring(0, soFar) + al
            if (nxt == goalSize) {
                if (goal == testStr) { ++hitCount /*; print(" HIT!")*/ }
            }
            else if (goal.substring(soFar, nxt) == al) soFars.add(nxt)
        }
    }
    return hitCount
}

private fun part1(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.count { possible(it, alphabet) }
}

private fun part2(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.mapIndexed { idx, c ->
        println("${idx + 1}/${candidates.size}: $c")
        possibilities0(c, alphabet)
        //possibilities1(c, alphabet)
    }.sum()
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
        check(oT1b == 16, { "$oT1b != 16" })

        val oP2 = part2(iP)
        println("Answer to part 2: $oP2")
    }
    println(dt)
}
