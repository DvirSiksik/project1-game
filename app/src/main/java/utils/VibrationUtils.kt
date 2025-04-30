package utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationUtils {
    fun vibrate(context: Context, duration: Long = 300) {
        val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (it as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }

        vibrator?.let {
            if (it.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(duration)
                }
            }
        }
    }
}