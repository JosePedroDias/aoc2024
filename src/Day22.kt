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

private fun onesDigit(n: UInt): Int {
    return (n % 10u).toInt()
}

private const val NUM_CHANGES = 2000
private const val WINDOW_SIZE = 4

private typealias SeqChanges = List<Int>
private typealias Forecast = Map<SeqChanges, Int>

private fun addToWin4(l: List<Int>, n: Int): List<Int> {
    val l2 = l.toMutableList()
    l2.add(n)
    if (l2.size > WINDOW_SIZE) l2.removeFirst()
    return l2.toList()
}

private fun forecast(seed: UInt): Forecast {
    //println("\nFORECAST: $seed")
    var v = seed
    var prev = onesDigit(v)
    var seq = listOf<Int>()
    val res = mutableMapOf<SeqChanges, Int>()

    repeat(NUM_CHANGES) {
        v = nextSecret(v)
        val b = onesDigit(v)
        val diff = b - prev
        prev = b

        seq = addToWin4(seq, diff)

        val prevValue = res[seq]
        if (prevValue == null/* || prevValue < b*/) { // FIRST TIME!!!
            res[seq] = b
            //println("$seq -> $b")
        }
    }

    return res
}

private fun bestWindow(forecasts: List<Forecast>): Int {
    val seqs = mutableSetOf<SeqChanges>()
    for (fc in forecasts) {
        for (sc in fc.keys) {
            /*if (sc.size == 4)*/ seqs.add(sc)
        }
    }

    var bestB = -1
    var bestSeq: List<Int> = listOf()
    for (seq in seqs) {
        var sum = 0
        for (fc in forecasts) {
            val contribution = fc[seq]
            if (contribution != null) {
                //println("contribution: $contribution")
                sum += contribution
            }
        }
        if (sum > bestB) {
            bestB = sum
            bestSeq = seq
            println("b: $bestB, seq: $bestSeq")
        }
    }

    return bestB
}

fun main() {
    val dt = measureTime {
        check(37u == mix(42u, 15u))

        check(16113920u == prune(100000000u))

        123u.let {
            val secrets = mutableListOf<UInt>()
            var v = it
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
        }

        ////

        val inT1 = parse(readInput("22t1"))
        val outT1 = inT1.map { nthNumber(it, NUM_CHANGES) }
        check(outT1 == listOf(8685429u, 4700978u, 15273692u, 8667524u))
        val sumT1 = outT1.sum()
        check(sumT1 == 37327623u)

        val inP = parse(readInput("22"))
        val resP1 = inP.sumOf { nthNumber(it, NUM_CHANGES).toULong() }
        println("Answer to part 1: $resP1")

        ////

        val forecastsT1 = listOf(1u, 2u, 3u, 2024u).map { forecast(it) }
        check( 23 == bestWindow(forecastsT1) )

        println(".....")

        val forecastsP = inP.map { forecast(it) }
        val bwP = bestWindow(forecastsP)
        println("Answer to part 2: $bwP")
    }
    println(dt)
}
