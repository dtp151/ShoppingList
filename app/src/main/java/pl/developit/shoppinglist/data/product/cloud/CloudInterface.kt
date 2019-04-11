package pl.developit.shoppinglist.data.product.cloud

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.developit.shoppinglist.BuildConfig
import pl.developit.shoppinglist.data.models.Product
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CloudInterface {

	object Builder {

		private const val API_URL = "https://us-central1-shoppinglist-4fa3b.cloudfunctions.net/"

		fun build(): CloudInterface {
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

	@POST("addProduct")
	fun addProduct(@Header("uid") uid: String,
	               @Query("id") id: Long,
	               @Query("name") name: String)
			: Single<String>

	@POST("deleteProduct")
	fun deleteProduct(@Header("uid") uid: String,
	                  @Query("id") id: Long)
			: Single<Boolean>

	@POST("synchronizeProducts")
	fun synchronizeProducts(@Header("uid") uid: String,
	                        @Body products: List<Product>)
			: Single<List<Product>>
}