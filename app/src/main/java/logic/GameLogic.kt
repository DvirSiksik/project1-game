package logic

import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.ImageView
import com.example.project1_game.R

class GameLogic(
    private val numRows: Int,
    private val numCols: Int
) {
    fun clearCell(cell: ImageView) {
        cell.visibility = ImageView.INVISIBLE
    }

    fun showObstacle(cell: ImageView) {
        cell.setImageResource(R.drawable.obstacle)
        cell.visibility = ImageView.VISIBLE
    }

    fun getRandomColumn(): Int = (0 until numCols).random()

}