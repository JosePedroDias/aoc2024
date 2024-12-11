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