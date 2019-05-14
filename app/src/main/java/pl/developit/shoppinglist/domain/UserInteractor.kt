package pl.developit.shoppinglist.domain

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Single
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.models.User
import pl.developit.shoppinglist.data.models.UserRequest
import pl.developit.shoppinglist.data.user.UserInterface
import pl.developit.shoppinglist.presentation.utils.ApiErrorParser
import pl.developit.shoppinglist.presentation.utils.boolean
import pl.developit.shoppinglist.presentation.utils.string

class UserInteractor(
		private val context: Context,
		private val sharedPrefs: SharedPreferences,
		private val userInterface: UserInterface,
		private val apiErrorParser: ApiErrorParser)
	: UserUseCases {

	private var SharedPreferences.uid by sharedPrefs.string()
	private var SharedPreferences.isLogInRememberable by sharedPrefs.boolean()
	private var SharedPreferences.email by sharedPrefs.string()
	private var SharedPreferences.password by sharedPrefs.string()

	override fun logIn(isLoginRememberable: Boolean?, email: String?, password: String?): Single<User> {
		return when {
			email.isNullOrBlank() -> Single.fromCallable { throw Exception(context.getString(R.string.enter_email)) }
			password.isNullOrEmpty() -> Single.fromCallable { throw Exception(context.getString(R.string.enter_password)) }
			isLoginRememberable == null -> Single.fromCallable { throw Exception(context.getString(R.string.isloginrememberable_is_null)) }
			else -> {
				val apiKey = context.getString(R.string.api_key)
				val userRequest = UserRequest(email, password)
				userInterface.logIn(apiKey, userRequest)
						.doOnSuccess { updateLogInData(it, isLoginRememberable, email, password) }
						.onErrorResumeNext { Single.error(apiErrorParser.parse(it)) }
			}
		}
	}

	override fun register(email: String?, password: String?): Single<User> {
		return when {
			email.isNullOrBlank() -> Single.fromCallable { throw Exception(context.getString(R.string.enter_email)) }
			password.isNullOrEmpty() -> Single.fromCallable { throw Exception(context.getString(R.string.enter_password)) }
			else -> {
				val apiKey = context.getString(R.string.api_key)
				val userRequest = UserRequest(email, password)
				userInterface.register(apiKey, userRequest)
						.doOnSuccess {
							sharedPrefs.uid = it?.uid
						}
						.onErrorResumeNext { Single.error(apiErrorParser.parse(it)) }
			}
		}
	}

	override fun getUid(): String = sharedPrefs.uid

	override fun isLoggedIn(): Boolean = sharedPrefs.uid.isNotEmpty()

	override fun isLoginRememberable(): Boolean = sharedPrefs.isLogInRememberable

	override fun getRememberedEmail(): String = sharedPrefs.email

	override fun getRememberedPassword(): String = sharedPrefs.password

	override fun logOut() {
		sharedPrefs.uid = ""
	}

	private fun updateLogInData(user: User, isLoginRememberable: Boolean, email: String, password: String) {
		sharedPrefs.uid = user.uid
		sharedPrefs.isLogInRememberable = isLoginRememberable
		updateRememberable(email, password)
	}

	private fun updateRememberable(email: String, password: String) {
		if (sharedPrefs.isLogInRememberable) {
			sharedPrefs.email = email
			sharedPrefs.password = password
		} else {
			sharedPrefs.email = ""
			sharedPrefs.password = ""
		}
	}
}