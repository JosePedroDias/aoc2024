import kotlin.math.pow
import kotlin.time.measureTime

const val OP_JNZ = 3

private data class PS(var rA: Int, var rB: Int, var rC: Int, var program: List<Int>, val outs: MutableList<Int> = mutableListOf()) {
    var ip: Int = 0 // +2 except jump, end once past program size
    var p = fun(s: String) {}
    var pp = fun(s: String) {}

    fun combo(i: Int): Int {
        return when (i) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> rA
            5 -> rB
            6 -> rC
            else -> throw Error("unexpected combo: $i")
        }
    }

    // opcode 0 C
    fun adv(v_: Int) {
        val v = combo((v_))
        pp("adv($v_/$v', rA:$rA)")
        //rA = (rA / 2.0.pow(v)).toInt()
        rA = rA.shr(v)
        p(" = $rA (rA)")
    }

    // opcode 1 L
    fun bxl(v: Int) {
        pp("bxl($v, rB:$rB)")
        rB = rB.xor(v)
        p(" = $rB (rB)")
    }

    // opcode 2 C
    fun bst(v_: Int) {
        val v = combo(v_)
        pp("bst($v_/$v')")
        rB = v % 8
        p(" = $rB (rB)")
    }

    // opcode 3 L
    fun jnz(v: Int) {
        pp("jnz($v, rA:$rA)")
        ip = if (rA != 0) v else ip + 1
        p("$ip (ip)")
    }

    // opcode 4 ?
    fun bxc() {
        pp("bxc(rB:$rB, rC:$rC)")
        rB = rB.xor(rC)
        p("$rB (rB)")
    }

    // opcode 5 C
    fun out(v_: Int) {
        val v = combo(v_)
        p("out($v_/$v')")
        val m8 = v % 8
        outs.add(m8)
        //println(m8)
    }

    // opcode 6 C
    fun bdv(v_: Int) {
        val v = combo(v_)
        pp("bdv($v_/$v', rA:$rA)")
        rB = rA.shr(v)
        p("$rB (rB)")
    }

    // opcode 7 C
    fun cdv(v_: Int) {
        val v = combo(v_)
        pp("cdv($v_/$v', rA:$rA)")
        rC = rA.shr(v)
        p("$rC (rC)")
    }

    fun run(debug: Boolean = false) {
        if (debug) {
            p = fun(s: String) { println(s) }
            pp = fun(s: String) { print(s) }
        }

        //p("PROGRAM: $program")
        while (ip < program.size) {
            val op = program[ip++]
            val v = program[ip]
            p("** ip:${ip-1}, ip+1:${ip}, op:$op, v:$v, rA:$rA, rB:$rB, rC:$rC **")
            when (op) {
                0 -> adv(v)
                1 -> bxl(v)
                2 -> bst(v)
                3 -> jnz(v)
                4 -> bxc()
                5 -> out(v)
                6 -> bdv(v)
                7 -> cdv(v)
                else -> throw Error("unexpected opcode: $op")
            }
            if (op != OP_JNZ) ++ip
        }
    }
}

private fun parse(lines: List<String>): PS {
    val rA = lines[0].split(": ")[1].toInt()
    val rB = lines[1].split(": ")[1].toInt()
    val rC = lines[2].split(": ")[1].toInt()
    val prog = lines[4].split(": ")[1].split(",").map { it.toInt() }
    return PS(rA, rB, rC, prog)
}

fun main() {
    val dt = measureTime {
        val t1 = PS(0, 0, 9, listOf(2, 6))
        t1.run()
        check(t1.rA == 0)
        check(t1.rB == 1) // fails
        check(t1.rC == 9)
        check(t1.outs.isEmpty())

        val t2 = PS(10, 0, 0, listOf(5, 0, 5, 1, 5, 4))
        t2.run()
        check(t2.rA == 10)
        check(t2.rB == 0)
        check(t2.rC == 0)
        check(t2.outs == listOf(0, 1, 2))

        val t3 = PS(2024, 0, 0, listOf(0,1,5,4,3,0))
        t3.run()
        check(t3.rA == 0)
        check(t3.rB == 0)
        check(t3.rC == 0)
        check(t3.outs == listOf(4,2,5,6,7,7,7,7,3,1,0))

        val t4 = PS(0, 29, 0, listOf(1,7))
        t4.run()
        check(t4.rA == 0)
        check(t4.rB == 26)
        check(t4.rC == 0)
        check(t4.outs == listOf<Int>())

        val t5 = PS(0, 2024, 43690, listOf(4,0))
        t5.run()
        check(t5.rA == 0)
        check(t5.rB == 44354)
        check(t5.rC == 43690)
        check(t5.outs == listOf<Int>())

        val pT1 = parse(readInput("17t1"))
        println(pT1)
        pT1.run()
        println(pT1)
        check(pT1.outs == listOf(4,6,3,5,6,3,5,2,1,0))

        println("----")

        val p = parse(readInput("17"))
        println(p)
        p.run()
        println(p)
        println("Answer to part 1: ${p.outs.joinToString(",")}")

        // part 2

        val ri = readInput("17")
        for (rA in 0..Int.MAX_VALUE) {
            if (rA % 100000 == 0) println("$rA of ${Int.MAX_VALUE} (${rA.toFloat()/Int.MAX_VALUE.toFloat() * 100})")
            val pp = parse(ri)
            pp.rA = rA
            pp.run()
            if (pp.program == pp.outs) {
                println("Answer to part 2: $rA}")
                break
            }
        }
        println("ALL DONE")
    }
    println(dt)
}
