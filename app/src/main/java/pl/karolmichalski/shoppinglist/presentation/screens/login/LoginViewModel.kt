package pl.karolmichalski.shoppinglist.presentation.screens.login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxkotlin.subscribeBy
import pl.karolmichalski.shoppinglist.R
import pl.karolmichalski.shoppinglist.data.sharedPrefs.Boolean
import pl.karolmichalski.shoppinglist.data.sharedPrefs.String
import pl.karolmichalski.shoppinglist.domain.user.UserRepository
import pl.karolmichalski.shoppinglist.presentation.App
import javax.inject.Inject

class LoginViewModel(app: App) : ViewModel() {

	class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			@Suppress("UNCHECKED_CAST")
			return LoginViewModel(application as App) as T
		}
	}

	private val sharedPrefs: SharedPreferences = app.applicationContext.getSharedPreferences(app.applicationContext.getString(R.string.sharedpreferences_file_key), Context.MODE_PRIVATE)

	private var SharedPreferences.isLoginRememberable by sharedPrefs.Boolean()
	private var SharedPreferences.email by sharedPrefs.String()
	private var SharedPreferences.password by sharedPrefs.String()

	val email = MutableLiveData<String>()
	val password = MutableLiveData<String>()
	val isLoading = MutableLiveData<Boolean>().apply { value = false }
	val isLoginRememberable = MutableLiveData<Boolean>().apply { value = sharedPrefs.isLoginRememberable }

	val loginSuccess = MutableLiveData<Boolean>()
	val errorMessage = MutableLiveData<String>()

	@Inject
	lateinit var userRepository: UserRepository

	init {
		app.appComponent.inject(this)
	}

	fun logInWithEmailAndPassword() {
		userRepository.logIn(email.value, password.value)
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribeBy(
						onSuccess = {
							isLoginRememberable.value?.let { value ->
								sharedPrefs.isLoginRememberable = value
								if (value) {
									sharedPrefs.email = email.value
									sharedPrefs.password = password.value
								} else {
									sharedPrefs.email = ""
									sharedPrefs.password = ""
								}
							}
							loginSuccess.value = true
						},
						onError = { errorMessage.value = it.localizedMessage }
				)
	}

	fun registerWithEmailAndPassword() {
		userRepository.register(email.value, password.value)
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribeBy(
						onSuccess = { loginSuccess.value = true },
						onError = { errorMessage.value = it.localizedMessage }
				)
	}

	fun isUserLogged(): Boolean {
		return userRepository.getCurrentUser() != null
	}

}