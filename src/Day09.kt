import kotlin.time.measureTime

private fun part1(lines: List<String>): Int {
    return 0
}

private fun part2(lines: List<String>): Int {
    return 0
}

fun main() {
    val dt = measureTime {
        check(9999 == part1(readInput("09_test")))
        println("part 1 answer: ${part1(readInput("09"))}")

        check(9999 == part2(readInput("09_test")))
        println("part 2 answer: ${part2(readInput("09"))}")
    }
    println(dt)
}