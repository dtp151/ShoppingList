package pl.developit.shoppinglist.presentation.screens.shopping

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.utils.observeOnce
import javax.inject.Inject

class ShoppingViewModel @Inject constructor(
		private val productRepository: ProductRepository,
		private val userRepository: UserRepository
) : ViewModel() {

	val newProductName = MutableLiveData<String>()

	val productList = MutableLiveData<List<Product>>().apply { value = ArrayList() }

	val selectedProducts = HashSet<Long>()

	val isRefreshing = MutableLiveData<Boolean>().apply { value = false }

	fun getProducts(owner: LifecycleOwner) {
		synchronizeProducts(owner)
		productRepository.getAll().observe(owner, Observer { list ->
			productList.value = list?.filter { it.status != Product.Status.DELETED }
					.apply { this?.map { it.isChecked = selectedProducts.contains(it.id) } }
		})
	}

	fun addProduct(name: String) {
		productRepository.insert(name)
	}

	fun synchronizeProducts(owner: LifecycleOwner) {
		productRepository.getAll().observeOnce(owner, Observer { productList ->
			productRepository.synchronize(productList,
					onSynchronized = { isRefreshing.postValue(false) })
		})
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
				productRepository.delete(product)
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