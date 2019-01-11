package pl.karolmichalski.shoppinglist.data.models

import com.fasterxml.jackson.annotation.JsonProperty

class ErrorResponse(@JsonProperty("error")
					val apiError: ApiError)