package com.tobiasschuerg.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit_text_prefix.prefix = "$"

        edit_text_suffix.suffix = "€"

        edit_text_both.prefix = "Age: "
        edit_text_both.suffix = " years"
    }
}
