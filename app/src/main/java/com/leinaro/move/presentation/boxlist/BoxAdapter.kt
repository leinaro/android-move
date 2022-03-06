package com.leinaro.move.presentation.boxlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.leinaro.move.presentation.data.BoxContent
import com.leinaro.move.R
import com.leinaro.move.databinding.FragmentBoxListItemBinding

class BoxAdapter(
  private val dataSet: Array<BoxContent>,
  private val listener: Listener? = null
) : RecyclerView.Adapter<BoxAdapter.ViewHolder>() {

  interface Listener {
    fun onItemClickListener(boxContent: BoxContent?)
  }

  class ViewHolder(
    val binding: FragmentBoxListItemBinding,
    private val listener: Listener? = null
  ) :
    RecyclerView.ViewHolder(binding.root) {

    var boxContent: BoxContent? = null

    init {
      binding.root.setOnClickListener {
        listener?.onItemClickListener(boxContent)
      }
    }

    fun bind(boxContent: BoxContent) {
      this.boxContent = boxContent
      binding.itemNumber.text = "# ${boxContent.counter}"
      binding.itemQr.text = boxContent.uuid
      binding.itemPlace.text = "Lugar: ${boxContent.location}"
      binding.content.text = boxContent.description
      if (boxContent.inventoried) {
        binding.card.setCardBackgroundColor(
          ContextCompat.getColor(binding.root.context, R.color.result_points)
        )
      } else {
        binding.card.setCardBackgroundColor(
          ContextCompat.getColor(binding.root.context, R.color.white)
          //ContextCompat.getColor(binding.root.context, R.color.viewfinder_laser)
        )
      }
    }
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      FragmentBoxListItemBinding.inflate(LayoutInflater.from(viewGroup.context)),
      listener
    )
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    viewHolder.bind(dataSet[position])
  }

  override fun getItemCount() = dataSet.size

  fun registerOnInventory(uuid: String) {
    dataSet.find { it.uuid == uuid }
  }
}