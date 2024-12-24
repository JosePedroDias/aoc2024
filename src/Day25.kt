import kotlin.time.measureTime

private fun parse(lines: List<String>) {
    // TODO
}

fun main() {
    val dt = measureTime {
        parse(readInput("25t1"))
        parse(readInput("25"))
    }
    println(dt)
}
