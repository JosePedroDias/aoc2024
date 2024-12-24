import kotlin.time.measureTime

private fun parse(lines: List<String>): List<Pair<String, String>> {
    return lines.map {
        val (a, b) = it.split('-')
        Pair(a, b)
    }
}

private fun part1(ins: List<Pair<String, String>>): Int {
    val connected = mutableMapOf<String, MutableSet<String>>()
    for ((a, b) in ins) {
        connected.getOrPut(a, { mutableSetOf() }).add(b)
        connected.getOrPut(b, { mutableSetOf() }).add(a)
    }

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
    return triples.size
}

private fun buildAdjacencyList(edges: List<Pair<Int, Int>>): Map<Int, MutableSet<Int>> {
    val adjacencyList = mutableMapOf<Int, MutableSet<Int>>()
    for ((u, v) in edges) {
        adjacencyList.computeIfAbsent(u) { mutableSetOf() }.add(v)
        adjacencyList.computeIfAbsent(v) { mutableSetOf() }.add(u)
    }
    return adjacencyList
}

// had chatgpt help me identify this algorithm as the tool to work out full connectivity
// https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
private fun bronKerbosch(
    R: Set<Int>,
    P: MutableSet<Int>,
    X: MutableSet<Int>,
    adjacencyList: Map<Int, Set<Int>>,
    cliques: MutableList<Set<Int>>
) {
    if (P.isEmpty() && X.isEmpty()) {
        cliques.add(R)
        return
    }
    val PCopy = P.toMutableSet()
    for (node in PCopy) {
        bronKerbosch(
            R + node,
            P.intersect((adjacencyList[node] ?: emptySet()).toSet()).toMutableSet(),
            X.intersect((adjacencyList[node] ?: emptySet()).toSet()).toMutableSet(),
            adjacencyList,
            cliques
        )
        P.remove(node)
        X.add(node)
    }
}

private fun findMaximalCliques(edges: List<Pair<Int, Int>>): List<Set<Int>> {
    val adjacencyList = buildAdjacencyList(edges)
    val allNodes = adjacencyList.keys.toMutableSet()
    val cliques = mutableListOf<Set<Int>>()
    bronKerbosch(emptySet(), allNodes, mutableSetOf(), adjacencyList, cliques)
    return cliques
}

private fun part2(edgesS: List<Pair<String, String>>): String {
    val nodes = mutableSetOf<String>()
    val s2i = mutableMapOf<String, Int>()
    val i2s = mutableMapOf<Int, String>()
    for ((a, b) in edgesS) {
        nodes.add(a)
        nodes.add(b)
    }
    nodes.forEachIndexed { i, s ->
        s2i[s] = i
        i2s[i] = s
    }

    val edges = edgesS.map { (a, b) -> Pair(s2i[a]!!, s2i[b]!!) }
    val cliques = findMaximalCliques(edges)

    val mt = MaxTracker<Set<Int>, Int>( { it.size }, Int.MIN_VALUE )
    cliques.forEach { mt.add(it) }

    val maxItem = mt.maxItem!!.toList().map { i2s[it]!! }
    val maxItemString = maxItem.sorted().joinToString(",")

    return maxItemString
}

fun main() {
    val dt = measureTime {
        val inT1 = parse(readInput("23t1"))
        val inP = parse(readInput("23"))

        // part 1
        val outT1 = part1(inT1)
        check(outT1 == 7)

        val outP = part1(inP)
        println("Answer of part 1: $outP")

        // part 2
        val outT1b = part2(inT1)
        check(outT1b == "co,de,ka,ta")

        val outPb = part2(inP)
        println("Answer of part 1: $outPb")
    }
    println(dt)
}
