package pl.developit.shoppinglist.presentation.screens.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.domain.user.UserRepository
import javax.inject.Inject

class LoginViewModel @Inject constructor(
		private val userRepository: UserRepository)
	: ViewModel() {

	val email = MutableLiveData<String>().apply { value = userRepository.getRememberedEmail() }
	val password = MutableLiveData<String>().apply { value = userRepository.getRememberedPassword() }
	val isLoading = MutableLiveData<Boolean>().apply { value = false }
	val isLoginRememberable = MutableLiveData<Boolean>().apply { value = userRepository.isLoginRememberable() }

	val loginSuccess = MutableLiveData<Boolean>()
	val errorMessage = MutableLiveData<String>()

	fun logIn() {
		userRepository.logIn(isLoginRememberable.value, email.value, password.value)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribeBy(
						onSuccess = { loginSuccess.value = true },
						onError = { errorMessage.value = it.localizedMessage }
				)
	}

	fun register() {
		userRepository.register(email.value, password.value)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribeBy(
						onSuccess = { loginSuccess.value = true },
						onError = { errorMessage.value = it.localizedMessage }
				)
	}

	fun isUserLogged(): Boolean {
		return userRepository.isLoggedIn()
	}


}