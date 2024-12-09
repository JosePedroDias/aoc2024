import kotlin.time.measureTime

typealias Disk = Array<Int?>

const val CH0 = '0'.code

private fun diskToString(d: Disk): String {
    val sb = StringBuilder()
    for (v in d) {
        if (v == null) {
            sb.append('.')
        } else {
            val ch = Char(CH0 + v)
            sb.append(ch)
        }
    }
    return sb.toString()
}

private fun toDisk(line: String): Disk {
    var i = 0
    var fileIndex: Int = 0
    val disk = mutableListOf<Int?>()
    for (ch in line.chars()) {
        val isFile = i % 2 == 0
        fileIndex = i / 2
        val v = ch - CH0
        val item = if (isFile) fileIndex else null
        repeat(v) { disk.add(item) }
        ++i
    }
    //println("last fileIndex: $fileIndex") // 9 | 9999
    return disk.toTypedArray()
}

private fun defrag(disk0: Disk): Disk {
    val disk = disk0.clone()
    var b = disk.size - 1 // from index (value is not null)
    var a = 0 // to index (value is null)
    while (b >= a) {
        val vb = disk[b]
        if (vb != null) {
            disk[b] = null
            var va = disk[a]
            while (va != null) {
                ++a
                va = disk[a]
            }
            if (a <= b) {
                disk[a] = vb
                ++a
                //if (disk.size < 100) { println(diskToString(disk)) }
            }
        }
        --b
    }
    return disk
}

private fun defrag2(disk0: Disk): Disk {
    val disk = disk0.clone()
    var b = disk.size - 1 // from index (value is not null)
    var a = 0 // to index (value is null)
    while (b >= a) {
        val vb = disk[b]
        if (vb != null) {
            disk[b] = null
            var va = disk[a]
            while (va != null) {
                ++a
                va = disk[a]
            }
            if (a <= b) {
                disk[a] = vb
                ++a
                //if (disk.size < 100) { println(diskToString(disk)) }
            }
        }
        --b
    }
    return disk
}

private fun checksum(disk: Disk): Long {
    var cs = 0L
    disk.forEachIndexed {
        idx, v ->
        if (v != null) {
            val add = idx.toLong() * v.toLong()
            cs += add
        }
    }
    return cs
}

fun main() {
    val dt = measureTime {
        val sT1 = readInputAsString("09_test")
        val dT1 = toDisk(sT1)
        val dT1S = diskToString(dT1)
        println(dT1S)
        check(dT1S == "00...111...2...333.44.5555.6666.777.888899")
        val dT2 = defrag(dT1)
        val dT2S = diskToString(dT2)
        check(dT2S == "0099811188827773336446555566..............")
        val csDT2 = checksum(dT2)
        check(csDT2 == 1928L)

        val s1 = readInputAsString("09")
        val d1 = toDisk(s1)
        val d2 = defrag(d1)
        val csD2 = checksum(d2)
        println("part 1 answer: $csD2")

        val dT3 = defrag2(dT1)
        val dT3S = diskToString(dT3)
        check(dT3S == "00992111777.44.333....5555.6666.....8888..")
        val csDT3 = checksum(dT3)
        check(csDT3 == 2858L)

        val d3 = defrag2(d1)
        val csD3 = checksum(d3)
        println("part 2 answer: $csD3")
    }
    println(dt)
}