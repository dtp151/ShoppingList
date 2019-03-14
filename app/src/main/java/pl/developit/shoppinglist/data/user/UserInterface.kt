package pl.developit.shoppinglist.data.user

import io.reactivex.Single
import pl.developit.shoppinglist.data.models.User
import pl.developit.shoppinglist.data.models.UserRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserInterface {

	@POST("signupNewUser")
	fun register(@Query("key") apiKey: String,
				 @Body userRequest: UserRequest):
			Single<User>

	@POST("verifyPassword")
	fun logIn(@Query("key") apiKey: String,
			  @Body userRequest: UserRequest):
			Single<User>
}