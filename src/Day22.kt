import kotlin.time.measureTime

private fun parse(lines: List<String>): List<UInt> {
    return lines.map { it.toUInt() }
}

private fun mix(a: UInt, b: UInt): UInt {
    return a.xor(b)
}

private fun prune(a: UInt): UInt {
    return a % 16777216u
}

private fun nextSecret(v_: UInt): UInt {
    var v = v_
    v = prune(mix(v * 64u, v))
    v = prune(mix(v.div(32u), v))
    v = prune(mix(v * 2048u, v))
    return v
}

private fun nthNumber(v_: UInt, times: Int): UInt {
    var v = v_
    repeat(times) {
        v = nextSecret(v)
    }
    return v
}

fun main() {
    val dt = measureTime {
        check(37u == mix(42u, 15u))

        check(16113920u == prune(100000000u))

        var v = 123u
        val secrets = mutableListOf<UInt>()
        repeat(10) {
            v = nextSecret(v)
            secrets.add(v)
        }
        check(secrets == listOf(
            15887950u,
            16495136u,
            527345u,
            704524u,
            1553684u,
            12683156u,
            11100544u,
            12249484u,
            7753432u,
            5908254u)
        )

        val inT1 = parse(readInput("22t1"))
        val outT1 = inT1.map { nthNumber(it, 2000) }
        check(outT1 == listOf(8685429u, 4700978u, 15273692u, 8667524u))
        val sumT1 = outT1.sum()
        check(sumT1 == 37327623u)

        val inP = parse(readInput("22"))
        val resP1 = inP.sumOf { nthNumber(it, 2000).toULong() }
        println("Answer to part 1: $resP1")
    }
    println(dt)
}
