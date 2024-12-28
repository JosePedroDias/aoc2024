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

private fun swap(statements: Statements, k1: String, k2: String) {
    val t1 = statements[k1]!!
    val t2 = statements[k2]!!
    println("$k1 <-> $k1")
    println("  $k1 = $t2")
    println("  $k2 = $t1")
    statements[k1] = t2
    statements[k2] = t1
}

private fun validateAdder(pair: Pair<Mem, Statements>) {
    val (mem_, statements) = pair

    val (ix, iy) = inVars(mem_)

    val bits = ix.size; check(iy.size == bits)

    for (bit in 0..< bits) {
        val expectedNum = 1UL.shl(bit)

        val mem1 = mem_.toMutableMap()
        setValue(mem1, ix, expectedNum)
        setValue(mem1, iy, 0U)
        val res1 = run(Pair(mem1, statements))
        check(res1 == expectedNum, { "i:$bit, exp:${longBinary(expectedNum, bits)}, res1:${longBinary(res1, bits)}" })

        val mem2 = mem_.toMutableMap()
        setValue(mem2, ix, 0U)
        setValue(mem2, iy, expectedNum)
        val res2 = run(Pair(mem2, statements))
        check(res2 == expectedNum, { "i:$bit, exp:${longBinary(expectedNum, bits)}, res2:${longBinary(res2, bits)}" })
    }
}

private fun validateAdder2(pair: Pair<Mem, Statements>) {
    val (mem_, statements) = pair

    val (ix, iy) = inVars(mem_)

    val bits = ix.size; check(iy.size == bits)

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

private fun checkStructure(pair: Pair<Mem, Statements>) {
    val (mem, statements) = pair

    val (ix, iy) = inVars(mem)
    val oz = outVars(statements)

    fun statementsHavingOp(k: String): Map<String, Triple<String, String, String>> {
        return statements.filter { it.value.second == k || it.value.third == k }
    }

    fun statementsReturning(k: String): Map<String, Triple<String, String, String>> {
        return statements.filter { it.key == k }
    }

    for (bit in ix.indices) {
        val kX = ix[bit]
        val kY = iy[bit]
        val kZ = oz[bit]
        println("\n** bit: $bit -> $kX $kY $kZ **")

        val havingX = statementsHavingOp(kX)
        val havingY = statementsHavingOp(kY)
        val havingZ = statementsReturning(kZ)

        for (sWithX in havingX) println("XX: $sWithX")
        check(havingX.size == 2)
        val xXor = havingX.entries.find { (k, v) -> v.first == "XOR" }!!
        val xAnd = havingX.entries.find { (k, v) -> v.first == "AND" }!!

        for (sWithY in havingY) println("YY: $sWithY")
        check(havingY.size == 2)
        val yXor = havingY.entries.find { (k, v) -> v.first == "XOR" }!!
        val yAnd = havingY.entries.find { (k, v) -> v.first == "AND" }!!

        for (sWithZ in havingZ) println("ZZ: $sWithZ")
        check(havingZ.size == 1)

        val candidates: Statements = mutableMapOf()
        statementsHavingOp(xXor.key).forEach { (k, v) -> candidates[k] = v }
        statementsHavingOp(xAnd.key).forEach { (k, v) -> candidates[k] = v }
        statementsHavingOp(yXor.key).forEach { (k, v) -> candidates[k] = v }
        statementsHavingOp(yAnd.key).forEach { (k, v) -> candidates[k] = v }
        //println(candidates)

        if (bit > 0) {
            val (t1, t2, t3) = candidates.values.toList()
            println(candidates.entries.joinToString("\n"))
            check(listOf("XOR", "AND").contains(t1.first), { "t1 is ${t1.first}" })
            check(listOf("XOR", "AND").contains(t2.first), { "t2 is ${t2.first}" })
            check(t3.first == "OR", { "t3 is ${t3.first}" })
        }

        val zXor = havingZ.entries.find { (_, v) -> v.first == "XOR" }!!
    }
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("24t1"))
        val iT2 = parse(readInput("24t2"))
        var i = parse(readInput("24"))

        val oT1 = run(iT1)
        check(oT1 == 4UL)

        val oT2 = run(iT2)
        check(oT2 == 2024UL)

        val o = run(i)
        println("Answer to part 1: $o")

        i = parse(readInput("24"))

        val mem = i.first
        val statements = i.second.toMutableMap()
        val i2 = Pair(mem, statements)

        // swap incorrect wires
        swap(statements, "gjc", "qjj") // bit 11
        swap(statements, "z17", "wmp") // bit 17
        swap(statements, "z26", "gvm") // bit 26
        swap(statements, "z39", "qsb") // bit 39

        checkStructure(i2)

        File("extras/24/prob.dot").writeText(dotGraph(i.second))
        File("extras/24/probFixed.dot").writeText(dotGraph(i2.second))

        validateAdder(i2)
        validateAdder2(i2)

        println(mutableListOf(
            "gjc", "qjj",
            "z17", "wmp",
            "z26", "gvm",
            "z39", "qsb",
        ).sorted().joinToString(","))
    }
    println(dt)
}
