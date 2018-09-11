package inc.ahmedmourad.inventorial.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public final class BitmapUtils {

	@NonNull
	public static byte[] toByteArray(@NonNull final ImageView imageView) {
		return toByteArray(imageView.getDrawable());
	}

	@NonNull
	private static byte[] toByteArray(@NonNull final Drawable drawable) {
		return toByteArray(((BitmapDrawable) drawable).getBitmap());
	}

	@NonNull
	private static byte[] toByteArray(@NonNull final Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

	@NonNull
	public static Bitmap fromByteArray(@NonNull final byte[] image) {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}

	private BitmapUtils() {

	}
}
