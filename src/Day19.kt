import kotlin.time.measureTime

private typealias Puzzle19 = Pair<List<String>, List<String>>

private fun parse(lines: List<String>): Puzzle19 {
    val alphabet = lines[0].split(", ")
    val candidates = lines.takeLast(lines.size - 2)
    return Pair(alphabet, candidates)
}

private fun part1(p: Puzzle19): Int {
    println(p)
    // TODO
    return 0
}

fun main() {
    val dt = measureTime {
        val t1 = parse(readInput("19t1"))
        val r1 = part1(t1)
        println(r1)
        //val p = parse(readInput("19"))
    }
    println(dt)
}
