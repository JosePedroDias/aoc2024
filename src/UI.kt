import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.time.Instant
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.random.Random

private const val W = 800
private const val H = 600
private const val R = 50

// TODO: still super janky in linux

fun main() {
    val circleColor = Color(0, 150, 255)

    val frame = JFrame("AWT Smooth Animation")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(W, H)

    val canvas = object : JPanel() {
        val pos = arrayOf(R, R)
        val vel = arrayOf(
            Random.nextInt(-4, 4),
            Random.nextInt(-4, 4),
        )
        var lastMs = System.currentTimeMillis()

        init {
            if (vel[0] == 0) ++vel[0]
            if (vel[1] == 0) --vel[1]

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    pos[0] = Random.nextInt(R, W - R)
                    pos[1] = Random.nextInt(R, H - R)
                }
            })

            addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ESCAPE) frame.dispose()
                }
            })

            isFocusable = true // ensure the panel can receive key events
            frame.isVisible = true

            // use Toolkit to schedule repaints during idle time
            Toolkit.getDefaultToolkit().setDynamicLayout(true)
            Thread {
                while (frame.isVisible) {
                    val tMs = System.currentTimeMillis()
                    val dtMs = tMs - lastMs
                    lastMs = tMs
                    //println("t:$tMs | dt: $dtMs")

                    updateState(dtMs)
                    repaint()

                    Thread.sleep(25) // ~40 FPS
                    //Thread.sleep(2) // ~40 FPS
                }
            }.start()
        }

        private fun updateState(dtMs: Long) {
            pos[0] += (vel[0] * dtMs / 6).toInt()
            pos[1] += (vel[1] * dtMs / 6).toInt()

            // Reflect if out of bounds
            if (pos[0] - R < 0 || pos[0] + R >= W) vel[0] *= -1
            if (pos[1] - R < 0 || pos[1] + R >= H) vel[1] *= -1
            // println("${pos[0]},${pos[1]}")
        }

        //override fun isOptimizedDrawingEnabled(): Boolean {repaint(x, y, width, height)
        //    return false
        //}

        override fun paintComponent(g: Graphics) {
            //println(Instant.now())
            super.paintComponent(g)
            val g2d = g as Graphics2D
            g2d.color = circleColor
            //g2d.fillRect(pos[0], pos[1], 2*R, 2*R)
            g2d.fillOval(pos[0] - R, pos[1] - R, 2 * R, 2 * R)
            parent.repaint(0, 0, W, H)
        }
    }

    frame.add(canvas)
}
