package maurya.devansh.defining

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import maurya.devansh.defining.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        binding.tvExactLocation.setTouchLocationText("Exact", event?.getTouchPoint())
        return super.onTouchEvent(event)
    }

    private fun TextView.setTouchLocationText(title: String, point: PointF?) {
        text = "$title location\n(${point?.x}, ${point?.y})"
    }

    private fun MotionEvent.getTouchPoint(): PointF = PointF(rawX, rawY)
}