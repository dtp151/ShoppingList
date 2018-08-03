package pl.karolmichalski.shoppinglist.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.karolmichalski.shoppinglist.models.Product

class ProductsRepository(application: Application) {

	private val localDatabase = LocalDatabase.getInstance(application).productsDao()
	private val cloudDatabase = CloudDatabase.getInstance().getDao()

	fun getAll(): LiveData<List<Product>> {
		return localDatabase.getAll()
	}

	fun insert(name: String) {
		cloudDatabase.generateKey()?.let { key ->
			val product = Product(key, name, Product.Status.ADDED)
			Completable.fromAction { localDatabase.insert(product) }
					.andThen(cloudDatabase.insert(product))
					.subscribeOn(Schedulers.io())
					.observeOn(Schedulers.io())
					.subscribeBy(onComplete = {
						product.status = Product.Status.SYNCED
						localDatabase.update(product)
					})
		}

	}

	fun delete(product: Product) {
		Completable.fromAction { localDatabase.delete(product) }
				.andThen(cloudDatabase.delete(product))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeBy(
						onComplete = {
							Log.d("awda", "awdaw")
						},
						onError = {
							Log.d("awda", "awdaw")
						}
				)
	}

//	fun merge(clientSingle: Single<ClientResponse>, statusesSingle: Single<List<LabelValue>>): Single<RequestMerger> {
//		return Single.zip(clientSingle, statusesSingle, BiFunction { client, statuses ->
//			client.client.applicationList?.let {
//				for (application in it)
//					application.setStatuses(statuses)
//			}
//			RequestMerger(client.client)
//		})
//	}

}