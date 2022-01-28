package com.leinaro.move.presentation.boxlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leinaro.move.BoxContent
import com.leinaro.move.databinding.FragmentBoxListItemBinding

class BoxAdapter(private val dataSet: Array<BoxContent>, private val listener: Listener? = null) :
  RecyclerView.Adapter<BoxAdapter.ViewHolder>() {

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
      binding.itemNumber.text = boxContent.uuid.take(8)
      binding.content.text = "${boxContent.location} : ${boxContent.description}"
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

}