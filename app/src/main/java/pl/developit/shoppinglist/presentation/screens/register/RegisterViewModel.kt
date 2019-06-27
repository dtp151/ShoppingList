package pl.developit.shoppinglist.presentation.screens.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.domain.UserUseCases

class RegisterViewModel(private val userUseCases: UserUseCases) : ViewModel() {

	val email = MutableLiveData<String>().apply { value = "" }
	val password = MutableLiveData<String>().apply { value = "" }
	val repeatedPassword = MutableLiveData<String>().apply { value = "" }

	val isLoading = MutableLiveData<Boolean>().apply { value = false }

	val liveEvent = MutableLiveData<RegisterEvent>()

	private val disposables = CompositeDisposable()

	override fun onCleared() {
		super.onCleared()
		disposables.clear()
	}

	fun register(email: String, password: String, repeatedPassword: String) {
		userUseCases.register(email, password, repeatedPassword)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe { isLoading.value = true }
				.doFinally { isLoading.value = false }
				.subscribe(
						{ liveEvent.value = RegisterEvent.Registration },
						{ liveEvent.value = RegisterEvent.Error(it.localizedMessage) }
				).addTo(disposables)
	}

	sealed class RegisterEvent {
		object Registration : RegisterEvent()
		class Error(val error: String) : RegisterEvent()
	}

}