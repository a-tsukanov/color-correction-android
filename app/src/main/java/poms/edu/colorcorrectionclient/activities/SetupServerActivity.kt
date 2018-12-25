/*
 * Copyright (c) 2018 Aliaksandr Tsukanau.
 * Licensed under MIT Licence.
 * You may not use this file except in compliance with MIT License.
 * See the MIT License for more details. https://www.mitlicense.org/
 */

package poms.edu.colorcorrectionclient.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_setup_server.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient

class SetupServerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_server)
        apply_address_button.onClick {
            try {

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
