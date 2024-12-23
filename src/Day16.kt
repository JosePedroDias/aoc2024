import kotlin.math.absoluteValue
import kotlin.time.measureTime

private const val START = 'S'
private const val END = 'E'
private const val WALL = '#'
private const val EMPTY = '.'

private const val MOVE_SCORE = 1
private const val TURN_SCORE = 1000

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

private data class State2(val m: Matrix7, val pos: Pos7, val posE: Pos7, val dir: Dir4, val score: Int, val decisions: List<Decision>) {
    fun moveForward(): State2 {
        val pos2 = when (dir) {
            Dir4.N -> Pos7(pos.x, pos.y - 1)
            Dir4.E -> Pos7(pos.x + 1, pos.y)
            Dir4.S -> Pos7(pos.x, pos.y + 1)
            Dir4.W -> Pos7(pos.x - 1, pos.y)
        }
        val de = decisions.toMutableList(); de.add(Decision.F)
        return State2(m, pos2, posE, dir, score + MOVE_SCORE, de)
    }

    fun turnLeft(): State2 {
        val dir2 = Dir4.entries[(dir.ordinal + Dir4.entries.size - 1) % Dir4.entries.size]
        val de = decisions.toMutableList(); de.add(Decision.L)
        return State2(m, pos, posE, dir2, score + TURN_SCORE, de)
    }

    fun turnRight(): State2 {
        val dir2 = Dir4.entries[(dir.ordinal + 1) % Dir4.entries.size]
        val de = decisions.toMutableList(); de.add(Decision.R)
        return State2(m, pos, posE, dir2, score + TURN_SCORE, de)
    }

    fun isValid(): Boolean {
        return m[pos] != WALL
    }

    fun isGoal(): Boolean {
        return pos == posE
    }

    override fun toString(): String {
        return m.toStringSpecial {
            p ->
            if (p == pos) when(dir) {
                Dir4.N -> '^'
                Dir4.E -> '>'
                Dir4.S -> 'v'
                Dir4.W -> '<'
            }
            else null }
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

    fun find(chTarget: Char): Pos7? {
        for ((p, ch) in m.entries) {
            if (ch == chTarget) {
                return p
            }
        }
        return null
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in ranges[1]) {
            for (x in ranges[0]) {
                sb.append(this[Pos7(x, y)])
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    fun toStringSpecial(fn: (p: Pos7) -> Char?): String {
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

private fun parse(lines: List<String>): State2 {
    val w = lines[0].length
    val h = lines.size
    val m = Matrix7(w, h)
    lines.forEachIndexed { y, l ->
        l.forEachIndexed { x, ch -> m[Pos7(x, y)] = ch }
    }

    val posS = m.find(START)
    check(posS != null)
    m[posS] = EMPTY

    val posE = m.find(END)
    check(posE != null)
    m[posE] = EMPTY

    return State2(m, posS, posE, Dir4.E, 0, listOf())
}

private fun part1(st: State2, debug: Boolean = false): Int {
    val visitedHeadings = mutableSetOf<Pair<Pos7, Dir4>>()
    val todo = mutableListOf(st)
    val successStates =  mutableListOf<State2>()
    if (debug) println("start pos: ${st.pos}")

    while (todo.size > 0) {
        val currentSt = todo.removeFirst()
        // if (debug) println("todo:${todo.size}|ss:${successStates.size}\n$currentSt")
        val candidates = listOf(
            currentSt.moveForward(),
            currentSt.turnLeft(),
            currentSt.turnRight(),
        ).filter {
            if (visitedHeadings.contains(Pair(it.pos, it.dir))) false
            else if (!it.isValid()) false
            else if (it.isGoal()) {
                successStates.add(it)
                false
            }
            else true
        }
        val pairs = candidates.map { Pair(it, it.pos.manhattan(currentSt.posE)) }
        pairs.sortedBy { it.second }
        val sortedCandidates = pairs.map { it.first }
        visitedHeadings.add(Pair(currentSt.pos, currentSt.dir))
        todo += sortedCandidates
    }

    check(successStates.size > 0)
    //for (ss in successStates) { println("- ${ss.score}: ${ss.decisions}") }

    //if (debug) println(successStates)
    successStates.sortBy { it.score }
    val bestState = successStates.first()
    if (debug) println(bestState)
    if (debug) println("best score: ${bestState.score} (out of ${successStates.size})")
    if (debug) println("best decisions: ${bestState.decisions}")

    if (false) {
        var currentSt2 = st
        println("\n***********\nwinning combination:\n")
        //println(currentSt2)
        bestState.decisions.forEachIndexed { idx, decision ->
            println("\n#$idx $decision:")
            currentSt2 = when (decision) {
                Decision.F -> currentSt2.moveForward()
                Decision.L -> currentSt2.turnLeft()
                Decision.R -> currentSt2.turnRight()
            }
            println(currentSt2)
        }
    }

    return bestState.score
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("16t1"))
        val oT1 = part1(iT1)
        check(oT1 == 7036)

        val iT2 = parse(readInput("16t2"))
        val oT2 = part1(iT2)
        check(oT2 == 11048)

        val i1 = parse(readInput("16"))
        val o1 = part1(i1, true)
        println("Answer to part 1: $o1") // 92364 X 10028 X*/
    }
    println(dt)
}
