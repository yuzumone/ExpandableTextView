package net.expandable

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

class ExpandableTextView : TextView {

    private var listener: OnExpandableClickListener? = null
    private var isExpand: Boolean = false

    constructor(context: Context?) :
            this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context!!, attrs!!, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        isClickable = true
        val array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyleAttr, 0)
        isExpand = array.getBoolean(R.styleable.ExpandableTextView_isExpand, false)
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

    override fun performClick(): Boolean {
        setExpand(!isExpand)
        if (listener != null) {
            if (isExpand) {
                listener!!.expand(this)
            } else {
                listener!!.unexpand (this)
            }
        }
        return super.performClick()
    }
}