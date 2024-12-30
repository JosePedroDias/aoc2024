import kotlin.math.abs
import kotlin.time.measureTime

private data class Pos8(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }

    fun neighbours(): List<Pos8> {
        return listOf(
            Pos8(x-1, y),
            Pos8(x+1, y),
            Pos8(x, y-1),
            Pos8(x, y+1),
        )
    }
}

private data class Matrix8(val w: Int, val h: Int, val forecast: List<Pos8>) {
    private var ranges = arrayOf(0..< w, 0..< h)

    fun isVisitable(p: Pos8, t: Int): Boolean {
        if (p.x !in ranges[0] || p.y !in ranges[1]) return false
        val corrupted = forecast.subList(0, t).toSet()
        return !corrupted.contains(p)
    }

    fun visitablePositions(t: Int) = sequence {
        val corrupted = forecast.subList(0, t).toSet()
        for (y in ranges[1]) {
            for (x in ranges[0]) {
                val pos = Pos8(x, y)
                if (!corrupted.contains(pos)) yield(pos)
            }
        }
    }

    fun toString(t: Int, path: List<Pos8> = emptyList()): String {
        val visitable = visitablePositions(t)
        val sb = StringBuilder()
        for (y in ranges[1]) {
            for (x in ranges[0]) {
                val pos = Pos8(x, y)
                val ch = if (path.contains(pos)) 'O' else if (visitable.contains(pos)) '.' else '#'
                sb.append(ch)
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

private fun parse(lines: List<String>, size: Int): Matrix8 {
    val forecast = lines.map { it ->
        val (x, y) = it.split(",").map { it.toInt() }
        Pos8(x, y)
    }
    //println("forecast size: ${forecast.size}")
    return Matrix8(size, size, forecast)
}

private fun manhattan(a: Pos8, b: Pos8): Int {
    return abs(a.x - b.x) + abs(a.y - b.y)
}

const val MAX_SCORE = 2024

// https://rosettacode.org/wiki/A*_search_algorithm#Kotlin
private fun navigate(m: Matrix8, t: Int, startP: Pos8, goalP: Pos8): Pair<List<Pos8>, Int> {
    val valid = m.visitablePositions(t)

    fun generatePath(currentPos: Pos8, cameFrom: Map<Pos8, Pos8>): List<Pos8> {
        val path = mutableListOf(currentPos)
        var current = currentPos
        while (cameFrom.containsKey(current)) {
            current = cameFrom.getValue(current)
            path.add(0, current)
        }
        return path.toList()
    }

    val openVertices = mutableSetOf(startP)
    val closedVertices = mutableSetOf<Pos8>()
    val costFromStart = mutableMapOf(startP to 0)
    val estimatedTotalCost = mutableMapOf(startP to manhattan(startP, goalP))
    val cameFrom = mutableMapOf<Pos8, Pos8>()

    while (openVertices.size > 0) {
        val currentPos = openVertices.minBy { estimatedTotalCost.getValue(it) }
        if (currentPos == goalP) {
            val path = generatePath(currentPos, cameFrom)
            return Pair(path, estimatedTotalCost.getValue(goalP))
        }
        openVertices.remove(currentPos)
        closedVertices.add(currentPos)
        currentPos.neighbours()
            .filterNot { closedVertices.contains(it) }
            .filter { valid.contains(it) }
            .forEach { neighbour ->
                val score = costFromStart.getValue(currentPos) + 1 //moveCost(currentPos, neighbour)
                if (score < costFromStart.getOrDefault(neighbour, MAX_SCORE)) {
                    if (!openVertices.contains(neighbour)) openVertices.add(neighbour)
                    cameFrom[neighbour] = currentPos
                    costFromStart[neighbour] = score
                    estimatedTotalCost[neighbour] = score + manhattan(neighbour, goalP)
                }
            }
    }
    throw IllegalArgumentException("no path from start $startP to goal $goalP")
}

fun main() {
    val dt = measureTime {
        val tT1 = 12
        val sizeT1 = 7
        val mT1 = parse(readInput("18t1"), sizeT1)
        val (pathT1, costT1) = navigate(
            mT1,
            tT1,
            Pos8(0, 0),
            Pos8(sizeT1-1, sizeT1-1),
        )
        //println(mT1.toString(tT1, pathT1))
        check(costT1 == 22)

        val tP = 1024
        val sizeP = 71
        val mP = parse(readInput("18"), sizeP)
        val (pathP, costP) = navigate(
            mP,
            tP,
            Pos8(0, 0),
            Pos8(sizeP-1,sizeP-1),
        )
        //println(mP.toString(tP, pathP))
        println("Answer to part 1: $costP")
    }
    println(dt)
}
