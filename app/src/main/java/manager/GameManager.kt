package manager

import android.app.Activity
import android.content.Intent
import android.widget.*
import com.bumptech.glide.Glide
import com.example.project1_game.GameOverActivity
import com.example.project1_game.R
import kotlinx.coroutines.*
import logic.GameLogic
import utils.VibrationUtils

class GameManager(private val activity: Activity) {

    private lateinit var grid: Array<Array<ImageView>>
    private lateinit var jewish: ImageView
    private lateinit var hearts: Array<ImageView>
    private lateinit var scoreText: TextView

    private var currentLane = 1
    private var lives = 3
    private var score = 0

    private val numRows = 4
    private val numCols = 3

    private var dropInterval = 1500L
    private var dropSpeedPerRowDelay = 300L
    private val minDropInterval = 300L
    private val minRowDelay = 80L
    private val dropAcceleration = 50L
    private val rowDelayAcceleration = 20L

    private var isGameRunning = true
    private var gameLoopJob: Job? = null
    private var currentToast: Toast? = null

    private val logic = GameLogic(numRows, numCols)
    private val vibrate = VibrationUtils

    fun initGame() {
        initViews()
        setupControls()
        startObstacleLoop()
    }

    private fun initViews() {
        jewish = activity.findViewById(R.id.jewish)
        Glide.with(activity).asGif().load(R.drawable.jewish).into(jewish)

        hearts = arrayOf(
            activity.findViewById(R.id.main_IMG_heart0),
            activity.findViewById(R.id.main_IMG_heart1),
            activity.findViewById(R.id.main_IMG_heart2)
        )

        scoreText = activity.findViewById(R.id.main_LBL_score)

        grid = Array(numRows) { row ->
            Array(numCols) { col ->
                val resId = activity.resources.getIdentifier("main_IMG_${row}${col}", "id", activity.packageName)
                val cell = activity.findViewById<ImageView>(resId)
                logic.clearCell(cell)
                cell
            }
        }

        updatePlayerPosition()
    }

    private fun setupControls() {
        activity.findViewById<Button>(R.id.btnLeft).setOnClickListener {
            if (currentLane > 0) {
                currentLane--
                updatePlayerPosition()
            }
        }

        activity.findViewById<Button>(R.id.btnRight).setOnClickListener {
            if (currentLane < numCols - 1) {
                currentLane++
                updatePlayerPosition()
            }
        }
    }

    private fun updatePlayerPosition() {
        val params = jewish.layoutParams as GridLayout.LayoutParams
        params.columnSpec = GridLayout.spec(currentLane)
        jewish.layoutParams = params
    }

    private fun startObstacleLoop() {
        gameLoopJob = CoroutineScope(Dispatchers.Main).launch {
            while (isGameRunning) {
                dropObstacle()
                delay(dropInterval)
            }
        }
    }

    private fun dropObstacle() {
        if (!isGameRunning) return
        val col = logic.getRandomColumn()

        for (row in 0 until numRows) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(row * dropSpeedPerRowDelay)
                if (!isGameRunning) return@launch

                if (row > 0) {
                    logic.clearCell(grid[row - 1][col])
                }

                logic.showObstacle(grid[row][col])

                if (row == numRows - 1 && col == currentLane) {
                    vibrate.vibrate(activity)
                    loseLife()
                } else if (row == numRows - 1) {
                    increaseScore()
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(numRows * dropSpeedPerRowDelay)
            if (isGameRunning) {
                logic.clearCell(grid[numRows - 1][col])
            }
        }
    }

    private fun increaseScore() {
        score++
        showToast("Good Job! score: $score")
        scoreText.text = "Score: $score"
        updateDifficulty()
    }

    private fun updateDifficulty() {
        if (score % 5 == 0) {
            if (dropInterval > minDropInterval) {
                dropInterval -= dropAcceleration
            }
            if (dropSpeedPerRowDelay > minRowDelay) {
                dropSpeedPerRowDelay -= rowDelayAcceleration
            }
        }
    }

    private fun loseLife() {
        if (lives > 0) {
            lives--
            hearts[lives].visibility = android.view.View.INVISIBLE
            showToast("Ouch! Lives left: $lives")

            if (lives == 0) {
                isGameRunning = false
                gameLoopJob?.cancel()

                val intent = Intent(activity, GameOverActivity::class.java)
                intent.putExtra("SCORE", score)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }

    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    fun cleanup() {
        gameLoopJob?.cancel()
    }
}