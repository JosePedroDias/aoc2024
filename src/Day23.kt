import kotlin.time.measureTime

private fun parse(lines: List<String>): List<Set<String>> {
    return lines.map {
        val (a, b) = it.split('-')
        setOf(a, b)
    }
}

private fun part1(ins: List<Set<String>>): Int {
    val connected = mutableMapOf<String, MutableSet<String>>()
    for (set in ins) {
        val (a, b) = set.toList()
        connected.getOrPut(a, { mutableSetOf() }).add(b)
        connected.getOrPut(b, { mutableSetOf() }).add(a)
    }
    //println("connected")
    //println(connected)
    //println(connected.size)

    val triples = mutableSetOf<Set<String>>()
    for ((a, to_) in connected) {
        val to = to_.toList()
        val vIndices = combinations(to.size)
        for ((i0, i1) in vIndices) {
            val b = to[i0]
            val c = to[i1]
            if (connected[b]?.contains(c) == true) {
                if (a[0] == 't' || b[0] == 't' || c[0] == 't') {
                    val ss = setOf(a, b, c)
                    triples.add(ss)
                }
            }
        }
    }
    //println("triples")
    //println(triples)
    //println(triples.size)
    return triples.size
}

fun main() {
    val dt = measureTime {
        val inT1 = parse(readInput("23t1"))
        val outT1 = part1(inT1)
        check(outT1 == 7)

        val inP = parse(readInput("23"))
        val outP = part1(inP)
        println("Answer of part 1: $outP")
    }
    println(dt)
}
