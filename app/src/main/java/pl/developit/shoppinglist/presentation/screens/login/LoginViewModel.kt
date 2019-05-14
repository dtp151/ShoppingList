package pl.developit.shoppinglist.presentation.screens.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.domain.UserUseCases

class LoginViewModel(private val userUseCases: UserUseCases) : ViewModel() {

	val email = MutableLiveData<String>().apply { value = userUseCases.getRememberedEmail() }
	val password = MutableLiveData<String>().apply { value = userUseCases.getRememberedPassword() }
	val isLoading = MutableLiveData<Boolean>().apply { value = false }
	val isLoginRememberable = MutableLiveData<Boolean>().apply { value = userUseCases.isLoginRememberable() }

	val liveState = MutableLiveData<LoginState>()

	private val disposables = CompositeDisposable()

	override fun onCleared() {
		super.onCleared()
		disposables.clear()
	}

	fun logIn() {
		userUseCases.logIn(isLoginRememberable.value, email.value, password.value)
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
		userUseCases.register(email.value, password.value)
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

}