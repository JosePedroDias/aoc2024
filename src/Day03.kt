fun main() {
    val mulRgx = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
    mulRgx.find("xxxmul(23,7)cenas").let { m ->
        check(m != null && m.groupValues.size == 3)
        check(m.groupValues[0] == "mul(23,7)")
        check(m.groupValues[1] == "23")
        check(m.groupValues[2] == "7")
    }

    val doOrDoNotRgx = Regex("""do(n't)?\(\)""")
    doOrDoNotRgx.find("xxxdo()cenas").let { m ->
        check(m != null && m.groupValues.size == 2)
        check(m.groupValues[0] == "do()")
        check(m.groupValues[1] == "")
    }
    doOrDoNotRgx.find("xxxdon't()cenas").let { m ->
        check(m != null && m.groupValues.size == 2)
        check(m.groupValues[0] == "don't()")
        check(m.groupValues[1] == "n't")
    }

    fun part1(input: List<String>): Int {
        val line = input.joinToString(" ")
        var res = 0
        for (m in mulRgx.findAll(line)) {
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
    check(161 == part1(readInput("Day03_test")))
    println("part 1 answer: ${part1(readInput("Day03"))}")

    fun part2(input: List<String>): Int {
        val line = input.joinToString(" ")
        var res = 0
        val actives = mutableListOf<Int>()
        val inactives = mutableListOf<Int>()

        for (m in doOrDoNotRgx.findAll(line)) {
            val i = m.range.first
            val isDont = m.groupValues[1].length > 0
            //println("$i: isDont:${isDont}")
            if (isDont) {
                inactives.add(i)
            } else {
                actives.add(i)
            }
        }

        for (m in mulRgx.findAll(line)) {
            if (m.groupValues.size > 2) {
                val a = m.groupValues[1].toInt()
                val b = m.groupValues[2].toInt()
                val c = a * b
                val i = m.range.first
                val lastInactive = inactives.findLast { it < i }
                val lastActive = actives.findLast { lastInactive == null || (it in (lastInactive + 1)..<i) }
                val isActive = lastInactive == null || (lastActive != null && lastActive > lastInactive)
                //println("i: $i | lI: $lastInactive | lA: $lastActive")
                //val activeS = if (isActive) "ON" else "OFF"; println("$i: $a * $b = $c ($lastInactive, $lastActive, $activeS)")
                if (isActive) {
                    res += c
                }
            }
        }
        return res
    }
    check(48 == part2(readInput("Day03_test2")))
    println("part 2 answer: ${part2(readInput("Day03"))}")
}
