/*
 * Copyright (c) 2019 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_setup_server.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient

class SetupServerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        fun hideKeyboard() {
            val i = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            i.hideSoftInputFromWindow(form.windowToken, 0)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_server)
        apply_address_button.onClick {
            try {

                hideKeyboard()

                ColorCorrectionHttpClient.BASE_URL = server_address_text.text.toString()
                val mainActivity = Intent(this@SetupServerActivity, MainActivity::class.java)
                startActivity(mainActivity)
            }
            catch (e: Throwable) {
                longToast("Something went wrong: ${e.message}")
            }
        }
    }
}
