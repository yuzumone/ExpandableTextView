package net.expandable.sample

import net.expandable.ExpandableTextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23, 34])
class MainActivityTest {
    @Test
    fun activityConfiguresExpandableTextAndHandlesClicks() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val textView = activity.findViewById<ExpandableTextView>(R.id.text)

        assertEquals(activity.getString(R.string.long_text), textView.fullText.toString())
        assertEquals(2, textView.collapseLines)
        assertFalse(textView.isExpanded)

        textView.performClick()

        assertTrue(textView.isExpanded)
        assertEquals("Expand", ShadowToast.getTextOfLatestToast())

        textView.performClick()

        assertFalse(textView.isExpanded)
        assertEquals("Collapse", ShadowToast.getTextOfLatestToast())
    }
}
