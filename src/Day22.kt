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

        val inT1 = readInput("22t1")

        val inP = readInput("22")
    }
    println(dt)
}
