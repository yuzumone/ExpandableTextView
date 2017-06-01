package net.expandable

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

class ExpandableTextView : TextView {

    private var listener: OnExpandableClickListener? = null
    private var isExpand: Boolean = false
    private var isExpandEnabled: Boolean = true
    private var collapseLines: Int = 1

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
        setExpand(isExpand)
        if (ellipsize == null) {
            ellipsize = TextUtils.TruncateAt.END
        }
        array.recycle()
    }

    fun setExpand(expand: Boolean) {
        isExpand = expand
        if (expand) {
            setHorizontallyScrolling(false)
        } else {
            setHorizontallyScrolling(true)
        }
    }

    fun setOnExpandableClickListener(listener: OnExpandableClickListener) {
        this.listener = listener
    }

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
        setExpand(!isExpand)
        if (listener != null) {
            if (isExpand) {
                listener!!.expand(this)
            } else {
                listener!!.collapse(this)
            }
        }
        return super.performClick()
    }
}