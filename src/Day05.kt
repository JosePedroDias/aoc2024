fun main() {
    fun part1(input: List<String>, debug: Boolean): Int {
        val orderings = mutableListOf<Pair<Int, Int>>()
        val candidates = mutableListOf<List<Int>>()
        var inHeader = true

        for (line in input) {
            if (line.isEmpty()) {
                inHeader = false
            } else if (inHeader) {
                line.split("|").map { it.toInt() }.also { (a, b) -> orderings.add(Pair(a, b)) }
            } else {
                candidates.add(line.split(",").map { it.toInt() })
            }
        }

        //println("orderings: $orderings")
        //println("candidates: $candidates")

        var sum = 0
        for (candidate in candidates) {
            val valid = orderings.all { (a, b) ->
                val ia = candidate.indexOf(a)
                val ib = candidate.indexOf(b)
                (ia == -1 || ib == -1) || (ia < ib)
            }
            if (valid) {
                val l = candidate.size
                check(l % 2 == 1)
                val middleVal = candidate[l / 2]
                println("+ $middleVal")
                sum += middleVal
            }
        }

        println(sum)

        return sum
    }
    check(143 == part1(readInput("05_test"), false))
    println("part 1 answer: ${part1(readInput("05"), false)}")
}
