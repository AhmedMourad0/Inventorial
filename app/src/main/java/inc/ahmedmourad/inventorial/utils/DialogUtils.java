package inc.ahmedmourad.inventorial.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import inc.ahmedmourad.inventorial.R;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public final class DialogUtils {

	public static void showDeleteProductConfirmationDialog(@NonNull final Context context,
	                                                       @NonNull final String productName,
	                                                       @NonNull final DialogInterface.OnClickListener positiveClickListener) {

		final String msg = StringUtils.fromHtml(context.getString(R.string.dialog_message_delete_product,
				productName
		));

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
				.setTitle(R.string.delete_product)
				.setMessage(msg)
				.setPositiveButton(R.string.delete, positiveClickListener)
				.create();

		dialog.setOnShowListener(d -> dialog.getButton(BUTTON_POSITIVE)
				.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
		);

		dialog.show();
	}

	public static void showNumberPickerDialog(@NonNull final Context context,
	                                          @NonNull final String title,
	                                          @StringRes final int positiveId,
	                                          @IntRange(from = 1, to = Integer.MAX_VALUE) final int maxValue,
	                                          @NonNull final OnNumberSelectedListener listener) {

		final NumberPicker numberPicker = new NumberPicker(new ContextThemeWrapper(context, R.style.DefaultNumberPickerTheme));
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(maxValue);
		numberPicker.setValue(1);
		numberPicker.setWrapSelectorWheel(true);

		final FrameLayout layout = new FrameLayout(context);
		layout.addView(numberPicker, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER)
		);

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
				.setTitle(title)
				.setPositiveButton(positiveId, (d, which) -> {
					// clearing focus selects the value when the user enters it using soft keyboard
					// not clearing focus would cause the value not to be selected
					numberPicker.clearFocus();
					listener.onSelected(numberPicker.getValue());
				}).setView(layout)
				.create();

		dialog.setOnShowListener(d ->
				dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
		);

		dialog.show();
	}

	@FunctionalInterface
	public interface OnNumberSelectedListener {
		void onSelected(final int number);
	}

	private DialogUtils() {

	}
}
