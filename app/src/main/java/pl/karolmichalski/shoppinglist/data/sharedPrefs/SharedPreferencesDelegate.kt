package pl.karolmichalski.shoppinglist.data.sharedPrefs

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <T> SharedPreferences.delegate(
		defaultValue: T,
		key: String?,
		crossinline getter: SharedPreferences.(String, T) -> T,
		crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> {
	return object : ReadWriteProperty<Any, T> {
		override fun getValue(thisRef: Any, property: KProperty<*>) =
				getter(key ?: property.name, defaultValue)

		override fun setValue(thisRef: Any, property: KProperty<*>,
							  value: T) =
				edit().setter(key ?: property.name, value).apply()
	}
}

fun SharedPreferences.Int(def: Int = 0, key: String? = null) =
		delegate(def, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun SharedPreferences.Long(def: Long = 0, key: String? = null) =
		delegate(def, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

fun SharedPreferences.Boolean(def: Boolean = false, key: String? = null) =
		delegate(def, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.String(def: String = "", key: String? = null) =
		delegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)
