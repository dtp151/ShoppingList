package pl.developit.shoppinglist.domain.product

import androidx.lifecycle.LiveData
import pl.developit.shoppinglist.data.models.Product

interface ProductRepository {

	val productList: LiveData<List<Product>>
	val isSyncing: LiveData<Boolean>

	fun sync()

	fun insert(name: String)

	fun markAsDeleted(product: Product)

	fun clearLocalDatabase()

	fun clearDisposables()

}