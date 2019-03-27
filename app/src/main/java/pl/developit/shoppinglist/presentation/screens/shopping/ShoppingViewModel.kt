package pl.developit.shoppinglist.presentation.screens.shopping

import androidx.lifecycle.*
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.utils.observeOnce
import javax.inject.Inject

class ShoppingViewModel @Inject constructor(
		private val productRepository: ProductRepository,
		private val userRepository: UserRepository
) : ViewModel() {

	val selectedProducts = HashSet<Long>()

	//bindings
	val newProductName = MutableLiveData<String>()
	val productList = MutableLiveData<List<Product>>()
	val isRefreshing: LiveData<Boolean> = productRepository.isSyncing

	override fun onCleared() {
		super.onCleared()
		productRepository.clearDisposables()
	}

	fun getProducts(owner: LifecycleOwner) {
		productRepository.productList.observe(owner, Observer { list ->
			productList.value = list.filter { it.status != Product.Status.DELETED }
			productList.value?.map { it.isChecked = selectedProducts.contains(it.id) }
		})
		productRepository.productList.observeOnce(owner, Observer { productRepository.sync() })
	}

	fun addNewProduct(name: String) {
		productRepository.insert(name)
	}

	fun syncProducts() {
		productRepository.sync()
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
				productRepository.markAsDeleted(product)
				selectedProducts.remove(product.id)
			}
		}
	}

	fun deselectAllProducts() {
		productList.value?.forEach { product ->
			if (selectedProducts.contains(product.id)) {
				product.isChecked = false
				selectedProducts.remove(product.id)
			}
		}
		notifyProductListChanged()
	}

	fun clearNewProductName() {
		newProductName.value = ""
	}

	fun logOut() {
		userRepository.logOut()
		productRepository.clearLocalDatabase()
	}

	private fun notifyProductListChanged() {
		productList.value = productList.value
	}

}