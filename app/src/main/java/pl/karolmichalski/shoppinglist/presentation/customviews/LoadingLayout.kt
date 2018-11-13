package pl.karolmichalski.shoppinglist.presentation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import pl.karolmichalski.shoppinglist.R

class LoadingLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val progressSize = context.resources.getDimension(R.dimen.progressbar_size).toInt()

        isClickable = true
        isFocusable = true
        visibility = View.GONE

        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent20))

        val progressBar = ProgressBar(context)
        progressBar.layoutParams = LayoutParams(progressSize, progressSize, Gravity.CENTER)
        addView(progressBar)
    }
}