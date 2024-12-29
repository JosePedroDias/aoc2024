import java.util.*
import kotlin.time.measureTime

private const val START = 'S'
private const val END = 'E'
private const val EMPTY = '.'
private const val WALL = '#'

private const val MOVE_SCORE = 1
private const val TURN_SCORE = 1000

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

private fun augmentedDijkstra(
    graph: Map<DirPos, List<Triple<DirPos, Int, Pos7>>>,
    startEdge: DirPos):
        Pair<MutableMap<DirPos, Int>, MutableMap<DirPos, MutableSet<Pos7>>>
{
    val distances = mutableMapOf<DirPos, Int>().withDefault { Int.MAX_VALUE }
    val positions = mutableMapOf<DirPos, MutableSet<Pos7>>()
    val priorityQueue = PriorityQueue<Triple<DirPos, Int, MutableSet<Pos7>>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<DirPos, Int>>()

    priorityQueue.add(Triple(startEdge, 0, mutableSetOf(startEdge.pos)))
    distances[startEdge] = 0
    positions[startEdge] = mutableSetOf(startEdge.pos)

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist, currentPositions) = priorityQueue.poll()
        if (visited.add(Pair(node, currentDist))) {
            graph[node]?.forEach { (adjacent, weight, pos) ->
                val totalDist = currentDist + weight
                val totalPos = currentPositions.toMutableSet()
                totalPos.add(pos)
                val dist = distances.getValue(adjacent)
                if (totalDist < dist) {
                    distances[adjacent] = totalDist
                    positions[adjacent] = totalPos
                    priorityQueue.add(Triple(adjacent, totalDist, totalPos))
                }
            }
        }
    }

    return Pair(distances, positions)
}


private enum class Dir4 { N, E, S, W }

private data class Pos7(val x: Int, val y: Int) {
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
    val graph = mutableMapOf<DirPos, List<Triple<DirPos, Int, Pos7>>>()
    for (edge in p.edges) {
        val edgeLeft = edge.left()
        val edgeRight = edge.right()
        val edgeFwd = edge.fwd()

        val lst = mutableListOf(
            Triple(edgeLeft, TURN_SCORE, edgeLeft.pos),
            Triple(edgeRight, TURN_SCORE, edgeRight.pos),
        )

        if (p.edges.contains(edgeFwd)) {
            lst.add(Triple(edgeFwd, MOVE_SCORE, edgeFwd.pos))
        }

        graph[edge] = lst
    }

    val (_, ways) = augmentedDijkstra(graph, p.startDP)

    val goalEdges = Dir4.entries.map { DirPos(it, p.goalPos) }
    val positions = mutableSetOf<Pos7>()
    for (ge in goalEdges) {
        val way = ways[ge]!!
        for (pos in way) positions.add(pos)
    }

    println(p.mtx.toString { pos -> if (positions.contains(pos)) 'O' else null })
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

        //return

        val oT2b = shortestPath2(iT2)
        //check(oT2b == 64)

        val iPb = parse(readInput("16"))
        val oPb = shortestPath2(iPb)
        println("Answer to part 1: $oPb")
    }
    println(dt)
}
