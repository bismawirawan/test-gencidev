package test.gencidev.ui.xml

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import test.gencidev.databinding.DialogDayOffBinding

class DialogDayOff(context: Context): Dialog(context) {

    init {
        val binding = DialogDayOffBinding.inflate(LayoutInflater.from(context))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
        this.setCancelable(false)

    }
}