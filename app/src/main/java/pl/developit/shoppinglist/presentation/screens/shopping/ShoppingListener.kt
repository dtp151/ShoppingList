package pl.developit.shoppinglist.presentation.screens.shopping

import pl.developit.shoppinglist.data.models.Product


interface ShoppingListener {
	fun onAddBtnClick()

	fun onProductClick(): (Product) -> Unit
}