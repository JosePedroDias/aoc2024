import kotlin.math.pow
import kotlin.time.measureTime

private typealias Mem = MutableMap<String, Int?>

private data class Tuple4(val a: String, val op: String, val b: String, val c: String)

private fun parse(lines: List<String>): Pair<Mem, List<Tuple4>> {
    var atSecondSection = false
    val mem = mutableMapOf<String, Int?>()
    val wirings = mutableListOf<Tuple4>()

    for (l in lines) {
        if (!atSecondSection) {
            if (l.isEmpty()) {
                atSecondSection = true
            } else {
                val (name, value) = l.split(": ")
                mem[name] = value.toInt()
            }
        } else {
            val (a, op, b, arrow, c) = l.split(" ")
            wirings.add(Tuple4(a, op, b, c))
        }
    }

    return Pair(mem, wirings)
}

private fun And(a: Int?, b: Int?): Int? {
    if (a == null || b == null) return null
    if (a == 1 && b == 1) return 1
    return 0
}

private fun Or(a: Int?, b: Int?): Int? {
    if (a == null || b == null) return null
    if (a == 1 || b == 1) return 1
    return 0
}

private fun Xor(a: Int?, b: Int?): Int? {
    if (a == null || b == null) return null
    if (a != b) return 1
    return 0
}

private fun zedVars(count: Int) = sequence {
    for (i in 0 ..< count) {
        yield("z${i.toString().padStart(2, '0')}")
    }
}

private fun isFilled(mem: Mem, count: Int): Boolean {
    for (varName in zedVars(count)) {
        if (mem[varName] == null) {
            //println("$varName is null. returning false")
            return false
        }
    }
    return true
}

private fun getValue(mem: Mem, count: Int): Long {
    var res = 0L
    for ((i, varName) in zedVars(count).withIndex()) {
        res += (2.0.pow(i) * mem[varName]!!).toLong()
    }
    return res
}

private fun Run(pair: Pair<Mem, List<Tuple4>>, count: Int): Long {
    var memPrev = pair.first
    val tuples = pair.second
    var mem: Mem

    while (!isFilled(memPrev, count)) {
        mem = memPrev.toMutableMap()
        //println(mem)
        for ((a, op, b, c) in tuples) {
            val vA = mem[a]
            val vB = mem[b]
            val vC = when (op) {
                "AND" -> And(vA, vB)
                "OR" -> Or(vA, vB)
                "XOR" -> Xor(vA, vB)
                else -> throw Error("OOPS")
            }
            mem[c] = vC
        }
        //println(mem)
        memPrev = mem
    }

    return getValue(memPrev, count)
}

fun main() {
    val dt = measureTime {
        val v = Run(parse(readInput("24t1")), 3); check(v == 4L)
        val v2 = Run(parse(readInput("24t2")), 12); check(v2 == 2024L)
        val r = parse(readInput("24"))
        val v3 = Run(r, 46)
        println("Answer to part 1: $v3")
    }
    println(dt)
}
