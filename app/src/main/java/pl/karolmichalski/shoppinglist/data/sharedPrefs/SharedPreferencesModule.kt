package pl.karolmichalski.shoppinglist.data.sharedPrefs

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import pl.karolmichalski.shoppinglist.R
import javax.inject.Singleton

@Module
class SharedPreferencesModule(private val context: Context) {

	@Provides
	@Singleton
	fun provideSharedPreferences(): SharedPreferences {
		return context.getSharedPreferences(context.getString(R.string.sharedpreferences_file_key), Context.MODE_PRIVATE)
	}
}