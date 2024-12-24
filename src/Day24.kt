import java.io.File
import kotlin.math.pow
import kotlin.time.measureTime

private typealias Mem = MutableMap<String, Int?>

private data class Tuple4(val a: String, val op: String, val b: String, val c: String)


private fun dotGraph(pair: Pair<Mem, List<Tuple4>>): String {
    val (mem, tuples) = pair

    val sb = StringBuilder()

    // start
    sb.append("""digraph {
    rankdir=LR;
    splines=ortho;
    node [style=filled, fontname="Helvetica"];
    edge [fontname="Helvetica"];
""")

    // nodes
    sb.append("""
    // NODES
    
""")

    tuples.forEachIndexed { i, t ->
        val style = when (t.op) {
            "AND" -> """label="AND", shape=box, fillcolor=lightyellow"""
            "OR" -> """label="OR", shape=diamond, fillcolor=lightpink"""
            "XOR" -> """label="XOR", shape=ellipse, fillcolor=lightblue"""
            else -> throw Error("OOPS")
        }
        sb.append("    GATE$i [$style];\n")
    }

    // edges
    sb.append("""
    // EDGES
    
""")

    tuples.forEachIndexed { i, t ->
        sb.append("    ${t.a} -> GATE$i;\n")
        sb.append("    ${t.b} -> GATE$i;\n")
        sb.append("    GATE$i -> ${t.c};\n")
    }

    // end
    sb.append("""}
""")

    return sb.toString()
}

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

    println(memPrev.keys)

    return getValue(memPrev, count)
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("24t1"))
        val oT1 = Run(iT1, 3)
        check(oT1 == 4L)

        val iT2 = parse(readInput("24t2"))
        val oT2 = Run(iT2, 12)
        check(oT2 == 2024L)

        val i = parse(readInput("24"))
        val o = Run(i, 46)
        println("Answer to part 1: $o")

        File("extras/24/t1.dot").writeText(dotGraph(iT1))
        File("extras/24/t2.dot").writeText(dotGraph(iT2))
        File("extras/24/prob.dot").writeText(dotGraph(i))
    }
    println(dt)
}
