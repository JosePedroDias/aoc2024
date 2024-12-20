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

private fun possibilities1(goal: String, alphabet: List<String>, soFar: String = ""): Int {
    if (soFar == goal) return 1
    if (soFar.length > goal.length) return 0
    var sum = 0
    for (al in alphabet) {
        val nxt = "$soFar$al"
        if (
            //soFar.length + al.length <= goal.length &&
            goal.startsWith(nxt)) {
            sum += possibilities1(goal, alphabet, nxt)
        }
    }
    return sum
}

private fun possibilities2(goal: String, alphabet: List<String>): Int {
    var sum = 0
    val queue = ArrayDeque<String>()
    queue.add("")

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        when {
            current == goal -> sum += 1
            goal.startsWith(current) -> {
                for (al in alphabet) {
                    val next = current + al
                    if (goal.startsWith(next)) {
                        queue.add(next)
                    }
                }
            }
        }
    }

    return sum
}


private fun part1(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.count { possible(it, alphabet) }
}

private fun part2(puzzle: Puzzle19): Int {
    val (alphabet, candidates) = puzzle
    return candidates.mapIndexed { idx, c ->
        println("${idx + 1}/${candidates.size}: $c")
        possibilities2(c, alphabet)
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
