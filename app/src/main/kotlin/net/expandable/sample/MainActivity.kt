package net.expandable.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import net.expandable.ExpandableTextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<ExpandableTextView>(R.id.text)
        textView.text = getString(R.string.long_text)
        textView.setCollapseLines(2)
        textView.setOnExpandableClickListener(
                onExpand = { showToast("Expand") },
                onCollapse = { showToast("Collapse") }
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
