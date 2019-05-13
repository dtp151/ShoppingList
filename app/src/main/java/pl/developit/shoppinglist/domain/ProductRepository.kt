package pl.developit.shoppinglist.domain

import io.reactivex.Observable
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.data.product.ProductRepositoryImpl

interface ProductRepository {

	fun observe(): Observable<ProductRepositoryImpl.State>

	fun sync()

	fun insert(name: String)

	fun markAsDeleted(product: Product)

	fun clearLocalDatabase()

	fun clearDisposables()

}