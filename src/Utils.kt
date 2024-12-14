import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

fun readInputAsString(name: String) = Path("src/$name.txt").readText().trim()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun combinations(n: Int) = sequence {
    for (i in 0 until n) {
        for (j in 0 until i) {
            yield(Pair(j, i))
        }
    }
}

fun <T> permutations(list: List<T>): Sequence<List<T>> = sequence {
    if (list.isEmpty()) {
        yield(emptyList())
    } else {
        for (i in list.indices) {
            val item = list[i]
            val remaining = list.take(i) + list.drop(i + 1)
            for (perm in permutations(remaining)) {
                yield(listOf(item) + perm)
            }
        }
    }
}

fun doesThrow(block: () -> Unit) {
    return try {
        block()
        throw Error("Did not throw")
    } catch (_: Error) {}
}

private const val MB = 1024 * 1024

fun memUsage() {
    val runtime = Runtime.getRuntime()

    // Total memory allocated by JVM
    val totalMemory = runtime.totalMemory()

    // Free memory available within the allocated memory
    val freeMemory = runtime.freeMemory()

    val usedMemory = totalMemory - freeMemory

    // Maximum memory that JVM can use
    val maxMemory = runtime.maxMemory()

    print("Used: ${usedMemory / MB} MB, ")
    print("Free: ${freeMemory / MB} MB, ")
    print("Total: ${totalMemory / MB} MB, ")
    println("Max: ${maxMemory / MB} MB")
}


/*
0-100a  +   0b (100)
0- 99a  +   1b ( 99)
0- 90a  +  10b ( 90)
0 -60a  +  40b ( 60)
0 -10a  +  90b ( 10)
0 - 1a  +  99b (  2)
0       + 100b (  1)
*/
fun aBUpTo(maxAmount: Int) = sequence {
    for (aPlusB in 0..maxAmount) {
        for (a in 0..aPlusB) {
            yield(Pair(a, maxAmount-aPlusB))
        }
    }
}

fun abTo(maxAmount: Int) = sequence {
    for (b in 0..maxAmount) {
        for (a in 0..maxAmount) {
            yield(Pair(a, b))
        }
    }
}
