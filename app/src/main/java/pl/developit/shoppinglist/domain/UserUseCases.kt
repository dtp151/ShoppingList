package pl.developit.shoppinglist.domain

import io.reactivex.Single
import pl.developit.shoppinglist.data.models.User

interface UserUseCases {
	fun logIn(isLoginRememberable: Boolean?, email: String?, password: String?): Single<User>

	fun register(email: String?, password: String?): Single<User>

	fun getUid(): String

	fun isLoggedIn(): Boolean

	fun isLoginRememberable(): Boolean

	fun getRememberedEmail(): String

	fun getRememberedPassword(): String

	fun logOut()
}