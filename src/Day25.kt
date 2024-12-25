import kotlin.time.measureTime

private const val W = 5
private const val H = 7

private const val F = '#'
private const val E = '.'

//private typealias Five = IntArray
private typealias Five = List<Int>

private typealias LocksAndKeys = Pair<List<Five>, List<Five>>

private fun parse(lines: List<String>): LocksAndKeys {
    val locks = mutableListOf<Five>() // top row filled #
    val keys = mutableListOf<Five>()  // top row empty  .

    for (lines2 in lines.windowed(7, 8)) {
        //println(lines2.joinToString("\n"))
        val ch = lines2[0][0]
        val isLock = ch == F
        val x = lines2.map { it.toCharArray().toList() }.toTypedArray().toList()
        val y = x.transpose()
        val z = y.map { it2 -> it2.count { it == F } - 1 }
        //println(y)
        //println(z)
        val bag = if (isLock) locks else keys
        //bag.add(z.toIntArray())
        bag.add(z)
    }

    println("#locks: ${locks.size}, #keys: ${locks.size}")
    return Pair(locks, keys)
}

private fun fittingKeys(lak: LocksAndKeys): Int {
    var count = 0
    val (locks, keys) = lak
    val nL = locks.size
    val nK = keys.size
    for (k in 0 until nK) {
        val key = keys[k]
        for (l in 0 until nL) {
            val lock = locks[l]
            val fits = lock.zip(key).all { (ll, kk) -> ll + kk < 6 }
            if (fits) ++count
        }
    }
    return count
}

fun main() {
    val dt = measureTime {
        val iT1 = parse(readInput("25t1"))
        val iP = parse(readInput("25"))
        check(fittingKeys(iT1) == 3)
        println("Answer to part 1: ${fittingKeys(iP)}")
    }
    println(dt)
}
