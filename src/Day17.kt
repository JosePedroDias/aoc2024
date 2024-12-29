import kotlin.time.measureTime

const val OP_JNZ = 3L

private data class PS(var rA: Long, var rB: Long, var rC: Long, var program: List<Long>, val outs: MutableList<Long> = mutableListOf()) {
    var ip: Int = 0 // +2 except jump, end once past program size
    var p = fun(s: String) {}
    var pp = fun(s: String) {}

    fun combo(i: Long): Long {
        return when (i) {
            0L -> 0L
            1L -> 1L
            2L -> 2L
            3L -> 3L
            4L -> rA
            5L -> rB
            6L -> rC
            else -> throw Error("unexpected combo: $i")
        }
    }

    // opcode 0 C - rA =/ 2powN (loses N bits)
    fun adv(v_: Long) {
        val v = combo((v_))
        pp("adv($v_/$v', A:${bin(rA)})")
        rA = rA.shr(v.toInt())
        p("->${bin(rA)}(A)")
    }

    // opcode 1 L - B = B xor N
    fun bxl(v: Long) {
        pp("bxl($v, B:${bin(rB)})")
        rB = rB.xor(v)
        p("->${bin(rB)}(B)")
    }

    // opcode 2 C - (4:A -> B becomes mod 8 of argument)
    fun bst(v_: Long) {
        val v = combo(v_)
        pp("bst($v_/$v')")
        rB = v % 8
        p("->${bin(rB)}(B)")
    }

    // opcode 3 L - jumps to value if not zero
    fun jnz(v: Long) {
        pp("jnz($v, A:${bin(rA)})")
        ip = if (rA != 0L) v.toInt() else ip + 1
        p("->$ip(ip)")
    }

    // opcode 4 ?
    fun bxc() {
        pp("bxc(B:${bin(rB)}, C:${bin(rC)})")
        rB = rB.xor(rC)
        p("->${bin(rB)}(B)")
    }

    // opcode 5 C (if 4=A, 5=B) ~ outs mod 8
    fun out(v_: Long) {
        val v = combo(v_)
        pp("out($v_/$v')")
        val m8 = v % 8L
        outs.add(m8)
        p("->${outs.joinToString(",")}(out)")
    }

    // opcode 6 C
    fun bdv(v_: Long) {
        val v = combo(v_)
        pp("bdv($v_/$v', A:${bin(rA)})")
        rB = rA.shr(v.toInt())
        p("->${bin(rB)}(B)")
    }

    // opcode 7 C - shift right /= 2powN (5=B) ~ to C
    fun cdv(v_: Long) {
        val v = combo(v_)
        pp("cdv($v_/$v', A:${bin(rA)})")
        rC = rA.shr(v.toInt())
        p("->${bin(rC)}(C)")
    }

    fun bin(n: Long): String {
        return n.toString(radix = 2).windowed(3, 3).joinToString(" ") + "($n)"
    }

    fun run(debug: Boolean = false) {
        if (debug) {
            p = fun(s: String) { println(s) }
            pp = fun(s: String) { print(s) }
        }

        p("\n         ${program.indices.map{ it / 10 }.joinToString(" ")}")
        p("         ${program.indices.map{ it % 10 }.joinToString(" ")}")
        p("program: ${program.joinToString(",")}")
        while (ip < program.size) {
            val op = program[ip++]
            val v = program[ip]
            p("- ip:${ip-1} A:${bin(rA)} B:${bin(rB)} C:${bin(rC)} -")
            when (op) {
                0L -> adv(v)
                1L -> bxl(v)
                2L -> bst(v)
                3L -> jnz(v)
                4L -> bxc()
                5L -> out(v)
                6L -> bdv(v)
                7L -> cdv(v)
                else -> throw Error("unexpected opcode: $op")
            }
            if (op != OP_JNZ) ++ip
        }
        p("program ended.")
    }
}

private fun parse(lines: List<String>): PS {
    val rA = lines[0].split(": ")[1].toLong()
    val rB = lines[1].split(": ")[1].toLong()
    val rC = lines[2].split(": ")[1].toLong()
    val prog = lines[4].split(": ")[1].split(",").map { it.toLong() }
    return PS(rA, rB, rC, prog)
}

fun main() {
    val dt = measureTime {
        val t1 = PS(0, 0, 9, listOf(2, 6))
        t1.run()
        check(t1.rA == 0L)
        check(t1.rB == 1L) // fails
        check(t1.rC == 9L)
        check(t1.outs.isEmpty())

        val t2 = PS(10, 0, 0, listOf(5L, 0L, 5L, 1L, 5L, 4L))
        t2.run()
        check(t2.rA == 10L)
        check(t2.rB == 0L)
        check(t2.rC == 0L)
        check(t2.outs == listOf(0L, 1L, 2L))

        val t3 = PS(2024, 0, 0, listOf(0L, 1L, 5L, 4L, 3L, 0L))
        t3.run()
        check(t3.rA == 0L)
        check(t3.rB == 0L)
        check(t3.rC == 0L)
        check(t3.outs == listOf(4L, 2L, 5L, 6L, 7L, 7L, 7L, 7L, 3L, 1L, 0))

        val t4 = PS(0, 29, 0, listOf(1L, 7L))
        t4.run()
        check(t4.rA == 0L)
        check(t4.rB == 26L)
        check(t4.rC == 0L)
        check(t4.outs == listOf<Long>())

        val t5 = PS(0, 2024, 43690, listOf(4L, 0L))
        t5.run()
        check(t5.rA == 0L)
        check(t5.rB == 44354L)
        check(t5.rC == 43690L)
        check(t5.outs == listOf<Long>())

        val pT1 = parse(readInput("17t1"))
        //println(pT1)
        pT1.run()
        //println(pT1)
        check(pT1.outs == listOf(4L, 6L, 3L, 5L, 6L, 3L, 5L, 2L, 1L, 0L))

        //println("----")

        val p = parse(readInput("17"))
        //println(p)
        p.run()
        //println(p)
        println("Answer to part 1: ${p.outs.joinToString(",")}")

        // part 2
        val ps = PS(2024, 0, 0, listOf(0L, 3L, 5L, 4L, 3L, 0L))
        ps.rA = 117440
        ps.run(false)
        check(ps.program == ps.outs, { "${ps.outs} != ${ps.program}" })

        val a0 = revEngReversed(listOf(2L, 4L, 1L, 1L, 7L, 5L, 1L, 5L, 4L, 2L, 5L, 5L, 0L, 3L, 3L, 0L))

        //println("----- a0: $a0 -----")
        val outs = revEng(a0)
        //println(outs)

        val p2 = parse(readInput("17"))
        p2.rA = a0 //6.shl(3)
        p2.run(false)
        check(p2.program == p2.outs, { "${p2.outs} != ${p2.program}" })
        println("Answer to part 2: $a0")
    }
    println(dt)
}

private fun revEng(a0: Long, showOutChanges: Boolean = false): List<Long> {
    var a = a0
    var b = 0L
    var c = 0L
    val outs = mutableListOf<Long>()

    do {
        // ip:0 | bst(4/6')-> b = a % 8 (B)
        b = a % 8 // B becomes the smaller 3 bits of A

        // ip:2 | bxl(1, B:110(6))->111(7)(B)
        b = b.xor(1) // negates 1st bit of B

        // ip:4 | cdv(5/7', A:110(6))->(0)(C)
        c = a.shr(b.toInt()) // C is then used in B = B.xor(C)

        // ip:6 | bxl(5, B)->(2)(B)
        b = b.xor(5) // B is set multiple times above...

        // ip:8 | bxc(B:2, C:(0))->(2)(B)
        b = b.xor(c)

        // ip:10 | out(5/2')->2(out)
        outs.add(b % 8L)
        if (showOutChanges) println(outs)

        // ip:12 | adv(3/3', A:110(6))->(0)(A)
        a = a.shr(3)

        // ip:14 | jnz(0, A:(0))->16(ip)
    } while (a != 0L)

    return outs
}

private fun revEngReversed(program: List<Long>): Long {
    /*
    // KUDOS to 0xdf for the strategy
    candidates = [0]
    for l in range(len(program)):
        next_candidates = []
        for val in candidates:
            for i in range(8):
                target = (val << 3) + i
                if computer(target) = program[-l-1:]:
                    next_candidates.append(target)
        candidates = next_candidates
        print(candidates)
     */

    val pLen = program.size
    var candidates = mutableListOf<Long>(0)
    for ((outIdx, outVal) in program.withIndex().reversed()) {
        val targetProg = program.subList(outIdx, pLen)
        //println("outIdx: $outIdx, outVal: $outVal, tgt: $targetProg")
        val nextCandidates = mutableListOf<Long>()
        for (candidate in candidates) {
            for (i in 0 until 8) {
                val target = candidate.shl(3) + i
                val gotProg = revEng(target)
                //print("~> $gotProg")
                if (gotProg == targetProg) {
                    //println(" OK!")
                    nextCandidates.add(target)
                } else {
                    //println(" X")
                }
            }
        }
        candidates = nextCandidates
        //println("CANDIDATES: $candidates")
    }

    //println(candidates)
    return candidates.min()
}
