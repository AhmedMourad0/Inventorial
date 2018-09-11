package inc.ahmedmourad.inventorial.model.pojo;

import android.database.Cursor;
import android.support.annotation.NonNull;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class ProductSupplierPair {

	private Product product;
	private Supplier supplier;

	@NonNull
	public static ProductSupplierPair fromCursor(@NonNull final Cursor cursor) {
		final ProductSupplierPair pair = new ProductSupplierPair();
		pair.setProduct(Product.fromCursor(cursor));
		pair.setSupplier(Supplier.fromCursor(cursor));
		return pair;
	}

	@NonNull
	public Product getProduct() {
		return product;
	}

	@SuppressWarnings("WeakerAccess")
	public void setProduct(@NonNull Product product) {
		this.product = product;
	}

	@NonNull
	public Supplier getSupplier() {
		return supplier;
	}

	@SuppressWarnings("WeakerAccess")
	public void setSupplier(@NonNull Supplier supplier) {
		this.supplier = supplier;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		final ProductSupplierPair pair = (ProductSupplierPair) o;

		return getProduct().equals(pair.getProduct()) &&
				getSupplier().equals(pair.getSupplier());
	}

	@Override
	public int hashCode() {

		int result = getProduct().hashCode();

		result = result * 31 + getSupplier().hashCode();

		return result;
	}

	@Override
	public String toString() {
		return "ProductSupplierPair{" +
				"product=" + getProduct() +
				", supplier=" + getSupplier() +
				'}';
	}
}
