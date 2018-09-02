package pl.karolmichalski.shoppinglist.domain

import com.google.firebase.auth.FirebaseUser
import io.reactivex.Single

interface UserRepository {
	fun login(email: String?, password: String?): Single<FirebaseUser>

	fun register(email: String?, password: String?): Single<FirebaseUser>
}