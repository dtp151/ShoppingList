package pl.developit.shoppinglist.presentation.screens.shopping

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.domain.ProductUseCases
import pl.developit.shoppinglist.domain.ProductUseCases.State.*
import pl.developit.shoppinglist.domain.UserUseCases
import pl.developit.shoppinglist.presentation.utils.notifyChanged

class ShoppingViewModel(
		private val productUseCases: ProductUseCases,
		private val userUseCases: UserUseCases
) : ViewModel() {

	val selectedProducts = HashSet<Long>()

	//bindings
	val newProductName = MutableLiveData<String>()
	val productList = MutableLiveData<List<Product>>()
	val isRefreshing = MutableLiveData<Boolean>()

	private val disposables = CompositeDisposable()

	override fun onCleared() {
		super.onCleared()
		disposables.clear()
		productUseCases.clearDisposables()
	}

	fun observeProducts() {
		productUseCases.observe()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { productUseCases.sync() }
				.subscribe { state ->
					when (state) {
						is Default -> productList.updateWith(state.products)
						Syncing -> isRefreshing.value = true
						Synced -> isRefreshing.value = false
					}
				}
				.addTo(disposables)
	}

	fun addNewProduct(name: String) {
		productUseCases.insert(name)
	}

	fun syncProducts() {
		productUseCases.sync()
	}

	fun invalidateSelectionFor(product: Product) {
		product.isChecked = product.isChecked.not()
		if (product.isChecked)
			selectedProducts.add(product.id)
		else
			selectedProducts.remove(product.id)
	}

	fun removeCheckedProducts() {
		productList.value?.forEach { product ->
			if (selectedProducts.contains(product.id)) {
				productUseCases.markAsDeleted(product)
				selectedProducts.remove(product.id)
			}
		}
	}

	fun deselectAllProducts() {
		productList.value?.map { it.isChecked = false }
		selectedProducts.clear()
		productList.notifyChanged()
	}

	fun clearNewProductName() {
		newProductName.value = ""
	}

	fun logOut() {
		userUseCases.logOut()
		productUseCases.clearLocalDatabase()
	}

	private fun MutableLiveData<List<Product>>.updateWith(products: List<Product>) {
		value = products.filter { it.status != Product.Status.DELETED }
		value?.map { it.isChecked = selectedProducts.contains(it.id) }
	}
}