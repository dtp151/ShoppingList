package pl.karolmichalski.shoppinglist.data.product

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.karolmichalski.shoppinglist.data.models.Product
import pl.karolmichalski.shoppinglist.data.product.cloud.CloudInterfaceWrapper
import pl.karolmichalski.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.karolmichalski.shoppinglist.domain.product.ProductRepository

class ProductRepositoryImpl(
		private val localDatabase: LocalDatabaseDAO,
		private val cloudInterfaceWrapper: CloudInterfaceWrapper)
	: ProductRepository {

	override fun getAll(): LiveData<List<Product>> {
		return localDatabase.getAll()
	}

	override fun insert(name: String) {
		val product = Product(name, Product.Status.ADDED)
		Single.fromCallable { localDatabase.insert(product) }
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onSuccess = { productId ->
							cloudInterfaceWrapper.addProduct(FirebaseAuth.getInstance().currentUser!!.uid, productId.toInt(), product.name)
									.subscribeBy(
											onSuccess = {
												product.status = Product.Status.SYNCED
												localDatabase.update(product)
											},
											onError = {

											})


						},
						onError = {
							Log.d("adaw", "wadawdaw")
						}
				)

	}

	override fun update(product: Product) {
		Completable.fromAction { localDatabase.update(product) }
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe()
	}

	override fun delete(product: Product) {
		product.status = Product.Status.DELETED
		Completable.fromAction { localDatabase.update(product) }
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onComplete = {
							cloudInterfaceWrapper.deleteProduct(FirebaseAuth.getInstance().currentUser!!.uid, product.id)
									.subscribeBy(
											onSuccess = {
												Completable.fromAction { localDatabase.delete(product) }
														.subscribe()

											},
											onError = {
												Log.d("awdaw", "awdawdaw")
											}
									)
						},
						onError = {
							Log.d("awdaw", "awdawdaw")
						}
				)

	}

	override fun synchronize(productList: List<Product>?) {
		cloudInterfaceWrapper.synchronizeProducts(FirebaseAuth.getInstance().currentUser!!.uid, productList)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onSuccess = {
							Log.d("awdaw0", "awdad")

						},
						onError = {
							Log.d("adaw", "wadawdaw")
						}
				)
	}

}