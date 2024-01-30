import kotlinx.browser.window

interface Drawable {
    fun draw()
}

fun recursiveDraw(drawable: Drawable) {
    drawable.draw()
    window.requestAnimationFrame {
        recursiveDraw(drawable)
    }
}