package pl.karolmichalski.shoppinglist.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "products")
class Product(@ColumnInfo(name = "name")
			  @JsonProperty("name")
			  val name: String,
			  @ColumnInfo(name = "status")
			  @JsonProperty("status")
			  var status: Int) {
	@PrimaryKey(autoGenerate = true)
	@JsonProperty("id")
	var id: Int = 0
	@Ignore
	@get:JsonIgnore
	var isChecked: Boolean = false

	class Status {
		companion object {
			const val ADDED = 0
			const val SYNCED = 1
			const val DELETED = 2
		}
	}
}