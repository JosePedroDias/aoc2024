import kotlin.time.measureTime

private fun parse(lines: List<String>) {
    // TODO
}

fun main() {
    val dt = measureTime {
        parse(readInput("20t1"))
        parse(readInput("20"))
    }
    println(dt)
}
