import kotlin.math.pow
import kotlin.time.measureTime

private data class PS(var rA: Int, var rB: Int, var rC: Int, var program: List<Int>) {
    var ip: Int = 0 // +2 except jump, end once past program size

    fun getValue(i: Int): Int {
        return when (i) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> rA
            5 -> rB
            6 -> rC
            // 7 -> ?
            else -> throw Error("unexpected")
        }
    }

    fun adv(num: Int) {
        rA = (rA / 2.0.pow(num)).toInt()
    }

    fun bxl(num: Int) {
        rB = rB.xor(num)
    }

    fun bst(num: Int) {
        rB = num % 8
    }

    fun jnz(num: Int) {
        if (rA != 0) {
            ip = num - 2
        }
    }

    fun bxc(num: Int) {
        rB = rB.xor(rC)
    }

    fun out(num: Int) {
        println(num % 8)
    }

    fun bdv(num: Int) {
        rB = (rA / 2.0.pow(num)).toInt()
    }

    fun cdv(num: Int) {
        rC = (rA / 2.0.pow(num)).toInt()
    }

    fun run() {
        fun p(s: String) { println(s) }
        //fun p(s: String) {}

        //println("START")
        while (true) {
            if (ip > program.size - 2) break
            val op = program[ip]
            val v = getValue(ip + 1)
            println("ip:$ip, registers:$rA, $rB, $rC, combo: $v")
            when (op) {
                0 -> { p("adv"); adv(v) }
                1 -> { p("bxl"); bxl(v) }
                2 -> { p("bst"); bst(v) }
                3 -> { p("jnz"); jnz(v) }
                4 -> { p("bxc"); bxc(v) }
                5 -> { p("out"); out(v) }
                6 -> { p("bdv"); bdv(v) }
                7 -> { p("cdv"); cdv(v) }
                else -> throw Error("unexpected")
            }
            ip += 2
        }
        //println("END")
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
        val pT1 = parse(readInput("17t1"))
        println(pT1)
        pT1.run()
        println(pT1)

        println("----")

        val p = parse(readInput("17"))
        println(p)
        p.run()
        println(p)
    }
    println(dt)
}
