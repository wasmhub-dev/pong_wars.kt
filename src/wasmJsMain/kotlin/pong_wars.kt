import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import kotlin.math.*
import kotlin.random.Random


const val SQUARE_SIZE = 25

const val DAY_COLOR = "#D9E8E3"
const val DAY_BALL_COLOR = "#114C5A"

const val NIGHT_COLOR = "#114C5A"
const val NIGHT_BALL_COLOR = "#D9E8E3"

const val MIN_SPEED = 5f
const val MAX_SPEED = 10f

class Ball(var x: Float, var y: Float, var dx: Float, var dy: Float, val reverseColor: String = DAY_COLOR, val ballColor: String = DAY_BALL_COLOR)

class PongWars: Drawable {

    private val canvas: HTMLCanvasElement = document.getElementById("pongCanvas") as HTMLCanvasElement
    private val scoreElement = document.getElementById("score") as HTMLDivElement
    private val ctx: CanvasRenderingContext2D = canvas.getContext("2d") as CanvasRenderingContext2D

    private val numSquaresX: Int = canvas.width / SQUARE_SIZE
    private val numSquaresY: Int = canvas.height / SQUARE_SIZE

    private val squares: Array<Array<String>> = Array(numSquaresX) { Array(numSquaresY) { DAY_COLOR } }

    private val balls: List<Ball> = listOf(
        Ball(canvas.width / 4f, canvas.height / 2f, 12.5f, -12.5f, DAY_COLOR, DAY_BALL_COLOR),
        Ball(canvas.width / 4f * 3, canvas.height / 2f, -12.5f, 12.5f, NIGHT_COLOR, NIGHT_BALL_COLOR)
    )

    init {
        for (i in 0..<numSquaresX) {
            for (j in 0..<numSquaresY) {
                if (i >= numSquaresX / 2) {
                    squares[i][j] = NIGHT_COLOR
                }
            }
        }
    }

    private fun drawBall(ball: Ball) {
        ctx.beginPath()
        ctx.arc(ball.x.toDouble(), ball.y.toDouble(), (SQUARE_SIZE / 2f).toDouble(), 0.0, 2 * PI)
        ctx.fillStyle = ball.ballColor.toJsString()
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


    private fun checkSquareCollision(ball: Ball) {
        var angle = 0.0
        while (angle < PI * 2) {

            val checkX = ball.x + cos(angle) * SQUARE_SIZE / 2f
            val checkY = ball.y + sin(angle) * SQUARE_SIZE / 2f
            val i = (checkX / SQUARE_SIZE).toInt()
            val j = (checkY / SQUARE_SIZE).toInt()

            if (i in 0..< numSquaresX && j in 0..< numSquaresY) {
                if (squares[i][j] !== ball.reverseColor) {
                    squares[i][j] = ball.reverseColor

                    if (abs(cos(angle)) > abs(sin(angle))) {
                        ball.dx = -ball.dx
                    } else {
                        ball.dy = -ball.dy
                    }
                }

            }

            angle += PI / 4f
        }
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

    private fun checkBoundaryCollision(ball: Ball) {
        if (ball.x + ball.dx > canvas.width - SQUARE_SIZE / 2f || ball.x + ball.dx < SQUARE_SIZE / 2f) {
            ball.dx = -ball.dx
        }

        if (ball.y + ball.dy > canvas.height - SQUARE_SIZE / 2f || ball.y + ball.dy < SQUARE_SIZE / 2f) {
            ball.dy = -ball.dy
        }
    }

    private fun addRandomness(ball: Ball) {
        ball.dx += (Random.nextInt(0, 100) / 100f) * 0.01f - 0.005f
        ball.dy += (Random.nextInt(0, 100) / 100f) * 0.01f - 0.005f

        ball.dx = min(max(ball.dx, -MAX_SPEED), MAX_SPEED)
        ball.dy = min(max(ball.dy, -MAX_SPEED), MAX_SPEED)

        if (abs(ball.dx) < MIN_SPEED) {
            ball.dx = if (ball.dx > 0) MIN_SPEED else -MIN_SPEED
        }

        if (abs(ball.dy) < MIN_SPEED) {
            ball.dy = if (ball.dy > 0) MIN_SPEED else -MIN_SPEED
        }
    }


    override fun draw() {
        ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
        drawSquares()

        updateScoreElement()

        balls.forEach { ball ->
            drawBall(ball)

            checkSquareCollision(ball)
            checkBoundaryCollision(ball)
            addRandomness(ball)

            ball.x += ball.dx
            ball.y += ball.dy

            addRandomness(ball)
        }
    }
}