package pl.developit.shoppinglist.domain

import io.reactivex.Single
import pl.developit.shoppinglist.data.models.User

interface UserUseCases {
	fun logIn(email: String, password: String, isLoginRememberable: Boolean): Single<User>

	fun register(email: String, password: String, repeatedPassword: String): Single<User>

	fun getUid(): String

	fun isLoggedIn(): Boolean

	fun isLoginRememberable(): Boolean

	fun getRememberedEmail(): String

	fun getRememberedPassword(): String

	fun logOut()
}