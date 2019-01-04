package pl.karolmichalski.shoppinglist.presentation.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Suppress("UNUSED_PARAMETER")
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observeForever(object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}