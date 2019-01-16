package pl.karolmichalski.shoppinglist.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import pl.karolmichalski.shoppinglist.presentation.screens.login.LoginViewModel
import pl.karolmichalski.shoppinglist.presentation.screens.main.MainViewModel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Suppress("unused")
@Module
abstract class ViewModelModule {
	@Binds
	internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

	@Binds
	@IntoMap
	@ViewModelKey(LoginViewModel::class)
	internal abstract fun postLoginViewModel(viewModel: LoginViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	internal abstract fun postMainViewModel(viewModel: MainViewModel): ViewModel

}

@Singleton
class ViewModelFactory @Inject constructor(
		private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>)
	: ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)