package manager

data class HighScore(
    val score: Int,
    val timestamp: Long,
    val latitude: Double? = null,
    val longitude: Double? = null
)