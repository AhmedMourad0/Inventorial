package inc.ahmedmourad.inventorial.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.services.DatabaseService;

public final class FileUtils {

	public static final String DIRECTORY_PRODUCTS_IMAGES = "Products Images";
	public static final String FORMAT_PRODUCTS_IMAGES = ".png";

	public static void saveToInternalStorage(@NonNull final Context context, @NonNull final ImageView imageView, @NonNull final String name) {
		saveToInternalStorage(context, imageView.getDrawable(), name);
	}

	private static void saveToInternalStorage(@NonNull final Context context, @NonNull final Drawable drawable, @NonNull final String name) {
		saveToInternalStorage(context, ((BitmapDrawable) drawable).getBitmap(), name);
	}

	private static void saveToInternalStorage(@NonNull final Context context, @NonNull final Bitmap bitmap, @NonNull final String name) {
		DatabaseService.startActionSaveFile(context, name, bitmap);
	}

	public static void loadImageFromStorage(@NonNull final Context context, @NonNull final String name, @NonNull final ImageView imageView) {

		// Path to /data/data/Inventorial/app_data/imageDir
		final File directory = context.getApplicationContext().getDir(DIRECTORY_PRODUCTS_IMAGES, Context.MODE_PRIVATE);

		Picasso.get()
				.load(new File(directory, name + FORMAT_PRODUCTS_IMAGES))
				.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
				.placeholder(R.drawable.placeholder)
				.into(imageView);
	}

	public static void deleteFile(@NonNull final Context context, @NonNull final String name) {
		DatabaseService.startActionDeleteFile(context, name);
	}

	private FileUtils() {

	}
}
