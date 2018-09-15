package inc.ahmedmourad.inventorial.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.bus.RxBus;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.utils.ErrorUtils;
import inc.ahmedmourad.inventorial.utils.FileUtils;

public class DatabaseService extends IntentService {

	private static final String ACTION_INSERT_SUPPLIER = "ds_a_is";
	private static final String ACTION_INSERT_PRODUCT = "ds_a_ip";
	private static final String ACTION_UPDATE_PRODUCT = "ds_a_up";
	private static final String ACTION_DELETE_PRODUCT = "ds_a_dp";
	private static final String ACTION_SAVE_FILE = "ds_a_sf";
	private static final String ACTION_DELETE_FILE = "ds_a_df";

	private static final String EXTRA_SUPPLIER = "ds_e_s";
	private static final String EXTRA_PRODUCT_CONTENT_VALUES = "ds_e_pcv";
	private static final String EXTRA_PRODUCT_SUPPLIER_ID = "ds_e_psi";
	private static final String EXTRA_PRODUCT_ID = "ds_e_pi";
	private static final String EXTRA_FILE_NAME = "ds_e_fn";

	private static final Stack<Bitmap> bitmaps = new Stack<>();

	public DatabaseService() {
		super("DatabaseService");
	}

	public static void startActionInsertSupplier(@NonNull final Context context, @NonNull final Supplier supplier) {
		Intent intent = new Intent(context.getApplicationContext(), DatabaseService.class);
		intent.setAction(ACTION_INSERT_SUPPLIER);
		intent.putExtra(EXTRA_SUPPLIER, Parcels.wrap(supplier));
		context.getApplicationContext().startService(intent);
	}

	public static void startActionInsertProduct(@NonNull final Context context, @NonNull final ContentValues values) {
		Intent intent = new Intent(context.getApplicationContext(), DatabaseService.class);
		intent.setAction(ACTION_INSERT_PRODUCT);
		intent.putExtra(EXTRA_PRODUCT_CONTENT_VALUES, values);
		context.getApplicationContext().startService(intent);
	}

	public static void startActionUpdateProduct(@NonNull final Context context, @NonNull final ContentValues values, final long supplierId) {
		Intent intent = new Intent(context.getApplicationContext(), DatabaseService.class);
		intent.setAction(ACTION_UPDATE_PRODUCT);
		intent.putExtra(EXTRA_PRODUCT_CONTENT_VALUES, values);
		intent.putExtra(EXTRA_PRODUCT_SUPPLIER_ID, supplierId);
		context.getApplicationContext().startService(intent);
	}

	public static void startActionDeleteProduct(@NonNull final Context context, final long productId) {
		Intent intent = new Intent(context.getApplicationContext(), DatabaseService.class);
		intent.setAction(ACTION_DELETE_PRODUCT);
		intent.putExtra(EXTRA_PRODUCT_ID, productId);
		context.getApplicationContext().startService(intent);
	}

	public static void startActionSaveFile(@NonNull final Context context, @NonNull final String name, @NonNull final Bitmap bitmap) {
		Intent intent = new Intent(context.getApplicationContext(), DatabaseService.class);
		intent.setAction(ACTION_SAVE_FILE);
		intent.putExtra(EXTRA_FILE_NAME, name);
		bitmaps.add(bitmap);
		context.getApplicationContext().startService(intent);
	}

	public static void startActionDeleteFile(@NonNull final Context context, @NonNull final String name) {
		Intent intent = new Intent(context.getApplicationContext(), DatabaseService.class);
		intent.setAction(ACTION_DELETE_FILE);
		intent.putExtra(EXTRA_FILE_NAME, name);
		context.getApplicationContext().startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (intent == null || intent.getAction() == null) {
			ErrorUtils.general(getApplicationContext(), null);
			RxBus.getInstance().setCurrentState(RxBus.STATE_IDLE);
			return;
		}

		RxBus.getInstance().setCurrentState(RxBus.STATE_IN_PROGRESS);

		switch (intent.getAction()) {

			case ACTION_INSERT_SUPPLIER:
				handleActionInsertSupplier(Parcels.unwrap(intent.getParcelableExtra(EXTRA_SUPPLIER)));
				break;

			case ACTION_INSERT_PRODUCT:
				handleActionInsertProduct(intent.getParcelableExtra(EXTRA_PRODUCT_CONTENT_VALUES));
				break;

			case ACTION_UPDATE_PRODUCT:
				handleActionUpdateProduct(intent.getParcelableExtra(EXTRA_PRODUCT_CONTENT_VALUES),
						intent.getLongExtra(EXTRA_PRODUCT_SUPPLIER_ID, -1)
				);
				break;

			case ACTION_DELETE_PRODUCT:
				handleActionDeleteProduct(intent.getLongExtra(EXTRA_PRODUCT_ID, -1));
				break;

			case ACTION_DELETE_FILE:
				handleActionDeleteFile(intent.getStringExtra(EXTRA_FILE_NAME));
				break;

			case ACTION_SAVE_FILE:
				if (!bitmaps.empty())
					handleActionSaveBitmap(intent.getStringExtra(EXTRA_FILE_NAME), bitmaps.pop());
				break;
		}

		RxBus.getInstance().setCurrentState(RxBus.STATE_IDLE);
	}

	private void handleActionInsertSupplier(@NonNull final Supplier supplier) {
		InventorialDatabase.getInstance().insertSupplier(getApplicationContext(), supplier);
		Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
	}

	private void handleActionInsertProduct(@NonNull final ContentValues values) {
		InventorialDatabase.getInstance().insertProduct(getApplicationContext(), values);
		Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
	}

	private void handleActionUpdateProduct(@NonNull final ContentValues values, final long supplierId) {
		InventorialDatabase.getInstance().updateProduct(getApplicationContext(), values, supplierId);
		Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
	}

	private void handleActionDeleteProduct(final long productId) {
		InventorialDatabase.getInstance().deleteProduct(getApplicationContext(), productId);
		Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
	}

	private void handleActionSaveBitmap(@NonNull final String name, @NonNull final Bitmap bitmap) {

		final File directory = getApplicationContext().getDir(FileUtils.DIRECTORY_PRODUCTS_IMAGES, Context.MODE_PRIVATE);

		// Create the Products Images directory
		final File path = new File(directory, name + FileUtils.FORMAT_PRODUCTS_IMAGES);

		FileOutputStream outputStream = null;

		try {

			outputStream = new FileOutputStream(path);

			// Write image to the outputStream
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} finally {

			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				RxBus.getInstance().setCurrentState(RxBus.STATE_IDLE);
			}
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void handleActionDeleteFile(@NonNull final String name) {

		final File directory = getApplicationContext().getDir(FileUtils.DIRECTORY_PRODUCTS_IMAGES, Context.MODE_PRIVATE);

		final File file = new File(directory, name + FileUtils.FORMAT_PRODUCTS_IMAGES);

		if (file.exists())
			file.delete();
	}
}
