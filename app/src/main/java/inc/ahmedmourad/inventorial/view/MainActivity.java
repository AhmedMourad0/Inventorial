package inc.ahmedmourad.inventorial.view;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.adapters.list.ProductsCursorAdapter;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.ProductSupplierPair;
import inc.ahmedmourad.inventorial.defaults.DefaultOnItemSelectedListener;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;
import inc.ahmedmourad.inventorial.utils.DialogUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	static final String KEY_SUPPLIER_TO_DISPLAY = "m_supplier";

	private static final int ID_LOADER = 0;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.main_list)
	ListView listView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.main_fab)
	SpeedDialView fab;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.main_empty)
	View emptyView;

	@Nullable
	private Supplier supplier;

	private CursorAdapter adapter;

	private Unbinder unbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		unbinder = ButterKnife.bind(this);

		final Intent intent = getIntent();

		if (intent != null && intent.hasExtra(KEY_SUPPLIER_TO_DISPLAY)) {

			supplier = Parcels.unwrap(intent.getParcelableExtra(KEY_SUPPLIER_TO_DISPLAY));
			setTitle(getString(R.string.products_from, supplier.getName()));

			if (getSupportActionBar() != null)
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		initializeListView();

		initializeSpeedDialFab();

		getSupportLoaderManager().initLoader(ID_LOADER, null, this);
	}

	private void initializeListView() {

		adapter = new ProductsCursorAdapter(this);

		listView.setAdapter(adapter);

		listView.setEmptyView(emptyView);

		listView.setOnItemSelectedListener(new DefaultOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

				intent.putExtra(DetailsActivity.KEY_PAIR,
						Parcels.wrap(ProductSupplierPair.fromCursor((Cursor) adapter.getItem(position)))
				);

				intent.putExtra(DetailsActivity.KEY_PAIR_CAN_VIEW_SUPPLIER_PRODUCTS, supplier == null);

				startActivity(intent);
			}
		});
	}

	private void initializeSpeedDialFab() {

		fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_supplier, R.drawable.ic_factory)
				.setLabel(getString(R.string.add_new_supplier))
				.setFabBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.create()
		);

		fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_product, R.drawable.ic_basket)
				.setLabel(getString(R.string.add_new_product))
				.setFabBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				.create()
		);

		fab.setOnActionSelectedListener(actionItem -> {

			switch (actionItem.getId()) {

				case R.id.fab_add_product:
					startActivity(new Intent(MainActivity.this, AddProductActivity.class));
					break;

				case R.id.fab_add_supplier:
					DialogUtils.showNewSupplierDialog(this);
					break;
			}

			fab.close();
			return true;
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}

	/* Loader */

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		if (supplier == null)
			return InventorialDatabase.getInstance().getAllPairs(this);
		else
			return InventorialDatabase.getInstance().getAllSupplierPairs(this, supplier.getId());
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
