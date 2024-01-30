import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


const val SQUARE_SIZE = 25

const val DAY_COLOR = "#D9E8E3"
const val DAY_BALL_COLOR = "#114C5A"

const val NIGHT_COLOR = "#114C5A"
const val NIGHT_BALL_COLOR = "#D9E8E3"

class PongWars: Drawable {

    private val canvas: HTMLCanvasElement = document.getElementById("pongCanvas") as HTMLCanvasElement
    private val scoreElement = document.getElementById("score") as HTMLDivElement
    private val ctx: CanvasRenderingContext2D = canvas.getContext("2d") as CanvasRenderingContext2D

    private val numSquaresX: Int = canvas.width / SQUARE_SIZE
    private val numSquaresY: Int = canvas.height / SQUARE_SIZE

    private val squares: Array<Array<String>> = Array(numSquaresX) { Array(numSquaresY) { DAY_COLOR } }

    private var x1: Float
    private var y1: Float
    private var dx1: Float
    private var dy1: Float

    private var x2: Float
    private var y2: Float
    private var dx2: Float
    private var dy2: Float

    init {
        for (i in 0..<numSquaresX) {
            for (j in 0..<numSquaresY) {
                squares[i][j] = if (i < numSquaresX / 2) DAY_COLOR else NIGHT_COLOR
            }
        }
        x1 = canvas.width / 4f
        y1 = canvas.height / 2f
        dx1 = 12.5f
        dy1 = -12.5f
        x2 = canvas.width / 4f * 3
        y2 = canvas.height / 2f
        dx2 = -12.5f
        dy2 = 12.5f
    }

    private fun drawBall(x: Float, y: Float, color: String) {
        ctx.beginPath()
        ctx.arc(x.toDouble(), y.toDouble(), (SQUARE_SIZE / 2f).toDouble(), 0.0, 2 * PI)
        ctx.fillStyle = color.toJsString()
        ctx.fill()
        ctx.closePath()
    }

    private fun drawSquares() {
        for (i in 0..<numSquaresX) {
            for (j in 0..<numSquaresY) {
                ctx.fillStyle = squares[i][j].toJsString()
                ctx.fillRect(
                    (i * SQUARE_SIZE).toDouble(),
                    (j * SQUARE_SIZE).toDouble(),
                    SQUARE_SIZE.toDouble(),
                    SQUARE_SIZE.toDouble()
                )
            }
        }
    }


    private fun updateSquareAndBounce(x: Float, y: Float, dx: Float, dy: Float, color: String): Pair<Float, Float> {
        var updatedDx = dx
        var updatedDy = dy

        var angle = 0.0
        while (angle < PI * 2) {

            val checkX = x + cos(angle) * SQUARE_SIZE / 2f
            val checkY = y + sin(angle) * SQUARE_SIZE / 2f
            val i = (checkX / SQUARE_SIZE).toInt()
            val j = (checkY / SQUARE_SIZE).toInt()

            if (i in 0..< numSquaresX && j in 0..< numSquaresY) {
                if (squares[i][j] !== color) {
                    squares[i][j] = color

                    if (abs(cos(angle)) > abs(sin(angle))) {
                        updatedDx = -updatedDx
                    } else {
                        updatedDy = -updatedDy
                    }
                }

            }

            angle += PI / 4f
        }

        return Pair(updatedDx, updatedDy)
    }

    private fun updateScoreElement() {
        var dayScore = 0
        var nightScore = 0
        for (i in 0..<numSquaresX) {
            for (j in 0..<numSquaresY) {
                if (squares[i][j] === DAY_COLOR) {
                    dayScore++
                } else {
                    nightScore++
                }
            }
        }

        scoreElement.textContent = "day $dayScore | night $nightScore"
    }

    private fun checkBoundaryCollision(x: Float, y: Float, dx: Float, dy: Float): Pair<Float, Float> {
        var updatedDx = dx
        var updatedDy = dy
        if (x + dx > canvas.width - SQUARE_SIZE / 2f || x + dx < SQUARE_SIZE / 2f) {
            updatedDx = -dx
        }

        if (y + dy > canvas.height - SQUARE_SIZE / 2f || y + dy < SQUARE_SIZE / 2f) {
            updatedDy = -dy
        }

        return Pair(updatedDx, updatedDy)

    }


    override fun draw() {
        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
        drawSquares()
        drawBall(x1, y1, DAY_BALL_COLOR)
        val bounce1 = updateSquareAndBounce(x1, y1, dx1, dy1, DAY_COLOR)
        dx1 = bounce1.first
        dy1 = bounce1.second

        drawBall(x2, y2, NIGHT_BALL_COLOR)
        val bounce2 = updateSquareAndBounce(x2, y2, dx2, dy2, NIGHT_COLOR)
        this.dx2 = bounce2.first
        this.dy2 = bounce2.second

        val boundary1 = checkBoundaryCollision(x1, y1, dx1, dy1)
        dx1 = boundary1.first
        dy1 = boundary1.second

        val boundary2 = checkBoundaryCollision(x2, y2, dx2, dy2)
        dx2 = boundary2.first
        dy2 = boundary2.second

        x1 += dx1
        y1 += dy1
        x2 += dx2
        y2 += dy2

        updateScoreElement()
    }
}