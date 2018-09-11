package inc.ahmedmourad.inventorial.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.adapters.spinner.SuppliersArrayAdapter;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.Product;
import inc.ahmedmourad.inventorial.model.pojo.ProductSupplierPair;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.utils.BitmapUtils;
import inc.ahmedmourad.inventorial.defaults.DefaultTextWatcher;
import inc.ahmedmourad.inventorial.utils.DialogUtils;
import inc.ahmedmourad.inventorial.utils.ErrorUtils;
import inc.ahmedmourad.inventorial.utils.StringUtils;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener, DefaultTextWatcher, LoaderManager.LoaderCallbacks<Cursor> {

	public static final String KEY_PAIR = "ap_pair";

	private static final int ID_IMAGE_PICKER_REQUEST = 0;
	private static final int ID_LOADER = 1;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_image)
	CircleImageView imageView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_image_text)
	TextView imageTextView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_name)
	TextInputEditText nameEditText;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_price)
	TextInputEditText priceEditText;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_quantity)
	TextInputEditText quantityEditText;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_suppliers)
	Spinner suppliersSpinner;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.product_new_supplier)
	Button newSupplierButton;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.add_product_save)
	Button saveButton;

	@Nullable
	private ProductSupplierPair pair;

	private SuppliersArrayAdapter adapter;

	private Unbinder unbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		unbinder = ButterKnife.bind(this);

		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Intent intent = getIntent();

		if (intent != null && intent.hasExtra(KEY_PAIR)) {
			pair = Parcels.unwrap(intent.getParcelableExtra(KEY_PAIR));
			saveButton.setText(R.string.update);
			populateUi();
		}

		imageView.setOnClickListener(this);
		imageTextView.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		newSupplierButton.setOnClickListener(this);

		nameEditText.addTextChangedListener(this);
		priceEditText.addTextChangedListener(this);
		quantityEditText.addTextChangedListener(this);

		validateProductInputs();
		initializeSpinnerAdapter();

		getSupportLoaderManager().initLoader(ID_LOADER, null, this);
	}

	private void populateUi() {
		if (pair != null) {
			imageView.setImageBitmap(BitmapUtils.fromByteArray(pair.getProduct().getImage()));
			nameEditText.setText(pair.getProduct().getName());
			priceEditText.setText(StringUtils.toString(pair.getProduct().getPrice()));
			quantityEditText.setText(StringUtils.toString(pair.getProduct().getQuantity()));
		}
	}

	private void initializeSpinnerAdapter() {
		adapter = new SuppliersArrayAdapter(this);
		suppliersSpinner.setAdapter(adapter);
	}

	private void validateProductInputs() {
		saveButton.setEnabled(areProductInputsValid());
	}

	private boolean areProductInputsValid() {
		return nameEditText.getText().toString().trim().length() > 0 &&
				priceEditText.getText().toString().trim().length() > 0 &&
				quantityEditText.getText().toString().trim().length() > 0;
	}

	private void startImagePicker() {

		setPictureEnabled(false);

		startActivityForResult(ImagePicker.create(this)
				.returnMode(ReturnMode.ALL)
				.folderMode(true)
				.toolbarFolderTitle(getString(R.string.pick_a_folder))
				.toolbarImageTitle(getString(R.string.tap_to_select))
				.single()
				.limit(1)
				.showCamera(true)
				.imageDirectory(getString(R.string.camera))
				.theme(R.style.ImagePickerTheme)
				.enableLog(true)
				.getIntent(this), ID_IMAGE_PICKER_REQUEST
		);
	}

	private void setPictureEnabled(final boolean enabled) {
		imageView.setEnabled(enabled);
		imageTextView.setEnabled(enabled);
	}

	private void insertProduct() {

		final Product product = Product.of(nameEditText.getText().toString(),
				Double.parseDouble(priceEditText.getText().toString()),
				Double.parseDouble(quantityEditText.getText().toString()),
				BitmapUtils.toByteArray(imageView)
		);

		final ContentValues values;
		final int position = suppliersSpinner.getSelectedItemPosition();

		if (position > 0) {

			final Supplier supplier = adapter.getItem(position);

			if (supplier != null)
				values = product.toContentValues(supplier.getId());
			else
				values = product.toContentValues();

		} else {
			values = product.toContentValues();
		}

		InventorialDatabase.getInstance().insertProduct(this, values);
	}

	private void updateProduct() {

		if (pair == null)
			return;

		final Product product = Product.of(nameEditText.getText().toString(),
				Double.parseDouble(priceEditText.getText().toString()),
				Double.parseDouble(quantityEditText.getText().toString()),
				BitmapUtils.toByteArray(imageView)
		);

		final ContentValues values;
		final int position = suppliersSpinner.getSelectedItemPosition();

		if (position > 0) {

			final Supplier supplier = adapter.getItem(position);

			if (supplier != null)
				values = product.toContentValues(supplier.getId());
			else
				values = product.toContentValues();

		} else {
			values = product.toContentValues();
		}

		InventorialDatabase.getInstance().updateProduct(this, values, pair.getProduct().getId());
	}

	private int getSpinnerItemPosition(final long supplierId) {

		if (supplierId == -1)
			return 0;

		for (int i = 1; i < adapter.getCount(); ++i) {

			final Supplier s = adapter.getItem(i);

			if (s != null && s.getId() == supplierId)
				return i;
		}

		return 0;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		setPictureEnabled(true);

		if (resultCode != RESULT_OK)
			return;

		if (data == null) {
			ErrorUtils.general(this, null);
			return;
		}

		if (requestCode == ID_IMAGE_PICKER_REQUEST) {

			final Image image = ImagePicker.getFirstImageOrNull(data);

			if (image != null)
				imageView.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}

	/* View */

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.add_product_image:
			case R.id.add_product_image_text:
				startImagePicker();
				break;

			case R.id.add_product_save:
				if (pair == null)
					insertProduct();
				else
					updateProduct();
				break;

			case R.id.product_new_supplier:
				DialogUtils.showNewSupplierDialog(this);
				break;
		}
	}

	/* TextWatcher */

	@Override
	public void afterTextChanged(Editable s) {
		validateProductInputs();
	}

	/* Loader */

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		suppliersSpinner.setEnabled(false);
		return InventorialDatabase.getInstance().getAllSuppliers(this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

		final boolean isFirstLoad = adapter.getCount() <= 1;

		final long selectedSupplierId;

		if (isFirstLoad && pair != null) {

			selectedSupplierId = pair.getProduct().getId();

		} else if (suppliersSpinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {

			final Supplier selectedSupplier = adapter.getItem(suppliersSpinner.getSelectedItemPosition());
			selectedSupplierId = selectedSupplier == null ? -1 : selectedSupplier.getId();

		} else {
			selectedSupplierId = -1;
		}

		adapter.clear();

		if (cursor.moveToFirst()) {

			final List<Supplier> suppliers = new ArrayList<>(cursor.getCount());

			do {

				final Supplier s = Supplier.fromCursor(cursor);

				suppliers.add(s);

			} while (cursor.moveToNext());

			suppliersSpinner.setEnabled(true);
			adapter.addAll(suppliers);
		}

		suppliersSpinner.setSelection(getSpinnerItemPosition(selectedSupplierId));
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		adapter.clear();
		suppliersSpinner.setSelection(0);
		suppliersSpinner.setEnabled(false);
	}
}
