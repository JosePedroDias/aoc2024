import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    fun extractInput(input: List<String>): Pair<MutableList<Pair<Int, Int>>, MutableList<List<Int>>> {
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

        return Pair(orderings, candidates)
    }

    fun part1(input: List<String>): Int {
        val (orderings, candidates) = extractInput(input)

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
                //println("+ $middleVal")
                sum += middleVal
            }
        }

        return sum
    }
    check(143 == part1(readInput("05_test")))
    println("part 1 answer: ${part1(readInput("05"))}")

    fun part2(input: List<String>): Int {
        val (orderings, candidates) = extractInput(input)

        var sum = 0
        for (candidate in candidates) {
            val valid = orderings.all { (a, b) ->
                val ia = candidate.indexOf(a)
                val ib = candidate.indexOf(b)
                (ia == -1 || ib == -1) || (ia < ib)
            }

            if (valid) {
                continue
            }

            println("was $candidate")

            var candidate2 = candidate.toMutableList()

            for (j in 0 until 5) { // this is a big hack! O:)
                for ((a, b) in orderings) {
                    val ia = candidate2.indexOf(a)
                    val ib = candidate2.indexOf(b)
                    if (ia != -1 && ib != -1 && ia > ib) {
                        candidate2[ia] = b
                        candidate2[ib] = a
                        println("  rule $a|$b -> $candidate2")
                    }
                }
            }
            val l = candidate2.size
            check(l % 2 == 1)
            val middleVal = candidate2[l / 2]
            println("+ $middleVal")
            sum += middleVal
        }

        return sum
    }
    check(123 == part2(readInput("05_test"))) // 4633, 4979: too low | 6498: too high
    println("part 2 answer: ${part2(readInput("05"))}")
}
