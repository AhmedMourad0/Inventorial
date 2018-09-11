package inc.ahmedmourad.inventorial.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.defaults.DefaultTextWatcher;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.ProductSupplierPair;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;

public final class DialogUtils {

	public static void showNewSupplierDialog(@NonNull final Context context) {

		final View view = View.inflate(context, R.layout.dialog_new_supplier, null);

		final TextInputEditText nameEditText = view.findViewById(R.id.new_supplier_name);
		final TextInputEditText phoneNumberEditText = view.findViewById(R.id.new_supplier_phone_number);

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
				.setTitle(R.string.new_supplier)
				.setView(view)
				.setPositiveButton(R.string.add, (d, which) -> insertSupplier(context, nameEditText, phoneNumberEditText))
				.create();

		dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
				.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
		);

		nameEditText.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				validateSupplierInputs(dialog, nameEditText, phoneNumberEditText);
			}
		});

		phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				validateSupplierInputs(dialog, nameEditText, phoneNumberEditText);
			}
		});

		dialog.show();

		validateSupplierInputs(dialog, nameEditText, phoneNumberEditText);
	}

	private static void validateSupplierInputs(@NonNull final AlertDialog dialog, @NonNull final EditText nameEditText, @NonNull final EditText phoneNumberEditText) {
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(areSupplierInputsValid(nameEditText, phoneNumberEditText));
	}

	private static boolean areSupplierInputsValid(@NonNull final EditText nameEditText, @NonNull final EditText phoneNumberEditText) {
		return nameEditText.getText().toString().trim().length() > 0 &&
				phoneNumberEditText.getText().toString().trim().length() > 0;
	}

	private static void insertSupplier(@NonNull final Context context, @NonNull final EditText nameEditText, @NonNull final EditText phoneNumberEditText) {

		final Supplier supplier = Supplier.of(nameEditText.getText().toString().trim(),
				phoneNumberEditText.getText().toString().trim()
		);

		InventorialDatabase.getInstance().insertSupplier(context, supplier);
	}

	public static void showDeleteProductConfirmationDialog(@NonNull final Context context,
	                                                       @NonNull final ProductSupplierPair pair,
	                                                       @NonNull final DialogInterface.OnClickListener positiveClickListener) {

		final String msg = StringUtils.fromHtml(context.getString(R.string.dialog_message_delete_product,
				pair.getProduct().getName(), pair.getSupplier().getName()
		));

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
				.setTitle(R.string.delete_product)
				.setMessage(msg)
				.setPositiveButton(R.string.delete, positiveClickListener)
				.create();

		dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
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
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
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
