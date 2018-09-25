package pl.karolmichalski.shoppinglist.presentation.screens.main.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.karolmichalski.shoppinglist.data.models.Product
import pl.karolmichalski.shoppinglist.databinding.ItemProductBinding

class ProductViewHolder(private val binding: ItemProductBinding,
						private val onProductClick: (Product) -> Unit)
	: RecyclerView.ViewHolder(binding.root), View.OnClickListener {


	init {
		binding.listener = this
	}

	override fun onClick(v: View?) {
		binding.product?.apply {
			onProductClick(this)
		}
		binding.invalidateAll()
	}

	fun bind(product: Product) {
		binding.product = product
	}

}