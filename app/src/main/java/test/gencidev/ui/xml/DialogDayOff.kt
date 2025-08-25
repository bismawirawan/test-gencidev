package test.gencidev.ui.xml

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import test.gencidev.R
import test.gencidev.databinding.DialogDayOffBinding
import test.gencidev.model.response.area.DayOffModel
import androidx.core.graphics.drawable.toDrawable

class DialogDayOff(context: Context, val data: DayOffModel) : Dialog(context) {

    init {
        val binding = DialogDayOffBinding.inflate(LayoutInflater.from(context))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        setContentView(binding.root)
        this.setCancelable(true)

        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.8).toInt()
        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        binding.tvDay.text = data.tanggal_display
        binding.tvKeterangan.text = data.keterangan

        binding.tvCuti.text = if (data.is_cuti) "Cuti" else "Tidak Cuti"
        binding.tvCuti.setTextColor(
            if (data.is_cuti) ContextCompat.getColor(
                context,
                R.color.primary
            ) else ContextCompat.getColor(
                context, R.color.merah
            )
        )

        binding.btnTutup.setOnClickListener {
            dismiss()
        }

    }
}