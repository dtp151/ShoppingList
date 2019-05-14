package pl.developit.shoppinglist.domain

import io.reactivex.Observable
import pl.developit.shoppinglist.data.models.Product

interface ProductRepository {

	fun observe(): Observable<State>

	fun sync()

	fun insert(name: String)

	fun markAsDeleted(product: Product)

	fun clearLocalDatabase()

	fun clearDisposables()

	sealed class State {
		class Default(val products: List<Product>) : State()
		object Syncing : State()
		object Synced : State()
	}
}