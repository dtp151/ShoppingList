package pl.developit.shoppinglist.data.product

import com.crashlytics.android.Crashlytics
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.developit.shoppinglist.data.product.remote.RemoteProductSource
import pl.developit.shoppinglist.domain.ProductRepository
import pl.developit.shoppinglist.presentation.utils.getTimeStamp

class ProductRepositoryImpl(
		private val uid: String,
		private val localProductSource: LocalDatabaseDAO,
		private val remoteProductSource: RemoteProductSource)
	: ProductRepository {

	private val disposables = CompositeDisposable()

	private val source = BehaviorSubject.create<State>()

	init {
		observeLocalTable()
	}

	override fun observe(): Observable<State> = source

	override fun sync() {
		disposables.add(localProductSource.selectAllOnce()
				.subscribeOn(Schedulers.io())
				.doOnSubscribe { source.onNext(State.Syncing) }
				.subscribeBy(
						onSuccess = { localProducts -> syncRemotely(localProducts) },
						onError = { Crashlytics.logException(it) }))
	}

	override fun insert(name: String) {
		val product = Product(getTimeStamp(), name, Product.Status.ADDED)
		insertLocallyAndRemotely(product)
	}

	override fun markAsDeleted(product: Product) {
		product.status = Product.Status.DELETED
		disposables.add(
				localProductSource.update(product)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(onComplete = { deleteRemotelyAndLocally(product) }))
	}

	override fun clearLocalDatabase() {
		disposables.add(
				Completable.fromAction { localProductSource.clearTable() }
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	override fun clearDisposables() {
		disposables.clear()
	}

	private fun observeLocalTable() {
		disposables.add(
				localProductSource.selectAll()
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribe { list -> source.onNext(State.Success(list)) })
	}

	private fun syncRemotely(productList: List<Product>) {
		disposables.add(
				remoteProductSource.synchronizeProducts(uid, productList)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.doFinally { source.onNext(State.Synced) }
						.subscribeBy(
								onSuccess = { newProductList -> replaceLocally(productList, newProductList) },
								onError = { Crashlytics.logException(it) }))
	}

	private fun insertLocallyAndRemotely(product: Product) {
		disposables.add(
				localProductSource.insert(product)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(onComplete = { insertRemotelyAndMarkSyncedLocally(product) }))
	}

	private fun deleteRemotelyAndLocally(product: Product) {
		disposables.add(
				remoteProductSource.deleteProduct(uid, product.id)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(
								onSuccess = { deleteLocally(product) },
								onError = { Crashlytics.logException(it) }))
	}

	private fun insertRemotelyAndMarkSyncedLocally(product: Product) {
		disposables.add(
				remoteProductSource.addProduct(uid, product.id, product.name)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(
								onSuccess = {
									product.status = Product.Status.SYNCED
									updateLocally(product)
								},
								onError = { Crashlytics.logException(it) }))
	}

	private fun replaceLocally(oldProducts: List<Product>?, newProducts: List<Product>) {
		disposables.add(
				localProductSource.delete(oldProducts)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribe {
							newProducts.map { it.status = Product.Status.SYNCED }
							insertLocally(newProducts)
						})
	}

	private fun insertLocally(products: List<Product>) {
		disposables.add(
				localProductSource.insert(products)
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	private fun updateLocally(product: Product) {
		disposables.add(
				localProductSource.update(product)
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	private fun deleteLocally(product: Product) {
		disposables.add(
				localProductSource.delete(product)
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	sealed class State {
		class Success(val products: List<Product>) : State()
		object Syncing : State()
		object Synced : State()
	}

}