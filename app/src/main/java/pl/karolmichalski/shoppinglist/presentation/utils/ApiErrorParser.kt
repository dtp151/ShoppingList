package pl.karolmichalski.shoppinglist.presentation.utils

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import pl.karolmichalski.shoppinglist.data.models.ErrorResponse
import retrofit2.HttpException


class ApiErrorParser(private val context: Context) {

	private val mapper = ObjectMapper().apply {
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
	}

	fun parse(throwable: Throwable): Throwable {
		return try {
			val json = (throwable as HttpException).response().errorBody()?.string()
			val error = mapper.readValue(json, ErrorResponse::class.java)
			val messageKey = error.apiError.message
			val message = getStringResourceByName(messageKey)
			Exception(message)
		} catch (exception: Exception) {
			throwable
		}
	}

	private fun getStringResourceByName(aString: String?): String {
		val packageName = context.packageName
		val resId = context.resources.getIdentifier(aString, "string", packageName)
		return context.getString(resId)
	}
}