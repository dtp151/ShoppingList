package pl.developit.shoppinglist.domain.product

import androidx.lifecycle.LiveData
import pl.developit.shoppinglist.data.models.Product

interface ProductRepository {

	fun getAll(): LiveData<List<Product>>

	fun insert(name: String)

	fun update(product: Product)

	fun delete(product: Product)

	fun clearLocalDatabase()

	fun synchronize(productList: List<Product>?, onSynchronized: () -> Unit)
}