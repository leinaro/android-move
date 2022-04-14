package com.leinaro.move.presentation.inventorylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leinaro.move.databinding.FragmentInventoryListItemBinding
import com.leinaro.move.domain.data.Inventory

class InventoryAdapter(
  private val dataSet: Array<Inventory>,
  private val listener: Listener? = null
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

  interface Listener {
    fun onItemClickListener(inventory: Inventory?)
  }

  class ViewHolder(
    val binding: FragmentInventoryListItemBinding,
    private val listener: Listener? = null
  ) : RecyclerView.ViewHolder(binding.root) {

    var inventory: Inventory? = null

    init {
      binding.root.setOnClickListener {
        listener?.onItemClickListener(inventory)
      }
    }

    fun bind(inventory: Inventory) {
      this.inventory = inventory
      binding.itemNumber.text = "----"//""# ${boxContent.counter}"
      binding.itemQr.text = "packing"//boxContent.uuid
      binding.itemPlace.text = "Origen ${inventory.origin}"
      binding.content.text = "Destino ${inventory.destination}"
    }

  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    viewType: Int
  ): ViewHolder {
    return ViewHolder(
      FragmentInventoryListItemBinding.inflate(LayoutInflater.from(viewGroup.context)),
      listener
    )
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    viewHolder.bind(dataSet[position])
  }

  override fun getItemCount() = dataSet.size

}
