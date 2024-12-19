import kotlin.time.measureTime

private fun parse(lines: List<String>) {
    // TODO
}

fun main() {
    val dt = measureTime {
        parse(readInput("18t1"))
        parse(readInput("18"))
    }
    println(dt)
}
