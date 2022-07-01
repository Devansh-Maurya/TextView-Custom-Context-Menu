package maurya.devansh.defining

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.backgroundColor
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import maurya.devansh.defining.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val wildcardPairs = arrayListOf<Pair<Int, Int>>()

    private val spanWildcard = "*"
    private val text = "”Space,” it says, “is big. Really big. You just won’t believe how *vastly*, hugely, *mindbogglingly* big it is. I mean, you may think it’s a long way down the road to the chemist’s, but that’s just *peanuts* to space.”"

    private val dictionary = mapOf(
        "vastly" to "To a very great extent; immensely.",
        "mindbogglingly" to "In a mindboggling manner; in such a way as to boggle the mind; so as to be beyond comprehension or understanding",
        "peanuts" to "the oval seed of a tropical South American plant, often roasted and salted and eaten as a snack or used to make oil or animal feed."
    )

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
                val touchPoint = event.getTouchPoint()
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
                        showDefinitionUi(word, event.getRawTouchPoint())
                        return@setOnTouchListener true
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

    private fun MotionEvent.getRawTouchPoint(): PointF = PointF(rawX, rawY)


    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDefinitionUi(word: String, touchPoint: PointF) {
        val definition = dictionary[word] ?: "No definition found for $word"
        binding.tvWord.text = word
        binding.tvDefinition.text = definition

        val viewRect = Rect()
        binding.containerDefinitionCard.getGlobalVisibleRect(viewRect)

        val guidelinePercent = (touchPoint.y - viewRect.top) / viewRect.height()
        val cardHorizontalBias = (touchPoint.x - viewRect.left) / viewRect.width()

        binding.guidelineCard.setGuidelinePercent(guidelinePercent)

        binding.cardViewDefinition.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = cardHorizontalBias
        }

        binding.cardViewDefinition.isVisible = true
    }
}