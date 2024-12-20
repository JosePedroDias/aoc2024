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

private fun possibilities1(goal: String, alphabet: List<String>, soFar: Int = 0): Int {
    if (soFar == goal.length) return 1
    var sum = 0
    for (al in alphabet) {
        val nxt = soFar + al.length
        if (nxt <= goal.length && goal.substring(soFar, nxt) == al) {
            //println(goal.substring(soFar, nxt))
            sum += possibilities1(goal, alphabet, nxt)
        }
    }
    return sum
}

private tailrec fun possibilities2(
    goal: String,
    alphabet: List<String>,
    stack: List<Int> = listOf(0),
    sum: Int = 0
): Int {
    if (stack.isEmpty()) return sum

    val current = stack.first()
    val rest = stack.drop(1)

    return if (current == goal.length) {
        possibilities2(goal, alphabet, rest, sum + 1)
    } else {
        val nextStates = alphabet.mapNotNull { al ->
            val nxt = current + al.length
            if (nxt <= goal.length && goal.substring(current, nxt) == al) nxt else null
        }
        possibilities2(goal, alphabet, rest + nextStates, sum)
    }
}

private fun part1(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.count { possible(it, alphabet) }
}

private fun part2(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.mapIndexed { idx, c ->
        println("${idx + 1}/${candidates.size}: $c")
        possibilities1(c, alphabet)
    }.sum()
}

fun main() {
    val dt = measureTime {
        val t1 = parse(readInput("19t1"))
        val r1 = part1(t1)
        check(r1 == 6)

        val t = parse(readInput("19"))
        val r = part1(t)
        println("Answer to part 1: $r")

        val r1b = part2(t1)
        check(r1b == 16)

        val rb = part2(t)
        println("Answer to part 2: $rb")
    }
    println(dt)
}
