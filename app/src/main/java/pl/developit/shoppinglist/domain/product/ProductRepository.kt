package pl.developit.shoppinglist.domain.product

import androidx.lifecycle.LiveData
import pl.developit.shoppinglist.data.models.Product

interface ProductRepository {

	fun getAll(): LiveData<List<Product>>

	fun syncAll(): LiveData<Boolean>

	fun insert(name: String)

	fun markAsDeleted(product: Product)

	fun clearLocalDatabase()

	fun clearDisposables()

}