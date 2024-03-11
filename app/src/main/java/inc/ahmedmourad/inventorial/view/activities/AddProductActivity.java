package inc.ahmedmourad.inventorial.view.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
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

import de.hdodenhof.circleimageview.CircleImageView;
import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.adapters.spinner.SuppliersArrayAdapter;
import inc.ahmedmourad.inventorial.bus.RxBus;
import inc.ahmedmourad.inventorial.databinding.ActivityAddProductBinding;
import inc.ahmedmourad.inventorial.defaults.DefaultTextWatcher;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.Product;
import inc.ahmedmourad.inventorial.model.pojo.ProductSupplierPair;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.services.DatabaseService;
import inc.ahmedmourad.inventorial.utils.ErrorUtils;
import inc.ahmedmourad.inventorial.utils.FileUtils;
import inc.ahmedmourad.inventorial.utils.StringUtils;
import inc.ahmedmourad.inventorial.view.activities.base.SnackbarActivity;
import inc.ahmedmourad.inventorial.view.fragments.AddSupplierDialogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class AddProductActivity extends SnackbarActivity implements View.OnClickListener, DefaultTextWatcher, LoaderManager.LoaderCallbacks<Cursor> {

	private static final int ID_IMAGE_PICKER_REQUEST = 0;
	private static final int ID_LOADER = 1;

	private static final String TAG_ADD_SUPPLIER_DIALOG = "t_apa_asd";

	public static final String KEY_PAIR = "ap_pair";

	@Nullable
	private ProductSupplierPair pair;

	private SuppliersArrayAdapter adapter;

	private Disposable disposable;

	private ActivityAddProductBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityAddProductBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		setSupportActionBar(binding.addProductToolbar);
		displayUpButton(binding.addProductToolbar);

		final Intent intent = getIntent();

		if (intent != null && intent.hasExtra(KEY_PAIR)) {
			pair = Parcels.unwrap(intent.getParcelableExtra(KEY_PAIR));
			binding.addProductSave.setText(R.string.update);
			populateUi();
		}

		binding.addProductImage.setOnClickListener(this);
		binding.addProductImageText.setOnClickListener(this);
		binding.addProductSave.setOnClickListener(this);
		binding.productNewSupplier.setOnClickListener(this);

		binding.addProductName.addTextChangedListener(this);
		binding.addProductPrice.addTextChangedListener(this);
		binding.addProductQuantity.addTextChangedListener(this);

		validateProductInputs();
		initializeSpinnerAdapter();

		getSupportLoaderManager().initLoader(ID_LOADER, null, this);
	}

	private void populateUi() {
		if (pair != null) {
			FileUtils.loadImageFromStorage(this, pair.getProduct().getName(), binding.addProductImage);
			binding.addProductName.setText(pair.getProduct().getName());
			binding.addProductPrice.setText(StringUtils.toString(pair.getProduct().getPrice()));
			binding.addProductQuantity.setText(StringUtils.toString(pair.getProduct().getQuantity()));
		}
	}

	private void initializeSpinnerAdapter() {
		adapter = new SuppliersArrayAdapter(this);
		binding.addProductSpinner.addProductSuppliers.setAdapter(adapter);
	}

	private void validateProductInputs() {
		setSaveButtonEnabled(areProductInputsValid());
	}

	private boolean areProductInputsValid() {
		return binding.addProductName.getText().toString().trim().length() > 0 &&
				binding.addProductPrice.getText().toString().trim().length() > 0 &&
				binding.addProductQuantity.getText().toString().trim().length() > 0;
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
				// I've just gained a lot more respect for this library.
				// You should be ashamed Google, you should be ashamed.
				.toolbarArrowColor(ContextCompat.getColor(this, android.R.color.black))
				.theme(R.style.ImagePickerTheme)
				.enableLog(true)
				.getIntent(this), ID_IMAGE_PICKER_REQUEST
		);
	}

	private void insertProduct() {

		final String productName = binding.addProductName.getText().toString().trim();

		if (!InventorialDatabase.getInstance().isProductNameValid(this, binding.getRoot(), productName))
			return;

		final ProductSupplierPair pair = new ProductSupplierPair();

		final Product product = Product.of(productName,
				Double.parseDouble(binding.addProductPrice.getText().toString().trim()),
				Double.parseDouble(binding.addProductQuantity.getText().toString().trim())
		);

		pair.setProduct(product);

		final ContentValues values;
		final int position = binding.addProductSpinner.addProductSuppliers.getSelectedItemPosition();

		if (position > 0) {

			final Supplier supplier = (Supplier) adapter.getItem(position);

			if (supplier != null) {
				values = product.toContentValues(supplier.getId());
				pair.setSupplier(supplier);
			} else {
				values = product.toContentValues();
				pair.setSupplier(new Supplier());
			}

		} else {
			values = product.toContentValues();
			pair.setSupplier(new Supplier());
		}

		FileUtils.saveToInternalStorage(this, binding.addProductImage, productName);
		DatabaseService.startActionInsertProduct(this, values);
		finish();

		final Intent intent = new Intent(this, DetailsActivity.class);
		intent.putExtra(DetailsActivity.KEY_PAIR, Parcels.wrap(pair));
		startActivity(intent);
	}

	private void updateProduct() {

		if (pair == null)
			return;

		final String productName = binding.addProductName.getText().toString().trim();

		if (!pair.getProduct().getName().equals(productName)) {

			if (!InventorialDatabase.getInstance().isProductNameValid(this, binding.getRoot(), productName))
				return;

			FileUtils.deleteFile(this, pair.getProduct().getName());
		}

		final ProductSupplierPair pair = new ProductSupplierPair();

		final Product product = Product.of(productName,
				Double.parseDouble(binding.addProductPrice.getText().toString().trim()),
				Double.parseDouble(binding.addProductQuantity.getText().toString().trim())
		);

		product.setId(this.pair.getProduct().getId());

		pair.setProduct(product);

		final ContentValues values;
		final int position = binding.addProductSpinner.addProductSuppliers.getSelectedItemPosition();

		if (position > 0) {

			final Supplier supplier = (Supplier) adapter.getItem(position);

			if (supplier != null) {
				values = product.toContentValues(supplier.getId());
				pair.setSupplier(supplier);
			} else {
				values = product.toContentValues();
				pair.setSupplier(new Supplier());
			}

		} else {
			values = product.toContentValues();
			pair.setSupplier(new Supplier());
		}

		FileUtils.saveToInternalStorage(this, binding.addProductImage, productName);
		DatabaseService.startActionUpdateProduct(this, values, pair.getProduct().getId());
		finish();
	}

	private void setPictureEnabled(final boolean enabled) {
		binding.addProductImage.setEnabled(enabled);
		binding.addProductImageText.setEnabled(enabled);
		binding.addProductImage.setAlpha(enabled ? 1f : 0.3f);
		binding.addProductImageText.setAlpha(enabled ? 1f : 0.3f);
	}

	private void setSaveButtonEnabled(final boolean enabled) {
		binding.addProductSave.setAlpha(enabled ? 1f : 0.3f);
		binding.addProductSave.setEnabled(enabled);
	}

	private void setSpinnerEnabled(final boolean enabled) {
		binding.addProductSpinner.addProductSuppliers.setEnabled(enabled);
		binding.addProductSpinner.addProductSuppliers.setAlpha(enabled ? 1f : 0.5f);
	}

	private int getSpinnerItemPosition(final long supplierId) {

		if (supplierId == -1)
			return 0;

		for (int i = 1; i < adapter.getCount(); ++i) {

			final Supplier s = (Supplier) adapter.getItem(i);

			if (s != null && s.getId() == supplierId)
				return i;
		}

		return 0;
	}

	@Override
	protected void onStart() {
		super.onStart();
		getSupportLoaderManager().restartLoader(ID_LOADER, null, this);

		displayCurrentState(RxBus.getInstance().getCurrentState());

		disposable = RxBus.getInstance()
				.getCurrentStateRelay()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::displayCurrentState,
						throwable -> ErrorUtils.general(getApplicationContext(), throwable));
	}

	private void displayCurrentState(final int state) {
		if (state == RxBus.STATE_IN_PROGRESS)
			binding.addProductProgressbar.setVisibility(View.VISIBLE);
		else
			binding.addProductProgressbar.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		disposable.dispose();
		super.onStop();
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
				binding.addProductImage.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/* View */
	@Override
	public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.add_product_image || id == R.id.add_product_image_text) {
            startImagePicker();
        } else if (id == R.id.add_product_save) {
            if (pair == null)
                insertProduct();
            else
                updateProduct();
        } else if (id == R.id.product_new_supplier) {
            new AddSupplierDialogFragment().show(getSupportFragmentManager(), TAG_ADD_SUPPLIER_DIALOG);
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
		setSpinnerEnabled(false);
		return InventorialDatabase.getInstance().getAllSuppliersLoader(this, null);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

		final boolean isFirstLoad = adapter.getCount() <= 1;

		final long selectedSupplierId;

		if (isFirstLoad && pair != null) {

			selectedSupplierId = pair.getSupplier().getId();

		} else if (binding.addProductSpinner.addProductSuppliers.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {

			final Supplier selectedSupplier = (Supplier) binding.addProductSpinner.addProductSuppliers.getSelectedItem();
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

			setSpinnerEnabled(true);
			adapter.addAll(suppliers);
		}

		binding.addProductSpinner.addProductSuppliers.setSelection(getSpinnerItemPosition(selectedSupplierId));
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		adapter.clear();
		binding.addProductSpinner.addProductSuppliers.setSelection(0);
		setSpinnerEnabled(false);
	}

	@NonNull
	@Override
	public View getRootView() {
		return binding.getRoot();
	}
}
