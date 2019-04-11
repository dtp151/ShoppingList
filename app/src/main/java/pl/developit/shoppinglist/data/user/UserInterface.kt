package pl.developit.shoppinglist.data.user

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.developit.shoppinglist.BuildConfig
import pl.developit.shoppinglist.data.models.User
import pl.developit.shoppinglist.data.models.UserRequest
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserInterface {

	object Builder {

		private const val API_URL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/"

		fun build(): UserInterface {
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
					.addConverterFactory(JacksonConverterFactory.create(objectMapper))
					.build()

			return retrofit.create(UserInterface::class.java)
		}
	}

	@POST("signupNewUser")
	fun register(@Query("key") apiKey: String,
	             @Body userRequest: UserRequest):
			Single<User>

	@POST("verifyPassword")
	fun logIn(@Query("key") apiKey: String,
	          @Body userRequest: UserRequest):
			Single<User>


}