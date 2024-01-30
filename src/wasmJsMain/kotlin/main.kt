import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement

fun main() {
    val canvas: HTMLCanvasElement = document.getElementById("pongCanvas") as HTMLCanvasElement
    val scoreElement = document.getElementById("score") as HTMLDivElement
    val pongWars = PongWars(canvas, scoreElement)
    draw(pongWars)
}

fun draw(pongWars: PongWars) {
    pongWars.draw()
    window.requestAnimationFrame {
        draw(pongWars)
    }
}
