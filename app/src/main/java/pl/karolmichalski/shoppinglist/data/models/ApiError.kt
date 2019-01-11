package pl.karolmichalski.shoppinglist.data.models

import com.fasterxml.jackson.annotation.JsonProperty

class ApiError(@JsonProperty("code")
			   val code: Int,
			   @JsonProperty("message")
			   val message: String)
