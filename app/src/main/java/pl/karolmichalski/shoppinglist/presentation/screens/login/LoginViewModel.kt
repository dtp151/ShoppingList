package pl.karolmichalski.shoppinglist.presentation.screens.login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.subscribeBy
import pl.karolmichalski.shoppinglist.domain.user.UserRepository
import pl.karolmichalski.shoppinglist.presentation.utils.boolean
import pl.karolmichalski.shoppinglist.presentation.utils.string
import javax.inject.Inject

class LoginViewModel @Inject constructor(
		private val sharedPrefs: SharedPreferences)
	: ViewModel() {

	private var SharedPreferences.isLoginRememberable by sharedPrefs.boolean()
	private var SharedPreferences.email by sharedPrefs.string()
	private var SharedPreferences.password by sharedPrefs.string()
	private var SharedPreferences.uid by sharedPrefs.string()

	val email = MutableLiveData<String>().apply { value = sharedPrefs.email }
	val password = MutableLiveData<String>().apply { value = sharedPrefs.password }
	val isLoading = MutableLiveData<Boolean>().apply { value = false }
	val isLoginRememberable = MutableLiveData<Boolean>().apply { value = sharedPrefs.isLoginRememberable }

	val loginSuccess = MutableLiveData<Boolean>()
	val errorMessage = MutableLiveData<String>()

	@Inject
	lateinit var userRepository: UserRepository

	fun logInWithEmailAndPassword() {
		userRepository.logIn(email.value, password.value)
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribeBy(
						onSuccess = {
							sharedPrefs.uid = it?.uid
							resolveRememberableLogin()
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

	private fun resolveRememberableLogin() {
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
	}

}