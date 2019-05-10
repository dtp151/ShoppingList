package pl.developit.shoppinglist.presentation.screens.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.domain.UserRepository

class LoginViewModel(
		private val userRepository: UserRepository)
	: ViewModel() {

	val email = MutableLiveData<String>().apply { value = userRepository.getRememberedEmail() }
	val password = MutableLiveData<String>().apply { value = userRepository.getRememberedPassword() }
	val isLoading = MutableLiveData<Boolean>().apply { value = false }
	val isLoginRememberable = MutableLiveData<Boolean>().apply { value = userRepository.isLoginRememberable() }

	val liveState = MutableLiveData<LoginState>()

	private val disposables = CompositeDisposable()

	override fun onCleared() {
		super.onCleared()
		disposables.clear()
	}

	fun logIn() {
		userRepository.logIn(isLoginRememberable.value, email.value, password.value)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribe(
						{ liveState.value = LoginState.Success },
						{ liveState.value = LoginState.Error(it.localizedMessage) }
				).addTo(disposables)
	}

	fun register() {
		userRepository.register(email.value, password.value)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribe(
						{ liveState.value = LoginState.Success },
						{ liveState.value = LoginState.Error(it.localizedMessage) }
				).addTo(disposables)
	}

	sealed class LoginState {
		object Success : LoginState()
		class Error(val error: String) : LoginState()
	}

	private fun Disposable.addTo(disposables: CompositeDisposable) {
		disposables.add(this)
	}

}