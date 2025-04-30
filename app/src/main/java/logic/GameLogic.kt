package logic


import android.widget.ImageView
import com.example.project1_game.R

class GameLogic(
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