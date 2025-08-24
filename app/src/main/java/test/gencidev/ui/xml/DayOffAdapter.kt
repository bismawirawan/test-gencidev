package test.gencidev.ui.xml

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import test.gencidev.databinding.ItemDayOffBinding
import test.gencidev.model.response.area.DayOffModel

class DayOffAdapter(
    private val onClicked: (DayOffModel) -> Unit
) :
    RecyclerView.Adapter<DayOffAdapter.ViewHolder>() {

    var listData: MutableList<DayOffModel> = ArrayList()

    fun insertAll(data: List<DayOffModel>) {
        data.forEach {
            listData.add(it)
            notifyItemInserted(listData.size - 1)
        }
    }

    fun clear() {
        if (listData.isNotEmpty()) {
            listData.clear()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDayOffBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bindTo(item)

    }

    override fun getItemCount() = listData.size

    inner class ViewHolder(val binding: ItemDayOffBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindTo(item: DayOffModel) {

            val context = binding.root.context

            binding.tvDay.text = item.tanggal_display
            binding.tvKeterangan.text = item.keterangan

            binding.layoutMain.setOnClickListener {
                onClicked(item)
            }

        }

    }

}