import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.measureTime

private const val START = 'S'
private const val END = 'E'
private const val EMPTY = '.'
private const val WALL = '#'

private const val MOVE_SCORE = 1
private const val TURN_SCORE = 1000

private val edges = mutableSetOf<DirPos>()

// graph: fromId, list <to, cost>
// start: id of the start edge
private fun dijkstra(graph: Map<Int, List<Pair<Int, Int>>>, start: Int): Map<Int, Int> {
    val distances = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<Int, Int>>()

    priorityQueue.add(Pair(start, 0))
    distances[start] = 0

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

    val edgeToId = mutableMapOf<DirPos, Int>()
    val idToEdge = mutableMapOf<Int, DirPos>()
    p.edges.toList().forEachIndexed { id, edge ->
        edgeToId[edge] = id
        idToEdge[id] = edge
    }

    val graph = mutableMapOf<Int, List<Pair<Int, Int>>>()
    for (edge in p.edges) {
        val lst = mutableListOf(
            Pair(edgeToId[edge.left()]!!, TURN_SCORE),
            Pair(edgeToId[edge.right()]!!, TURN_SCORE),
        )

        val edgeFwd = edge.fwd()
        if (edgeToId.containsKey(edgeFwd)) {
            lst.add(Pair(edgeToId[edgeFwd]!!, MOVE_SCORE))
        }

        graph[edgeToId[edge]!!] = lst
    }

    val costs = dijkstra(graph, edgeToId[p.startDP]!!)

    val goalEdges = Dir4.entries.map { DirPos(it, p.goalPos) }
    val goalEdgeIds = goalEdges.map { edgeToId[it]!! }
    val costsToGoal = goalEdgeIds.map { costs[it]!! }.sorted()

    //println(costsToGoal)
    return costsToGoal[0]
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("16t1"))
        //println(iT1.mtx.toString { null })
        //println(iT1.edges.size)
        //println(iT1.startDP)
        //println(iT1.goalPos)
        val oT1 = shortestPath(iT1)
        check(oT1 == 7036)

        val iT2 = parse(readInput("16t2"))
        val oT2 = shortestPath(iT2)
        check(oT2 == 11048)

        val iP = parse(readInput("16"))
        val o1 = shortestPath(iP)
        println("Answer to part 1: $o1")
    }
    println(dt)
}
