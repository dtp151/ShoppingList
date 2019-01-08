package pl.karolmichalski.shoppinglist.data.models

import com.fasterxml.jackson.annotation.JsonProperty

class LoginRequest(@JsonProperty("email")
				   val email: String,
				   @JsonProperty("password")
				   val password: String)