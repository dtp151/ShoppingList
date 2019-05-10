package pl.developit.shoppinglist.presentation.screens.shopping.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.databinding.ItemProductBinding
import pl.developit.shoppinglist.presentation.screens.shopping.adapters.ProductAdapter

class ProductViewHolder(private val binding: ItemProductBinding,
                        private val callback: ProductAdapter.OnProductClickCallback)
	: RecyclerView.ViewHolder(binding.root), View.OnClickListener {

	override fun onClick(v: View?) {
		binding.invalidateAll()
		callback.onProductClick(binding.product!!)
	}

	fun bind(product: Product) {
		binding.product = product
		binding.listener = this
	}

}