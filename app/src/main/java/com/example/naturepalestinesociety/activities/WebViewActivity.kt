package com.example.naturepalestinesociety.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivityWebViewBinding
import com.example.naturepalestinesociety.helpers.setFullScreen
import com.example.naturepalestinesociety.helpers.setLightStatusBar

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)

        setLightStatusBar(window,true)

        binding.webView.visibility = View.VISIBLE
        binding.webView.loadData(
            "<html><body><h1>Privacy Policy</h1>\n" +
                    "\n" +
                    "\t\t\t\t<p><strong>Pellentesque habitant morbi tristique</strong> senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. <em>Aenean ultricies mi vitae est.</em> Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, <code>commodo vitae</code>, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. <a href=\"#\">Donec non enim</a> in turpis pulvinar facilisis. Ut felis.</p>\n" +
                    "\n" +
                    "\t\t\t\t<h2>Header Level 2</h2>\n" +
                    "\n" +
                    "\t\t\t\t<ol>\n" +
                    "\t\t\t\t   <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n" +
                    "\t\t\t\t   <li>Aliquam tincidunt mauris eu risus.</li>\n" +
                    "\t\t\t\t</ol>\n" +
                    "\n" +
                    "\t\t\t\t<blockquote><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus magna. Cras in mi at felis aliquet congue. Ut a est eget ligula molestie gravida. Curabitur massa. Donec eleifend, libero at sagittis mollis, tellus est malesuada tellus, at luctus turpis elit sit amet quam. Vivamus pretium ornare est.</p></blockquote>\n" +
                    "\n" +
                    "\t\t\t\t<h3>Header Level 3</h3>\n" +
                    "\n" +
                    "\t\t\t\t<ul>\n" +
                    "\t\t\t\t   <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n" +
                    "\t\t\t\t   <li>Aliquam tincidunt mauris eu risus.</li>\n" +
                    "\t\t\t\t</ul>\n" +
                    "\n" +
                    "\t\t\t\t<pre><code>\n" +
                    "\t\t\t\t#header h1 a {\n" +
                    "\t\t\t\t  display: block;\n" +
                    "\t\t\t\t  width: 300px;\n" +
                    "\t\t\t\t  height: 80px;\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t\t</code></pre></body></html>",
            "text/html", "UTF-8"
        )


    }
}