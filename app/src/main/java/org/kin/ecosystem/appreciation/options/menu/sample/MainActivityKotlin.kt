package org.kin.ecosystem.appreciation.options.menu.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import org.kin.ecosystem.appreciation.options.menu.ui.CloseType
import org.kin.ecosystem.appreciation.options.menu.ui.DialogTheme
import org.kin.ecosystem.appreciation.options.menu.ui.EventsListener
import org.kin.ecosystem.appreciation.options.menu.ui.GiftingDialog


class MainActivityKotlin : AppCompatActivity() {

    private val eventsListener = object : EventsListener {
        override fun onDialogOpened() {
            showToast("Dialog opened")
        }

        override fun onItemSelected(itemIndex: Int, amount: Int) {
            showToast("onItemSelected -> index: $itemIndex amount: $amount")
        }

        override fun onDialogClosed(closeType: CloseType) {
            showToast("Dialog closed -> type: " + closeType.name)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivityKotlin, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button_open_dialog_light).apply {
            setOnClickListener { openGiftingDialog() }
        }

        findViewById<Button>(R.id.button_open_dialog_dark).apply {
            setOnClickListener { openGiftingDialog(amount = 8L, dialogTheme = DialogTheme.DARK) }
        }
    }

    private fun openGiftingDialog(amount: Long = 200L, dialogTheme: DialogTheme = DialogTheme.LIGHT) {
        GiftingDialog.Builder(this)
            .balance(amount)
            .theme(dialogTheme)
            .eventsListener(eventsListener)
            .build()
	        .show()
    }
}
