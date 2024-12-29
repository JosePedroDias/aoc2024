import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.measureTime

private const val START = 'S'
private const val END = 'E'
private const val EMPTY = '.'
private const val WALL = '#'

private const val MOVE_SCORE = 1
private const val TURN_SCORE = 1000

private interface Addable<T> {
    operator fun plus(other: T): T
}

private data class AddableInt(val value: Int) : Addable<AddableInt>, Comparable<AddableInt> {
    override operator fun plus(other: AddableInt): AddableInt {
        return AddableInt(this.value + other.value)
    }

    override operator fun compareTo(other: AddableInt): Int {
        return this.value.compareTo(other.value)
    }
}

private fun <E>dijkstra(
    graph: Map<E, List<Pair<E, Int>>>,
    startEdge: E,
): Map<E, Int>  {
    val distances = mutableMapOf<E, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<E, Int>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<E, Int>>()

    priorityQueue.add(Pair(startEdge, 0))
    distances[startEdge] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist) = priorityQueue.poll()
        if (visited.add(Pair(node, currentDist))) {
            graph[node]?.forEach { (adjacent, weight) ->
                val totalDist = currentDist + weight
                if (totalDist < distances.getValue(adjacent)) {
                    distances[adjacent] = totalDist
                    priorityQueue.add(Pair(adjacent, totalDist))
                }
            }
        }
    }

    return distances
}

private fun <E>augmentedDijkstra(
    graph: Map<E, List<Triple<E, Int, Decision>>>,
    startEdge: E):
        Pair<MutableMap<E, Int>, MutableMap<E, MutableList<Decision>>> {
    val distances = mutableMapOf<E, Int>().withDefault { Int.MAX_VALUE }
    val decisions = mutableMapOf<E, MutableList<Decision>>()
    val priorityQueue = PriorityQueue<Triple<E, Int, MutableList<Decision>>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<E, List<Decision>>>()

    priorityQueue.add(Triple(startEdge, 0, mutableListOf()))
    distances[startEdge] = 0
    decisions[startEdge] = mutableListOf()

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist, currentDecisions) = priorityQueue.poll()
        if (visited.add(Pair(node, currentDecisions))) {
            graph[node]?.forEach { (adjacent, weight, decision) ->
                val totalDist = currentDist + weight
                val totalDecisions = currentDecisions.toMutableList()
                totalDecisions.add(decision)
                val dist = distances.getValue(adjacent)
                if (totalDist < dist) {
                    distances[adjacent] = totalDist
                    decisions[adjacent] = totalDecisions
                    priorityQueue.add(Triple(adjacent, totalDist, totalDecisions))
                }
            }
        }
    }

    return Pair(distances, decisions)
}


private enum class Dir4 { N, E, S, W }

private enum class Decision { F, L, R }

private data class Pos7(val x: Int, val y: Int) {
    fun manhattan(p: Pos7): Int {
        return (x - p.x).absoluteValue + (y - p.y).absoluteValue
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}

private data class DirPos(val dir: Dir4, val pos: Pos7) {
    fun fwd(): DirPos {
        val pos2 = when (dir) {
            Dir4.N -> Pos7(pos.x, pos.y - 1)
            Dir4.E -> Pos7(pos.x + 1, pos.y)
            Dir4.S -> Pos7(pos.x, pos.y + 1)
            Dir4.W -> Pos7(pos.x - 1, pos.y)
        }
        return DirPos(dir, pos2)
    }

    fun left(): DirPos {
        val dir2 = Dir4.entries[(dir.ordinal + Dir4.entries.size - 1) % Dir4.entries.size]
        return DirPos(dir2, pos)
    }

    fun right(): DirPos {
        val dir2 = Dir4.entries[(dir.ordinal + 1) % Dir4.entries.size]
        return DirPos(dir2, pos)
    }

    fun act(d: Decision): DirPos {
        return when (d) {
            Decision.F -> fwd()
            Decision.L -> left()
            Decision.R -> right()
        }
    }
}

private data class Matrix7(val w: Int, val h: Int) {
    private val m: MutableMap<Pos7, Char> = mutableMapOf()
    private var ranges = arrayOf(0..< w, 0..< h)

    operator fun set(p: Pos7, ch: Char) {
        m[p] = ch
    }

    operator fun get(p: Pos7): Char {
        return m.getOrDefault(p, '.')
    }

    fun toString(fn: (p: Pos7) -> Char? = { null }): String {
        val sb = StringBuilder()
        for (y in ranges[1]) {
            for (x in ranges[0]) {
                val pos = Pos7(x, y)
                val ch = fn(pos) ?: this[pos]
                sb.append(ch)
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

private data class Prob16(val mtx: Matrix7, val edges: MutableSet<DirPos>, val startDP: DirPos, val goalPos: Pos7)

private fun parse(lines: List<String>): Prob16 {
    val w = lines[0].length
    val h = lines.size

    val mtx = Matrix7(w, h)
    val edges = mutableSetOf<DirPos>()
    var startDP: DirPos? = null
    var goalPos: Pos7? = null

    lines.forEachIndexed { y, l ->
        l.forEachIndexed { x, ch ->
            val isStart = ch == START
            val isGoal = ch == END
            val isVisitable = ch == EMPTY || isStart || isGoal
            mtx[Pos7(x, y)] = if (isVisitable) EMPTY else WALL
            val p = Pos7(x, y)
            if (isVisitable) {
                for (d in Dir4.entries) {
                    edges.add(DirPos(d, p))
                }
                if (isStart) startDP = DirPos(Dir4.E, p)
                else if (isGoal) goalPos = p
            }
        }
    }

    return Prob16(mtx, edges, startDP!!, goalPos!!)
}

private fun shortestPath(p: Prob16): Int {
    //println(p.mtx.toString { null })

    val graph = mutableMapOf<DirPos, List<Pair<DirPos, Int>>>()
    for (edge in p.edges) {
        val lst = mutableListOf(
            Pair(edge.left(), TURN_SCORE),
            Pair(edge.right(), TURN_SCORE),
        )

        val edgeFwd = edge.fwd()
        if (p.edges.contains(edgeFwd)) {
            lst.add(Pair(edgeFwd, MOVE_SCORE))
        }

        graph[edge] = lst
    }

    val costs = dijkstra(graph, p.startDP)

    val goalEdges = Dir4.entries.map { DirPos(it, p.goalPos) }
    val costsToGoal = goalEdges.map { costs[it]!! }.sorted()

    //println(costsToGoal)
    return costsToGoal[0]
}

private fun shortestPath2(p: Prob16): Int {
    val graph = mutableMapOf<DirPos, List<Triple<DirPos, Int, Decision>>>()
    for (edge in p.edges) {
        val edgeLeft = edge.left()
        val edgeRight = edge.right()
        val edgeFwd = edge.fwd()

        val lst = mutableListOf(
            Triple(edgeLeft, TURN_SCORE, Decision.L),
            Triple(edgeRight, TURN_SCORE, Decision.R),
        )

        if (p.edges.contains(edgeFwd)) {
            lst.add(Triple(edgeFwd, MOVE_SCORE, Decision.F))
        }

        graph[edge] = lst
    }

    val (_, decisions) = augmentedDijkstra(graph, p.startDP)

    val goalEdges = Dir4.entries.map { DirPos(it, p.goalPos) }

    val ways = goalEdges.map { decisions[it]!! }

    val positions = mutableSetOf<Pos7>()
    for (way in ways) {
        var dp = p.startDP
        positions.add(dp.pos)
        for (d in way) {
            dp = dp.act(d)
            positions.add(dp.pos)
        }
    }

    println(p.mtx.toString { p -> if (positions.contains(p)) 'O' else null })
    println(positions.size)

    return positions.size
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("16t1"))
        val oT1 = shortestPath(iT1)
        check(oT1 == 7036)

        val iT2 = parse(readInput("16t2"))
        val oT2 = shortestPath(iT2)
        check(oT2 == 11048)

        val iP = parse(readInput("16"))
        val oP = shortestPath(iP)
        println("Answer to part 1: $oP")

        //

        val oT1b = shortestPath2(iT1)
        //check(oT1b == 45)

        val oT2b = shortestPath2(iT2)
        //check(oT2b == 64)

        val iPb = parse(readInput("16"))
        val oPb = shortestPath2(iPb)
        println("Answer to part 1: $oPb")
    }
    println(dt)
}
