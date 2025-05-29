package manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import android.media.MediaPlayer
import android.widget.*
import com.bumptech.glide.Glide
import com.example.project1_game.GameOverActivity
import com.example.project1_game.R
import kotlinx.coroutines.*
import logic.GameLogic
import utils.VibrationUtils

class GameManager(
    private val activity: Activity,
    private val useSensorMode: Boolean,
    private val isFastMode: Boolean
){

    private lateinit var grid: Array<Array<ImageView>>
    private lateinit var coinGrid: Array<Array<ImageView>>
    private lateinit var player: ImageView
    private lateinit var hearts: Array<ImageView>
    private lateinit var scoreText: TextView

    private var currentLane = 1
    private var lives = 3
    private var score = 0

    private val numRows = 7
    private val numCols = 5

    private var dropInterval = if (isFastMode) 700L else 1500L
    private var dropSpeedPerRowDelay = if (isFastMode) 150L else 300L


    private var isGameRunning = true
    private var gameLoopJob: Job? = null
    private var currentToast: Toast? = null

    private val logic = GameLogic(numCols)
    private val vibrate = VibrationUtils

    // Sensor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var hitSound: MediaPlayer? = null
    private var coinSound: MediaPlayer? = null

    private enum class TiltState { LEFT, CENTER, RIGHT }
    private var lastTilt: TiltState = TiltState.CENTER

    fun initGame() {
        initViews()
        if (useSensorMode) {
            setupSensorControls()
            hideControlButtons()
        } else {
            setupButtonControls()
        }
        startObstacleLoop()
    }

    private fun initViews() {
        player = activity.findViewById(R.id.player)
        Glide.with(activity.applicationContext).asGif().load(R.drawable.player).into(player)

        hitSound = MediaPlayer.create(activity, R.raw.hit_sound)
        coinSound = MediaPlayer.create(activity, R.raw.coin_sound)

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

        coinGrid = Array(numRows) { row ->
            Array(numCols) { col ->
                val resId = activity.resources.getIdentifier("main_COIN_${row}${col}", "id", activity.packageName)
                val imageView = activity.findViewById<ImageView>(resId)

                Glide.with(activity.applicationContext)
                    .asGif()
                    .load(R.drawable.coin)
                    .into(imageView)

                imageView.visibility = View.GONE
                imageView
            }
        }

        updatePlayerPosition()
    }

    private fun setupButtonControls() {
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

    private fun hideControlButtons() {
        activity.findViewById<Button>(R.id.btnLeft).visibility = View.GONE
        activity.findViewById<Button>(R.id.btnRight).visibility = View.GONE
    }

    private fun setupSensorControls() {
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME)
        } ?: run {
            Toast.makeText(activity, "No accelerometer found!", Toast.LENGTH_LONG).show()
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return
            val x = event.values[0]

            val newTilt = when {
                x > 4 -> TiltState.LEFT
                x < -4 -> TiltState.RIGHT
                else -> TiltState.CENTER
            }

            if (newTilt != lastTilt) {
                lastTilt = newTilt
                when (newTilt) {
                    TiltState.LEFT -> {
                        if (currentLane > 0) {
                            currentLane--
                            updatePlayerPosition()
                        }
                    }
                    TiltState.RIGHT -> {
                        if (currentLane < numCols - 1) {
                            currentLane++
                            updatePlayerPosition()
                        }
                    }
                    TiltState.CENTER -> {}
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun updatePlayerPosition() {
        val params = player.layoutParams as androidx.gridlayout.widget.GridLayout.LayoutParams
        params.columnSpec = androidx.gridlayout.widget.GridLayout.spec(currentLane)
        player.layoutParams = params
    }

    private fun startObstacleLoop() {
        gameLoopJob = CoroutineScope(Dispatchers.Main).launch {
            while (isGameRunning) {
                dropEntity()
                delay(dropInterval)
            }
        }
    }

    private fun dropEntity() {
        if (!isGameRunning) return
        val col = logic.getRandomColumn()
        val isCoin = (0..3).random() == 0 // 25% chance

        for (row in 0 until numRows) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(row * dropSpeedPerRowDelay)
                if (!isGameRunning) return@launch

                if (row > 0) {
                    logic.clearCell(grid[row - 1][col])
                    coinGrid[row - 1][col].visibility = View.GONE
                }

                if (isCoin) {
                    coinGrid[row][col].visibility = View.VISIBLE
                } else {
                    logic.showObstacle(grid[row][col])
                }

                if (row == numRows - 1 && col == currentLane) {
                    if (isCoin) {
                        coinGrid[row][col].visibility = View.GONE
                        coinSound?.start()
                        increaseScore()
                    } else {
                        hitSound?.start()
                        vibrate.vibrate(activity)
                        loseLife()
                    }
                } else if (row == numRows - 1 && isCoin) {
                    coinGrid[row][col].visibility = View.GONE
                } else if (row == numRows - 1) {
                    increaseScore()
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(numRows * dropSpeedPerRowDelay)
            if (isGameRunning) {
                logic.clearCell(grid[numRows - 1][col])
                coinGrid[numRows - 1][col].visibility = View.GONE
            }
        }
    }
    private fun increaseScore() {
        score++
        showToast("Good Job! score: $score")
        scoreText.text = "Score: $score"
    }



    private fun loseLife() {
        if (lives > 0) {
            lives--
            hearts[lives].visibility = View.INVISIBLE
            showToast("Ouch! Lives left: $lives")

            if (lives == 0) {
                isGameRunning = false
                gameLoopJob?.cancel()

                val intent = Intent(activity, GameOverActivity::class.java)
                intent.putExtra("SCORE", score)
                intent.putExtra("USE_SENSOR_MODE", useSensorMode)
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
        hitSound?.release()
        coinSound?.release()
        hitSound = null
        coinSound = null
        if (useSensorMode) {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}