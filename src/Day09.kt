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

/*
    A a        b B
    NNN        444

00...111...2...333.44.5555.6666.777.888899          B:41  99   40-41 ->  2- 4  b=40-1
0099.111...2...333.44.5555.6666.777.8888..          B:39  8888 36-39 -> NO     b=36-1
                                                    B:35  777  32-34 ->  9-11  b=32-1
0099.1117772...333.44.5555.6666.....8888..          B:31  6666 27-30 -> NO     b=27-1
                                                    B:26  5555 22-25 -> NO     b=22-1
                                                    B:21  44   19-20 -> 12-13  b=19-1
0099.111777244.333....5555.6666.....8888..          B:18  333  15-17 -> NO     b=15-1
00992111777.44.333....5555.6666.....8888..          B:14  2    11-11 ->  4- 4  b=11-1
                                                    B:10  111   5- 7 -> NO     b= 5-1
                                                    B: 4  00    0- 1 ->
*/
private fun defrag2(disk0: Disk): Disk {
    val disk = disk0.clone()

    var candidateEnd = disk.size - 1 // from index (value is not null)

    // find last vb other than null
    var ivb = candidateEnd
    while (disk[ivb] == null) { --ivb }
    var vb = disk[ivb]!!

    while (candidateEnd >= 0) {
        //println(diskToString(disk))

        // find candidate
        while (disk[candidateEnd] != vb) { --candidateEnd }
        var candidateStart = candidateEnd
        while (candidateStart > 0 && disk[candidateStart-1] == vb) { --candidateStart }
        val candidateSize = candidateEnd - candidateStart + 1
        //println("** candidate of $vb's [$candidateStart,$candidateEnd] (len: $candidateSize) **")

        // find gap
        var gapStart = 0
        var gapSize = 0
        do {
            while (disk[gapStart] != null) { ++gapStart }
            var gapEnd = gapStart
            while (gapEnd + 1 < disk.size && disk[gapEnd + 1] == null) { ++gapEnd }
            if (gapEnd - gapStart + 1 < candidateSize) {
                //println("( gap [$gapStart, $gapEnd] is too small )")
                if (gapEnd + 1 > candidateStart) { break }
                gapStart = gapEnd + 1
            } else if (gapEnd > candidateStart) {
                //println("( gap only to the right )")
                break
            } else {
                gapSize = gapEnd - gapStart + 1
                //println("-> found gap of null's [$gapStart,$gapEnd] (len: ${gapEnd - gapStart + 1})")
            }
        } while (gapSize == 0)

        if (gapSize > 0) {
            // fits: fill it and move nonNullStart forward
            //println("-> fits")
            for (i in 0 until candidateSize) {
                disk[candidateEnd - i] = null
                disk[gapStart + i] = vb
            }
        } else {
            // does not fit: ignore candidate
            //println("-> no gap!")
        }

        --vb
        if (vb < 0) {
            //println("all done")
            return disk
        }
        candidateEnd = candidateStart - 1
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
        //println(dT1S)
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