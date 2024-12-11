import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.measureTime

/*
0 -> 1
even nr of digits -> left half (more significant) | right half (less significant)
else -> 2024 x

nr of stones after 25 iterations?

#0     0     1  10    99  999                                                                          [ 5]
#1     1  2024   1     0    9      9  2021976                                                          [ 7]
#2  2024    20  24  2024    1  18216    18216  4092479424                                              [ 8]
#3    20    24   2     0    2      4       20          24  2024  36869184  36869184  40924  79424      [13]
*/

private data class Node(var value: Long, var next: Node? = null)

private class LList {
    private var head: Node? = null
    private var prev: Node? = null
    private var curr: Node? = null

    fun rewind() {
        prev = null
        curr = head
    }

    fun change(value: Long) {
        if (curr == null) {
            throw Error("empty?")
        }
        curr!!.value = value
    }

    fun add(value: Long) {
        if (curr != null && curr!!.next != null) {
            throw Error("unsupported: not at the last node")
        }
        val newNode = Node(value)
        if (curr != null) {
            if (prev != null) {
                prev!!.next = curr
            }
            prev = curr
        }
        if (curr != null) {
            curr!!.next = newNode
        }
        if (head == null) {
            head = newNode
        }
        curr = newNode
    }

    fun addBefore(value: Long) {
        val newNode = Node(value)
        if (head == null) {
            throw Error("??")
        }
        if (head == curr) {
            head = newNode
        }
        if (prev != null) {
            prev!!.next = newNode
        }
        if (curr != null) {
            //curr!!.next = newNode
            newNode.next = curr
        }
        curr = newNode
        next()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        var current = head
        while (current != null) {
            sb.append(current.value)
            sb.append(", ")
            current = current.next
        }
        return sb.toString()
    }

    fun getCurrentValue(): Long? {
        //println("curr: $curr")
        return curr?.value
    }

    fun next(): Boolean {
        if (curr == null) { return false }
        prev = curr
        curr = curr!!.next
        return true
    }

    fun size(): Int {
        var count = 0
        var current = head
        while (current != null) {
            ++count
            current = current.next
        }
        return count
    }
}

private fun parse(line: String, l: LList) {
    return line.split(" ").forEach { l.add(it.toLong()) }
}

private fun nrDigits(n: Long): Int {
    return if (n == 0L) 0 else log10(n.toDouble()).toInt() + 1
}

private fun splitEvenlySizedNumber(n: Long): Pair<Long, Long> {
    val nrDi = nrDigits(n)
    val order = 10.0.pow(nrDi/2).toInt()
    val a = n / order
    val b = n - a * order
    return Pair(a, b)
}

private fun blinkNTimes(line: String, times: Int): Int {
    val stones = LList()
    println("line: $line")
    parse(line, stones)
    var t = 0
    repeat(times) {
        ++t
        println("#$t")
        //memUsage()
        stones.rewind()
        //println(stones)
        var n: Long?
        while (true) {
            n = stones.getCurrentValue()
            //println("n:$n")
            if (n == null) { break }

            when {
                n == 0L -> stones.change(1)
                nrDigits(n) % 2 == 0 -> {
                    val (a, b) = splitEvenlySizedNumber(n)
                    stones.addBefore(a)
                    stones.change(b)
                }
                else -> stones.change(2024 * n)
            }
            //println(stones)

            stones.next()
        }
    }
    //println(stones)
    return stones.size()
}

fun main() {
    val dt = measureTime {
        check(nrDigits(2314) == 4)
        check(splitEvenlySizedNumber(2314L).first == 23L)
        check(splitEvenlySizedNumber(2314L).second == 14L)

        var l = LList()
        check(l.size() == 0)
        check(l.toString() == "")
        l.add(11)
        check(l.size() == 1)
        check(l.toString() == "11, ")
        l.add(22)
        check(l.size() == 2)
        check(l.toString() == "11, 22, ")

        l = LList()
        check(l.size() == 0)
        l.add(111)
        l.add(222)
        l.add(333)
        l.rewind()
        check(l.size() == 3)
        check(l.toString() == "111, 222, 333, ")
        l.change(1111)
        check(l.size() == 3)
        check(l.toString() == "1111, 222, 333, ")
        l.addBefore(77) // FAILS
        check(l.size() == 4)
        check(l.toString() == "77, 1111, 222, 333, ")

        l = LList()
        check(l.size() == 0)
        l.add(111)
        l.add(222)
        l.add(333)
        check(333L == l.getCurrentValue())
        l.rewind()
        check(111L == l.getCurrentValue())
        l.next()
        check(222L == l.getCurrentValue())
        l.addBefore(22)
        check(l.size() == 4)
        check(l.toString() == "111, 22, 222, 333, ")
        l.change(66)
        check(l.size() == 4)
        check(l.toString() == "111, 22, 66, 333, ")

        //println("\n\n\n ********** \n\n\n")

        //check(22 == blinkNTimes("125 17", 6))

        //check(55312 == blinkNTimes(readInputAsString("11_test"), 25))

        //println("part 1 answer: ${blinkNTimes(readInputAsString("11"), 25)}")
        println("part 2 answer: ${blinkNTimes(readInputAsString("11"), 75)}") // breaks at 36

        //memUsage()
    }
    println(dt)
}