package pl.developit.shoppinglist.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.developit.shoppinglist.BuildConfig
import pl.developit.shoppinglist.data.user.UserInterface
import pl.developit.shoppinglist.data.user.UserRepositoryImpl
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.utils.ApiErrorParser
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

private const val API_URL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/"

@Module
class UserModule(private val context: Context) {

	@Provides
	@Singleton
	fun provideUserRepository(sharedPrefs: SharedPreferences, userInterface: UserInterface): UserRepository {
		return UserRepositoryImpl(context, sharedPrefs, userInterface, ApiErrorParser(context))
	}

	@Provides
	@Singleton
	fun provideUserInterface(): UserInterface {
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