package pl.developit.shoppinglist.presentation.screens.shopping

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.developit.shoppinglist.presentation.screens.shopping.adapters.ProductAdapter


interface ShoppingListener: ProductAdapter.OnProductClickCallback, SwipeRefreshLayout.OnRefreshListener {
	fun onAddBtnClick(productName: String)
}