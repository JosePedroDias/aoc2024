import kotlin.time.measureTime

/*
+---+---+---+
| 7 | 8 | 9 |
+---+---+---+
| 4 | 5 | 6 |
+---+---+---+
| 1 | 2 | 3 |
+---+---+---+
    | 0 | A |
    +---+---+

    +---+---+
    | ^ | A |
+---+---+---+
| < | v | > |
+---+---+---+

robot starts pointing at A


NUMPAD <(R1) DIRECTIONALS <(R2)

*/

private fun parse(lines: List<String>) {
    // TODO
}

fun main() {
    val dt = measureTime {
        parse(readInput("21t1"))
        parse(readInput("21"))
    }
    println(dt)
}
