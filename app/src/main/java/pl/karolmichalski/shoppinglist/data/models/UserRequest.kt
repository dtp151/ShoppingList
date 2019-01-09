package pl.karolmichalski.shoppinglist.data.models

import com.fasterxml.jackson.annotation.JsonProperty

class UserRequest(@JsonProperty("email")
				   val email: String,
				  @JsonProperty("password")
				   val password: String)