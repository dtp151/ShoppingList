package pl.developit.shoppinglist.di.modules

import android.content.Context
import androidx.room.Room
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.developit.shoppinglist.BuildConfig
import pl.developit.shoppinglist.data.product.ProductRepositoryImpl
import pl.developit.shoppinglist.data.product.cloud.CloudInterface
import pl.developit.shoppinglist.data.product.cloud.CloudInterfaceWrapper
import pl.developit.shoppinglist.data.product.cloud.CloudInterfaceWrapperImpl
import pl.developit.shoppinglist.data.product.local.LocalDatabase
import pl.developit.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

private const val API_URL = "https://us-central1-shoppinglist-4fa3b.cloudfunctions.net/"

@Module
class ProductModule(private val context: Context) {

	@Provides
	@Singleton
	fun provideProductsRepository(
			userRepository: UserRepository,
			localDatabase: LocalDatabaseDAO,
			cloudInterfaceWrapper: CloudInterfaceWrapper): ProductRepository {
		return ProductRepositoryImpl(userRepository, localDatabase, cloudInterfaceWrapper)
	}

	@Provides
	@Singleton
	fun provideLocalDatabase(): LocalDatabaseDAO {
		val database = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, "shopping.db")
				.fallbackToDestructiveMigration()
				.build()
		return database.productsDao()
	}

	@Provides
	@Singleton
	fun provideCloudInterfaceWrapper(cloudInterface: CloudInterface): CloudInterfaceWrapper {
		return CloudInterfaceWrapperImpl(cloudInterface)
	}

	@Provides
	@Singleton
	fun provideCloudInterface(): CloudInterface {
		val loggingInterceptor = HttpLoggingInterceptor().apply {
			level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
		}

		val okHttpClient = OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build()

		val objectMapper = ObjectMapper()
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

		val retrofit = Retrofit.Builder()
				.baseUrl(API_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(okHttpClient)
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create(objectMapper))
				.build()

		return retrofit.create(CloudInterface::class.java)
	}

}