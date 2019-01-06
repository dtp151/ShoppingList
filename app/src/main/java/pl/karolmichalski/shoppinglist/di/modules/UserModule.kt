package pl.karolmichalski.shoppinglist.di.modules

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import pl.karolmichalski.shoppinglist.data.user.UserRepositoryImpl
import pl.karolmichalski.shoppinglist.domain.user.UserRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
class UserModule {

	@Provides
	@Singleton
	fun provideUserRepository(
			@Named("firebaseAuth") firebaseAuth: FirebaseAuth): UserRepository {
		return UserRepositoryImpl(firebaseAuth)
	}

	@Provides
	@Singleton
	@Named("firebaseAuth")
	fun provideFirebaseAuth(): FirebaseAuth {
		return FirebaseAuth.getInstance()
	}
}