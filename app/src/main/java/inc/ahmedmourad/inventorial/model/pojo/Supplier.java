package inc.ahmedmourad.inventorial.model.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.parceler.Parcel;

import inc.ahmedmourad.inventorial.model.database.InventorialContract.SuppliersEntry;

@Parcel(Parcel.Serialization.BEAN)
public class Supplier {

	private String name = "";
	private String phoneNumber = "";

	private long id = -1;

	@NonNull
	public static Supplier of(@NonNull final String name, @NonNull final String phoneNumber) {
		final Supplier supplier = new Supplier();
		supplier.setName(name);
		supplier.setPhoneNumber(phoneNumber);
		return supplier;
	}

	@NonNull
	public static Supplier fromCursor(@NonNull final Cursor cursor) {
		final Supplier supplier = new Supplier();
		supplier.setId(cursor.getLong(cursor.getColumnIndex(SuppliersEntry.COLUMN_ID)));
		supplier.setName(cursor.getString(cursor.getColumnIndex(SuppliersEntry.COLUMN_NAME)));
		supplier.setPhoneNumber(cursor.getString(cursor.getColumnIndex(SuppliersEntry.COLUMN_PHONE_NUMBER)));
		return supplier;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("WeakerAccess")
	public void setName(@NonNull final String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	@SuppressWarnings("WeakerAccess")
	public void setPhoneNumber(@NonNull final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getId() {
		return id;
	}

	@SuppressWarnings("WeakerAccess")
	public void setId(final long id) {
		this.id = id;
	}

	@NonNull
	public ContentValues toContentValues() {

		final ContentValues contentValues = new ContentValues(2);

		contentValues.put(SuppliersEntry.COLUMN_NAME, getName());
		contentValues.put(SuppliersEntry.COLUMN_PHONE_NUMBER, getPhoneNumber());

		return contentValues;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		final Supplier supplier = (Supplier) o;

		return getId() == supplier.getId() &&
				getName().equals(supplier.getName()) &&
				getPhoneNumber().equals(supplier.getPhoneNumber());
	}

	@Override
	public int hashCode() {

		int result = getName().hashCode();

		result = result * 31 + getPhoneNumber().hashCode();
		result = result * 31 + (int) getId();

		return result;
	}

	@Override
	public String toString() {
		return "Supplier{" +
				"id=" + getId() +
				", name='" + getName() + '\'' +
				", phoneNumber='" + getPhoneNumber() + '\'' +
				'}';
	}
}
