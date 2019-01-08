package pl.karolmichalski.shoppinglist.data.models

import com.fasterxml.jackson.annotation.JsonProperty

class User(
		@JsonProperty("localId")
		val uid: String,
		@JsonProperty("email")
		val email: String)
