package inc.ahmedmourad.inventorial.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.databinding.DialogAddSupplierBinding;
import inc.ahmedmourad.inventorial.defaults.DefaultTextWatcher;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.services.DatabaseService;
import inc.ahmedmourad.inventorial.view.activities.base.SnackbarActivity;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class AddSupplierDialogFragment extends DialogFragment {

	private Button positiveButton;
	private SnackbarActivity snackbarActivity;
	private AlertDialog alertDialog;

	private Context context;

	private DialogAddSupplierBinding binding;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		context = getContext();

		final View view = View.inflate(context, R.layout.dialog_add_supplier, null);

		binding = DialogAddSupplierBinding.bind(view);

		binding.newSupplierName.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				validateSupplierInputs();
			}
		});

		binding.newSupplierPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				validateSupplierInputs();
			}
		});

		view.post(() -> {

			getPositiveButton().setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

			getPositiveButton().setOnClickListener(v -> {

				if (!InventorialDatabase.getInstance().isSupplierNameValid(context, getSnackbarActivity().getRootView(), binding.newSupplierName.getText().toString().trim()))
					return;

				insertSupplier(context);

				dismiss();
			});

			validateSupplierInputs();
		});

		return new AlertDialog.Builder(context)
				.setPositiveButton(R.string.add, null)
				.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
				.setTitle(R.string.new_supplier)
				.setView(view)
				.create();
	}

	private void validateSupplierInputs() {
		setPositiveButtonEnabled(areSupplierInputsValid());
	}

	private boolean areSupplierInputsValid() {
		return binding.newSupplierName.getText().toString().trim().length() > 0 &&
				binding.newSupplierPhoneNumber.getText().toString().trim().length() > 0;
	}

	private void insertSupplier(@NonNull final Context context) {

		final Supplier supplier = Supplier.of(binding.newSupplierName.getText().toString().trim(),
				binding.newSupplierPhoneNumber.getText().toString().trim()
		);

		DatabaseService.startActionInsertSupplier(context, supplier);
	}

	private void setPositiveButtonEnabled(final boolean enabled) {
		getPositiveButton().setAlpha(enabled ? 1f : 0.3f);
		getPositiveButton().setEnabled(enabled);
	}

	private AlertDialog getAlertDialog() {

		if (alertDialog == null)
			alertDialog = (AlertDialog) getDialog();

		return alertDialog;
	}

	private SnackbarActivity getSnackbarActivity() {

		if (snackbarActivity == null)
			snackbarActivity = (SnackbarActivity) getActivity();

		return snackbarActivity;
	}

	private Button getPositiveButton() {

		if (positiveButton == null)
			positiveButton = getAlertDialog().getButton(BUTTON_POSITIVE);

		return positiveButton;
	}

	@Override
	public void onDestroyView() {
		binding = null;
		super.onDestroyView();
	}
}
