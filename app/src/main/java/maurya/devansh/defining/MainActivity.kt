package maurya.devansh.defining

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.backgroundColor
import androidx.core.text.buildSpannedString
import maurya.devansh.defining.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val wildcardPairs = arrayListOf<Pair<Int, Int>>()

    private val spanWildcard = "*"
    private val text = "”Space,” it says, “is big. Really big. You just won’t believe how *vastly*, hugely, *mindbogglingly* big it is. I mean, you may think it’s a long way down the road to the chemist’s, but that’s just peanuts to space.”"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.textView.text = text

        val wildcardIndices = arrayListOf<Int>()
        var index = text.indexOf(spanWildcard)
        while (index != -1) {
            wildcardIndices.add(index)
            index = text.indexOf(spanWildcard, index + 1)
        }

        wildcardPairs.clear()
        for (i in 0 until wildcardIndices.size step 2) {
            val pair = Pair(wildcardIndices[i], wildcardIndices[i + 1])
            wildcardPairs.add(pair)
        }

        buildSpannedString {
            var plainTextStart = 0

            // Remove stars as formatted text is built
            wildcardPairs.forEach {
                val (wildcardStart, wildcardEnd) = it //start and end for word

                val plainText = text.substring(plainTextStart until wildcardStart)
                if (plainText.isNotEmpty()) {
                    append(plainText)
                }
                plainTextStart = wildcardEnd + 1

                backgroundColor(Color.YELLOW) {
                    append(text.substring(wildcardStart + 1, wildcardEnd))
                }
            }
            val plainText = text.substring(plainTextStart)
            if (plainText.isNotEmpty()) {
                append(plainText)
            }
        }.also { binding.textView.text = it }

        binding.textView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val touchPoint = event?.getTouchPoint() ?: PointF(-1f, -1f)
                val offsetForPosition =
                    binding.textView.getOffsetForPosition(touchPoint.x, touchPoint.y)
                val textOffsetForTouch: Int = binding.textView.layout.run {
                    val line: Int = getLineForVertical(touchPoint.y.toInt())
                    val offset: Int = getOffsetForHorizontal(line, touchPoint.x)
                    offset
                }
                //Both offsetForPosition and textOffsetForTouch are giving the same value,
                //although answers at StackOverflow recommend the later approach citing that there
                //are some bugs in using the former method. Will have to test.

                wildcardPairs.forEach { (start, end) ->
                    val word = text.substring(start + 1, end)
                    if (textOffsetForTouch in start..end) {
                        toast("Touching on word: $word")
                    }
                }

                binding.tvExactLocation.setTouchLocationText("Exact", PointF(event.rawX, event.rawY))
                binding.tvCalculatedLocation.setTouchLocationText("Relative", touchPoint)
            }
            true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchPoint = event?.getTouchPoint() ?: PointF(-1f, -1f)
        binding.tvExactLocation.setTouchLocationText("Exact", touchPoint)
        return super.onTouchEvent(event)
    }

    private fun TextView.setTouchLocationText(title: String, point: PointF?) {
        text = "$title location\n(${point?.x}, ${point?.y})"
    }

    private fun MotionEvent.getTouchPoint(): PointF = PointF(x, y)

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}