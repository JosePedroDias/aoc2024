import com.microsoft.z3.*

fun solveEq(
    v1: Pair<Long, Long>,
    v2: Pair<Long, Long>,
    goal: Pair<Long, Long>
): Pair<Long, Long>? {
    val ctx = Context()
    try {
        // variables a and b
        val a = ctx.mkIntConst("a")
        val b = ctx.mkIntConst("b")

        // equation constraints
        val v1x = ctx.mkInt(v1.first)
        val v1y = ctx.mkInt(v1.second)
        val v2x = ctx.mkInt(v2.first)
        val v2y = ctx.mkInt(v2.second)
        val goalX = ctx.mkInt(goal.first)
        val goalY = ctx.mkInt(goal.second)

        // constraints:
        // #1: a * v1_x + b * v2_x = goal_x
        // #2: a * v1_y + b * v2_y = goal_y

        val c1 = ctx.mkEq(ctx.mkAdd(ctx.mkMul(a, v1x), ctx.mkMul(b, v2x)), goalX)
        val c2 = ctx.mkEq(ctx.mkAdd(ctx.mkMul(a, v1y), ctx.mkMul(b, v2y)), goalY)
        val c3 = ctx.mkGe(a, ctx.mkInt(0))
        val c4 = ctx.mkGe(b, ctx.mkInt(0))

        // solver setup
        val solver = ctx.mkSolver()
        solver.add(c1, c2, c3, c4)

        // check satisfiability
        return if (solver.check() == Status.SATISFIABLE) {
            val model = solver.model
            val aValue = model.eval(a, true).toString().toLong()
            val bValue = model.eval(b, true).toString().toLong()
            aValue to bValue
        } else {
            null
        }
    } finally {
        ctx.close()
    }
}

/*
fun main() {
    val result = solveEq(Pair(94L, 34L), Pair(22L, 67L), Pair(8400L, 5400L))
    if (result != null) {
        val (a, b) = result
        println("Solution: a=$a, b=$b")
    } else {
        println("No integer solution exists.")
    }
}
*/