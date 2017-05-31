package net.expandable.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import net.expandable.ExpandableTextView
import net.expandable.OnExpandableClickListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById(R.id.text) as ExpandableTextView
        textView.text = getString(R.string.long_text)
        textView.setOnExpandableClickListener(object : OnExpandableClickListener{
            override fun expand(view: ExpandableTextView) {
                showToast("Expand")
            }

            override fun collapse(view: ExpandableTextView) {
                showToast("Collapse")
            }
        })
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
