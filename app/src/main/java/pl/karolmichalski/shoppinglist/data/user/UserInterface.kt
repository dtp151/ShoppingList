package pl.karolmichalski.shoppinglist.data.user

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.LoginRequest
import pl.karolmichalski.shoppinglist.data.models.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserInterface {

	@POST("verifyPassword")
	fun logIn(@Query("key") apiKey: String,
			  @Body loginRequest: LoginRequest):
			Single<User>
}