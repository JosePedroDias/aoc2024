import kotlin.time.measureTime

typealias Disk = List<Int?>

private fun toDisk(lines: List<String>, debug: Boolean): Disk {
    check(lines.size == 1)
    val line = lines[0]
    val ch0 = '0'.code
    var i = 0
    var fileIndex: Int = 0
    val disk = mutableListOf<Int?>()
    for (ch in line.chars()) {
        val isFile = i % 2 == 0
        fileIndex = i / 2
        val v = ch - ch0
        val item = if (isFile) fileIndex else null
        val ch2 = if (isFile) Char(fileIndex + ch0) else '.'
        repeat(v) {
            disk.add(item)
            if (debug) { print(ch2) }
        }
        ++i
    }
    if (debug) { println() }
    //println("last fileIndex: $fileIndex") // 9 | 9999
    return disk
}

private fun part1(lines: List<String>): Int {
    return 0
}

private fun part2(lines: List<String>): Int {
    return 0
}

fun main() {
    val dt = measureTime {
        toDisk(readInput("09_test"), true)
        val d = toDisk(readInput("09"), false)

        //check(9999 == part1(readInput("09_test")))
        //println("part 1 answer: ${part1(readInput("09"))}")

        //check(9999 == part2(readInput("09_test")))
        //println("part 2 answer: ${part2(readInput("09"))}")
    }
    println(dt)
}