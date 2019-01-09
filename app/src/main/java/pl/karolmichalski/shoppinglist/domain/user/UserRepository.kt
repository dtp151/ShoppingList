package pl.karolmichalski.shoppinglist.domain.user

import com.google.firebase.auth.FirebaseUser
import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.User

interface UserRepository {
	fun logIn(isLoginRememberable: Boolean?, email: String?, password: String?): Single<User>

	fun register(email: String?, password: String?): Single<FirebaseUser>

	fun getUid(): String

	fun isLoggedIn(): Boolean

	fun isLoginRememberable(): Boolean

	fun getRememberedEmail(): String

	fun getRememberedPassword(): String

	fun logOut()

}