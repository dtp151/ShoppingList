package pl.developit.shoppinglist.presentation.screens.shopping

import pl.developit.shoppinglist.data.models.Product


interface ShoppingListener {
	fun onAddBtnClick(productName: String)

	fun onProductClick(): (Product) -> Unit
}