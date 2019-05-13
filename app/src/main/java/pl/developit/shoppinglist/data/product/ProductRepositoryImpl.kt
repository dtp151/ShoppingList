package pl.developit.shoppinglist.data.product

import com.crashlytics.android.Crashlytics
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
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

	private val subject = PublishSubject.create<State>()

	init {
		observeLocalTable()
	}

	override fun observe(): Observable<State> = subject

	override fun sync() {
		localProductSource.selectAllOnce()
				.subscribeOn(Schedulers.io())
				.doOnSubscribe { subject.onNext(State.Syncing) }
				.subscribe(
						{ localProducts -> syncRemotely(localProducts) },
						{ Crashlytics.logException(it) })
				.addTo(disposables)
	}

	override fun insert(name: String) {
		val product = Product(getTimeStamp(), name, Product.Status.ADDED)
		insertLocallyAndRemotely(product)
	}

	override fun markAsDeleted(product: Product) {
		product.status = Product.Status.DELETED
		localProductSource.update(product)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe { deleteRemotelyAndLocally(product) }
				.addTo(disposables)
	}

	override fun clearLocalDatabase() {
		Completable.fromAction { localProductSource.clearTable() }
				.subscribeOn(Schedulers.io())
				.subscribe()
				.addTo(disposables)
	}

	override fun clearDisposables() {
		disposables.clear()
	}

	private fun observeLocalTable() {
		localProductSource.selectAll()
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe { list -> subject.onNext(State.Success(list)) }
				.addTo(disposables)
	}

	private fun syncRemotely(productList: List<Product>) {
		remoteProductSource.synchronizeProducts(uid, productList)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.doFinally { subject.onNext(State.Synced) }
				.subscribe(
						{ newProductList -> replaceLocally(productList, newProductList) },
						{ Crashlytics.logException(it) })
				.addTo(disposables)
	}

	private fun insertLocallyAndRemotely(product: Product) {
		localProductSource.insert(product)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe { insertRemotelyAndMarkSyncedLocally(product) }
				.addTo(disposables)
	}

	private fun deleteRemotelyAndLocally(product: Product) {
		remoteProductSource.deleteProduct(uid, product.id)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(
						{ deleteLocally(product) },
						{ Crashlytics.logException(it) })
				.addTo(disposables)
	}

	private fun insertRemotelyAndMarkSyncedLocally(product: Product) {
		remoteProductSource.addProduct(uid, product.id, product.name)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(
						{ product.status = Product.Status.SYNCED; updateLocally(product) },
						{ Crashlytics.logException(it) })
				.addTo(disposables)
	}

	private fun replaceLocally(oldProducts: List<Product>?, newProducts: List<Product>) {
		localProductSource.delete(oldProducts)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe { newProducts.map { it.status = Product.Status.SYNCED }; insertLocally(newProducts) }
				.addTo(disposables)
	}

	private fun insertLocally(products: List<Product>) {
		localProductSource.insert(products)
				.subscribeOn(Schedulers.io())
				.subscribe()
				.addTo(disposables)
	}

	private fun updateLocally(product: Product) {
		localProductSource.update(product)
				.subscribeOn(Schedulers.io())
				.subscribe()
				.addTo(disposables)
	}

	private fun deleteLocally(product: Product) {
		localProductSource.delete(product)
				.subscribeOn(Schedulers.io())
				.subscribe()
				.addTo(disposables)
	}

	sealed class State {
		class Success(val products: List<Product>) : State()
		object Syncing : State()
		object Synced : State()
	}

}