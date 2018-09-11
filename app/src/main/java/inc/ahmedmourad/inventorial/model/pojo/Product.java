package inc.ahmedmourad.inventorial.model.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.parceler.Parcel;

import java.util.Arrays;

import inc.ahmedmourad.inventorial.model.database.InventorialContract.ProductsEntry;

@Parcel(Parcel.Serialization.BEAN)
public class Product {

	private String name = "";

	private double price = 0.0;
	private double quantity = 0.0;

	private byte[] image = null;

	private long id = -1;

	@NonNull
	public static Product of(@NonNull final String name, final double price, final double quantity, @NonNull final byte[] image) {
		final Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		product.setQuantity(quantity);
		product.setImage(image);
		return product;
	}

	@NonNull
	public static Product fromCursor(@NonNull final Cursor cursor) {
		final Product product = new Product();
		product.setId(cursor.getLong(cursor.getColumnIndex(ProductsEntry.COLUMN_ID)));
		product.setName(cursor.getString(cursor.getColumnIndex(ProductsEntry.COLUMN_NAME)));
		product.setPrice(cursor.getDouble(cursor.getColumnIndex(ProductsEntry.COLUMN_PRICE)));
		product.setQuantity(cursor.getDouble(cursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY)));
		product.setImage(cursor.getBlob(cursor.getColumnIndex(ProductsEntry.COLUMN_IMAGE)));
		return product;
	}

	@NonNull
	public String getName() {
		return name;
	}

	@SuppressWarnings("WeakerAccess")
	public void setName(@NonNull final String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	@SuppressWarnings("WeakerAccess")
	public void setPrice(final double price) {
		this.price = price;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(final double quantity) {
		this.quantity = quantity;
	}

	@NonNull
	public byte[] getImage() {
		return image;
	}

	@SuppressWarnings("WeakerAccess")
	public void setImage(@NonNull final byte[] image) {
		this.image = image;
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
		return toContentValues(-1);
	}

	@NonNull
	public ContentValues toContentValues(final long supplierId) {

		final ContentValues contentValues = new ContentValues(5);

		contentValues.put(ProductsEntry.COLUMN_NAME, getName());
		contentValues.put(ProductsEntry.COLUMN_PRICE, getPrice() > 0 ? getPrice() : 0);
		contentValues.put(ProductsEntry.COLUMN_QUANTITY, getQuantity() > 0 ? getQuantity() : 0);
		contentValues.put(ProductsEntry.COLUMN_IMAGE, getImage());
		contentValues.put(ProductsEntry.COLUMN_SUPPLIER_ID, supplierId);

		return contentValues;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		final Product product = (Product) o;

		return Double.compare(product.getPrice(), getPrice()) == 0 &&
				Double.compare(product.getQuantity(), getQuantity()) == 0 &&
				getId() == product.getId() &&
				getName().equals(product.getName()) &&
				Arrays.equals(getImage(), product.getImage());
	}

	@Override
	public int hashCode() {

		int result = getName().hashCode();

		result = result * 31 + (int) getPrice();
		result = result * 31 + (int) getQuantity();
		result = result * 31 + (int) getId();
		result = 31 * result + Arrays.hashCode(getImage());

		return result;
	}

	@Override
	public String toString() {
		return "Product{" +
				"id=" + getId() +
				"name='" + getName() + '\'' +
				", price=" + getPrice() +
				", quantity=" + getQuantity() +
				'}';
	}
}
