fun main() {
    val rgx = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

    fun part1(input: List<String>): Int {
        var res = 0
        for (line in input) {
            for (m in rgx.findAll(line)) {
                if (m.groupValues.size > 2) {
                    val a = m.groupValues[1].toInt()
                    val b = m.groupValues[2].toInt()
                    val c = a * b
                    //println("$a * $b = $c")
                    res += c
                }
            }
        }
        return res
    }
    check(161 == part1(readInput("Day03_test")))
    println("part 1 answer: ${part1(readInput("Day03"))}")

    fun part2(input: List<String>): Int {
        var res = 0
        for (line in input) {
            for (m in rgx.findAll(line)) {
                if (m.groupValues.size > 2) {
                    val a = m.groupValues[1].toInt()
                    val b = m.groupValues[2].toInt()
                    val c = a * b
                    //println("$a * $b = $c")
                    res += c
                }
            }
        }
        return res
    }
    check(161 == part2(readInput("Day03_test")))
    println("part 2 answer: ${part2(readInput("Day03"))}")
}