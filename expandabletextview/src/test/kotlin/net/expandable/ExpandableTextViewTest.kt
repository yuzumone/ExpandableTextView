package net.expandable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils.TruncateAt
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23, 34])
@Suppress("DEPRECATION")
class ExpandableTextViewTest {
    private val context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun defaultsMatchDocumentedValues() {
        val view = ExpandableTextView(context)

        assertFalse(view.isExpanded)
        assertTrue(view.isExpandEnabled)
        assertEquals(1, view.collapseLines)
        assertTrue(view.isClickable)
        assertEquals(TruncateAt.END, view.ellipsize)
        assertEquals("", view.fullText.toString())
    }

    @Test
    fun constructorReadsCustomAttributes() {
        val attributes = Robolectric.buildAttributeSet()
            .addAttribute(android.R.attr.text, "Full text")
            .addAttribute(R.attr.expanded, "true")
            .addAttribute(R.attr.expand_enabled, "false")
            .addAttribute(R.attr.collapse_lines, "3")
            .build()

        val view = ExpandableTextView(context, attributes)

        assertTrue(view.isExpanded)
        assertFalse(view.isExpandEnabled)
        assertEquals(3, view.collapseLines)
        assertEquals("Full text", view.fullText.toString())
        assertEquals("Full text", view.text.toString())
    }

    @Test
    fun expandedAttributeWithoutTextInitializesEmptyFullText() {
        val attributes = Robolectric.buildAttributeSet()
            .addAttribute(R.attr.expanded, "true")
            .build()

        val view = ExpandableTextView(context, attributes)

        assertTrue(view.isExpanded)
        assertEquals("", view.fullText.toString())
        assertEquals("", view.text.toString())
    }

    @Test
    fun assigningExpandedStateDoesNotNotifyClickListener() {
        val callbacks = mutableListOf<String>()
        val view = ExpandableTextView(context).apply {
            text = LONG_TEXT
            setOnExpandableClickListener(object : OnExpandableClickListener {
                override fun expand(view: ExpandableTextView) {
                    callbacks += "expand"
                }

                override fun collapse(view: ExpandableTextView) {
                    callbacks += "collapse"
                }
            })
        }

        view.isExpanded = true
        view.isExpanded = false
        layoutAndDraw(view)
        view.viewTreeObserver.dispatchOnGlobalLayout()

        assertTrue(callbacks.isEmpty())
    }

    @Test
    fun clickNotifiesExactlyOnceAfterEachStateChange() {
        val callbacks = mutableListOf<String>()
        val view = ExpandableTextView(context).apply {
            visibility = View.GONE
            text = ""
            collapseLines = Int.MAX_VALUE
            setOnExpandableClickListener(
                onExpand = { callbacks += "expand:${it.isExpanded}" },
                onCollapse = { callbacks += "collapse:${it.isExpanded}" }
            )
        }

        view.performClick()
        assertEquals(listOf("expand:true"), callbacks)

        view.performClick()
        assertEquals(listOf("expand:true", "collapse:false"), callbacks)
    }

    @Test
    fun disabledExpansionIgnoresClicks() {
        val callbacks = mutableListOf<String>()
        val view = ExpandableTextView(context).apply {
            isExpandEnabled = false
            setOnExpandableClickListener(
                onExpand = { callbacks += "expand" },
                onCollapse = { callbacks += "collapse" }
            )
        }

        view.performClick()

        assertFalse(view.isExpanded)
        assertTrue(callbacks.isEmpty())
    }

    @Test
    fun assigningTextUpdatesFullText() {
        val view = ExpandableTextView(context)

        view.text = "First"
        assertEquals("First", view.fullText.toString())

        view.text = "Second"
        assertEquals("Second", view.fullText.toString())
    }

    @Test
    fun collapsingEllipsizesLongTextAndExpandingRestoresIt() {
        val view = ExpandableTextView(context).apply {
            isExpanded = true
            collapseLines = 1
            text = LONG_TEXT
        }
        layoutAndDraw(view)
        assertTrue("Expected wrapped text, lineCount=${view.lineCount}", view.lineCount > 1)

        view.isExpanded = false

        assertEquals(LONG_TEXT, view.fullText.toString())
        assertNotEquals(LONG_TEXT, view.text.toString())
        assertTrue(view.text.length < LONG_TEXT.length)

        view.isExpanded = true

        assertEquals(LONG_TEXT, view.text.toString())
    }

    @Test
    fun collapsingLeavesShortTextUnchanged() {
        val view = ExpandableTextView(context).apply {
            isExpanded = true
            collapseLines = 1
            text = "Short text"
        }
        layoutAndDraw(view, width = 500)

        view.isExpanded = false

        assertEquals("Short text", view.text.toString())
        assertEquals("Short text", view.fullText.toString())
    }

    @Test
    fun ellipsizePropertyCanBeChanged() {
        val view = ExpandableTextView(context)

        view.setEllipsize(TruncateAt.MIDDLE)

        assertEquals(TruncateAt.MIDDLE, view.ellipsize)
    }

    private fun layoutAndDraw(view: ExpandableTextView, width: Int = 240) {
        view.layoutParams = ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(2_000, View.MeasureSpec.AT_MOST)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            view.measuredWidth.coerceAtLeast(1),
            view.measuredHeight.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        view.draw(Canvas(bitmap))
    }

    private companion object {
        const val LONG_TEXT =
            "ExpandableTextView keeps the complete text.\n" +
                "It shows a shortened multiline preview.\n" +
                "The user can expand it to restore every line."
    }
}
