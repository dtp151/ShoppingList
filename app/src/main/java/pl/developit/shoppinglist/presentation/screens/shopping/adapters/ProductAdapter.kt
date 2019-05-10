package pl.developit.shoppinglist.presentation.screens.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.presentation.screens.shopping.viewholders.ProductViewHolder

@BindingAdapter("productList", "onProductClick")
fun RecyclerView.setProducts(productList: List<Product>?, onProductClickCallback: ProductAdapter.OnProductClickCallback) {
	if (adapter == null)
		adapter = ProductAdapter(onProductClickCallback)
	(adapter as ProductAdapter).submitList(productList)
}

class ProductAdapter(private val onProductClickCallback: OnProductClickCallback)
	: ListAdapter<Product, ProductViewHolder>(ProductDiff()) {

	class ProductDiff : DiffUtil.ItemCallback<Product>() {
		override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
			return oldItem.name == newItem.name
		}
	}

	private var recyclerView: RecyclerView? = null

	init {
		setHasStableIds(true)
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		this.recyclerView = recyclerView
	}

	override fun submitList(list: List<Product>?) {
		super.submitList(list)
		list?.apply {
			if (size > itemCount)
				recyclerView?.smoothScrollToPosition(size)
			else if (size == itemCount)
				notifyDataSetChanged()
		}
	}

	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductViewHolder {
		return ProductViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_product, viewGroup, false), onProductClickCallback)
	}

	override fun onBindViewHolder(viewHolder: ProductViewHolder, i: Int) {
		viewHolder.bind(getItem(i))
	}

	override fun getItemId(position: Int): Long {
		return getItem(position).id
	}

	interface OnProductClickCallback {
		fun onProductClick(product: Product)
	}
}