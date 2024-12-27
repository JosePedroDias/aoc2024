import java.io.File
import kotlin.time.measureTime

/*
full adder for n-bit numbers

inputs:
- X0, .., n-1
- Y0, .., n-1

gates:
- 2n XOR gates
- 2n AND gates
-  n OR  gates

output:
- Z0, ... n-1
- Cout

for layer i:
- Xi XOR Yi   -> Pi
- Pi XOR Ci-1 -> Zi
- Xi AND Yi   -> Gi
- Pi AND Ci-1 -> Hi
- Hi OR  Gi   -> Ci
*/

private typealias Mem = MutableMap<String, UInt>
private typealias Statements = MutableMap<String, Triple<String, String, String>>

private fun dotGraph(statements: Statements): String {
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

    statements.entries.forEachIndexed { i, (_, t) ->
        val (op) = t
        val style = when (op) {
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

    statements.entries.forEachIndexed { i, (c, t) ->
        val (_, a, b) = t
        sb.append("    $a -> GATE$i;\n")
        sb.append("    $b -> GATE$i;\n")
        sb.append("    GATE$i -> $c;\n")
    }

    // end
    sb.append("""}
""")

    return sb.toString()
}

private fun parse(lines: List<String>): Pair<Mem, Statements> {
    var atSecondSection = false
    val mem = mutableMapOf<String, UInt>()
    val statements = mutableMapOf<String, Triple<String, String, String>>()

    for (l in lines) {
        if (!atSecondSection) {
            if (l.isEmpty()) {
                atSecondSection = true
            } else {
                val (name, value) = l.split(": ")
                mem[name] = value.toUInt()
            }
        } else {
            val (a, op, b, _, c) = l.split(" ")
            statements[c] = Triple(op, a, b)
        }
    }

    return Pair(mem, statements)
}

private fun run(pair: Pair<Mem, Statements>): ULong {
    val (mem, statements) = pair

    fun calc(c: String): UInt {
        if (mem.containsKey(c)) return mem[c]!!
        val (op, a, b) = statements[c]!!
        val va = calc(a)
        val vb = calc(b)
        val vc = when (op) {
            "XOR" -> va.xor(vb)
            "AND" -> va.and(vb)
            "OR" -> va.or(vb)
            else -> throw Error("oops")
        }
        mem[c] = vc
        return vc
    }

    val ov = outVars(statements)
    val res = mutableListOf<UInt>()
    for (zi in ov) res.add(calc(zi))

    //val l = ov.fold(mutableListOf<Int>(), { lst, k -> lst.addFirst(mem[k]!!); lst })
    //println(l.joinToString(""))

    return getValue(mem, ov)
}

private fun inVars(mem: Mem): Pair<List<String>, List<String>> {
    val inVars = mutableListOf<String>()
    for (k in mem.keys) inVars.add(k)
    val xVars = inVars.filter { it.startsWith("x") }
    val yVars = inVars.filter { it.startsWith("y") }
    return Pair(xVars.sorted(), yVars.sorted())
}

private fun outVars(statements: Statements): List<String> {
    val outVars = mutableListOf<String>()
    for ((c) in statements) {
        if (c.startsWith("z")) outVars.add(c)
    }
    return outVars.sorted()
}

private fun getValue(mem: Mem, ov:List<String>): ULong {
    var res = 0UL
    for ((i, varName) in ov.withIndex()) {
        res += (1UL.shl(i) * mem[varName]!!)
    }
    return res
}

private fun setValue(mem: Mem, iv:List<String>, v: ULong) {
    for ((i, varName) in iv.withIndex()) {
        mem[varName] = v.shr(i).toUInt().and(1U)
    }
    /*println(longBinary(v, iv.size))
    val l = iv.fold(mutableListOf<Int>(), { lst, k -> lst.addFirst(mem[k]!!); lst })
    println(l.joinToString(""))*/
}

private fun longBinary(v: ULong, len: Int): String {
    return v.toString(radix = 2).padStart(len, '0')
}

/*
private fun swap(tuples: List<Tuple4>, idx1: Int, idx2: Int):List<Tuple4> {
    val newTuples = tuples.toMutableList()

    val t1 = tuples.get(idx1)
    val t2 = tuples.get(idx2)

    println(listOf(t1, t2))

    newTuples[idx1] = t1.withNewC(t2.c)
    newTuples[idx2] = t2.withNewC(t1.c)

    return newTuples
}

private fun swapByLabels(tuples: List<Tuple4>, l1: String, l2: String, nth0: Int? = null, nth1: Int? = null):List<Tuple4> {
    val candidates = mutableListOf<Tuple4>()
    val indices = mutableListOf<Int>()
    tuples.forEachIndexed { idx, tup ->
        if (tup.hasLabel(l1) || tup.hasLabel(l2)) {
            indices.add(idx)
            candidates.add(tup)
        }
    }

    if (nth0 != null && nth1 != null) {
        return swap(tuples, indices[nth0], indices[nth1])
    }

    check(indices.size == 2, { "too many candidates:\n${candidates.joinToString("\n")}" })
    val (i1, i2) = indices
    return swap(tuples, i1, i2)
}

private fun swapByCs(tuples: List<Tuple4>, c1: String, c2: String):List<Tuple4> {
    val i1 = tuples.indexOfFirst { it.c == c1 }
    val i2 = tuples.indexOfFirst { it.c == c2 }
    return swap(tuples, i1, i2)
}
*/

private fun validateAdder(pair: Pair<Mem, Statements>) {
    val (mem_, statements) = pair

    val (ix, iy) = inVars(mem_)

    val bits = ix.size
    check(iy.size == bits)

    for (bit in 0..< bits) {
        val expectedNum = 1UL.shl(bit)

        val mem1 = mem_.toMutableMap()
        setValue(mem1, ix, expectedNum)
        setValue(mem1, iy, 0U)
        val res1 = run(Pair(mem1, statements))
        check(res1 == expectedNum, { "exp:${longBinary(expectedNum, bits)}, res1:${longBinary(res1, bits)}" })

        val mem2 = mem_.toMutableMap()
        setValue(mem2, ix, 0U)
        setValue(mem2, iy, expectedNum)
        val res2 = run(Pair(mem2, statements))
        check(res2 == expectedNum, { "exp:${longBinary(expectedNum, bits)}, res2:${longBinary(res2, bits)}" })
    }
}

private fun validateAdder2(pair: Pair<Mem, Statements>) {
    val (mem_, statements) = pair

    val (ix, iy) = inVars(mem_)

    val bits = ix.size
    check(iy.size == bits)

    val with = listOf(
        //Pair(0UL, 0UL),
        Pair(0UL, 1UL),
        Pair(1UL, 0UL),
        Pair(1UL, 1UL),
    )

    for (bit in 0..< bits) {
        println("bit: $bit")
        for ((d1, d2) in with) {
            val X0 = 1UL.shl(bit) - 1U
            val X = X0 + d1.shl(bit)
            val Y = X0 + d2.shl(bit)
            val Z = X + Y
            println("${longBinary(X, bits)} + ${longBinary(Y, bits)} = ${longBinary(Z, bits)}")
            val mem = mem_.toMutableMap()
            setValue(mem, ix, X)
            setValue(mem, iy, Y)
            val res = run(Pair(mem, statements))
            check(res == Z, { "${longBinary(res, bits)} != ${longBinary(Z, bits)}" })
        }
    }
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("24t1"))
        val iT2 = parse(readInput("24t2"))
        //val iT3 = parse(readInput("24t3"))
        val i = parse(readInput("24"))

        val oT1 = run(iT1)
        check(oT1 == 4UL)
        //ValidateAdder(iT1)

        val oT2 = run(iT2)
        check(oT2 == 2024UL)

        //ValidateAdder(iT3)

        val o = run(i)
        println("Answer to part 1: $o")

        val i2 = Pair(i.first, i.second.toMutableMap())

        validateAdder(i2)
        validateAdder2(i2)

        File("extras/24/prob.dot").writeText(dotGraph(i.second))
        File("extras/24/probFixed.dot").writeText(dotGraph(i2.second))

        /*
        var (mem, tuples) = i

        // broken at bit 11
        //tuples = swapByLabels(tuples, "y11", "x11") // gjc, gvm
        tuples = swapByCs(tuples, "gjc", "qjj")

        // z should come from XOR from AND and XOR (z17!)
        //tuples = swapByCs(tuples, "rqq", "pqv")
        //tuples = swapByLabels(tuples, "z17", "pqv")
        // broken at bit 16 | wkv rcr | x16 y16 z16 | twg vwv nsf dmw pvh
        //tuples = swapByCs(tuples, "z17", "ffg")
        //tuples = swapByCs(tuples, "vwv", "dmw")
        //tuples = swapByCs(tuples, "x16", "z16") //

        // broken at bit 17
        //tuples = swapByCs(tuples, "y17", "x17") // rqq, pqv

        // broken at bit 26
        //tuples = swapByCs(tuples, "y26", "x26")
        //tuples = swapByCs(tuples, "z26", "kfq", 1, 2) // z26, gvm

        // broken at bit 39
        //tuples = swapByCs(tuples, "y39", "x39") // z39, sbq

        println(mutableListOf(
            "gjc", "qjj",
            "rqq", "pqv",
            "z26", "gvm",
            "z39", "sbq",
        ).sorted().joinToString(","))

        val i2 = Pair(mem, tuples)

        val p = process(i2)
        for (k in p.keys.filter { it.contains("xor") }) println(p[k])
        //for (k in p.keys.filter { it.contains("17") }) println(p[k])

        ValidateAdder2(i2)
        //ValidateAdder(i2)

        //File("extras/24/t1.dot").writeText(dotGraph(iT1))
        //File("extras/24/t2.dot").writeText(dotGraph(iT2))
        File("extras/24/prob.dot").writeText(dotGraph(i))
        File("extras/24/probFixed.dot").writeText(dotGraph(i2))

         */
    }
    println(dt)
}
