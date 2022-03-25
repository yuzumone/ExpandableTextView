package net.expandable

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView

class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var listener: OnExpandableClickListener? = null
    private var isFinishDraw: Boolean = false
    private var ellipsize: TruncateAt? = null
    private var ellipsizedText: CharSequence = ""

    lateinit var fullText: CharSequence
        private set

    var isExpanded: Boolean = false
        set(value) {
            field = value
            if (value) {
                expandText()
            } else {
                collapseText()
            }
        }
    
    var isExpandEnabled: Boolean = true
    var isOnlyFirstExpand: Boolean = false
    var collapseLines: Int = 1

    init {
        isClickable = true
        val array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyleAttr, 0)
        isExpanded = array.getBoolean(R.styleable.ExpandableTextView_expanded, false)
        isExpandEnabled = array.getBoolean(R.styleable.ExpandableTextView_expand_enabled, true)
        isOnlyFirstExpand = array.getBoolean(R.styleable.ExpandableTextView_expand_first_time, false)
        collapseLines = array.getInt(R.styleable.ExpandableTextView_collapse_lines, 1)
        if (ellipsize == null) {
            ellipsize = TruncateAt.END
        }
        array.recycle()
    }

    fun setOnExpandableClickListener(listener: OnExpandableClickListener) {
        this.listener = listener
    }

    fun setOnExpandableClickListener(
            onExpand: (ExpandableTextView) -> Unit = { },
            onCollapse: (ExpandableTextView) -> Unit = { }
    ) = setOnExpandableClickListener(object : OnExpandableClickListener {
        override fun expand(view: ExpandableTextView): Unit = onExpand(view)
        override fun collapse(view: ExpandableTextView): Unit = onCollapse(view)
    })

    override fun performClick(): Boolean {
        if ((isExpandEnabled && !isOnlyFirstExpand)
            || (isOnlyFirstExpand && !isExpanded)
        ) {
            isExpanded = isExpanded.not()
        }
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        isFinishDraw = true
    }

    override fun setText(text: CharSequence, type: BufferType?) {
        super.setText(text, type)
        if (ellipsizedText != text) {
            fullText = text
        }
    }

    override fun setMaxLines(lines: Int) {
        // do nothing
    }

    override fun setEllipsize(where: TruncateAt) {
        ellipsize = where
    }

    override fun getEllipsize(): TruncateAt? {
        return ellipsize
    }

    private fun expandText() {
        text = fullText
        listener?.expand(this)
    }

    private fun collapseText() {
        if (isFinishDraw) {
            performEllipsize()
        } else {
            viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    performEllipsize()
                }
            })
        }
    }

    private fun performEllipsize() {
        if (visibility == VISIBLE && !isExpanded) {
            layout?.let {
                if (it.lineCount <= collapseLines) return
                val avail = (0 until collapseLines)
                    .map { lines -> it.getLineMax(lines) }
                    .sum()
                ellipsizedText = TextUtils.ellipsize(text, paint, avail, ellipsize)
                text = ellipsizedText
            }
            listener?.collapse(this)
        }
    }
}