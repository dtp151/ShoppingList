package pl.developit.shoppinglist.presentation.screens.main

import pl.developit.shoppinglist.data.models.Product


interface MainListener {
	fun onAddBtnClick()

	fun onProductClick(): (Product) -> Unit
}