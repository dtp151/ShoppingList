package pl.karolmichalski.shoppinglist.domain.user

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.User

interface UserRepository {
	fun logIn(isLoginRememberable: Boolean?, email: String?, password: String?): Single<User>

	fun register(email: String?, password: String?): Single<User>

	fun getUid(): String

	fun isLoggedIn(): Boolean

	fun isLoginRememberable(): Boolean

	fun getRememberedEmail(): String

	fun getRememberedPassword(): String

	fun logOut()

}