package com.leinaro.move.presentation.inventorybanner

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.leinaro.move.databinding.InventoryBannerViewBinding
import com.leinaro.move.domain.data.Inventory

interface InventoryBannerListener {
  fun onStartInventoryClick()
  fun onInitInventoryClick()
}

class InventoryBannerView : FrameLayout {

  private lateinit var binding: InventoryBannerViewBinding
  private var listener: InventoryBannerListener? = null

  constructor(context: Context) : super(context) {
    init(null, 0)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init(attrs, 0)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
    context,
    attrs,
    defStyle
  ) {
    init(attrs, defStyle)
  }

  private fun init(attrs: AttributeSet?, defStyle: Int) {
    binding = InventoryBannerViewBinding.inflate(LayoutInflater.from(context), this, true)
    binding.initButton.setOnClickListener {
      listener?.onInitInventoryClick()
    }
    binding.startInventoryButton.setOnClickListener { listener?.onStartInventoryClick() }

  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

  }

  fun setBannerListener(inventoryBannerListener: InventoryBannerListener?) {
    listener = inventoryBannerListener
  }

  fun setInventory(inventory: Inventory?) {
    inventory?.let {
      binding.initialView.isVisible = false
      binding.originInventory.text = inventory.origin
      binding.destinationInventory.text = inventory.destination
      binding.startInventoryView.isVisible = true
    }
  }
}