package inc.ahmedmourad.inventorial.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.model.database.InventorialContract.ProductsEntry;
import inc.ahmedmourad.inventorial.model.database.InventorialContract.SuppliersEntry;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.view.activities.DetailsActivity;

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

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isProductNameValid(@NonNull final Context context, @NonNull final View root, @NonNull final String productName) {

		final Cursor cursor = getAllProducts(context);

		if (cursor.moveToFirst()) {

			do {

				if (cursor.getString(cursor.getColumnIndex(ProductsEntry.COLUMN_NAME)).equals(productName)) {

					Snackbar.make(root, R.string.product_name_exists, Snackbar.LENGTH_LONG)
							.setAction(context.getString(R.string.view), v -> {

								final Intent intent = new Intent(context, DetailsActivity.class);

								intent.putExtra(DetailsActivity.KEY_PAIR_ID,
										cursor.getLong(cursor.getColumnIndex(ProductsEntry.COLUMN_ID))
								);

								context.startActivity(intent);

							}).setActionTextColor(ContextCompat.getColor(context, R.color.colorSnackbarAction))
							.show();

					return false;
				}

			} while (cursor.moveToNext());
		}

		return true;
	}

	@NonNull
	private Cursor getAllProducts(@NonNull final Context c) {

		final String[] projection = {ProductsEntry.COLUMN_ID,
				ProductsEntry.COLUMN_NAME
		};

		return execute(c, context -> context.getContentResolver().query(ProductsEntry.CONTENT_URI,
				projection,
				null,
				null,
				ProductsEntry.TABLE_NAME + "." + ProductsEntry.COLUMN_NAME + " ASC"
				)
		);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isSupplierNameValid(@NonNull final Context context, @NonNull final View root, @NonNull final String supplierName) {

		final Cursor cursor = getAllSuppliers(context);

		if (cursor.moveToFirst()) {

			do {

				if (cursor.getString(cursor.getColumnIndex(SuppliersEntry.COLUMN_NAME)).equals(supplierName)) {
					Snackbar.make(root, R.string.supplier_name_exists, Snackbar.LENGTH_LONG).show();
					return false;
				}

			} while (cursor.moveToNext());
		}

		return true;
	}

	@NonNull
	private Cursor getAllSuppliers(@NonNull final Context c) {

		final String[] projection = {SuppliersEntry.COLUMN_ID,
				SuppliersEntry.COLUMN_NAME
		};

		return execute(c, context -> context.getContentResolver().query(SuppliersEntry.CONTENT_URI,
				projection,
				null,
				null,
				SuppliersEntry.TABLE_NAME + "." + SuppliersEntry.COLUMN_NAME + " ASC"
				)
		);
	}

	@NonNull
	public Loader<Cursor> getAllPairsLoader(@NonNull final Context c) {
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
	public Loader<Cursor> getPairLoader(@NonNull final Context c, final long productId) {
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
	public Loader<Cursor> getPairLoader(@NonNull final Context c, @NonNull final String productName) {
		return execute(c, context -> new CursorLoader(context,
						InventorialContract.ProductsEntry.buildPairUriWithProductName(productName),
						null,
						null,
						null,
						null
				)
		);
	}

	@NonNull
	public Loader<Cursor> getAllSupplierPairsLoader(@NonNull final Context c, final long supplierId) {
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
	public Loader<Cursor> getAllSuppliersLoader(@NonNull final Context c, @Nullable final String[] projection) {
		return execute(c, context -> new CursorLoader(context,
						SuppliersEntry.CONTENT_URI,
				projection,
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
