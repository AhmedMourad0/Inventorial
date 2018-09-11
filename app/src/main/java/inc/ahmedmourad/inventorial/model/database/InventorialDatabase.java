package inc.ahmedmourad.inventorial.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import inc.ahmedmourad.inventorial.model.database.InventorialContract.ProductsEntry;
import inc.ahmedmourad.inventorial.model.database.InventorialContract.SuppliersEntry;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;

public class InventorialDatabase {

	@SuppressWarnings("CanBeFinal")
	private static volatile InventorialDatabase INSTANCE = null;

	@NonNull
	public static InventorialDatabase getInstance() {
		if (INSTANCE != null) {
			return INSTANCE;
		} else {
			synchronized (InventorialDatabase.class) {
				return INSTANCE != null ? INSTANCE : (INSTANCE = new InventorialDatabase());
			}
		}
	}

	public void insertSupplier(@NonNull final Context c, @NonNull final Supplier supplier) {
		execute(c, context -> context.getContentResolver().insert(SuppliersEntry.CONTENT_URI, supplier.toContentValues()));
	}

	public void insertProduct(@NonNull final Context c, @NonNull final ContentValues values) {
		execute(c, context -> context.getContentResolver().insert(ProductsEntry.CONTENT_URI, values));
	}

	public void updateProduct(@NonNull final Context c, @NonNull final ContentValues values, final long productId) {
		execute(c, context ->
				context.getContentResolver().update(ProductsEntry.CONTENT_URI,
						values,
						ProductsEntry.COLUMN_ID + " = ?",
						new String[]{Long.toString(productId)}
				)
		);
	}

	public void deleteProduct(@NonNull final Context c, final long productId) {
		execute(c, context ->
				context.getContentResolver().delete(ProductsEntry.CONTENT_URI,
						ProductsEntry.COLUMN_ID + " = ?",
						new String[]{Long.toString(productId)}
				)
		);
	}

	@NonNull
	public Loader<Cursor> getAllPairs(@NonNull final Context c) {
		return execute(c, context -> new CursorLoader(context,
						ProductsEntry.buildAllPairsUri(),
						null,
						null,
						null,
						null
				)
		);
	}

	@NonNull
	public Loader<Cursor> getPair(@NonNull final Context c, final long productId) {
		return execute(c, context -> new CursorLoader(context,
						InventorialContract.ProductsEntry.buildPairUriWithProductId(productId),
						null,
						null,
						null,
						null
				)
		);
	}

	@NonNull
	public Loader<Cursor> getAllSupplierPairs(@NonNull final Context c, final long supplierId) {
		return execute(c, context -> new CursorLoader(context,
						ProductsEntry.buildPairsUriWithSupplierId(supplierId),
						null,
						null,
						null,
						null
				)
		);
	}

	@NonNull
	public Loader<Cursor> getAllSuppliers(@NonNull final Context c) {
		return execute(c, context -> new CursorLoader(context,
						SuppliersEntry.CONTENT_URI,
						null,
						null,
						null,
						SuppliersEntry.TABLE_NAME + "." + SuppliersEntry.COLUMN_NAME + " ASC"
				)
		);
	}

	private <T> T execute(@NonNull final Context context, @NonNull final Executable<T> executable) {
		return executable.execute(context.getApplicationContext());
	}

	@FunctionalInterface
	interface Executable<T> {
		T execute(@NonNull Context context);
	}
}
