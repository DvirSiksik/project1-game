package manager

data class GameState(
    var currentLane: Int = 1,
    var lives: Int = 3,
    var score: Int = 0,
    var isGameRunning: Boolean = true
)