private val rgxMul = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
private val rgx = Regex("""mul\((\d{1,3}),(\d{1,3})\)|do(n't)?\(\)""")

private fun part1(input: List<String>): Int {
    val line = input.joinToString(" ")
    var res = 0
    for (m in rgxMul.findAll(line)) {
        if (m.groupValues.size > 2) {
            val a = m.groupValues[1].toInt()
            val b = m.groupValues[2].toInt()
            val c = a * b
            //println("$a * $b = $c")
            res += c
        }
    }
    return res
}

private fun part2(input: List<String>): Int {
    val line = input.joinToString(" ")
    var res = 0
    var active = true
    for (m in rgx.findAll(line)) {
        if (m.groupValues.size > 3) {
            when {
                m.groupValues[0] == "do()" -> active = true
                m.groupValues[0] == "don't()" -> active = false
                active -> {
                    val (a, b) = listOf(m.groupValues[1], m.groupValues[2]).map(String::toInt)
                    val c = a * b
                    //println("$a * $b = $c")
                    res += c
                }
            }
        }
    }
    return res
}

fun main() {
    rgxMul.find("xxxmul(23,7)cenas").let { m ->
        check(m != null && m.groupValues.size == 3)
        check(m.groupValues[0] == "mul(23,7)")
        check(m.groupValues[1] == "23")
        check(m.groupValues[2] == "7")
    }

    rgx.find("xxxmul(23,7)cenas").let { m ->
        check(m != null && m.groupValues.size == 4)
        check(m.groupValues[0] == "mul(23,7)")
        check(m.groupValues[1] == "23")
        check(m.groupValues[2] == "7")
    }
    rgx.find("xxxdo()cenas").let { m ->
        check(m != null && m.groupValues.size == 4)
        check(m.groupValues[0] == "do()")
    }
    rgx.find("xxxdon't()cenas").let { m ->
        check(m != null && m.groupValues.size == 4)
        check(m.groupValues[0] == "don't()")
    }

    check(161 == part1(readInput("03_test")))
    println("part 1 answer: ${part1(readInput("03"))}")

    check(48 == part2(readInput("03_test2")))
    println("part 2 answer: ${part2(readInput("03"))}")
}
