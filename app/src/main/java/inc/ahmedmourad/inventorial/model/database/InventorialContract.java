package inc.ahmedmourad.inventorial.model.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class InventorialContract {

	static final String CONTENT_AUTHORITY = "inc.ahmedmourad.inventorial";

	static final String PATH_SUPPLIERS = "suppliers";
	static final String PATH_PRODUCTS = "products";

	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	// we're using prefixes to column names because obviously, android doesn't know how to handle joins
	// when the two tables have columns with similar names
	public static final class SuppliersEntry {

		public static final String TABLE_NAME = "suppliers";

		public static final String COLUMN_ID = "s_id";
		public static final String COLUMN_NAME = "s_name";
		public static final String COLUMN_PHONE_NUMBER = "phone_number";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUPPLIERS).build();

		static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

		// content://inc.ahmedmourad.inventorial/suppliers/#
		public static Uri buildSupplierUriWithId(final long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static long getSupplierIdFromUri(final Uri uri) {
			return ContentUris.parseId(uri);
		}

		private SuppliersEntry() {

		}
	}

	public static final class ProductsEntry {

		public static final String TABLE_NAME = "products";

		public static final String COLUMN_ID = "p_id";
		public static final String COLUMN_NAME = "p_name";
		public static final String COLUMN_PRICE = "price";
		public static final String COLUMN_QUANTITY = "quantity";
		public static final String COLUMN_SUPPLIER_ID = "supplier_id";

		public static final String PATH_PAIRS = "pairs";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).build();

		static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
		static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		// content://inc.ahmedmourad.inventorial/products/#
		public static Uri buildProductUriWithId(final long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static long getProductIdFromUri(final Uri uri) {
			return ContentUris.parseId(uri);
		}

		// content://inc.ahmedmourad.inventorial/products/pairs/#
		public static Uri buildPairUriWithProductId(final long id) {
			return CONTENT_URI.buildUpon().appendPath(PATH_PAIRS).appendPath(Long.toString(id)).build();
		}

		public static long getProductIdFromPairUri(final Uri uri) {
			return Long.parseLong(uri.getLastPathSegment());
		}

		// content://inc.ahmedmourad.inventorial/products/pairs/*
		public static Uri buildPairUriWithProductName(@NonNull final String productName) {
			return CONTENT_URI.buildUpon().appendPath(PATH_PAIRS).appendPath(productName).build();
		}

		@NonNull
		public static String getProductNameFromPairUri(final Uri uri) {
			return uri.getLastPathSegment();
		}

		// content://inc.ahmedmourad.inventorial/products/pairs/supplier_id/#
		public static Uri buildPairsUriWithSupplierId(final long id) {
			return CONTENT_URI.buildUpon().appendPath(PATH_PAIRS).appendPath(COLUMN_SUPPLIER_ID).appendPath(Long.toString(id)).build();
		}

		public static long getSupplierIdFromPairUri(final Uri uri) {
			return Long.parseLong(uri.getLastPathSegment());
		}

		// content://inc.ahmedmourad.inventorial/products/pairs
		public static Uri buildAllPairsUri() {
			return CONTENT_URI.buildUpon().appendPath(PATH_PAIRS).build();
		}

		private ProductsEntry() {

		}
	}

	private InventorialContract() {

	}
}
