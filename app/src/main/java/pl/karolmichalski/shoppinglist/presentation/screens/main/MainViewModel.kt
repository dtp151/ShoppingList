package pl.karolmichalski.shoppinglist.presentation.screens.main

import android.app.Application
import androidx.lifecycle.*
import pl.karolmichalski.shoppinglist.data.models.Product
import pl.karolmichalski.shoppinglist.domain.product.ProductRepository
import pl.karolmichalski.shoppinglist.domain.user.UserRepository
import pl.karolmichalski.shoppinglist.presentation.App
import pl.karolmichalski.shoppinglist.presentation.utils.observeOnce
import javax.inject.Inject

class MainViewModel(app: App) : ViewModel() {

	class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			@Suppress("UNCHECKED_CAST")
			return MainViewModel(application as App) as T
		}
	}

	val newProductName = MutableLiveData<String>()

	val productList = MutableLiveData<List<Product>>().apply { value = ArrayList() }

	val selectedProducts = HashSet<String>()

	@Inject
	lateinit var productRepository: ProductRepository

	@Inject
	lateinit var userRepository: UserRepository

	init {
		app.appComponent.inject(this)
	}

	fun getProducts(owner: LifecycleOwner) {
		productRepository.getAll().observe(owner, Observer { list ->
			productList.value = list?.filter { it.status != Product.Status.DELETED }
					.apply { this?.map { it.isChecked = selectedProducts.contains(it.key) } }
		})
	}

	fun addProduct(name: String) {
		productRepository.insert(name)
	}

	fun synchronizeProducts(owner: LifecycleOwner) {
		productRepository.getAll().observeOnce(owner, Observer {
			productRepository.synchronize(it)
		})
	}

	fun invalidateProductSelection(product: Product) {
		product.isChecked = product.isChecked.not()
		if (product.isChecked)
			selectedProducts.add(product.key)
		else
			selectedProducts.remove(product.key)
	}

	fun removeCheckedProducts() {
		productList.value?.forEach { product ->
			if (selectedProducts.contains(product.key)) {
				productRepository.delete(product)
				selectedProducts.remove(product.key)
			}
		}
	}

	fun deselectAllProducts() {
		productList.value?.forEach { product ->
			if (selectedProducts.contains(product.key)) {
				product.isChecked = false
				selectedProducts.remove(product.key)
			}
		}
	}

	fun clearNewProductName() {
		newProductName.value = ""
	}

	fun logOut() {
		userRepository.logOut()
	}

}