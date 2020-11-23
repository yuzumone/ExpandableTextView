package net.expandable

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.TextView

class ExpandableTextView : TextView {

    private var listener: OnExpandableClickListener? = null
    private var isExpand: Boolean = false
    private var isExpandEnabled: Boolean = true
    private var isFinishDraw: Boolean = false
    private var collapseLines: Int = 1
    private var ellipsize: TruncateAt? = null
    private var ellipsizedText: CharSequence = ""
    private lateinit var fullText: CharSequence

    constructor(context: Context?) :
            this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context!!, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        isClickable = true
        val array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyleAttr, 0)
        isExpand = array.getBoolean(R.styleable.ExpandableTextView_expand, false)
        isExpandEnabled = array.getBoolean(R.styleable.ExpandableTextView_expand_enabled, true)
        collapseLines = array.getInt(R.styleable.ExpandableTextView_collapse_lines, 1)
        if (ellipsize == null) {
            ellipsize = TruncateAt.END
        }
        setExpand(isExpand)
        array.recycle()
    }

    fun setExpand(expand: Boolean) {
        isExpand = expand
        if (expand) {
            expandText()
        } else {
            collapseText()
        }
    }

    fun setOnExpandableClickListener(listener: OnExpandableClickListener) {
        this.listener = listener
    }

    fun setOnExpandableClickListener(
            onExpand: (ExpandableTextView) -> Unit,
            onCollapse: (ExpandableTextView) -> Unit
    ) = setOnExpandableClickListener(object : OnExpandableClickListener {
        override fun expand(view: ExpandableTextView): Unit = onExpand(view)
        override fun collapse(view: ExpandableTextView): Unit = onCollapse(view)
    })

    fun setCollapseLines(lines: Int) {
        collapseLines = lines
    }

    fun getCollapseLines(): Int {
        return collapseLines
    }

    fun setExpandEnabled(expandEnabled: Boolean) {
        isExpandEnabled = expandEnabled
    }

    fun getExpandEnabled(): Boolean {
        return isExpandEnabled
    }

    override fun performClick(): Boolean {
        if (isExpandEnabled) {
            setExpand(!isExpand)
            if (listener != null) {
                if (isExpand) {
                    listener!!.expand(this)
                } else {
                    listener!!.collapse(this)
                }
            }
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

    fun getFullText(): CharSequence {
        return fullText
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
    }

    private fun collapseText() {
        if (isFinishDraw) {
            performEllipsize()
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    performEllipsize()
                }
            })
        }
    }

    private fun performEllipsize() {
        if (visibility == VISIBLE && !isExpand) {
            if (layout.lineCount <= collapseLines) return
            val avail = (0 until collapseLines)
                    .map { layout.getLineMax(it) }
                    .sum()
            ellipsizedText = TextUtils.ellipsize(text, paint, avail, ellipsize)
            text = ellipsizedText
        }
    }
}