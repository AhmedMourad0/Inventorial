package inc.ahmedmourad.inventorial.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import inc.ahmedmourad.inventorial.R;

public final class ErrorUtils {

	/**
	 * A not so serious error
	 *
	 * @param context   context
	 * @param throwable the error throwable
	 */
	public static void general(@NonNull final Context context, @Nullable final Throwable throwable) {

		if (throwable == null)
			Toast.makeText(context,
					context.getString(R.string.error),
					Toast.LENGTH_LONG
			).show();
		else if (throwable.getCause() == null)
			Toast.makeText(context,
					context.getString(R.string.error_no_cause,
							throwable.getLocalizedMessage()
					), Toast.LENGTH_LONG
			).show();
		else
			Toast.makeText(context,
					context.getString(R.string.error_cause,
							throwable.getLocalizedMessage(),
							throwable.getCause().getLocalizedMessage()
					), Toast.LENGTH_LONG
			).show();

		if (throwable != null) {

			throwable.printStackTrace();

			if (throwable.getCause() != null)
				throwable.getCause().printStackTrace();
		}
	}

	private ErrorUtils() {

	}
}
