import kotlin.math.absoluteValue

fun <T> List<T>.allExcept(index: Int): List<T> {
    require(index in indices) { "Index out of bounds: $index" }
    return subList(0, index) + subList(index + 1, size)
}

fun main() {
    fun lineToReport(line: String): List<Int> {
        return line.split(" ").map { it.toInt() }
    }
    //println(lineToReport("22 44 -12 2")) // [22, 44, -12, 2]

    fun sign(n: Int): Int = when {
        n < 0 -> -1
        n > 0 -> 1
        else -> 0
    }
    //println("signs: ${sign(-2)}, ${sign(7)}, ${sign(0)}")

    fun isValidReport(report: List<Int>): Boolean {
        val pairs = report.zipWithNext()
        val (a0, b0) = pairs.take(1)[0]
        val diff0 = b0 - a0
        val repSign = sign(diff0)
        return pairs.all { (a, b) ->
            val diff = b - a
            if (repSign != sign(diff)) {
                return false
            }
            val ad = diff.absoluteValue
            val valid = ad in 1..3
            valid // prefixing with return behaves differently. beware!
        }
    }
    check(isValidReport(listOf(2, 4, 7)))
    check(!isValidReport(listOf(2, 5, 9)))
    check(isValidReport(listOf(-2, -4, -7)))
    check(!isValidReport(listOf(-2, -5, -9)))

    fun part1(input: List<String>): Int {
        val reports = input.map { lineToReport(it) }
        return reports.count { isValidReport(it) }
    }
    check(2 == part1(readInput("Day02_test")))
    println("part 1 answer: ${part1(readInput("Day02"))}")

    fun combinationsRemovingOne(el: List<Int>): Sequence <List<Int>> = sequence {
        for (i in el.indices) {
            yield(el.allExcept(i))
        }
    }

    fun isValidReportWithDampener(report: List<Int>): Boolean {
        return combinationsRemovingOne(report).any { isValidReport(it) }
    }
    check(isValidReportWithDampener(listOf(2, 4, 7)))
    check(isValidReportWithDampener(listOf(2, 5, 9)))
    check(!isValidReportWithDampener(listOf(2, 5, 9, 21)))

    fun part2(input: List<String>): Int {
        val reports = input.map { lineToReport(it) }
        return reports.count { isValidReportWithDampener(it) }
    }
    check(4 == part2(readInput("Day02_test")))
    println("part 2 answer: ${part2(readInput("Day02"))}")
}