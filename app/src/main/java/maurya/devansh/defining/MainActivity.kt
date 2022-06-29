package maurya.devansh.defining

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import maurya.devansh.defining.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val spanWildcard = "*"
        val text = "”Space,” it says, “is big. Really big. You just won’t believe how *vastly*, hugely, *mindbogglingly* big it is. I mean, you may think it’s a long way down the road to the chemist’s, but that’s just peanuts to space.”"

        val wildcardIndices = arrayListOf<Int>()
        var index = text.indexOf(spanWildcard)
        while (index != -1) {
            wildcardIndices.add(index)
            index = text.indexOf(spanWildcard, index + 1)
        }

        val wildcardPairs = arrayListOf<Pair<Int, Int>>()

        for (i in 0 until wildcardIndices.size step 2) {
            val pair = Pair(wildcardIndices[i], wildcardIndices[i + 1])
            wildcardPairs.add(pair)
        }

        Log.d(TAG, "wildcardPairs: $wildcardPairs")
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