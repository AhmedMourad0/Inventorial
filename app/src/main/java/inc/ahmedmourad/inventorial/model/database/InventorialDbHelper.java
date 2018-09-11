package inc.ahmedmourad.inventorial.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import inc.ahmedmourad.inventorial.model.database.InventorialContract.ProductsEntry;
import inc.ahmedmourad.inventorial.model.database.InventorialContract.SuppliersEntry;

class InventorialDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "inventorial.db";

	@SuppressWarnings("CanBeFinal")
	private static volatile InventorialDbHelper INSTANCE = null;

	@NonNull
	public static InventorialDbHelper getInstance(@NonNull final Context context) {
		if (INSTANCE != null) {
			return INSTANCE;
		} else {
			synchronized (InventorialDbHelper.class) {
				return INSTANCE != null ? INSTANCE : (INSTANCE = new InventorialDbHelper(context.getApplicationContext()));
			}
		}
	}

	private InventorialDbHelper(@NonNull final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase sqLiteDatabase) {

		final String SQL_CREATE_SUPPLIERS_TABLE = "CREATE TABLE IF NOT EXISTS " + SuppliersEntry.TABLE_NAME + " (" +
				SuppliersEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SuppliersEntry.COLUMN_NAME + " TEXT NOT NULL UNIQUE, " +
				SuppliersEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL" +
				");";

		final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS " + ProductsEntry.TABLE_NAME + " (" +
				ProductsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ProductsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
				ProductsEntry.COLUMN_PRICE + " REAL NOT NULL, " +
				ProductsEntry.COLUMN_QUANTITY + " REAL NOT NULL, " +
				ProductsEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
				ProductsEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, " +
				" FOREIGN KEY (" + ProductsEntry.COLUMN_SUPPLIER_ID + ") REFERENCES " +
				SuppliersEntry.TABLE_NAME + " (" + SuppliersEntry.COLUMN_ID + ") ON DELETE CASCADE" +
				");";

		sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
		sqLiteDatabase.execSQL(SQL_CREATE_SUPPLIERS_TABLE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {

		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SuppliersEntry.TABLE_NAME);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsEntry.TABLE_NAME);

		onCreate(sqLiteDatabase);
	}
}
