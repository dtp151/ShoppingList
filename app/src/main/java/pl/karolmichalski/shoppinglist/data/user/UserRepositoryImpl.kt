package pl.karolmichalski.shoppinglist.data.user

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Single
import pl.karolmichalski.shoppinglist.R
import pl.karolmichalski.shoppinglist.data.models.LoginRequest
import pl.karolmichalski.shoppinglist.data.models.User
import pl.karolmichalski.shoppinglist.domain.user.UserRepository
import pl.karolmichalski.shoppinglist.presentation.utils.boolean
import pl.karolmichalski.shoppinglist.presentation.utils.string

class UserRepositoryImpl(
		private val context: Context,
		private val sharedPrefs: SharedPreferences,
		private val userInterface: UserInterface)
	: UserRepository {

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
				val apiKey = context.resources.getString(R.string.google_api_key)
				val loginRequest = LoginRequest(email!!, password!!)
				userInterface.logIn(apiKey, loginRequest)
						.doOnSuccess {
							sharedPrefs.uid = it?.uid
							sharedPrefs.isLogInRememberable = isLoginRememberable
							updateRememberableLogIn(email, password)
						}
			}

		}
	}

	override fun register(email: String?, password: String?): Single<FirebaseUser> {
		return when {
			email.isNullOrBlank() -> Single.fromCallable { throw Exception(context.getString(R.string.enter_email)) }
			password.isNullOrEmpty() -> Single.fromCallable { throw Exception(context.getString(R.string.enter_password)) }
			else -> Single.fromCallable { throw Exception("Enter Password!") }
		}
	}

	override fun getUid(): String {
		return sharedPrefs.uid
	}

	override fun isLoggedIn(): Boolean {
		return sharedPrefs.uid.isNotEmpty()
	}

	override fun isLoginRememberable(): Boolean {
		return sharedPrefs.isLogInRememberable
	}

	override fun getRememberedEmail(): String {
		return sharedPrefs.email
	}

	override fun getRememberedPassword(): String {
		return sharedPrefs.password
	}

	override fun logOut() {
		sharedPrefs.uid = ""
	}

	private fun updateRememberableLogIn(email: String, password: String) {
		if (sharedPrefs.isLogInRememberable) {
			sharedPrefs.email = email
			sharedPrefs.password = password
		} else {
			sharedPrefs.email = ""
			sharedPrefs.password = ""
		}
	}
}