package inc.ahmedmourad.inventorial.view.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.MenuItem;
import android.view.View;

import com.leinardi.android.speeddial.SpeedDialActionItem;

import org.parceler.Parcels;

import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.adapters.list.ProductsCursorAdapter;
import inc.ahmedmourad.inventorial.bus.RxBus;
import inc.ahmedmourad.inventorial.databinding.ActivityMainBinding;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.utils.ErrorUtils;
import inc.ahmedmourad.inventorial.view.activities.base.SnackbarActivity;
import inc.ahmedmourad.inventorial.view.fragments.AddSupplierDialogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends SnackbarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int ID_LOADER = 0;

	private static final String TAG_ADD_SUPPLIER_DIALOG = "t_ma_asd";

	static final String KEY_SUPPLIER_TO_DISPLAY = "m_supplier";

	@Nullable
	private Supplier supplier;

	@Nullable
	private CursorAdapter adapter;

	private Disposable disposable;

	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		setSupportActionBar(binding.mainToolbar);

		final Intent intent = getIntent();

		if (intent != null && intent.hasExtra(KEY_SUPPLIER_TO_DISPLAY)) {
			supplier = Parcels.unwrap(intent.getParcelableExtra(KEY_SUPPLIER_TO_DISPLAY));
			setTitle(getString(R.string.products_from, supplier.getName()));
			displayUpButton(binding.mainToolbar);
			binding.mainFab.setVisibility(View.GONE);
		}

		initializeSpeedDialFab();
	}

	private void initializeListView(@NonNull final Cursor cursor) {

		adapter = new ProductsCursorAdapter(this, cursor, (adapter1, pair, position) -> {

			final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

			intent.putExtra(DetailsActivity.KEY_PAIR, Parcels.wrap(pair));

			intent.putExtra(DetailsActivity.KEY_PAIR_CAN_VIEW_SUPPLIER_PRODUCTS, supplier == null);

			startActivity(intent);
		});

		binding.mainList.setAdapter(adapter);

		binding.mainList.setEmptyView(binding.mainEmpty.getRoot());
	}

	private void initializeSpeedDialFab() {

		binding.mainFab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_supplier, R.drawable.ic_factory)
				.setLabel(getString(R.string.add_new_supplier))
				.setFabBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.create()
		);

		binding.mainFab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_product, R.drawable.ic_basket)
				.setLabel(getString(R.string.add_new_product))
				.setFabBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.create()
		);

		binding.mainFab.setOnActionSelectedListener(actionItem -> {

            int id = actionItem.getId();
            if (id == R.id.fab_add_product) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            } else if (id == R.id.fab_add_supplier) {
                new AddSupplierDialogFragment().show(getSupportFragmentManager(), TAG_ADD_SUPPLIER_DIALOG);
            }

			binding.mainFab.close(); // Close with animation
			return true;
		});
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
			binding.mainProgressbar.setVisibility(View.VISIBLE);
		else
			binding.mainProgressbar.setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		disposable.dispose();
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/* Loader */
	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		if (supplier == null)
			return InventorialDatabase.getInstance().getAllPairsLoader(this);
		else
			return InventorialDatabase.getInstance().getAllSupplierPairsLoader(this, supplier.getId());
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		if (adapter == null)
			initializeListView(cursor);
		else
			adapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		if (adapter != null)
			adapter.changeCursor(null);
	}

	@NonNull
	@Override
	public View getRootView() {
		return binding.getRoot();
	}
}
