package inc.ahmedmourad.inventorial.view.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;

import org.parceler.Parcels;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.bus.RxBus;
import inc.ahmedmourad.inventorial.databinding.ActivityDetailsBinding;
import inc.ahmedmourad.inventorial.defaults.DefaultSpaceOnClickListener;
import inc.ahmedmourad.inventorial.defaults.DefaultSpaceOnLongClickListener;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.ProductSupplierPair;
import inc.ahmedmourad.inventorial.services.DatabaseService;
import inc.ahmedmourad.inventorial.utils.DialogUtils;
import inc.ahmedmourad.inventorial.utils.ErrorUtils;
import inc.ahmedmourad.inventorial.utils.FileUtils;
import inc.ahmedmourad.inventorial.utils.StringUtils;
import inc.ahmedmourad.inventorial.view.activities.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class DetailsActivity extends BaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final int ID_LOADER = 2;

	public static final String KEY_PAIR_ID = "d_pair_id";

	static final String KEY_PAIR = "d_pair";
	static final String KEY_PAIR_CAN_VIEW_SUPPLIER_PRODUCTS = "d_can_view_supplier_products";

	@Nullable
	private ProductSupplierPair pair;

	private long productId = -1;

	private Disposable disposable;

	private ActivityDetailsBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityDetailsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.detailsCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.black));

		setSupportActionBar(binding.detailsToolbar);
		displayUpButton(binding.detailsToolbar);

		final Intent intent = getIntent();

		if (intent != null) {

			if (intent.hasExtra(KEY_PAIR)) {
				pair = Parcels.unwrap(intent.getParcelableExtra(KEY_PAIR));
			} else if (intent.hasExtra(KEY_PAIR_ID)) {
				productId = intent.getLongExtra(KEY_PAIR_ID, -1);
			} else {
				ErrorUtils.general(getApplicationContext(), null);
				finish();
			}

			if (pair == null && productId < 0) {
				ErrorUtils.general(this, null);
				finish();
				return;
			}

			// Preventing inception
			if (intent.getBooleanExtra(KEY_PAIR_CAN_VIEW_SUPPLIER_PRODUCTS, true)) {
				binding.detailsSupplierProducts.setVisibility(View.VISIBLE);
				binding.detailsSupplierProductsDivider.setVisibility(View.VISIBLE);
			} else {
				binding.detailsSupplierProducts.setVisibility(View.GONE);
				binding.detailsSupplierProductsDivider.setVisibility(View.GONE);
			}
		}

		binding.detailsSupplierProducts.setOnClickListener(this);

		initializeSpaceView();
		populateUi(true);

		getSupportLoaderManager().initLoader(ID_LOADER, null, this);
	}

	private void populateUi(final boolean loadImage) {

		if (pair == null)
			return;

		binding.detailsCollapsingToolbar.setTitle(pair.getProduct().getName());

		if (pair.getProduct().getQuantity() < 0)
			pair.getProduct().setQuantity(0);

		binding.detailsProductPrice.setText(getString(R.string.details_price, StringUtils.toString(pair.getProduct().getPrice())));

		if (loadImage)
			FileUtils.loadImageFromStorage(this, pair.getProduct().getName(), binding.detailsProductImage);

		displayQuantity();

		if (pair.getSupplier().getName() != null &&
				pair.getSupplier().getPhoneNumber() != null &&
				pair.getSupplier().getName().trim().length() > 0 &&
				pair.getSupplier().getPhoneNumber().trim().length() > 0) {

			setSupplierVisible(true);

			binding.detailsSupplierName.setText(pair.getSupplier().getName());
			binding.detailsSupplierPhoneNumber.setText(pair.getSupplier().getPhoneNumber());

		} else {
			setSupplierVisible(false);
		}
	}

	private void setSupplierVisible(final boolean visible) {
		if (visible) {
			binding.detailsSupplierLabel.setVisibility(View.VISIBLE);
			binding.detailsSupplierCard.setVisibility(View.VISIBLE);
		} else {
			binding.detailsSupplierLabel.setVisibility(View.GONE);
			binding.detailsSupplierCard.setVisibility(View.GONE);
		}
	}

	private void displayQuantity() {
		if (pair != null)
			binding.detailsProductQuantity.setText(getString(R.string.details_quantity, StringUtils.toString(pair.getProduct().getQuantity())));
	}

	private void initializeSpaceView() {

		binding.detailsSpace.setCentreButtonIcon(R.drawable.ic_edit);
		binding.detailsSpace.setCentreButtonColor(ContextCompat.getColor(this, R.color.colorSpaceCenter));
		binding.detailsSpace.setInActiveSpaceItemColor(Color.WHITE);
		binding.detailsSpace.setActiveSpaceItemColor(Color.WHITE);
		binding.detailsSpace.setInActiveCentreButtonIconColor(Color.BLACK);
		binding.detailsSpace.setActiveCentreButtonIconColor(Color.BLACK);
		binding.detailsSpace.setSpaceBackgroundColor(ContextCompat.getColor(this, R.color.colorSpaceBackground));
		binding.detailsSpace.setSpaceItemTextSize(getResources().getDimensionPixelSize(R.dimen.space_text_size));

		binding.detailsSpace.addSpaceItem(new SpaceItem(getString(R.string.decrement), R.drawable.ic_down));
		binding.detailsSpace.addSpaceItem(new SpaceItem(getString(R.string.increment), R.drawable.ic_up));

		binding.detailsSpace.setSpaceOnClickListener(new DefaultSpaceOnClickListener() {
			@Override
			public void onCentreButtonClick() {
				final Intent intent = new Intent(DetailsActivity.this, AddProductActivity.class);
				intent.putExtra(AddProductActivity.KEY_PAIR, Parcels.wrap(pair));
				startActivity(intent);
			}

			@Override
			public void onItemClick(int itemIndex, String itemName) {

				if (pair == null)
					return;

				if (getString(R.string.increment).equals(itemName)) {

					increaseQuantity(1);

				} else if (getString(R.string.decrement).equals(itemName)) {

					if (pair.getProduct().getQuantity() < 1) {
						Toast.makeText(DetailsActivity.this, R.string.no_entities_left, Toast.LENGTH_LONG).show();
						return;
					}

					decreaseQuantity(1);
				}

				binding.detailsSpace.changeCurrentItem(-1);
			}
		});

		binding.detailsSpace.setSpaceOnLongClickListener(new DefaultSpaceOnLongClickListener() {
			@Override
			public void onItemLongClick(int itemIndex, String itemName) {

				if (pair == null)
					return;

				if (getString(R.string.increment).equals(itemName)) {

					DialogUtils.showNumberPickerDialog(DetailsActivity.this,
							getString(R.string.increase_quantity_by, pair.getProduct().getName()),
							R.string.increase,
							(int) (Integer.MAX_VALUE - pair.getProduct().getQuantity()),
							DetailsActivity.this::increaseQuantity
					);

				} else if (getString(R.string.decrement).equals(itemName)) {

					if (pair.getProduct().getQuantity() < 1) {
						Toast.makeText(DetailsActivity.this, R.string.no_entities_left, Toast.LENGTH_LONG).show();
						return;
					}

					DialogUtils.showNumberPickerDialog(DetailsActivity.this,
							getString(R.string.decrease_quantity_by, pair.getProduct().getName()),
							R.string.decrease,
							(int) pair.getProduct().getQuantity(),
							DetailsActivity.this::decreaseQuantity
					);
				}
			}
		});
	}

	@SuppressWarnings("SameParameterValue")
	private void increaseQuantity(@IntRange(from = 1, to = Integer.MAX_VALUE) final int value) {

		if (pair == null)
			return;

		pair.getProduct().setQuantity(pair.getProduct().getQuantity() + value);

		displayQuantity();

		InventorialDatabase.getInstance().updateProduct(DetailsActivity.this,
				pair.getProduct().toContentValues(pair.getSupplier().getId()),
				pair.getProduct().getId()
		);
	}

	@SuppressWarnings("SameParameterValue")
	private void decreaseQuantity(@IntRange(from = 1, to = Integer.MAX_VALUE) final int value) {

		if (pair == null)
			return;

		pair.getProduct().setQuantity(pair.getProduct().getQuantity() - value);

		if (pair.getProduct().getQuantity() < 0)
			pair.getProduct().setQuantity(0);

		displayQuantity();

		InventorialDatabase.getInstance().updateProduct(DetailsActivity.this,
				pair.getProduct().toContentValues(pair.getSupplier().getId()),
				pair.getProduct().getId()
		);
	}

	private void deleteProduct() {

		if (pair == null)
			return;

		DialogUtils.showDeleteProductConfirmationDialog(this, pair.getProduct().getName(), (dialog, which) -> {
			FileUtils.deleteFile(this, pair.getProduct().getName());
			DatabaseService.startActionDeleteProduct(this, pair.getProduct().getId());
			finish();
		});
	}

	private void callNumber(@NonNull final String number) {
		final Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + number));
		if (intent.resolveActivity(getPackageManager()) != null)
			startActivity(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();

		displayCurrentState(RxBus.getInstance().getCurrentState());

		disposable = RxBus.getInstance()
				.getCurrentStateRelay()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::displayCurrentState,
						throwable -> ErrorUtils.general(getApplicationContext(), throwable));
	}

	private void displayCurrentState(final int state) {

		if (state == RxBus.STATE_IN_PROGRESS) {

			binding.detailsProgressbar.setVisibility(View.VISIBLE);

		} else {

			binding.detailsProgressbar.setVisibility(View.GONE);

			if (pair != null)
				FileUtils.loadImageFromStorage(this, pair.getProduct().getName(), binding.detailsProductImage);
		}
	}

	@Override
	protected void onStop() {
		disposable.dispose();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.details_action_delete) {
            deleteProduct();
            return true;
        } else if (itemId == R.id.details_action_order) {
            if (pair != null)
                callNumber(pair.getSupplier().getPhoneNumber());
            return true;
        }

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.details_supplier_products && pair != null) {
			final Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(MainActivity.KEY_SUPPLIER_TO_DISPLAY, Parcels.wrap(pair.getSupplier()));
			startActivity(intent);
		}
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {

		final long id;

		if (pair != null) {

			id = pair.getProduct().getId();

			if (id < 0)
				return InventorialDatabase.getInstance().getPairLoader(this, pair.getProduct().getName());

		} else {
			id = productId;
		}

		return InventorialDatabase.getInstance().getPairLoader(this, id);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToFirst()) {
			pair = ProductSupplierPair.fromCursor(cursor);
			populateUi(false);
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}
}
