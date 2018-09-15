package inc.ahmedmourad.inventorial.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;

import java.math.BigDecimal;

public final class StringUtils {

	@NonNull
	public static String toString(final double d) {
		return new BigDecimal(d).toPlainString();
	}

	@SuppressWarnings("deprecation")
	@NonNull
	public static String fromHtml(@NonNull final String html) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString();
		else
			return Html.fromHtml(html).toString();
	}

	private StringUtils() {

	}
}
