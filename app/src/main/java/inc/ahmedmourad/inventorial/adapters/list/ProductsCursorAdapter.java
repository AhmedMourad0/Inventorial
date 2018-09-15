package inc.ahmedmourad.inventorial.adapters.list;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.ProductSupplierPair;
import inc.ahmedmourad.inventorial.utils.DialogUtils;
import inc.ahmedmourad.inventorial.utils.FileUtils;
import inc.ahmedmourad.inventorial.utils.StringUtils;
import inc.ahmedmourad.inventorial.wrappers.ProductsCursorWrapper;

public class ProductsCursorAdapter extends CursorAdapter {

	private final OnItemClickListener listener;

	public ProductsCursorAdapter(@NonNull final Context context, @NonNull final Cursor cursor, @NonNull final OnItemClickListener listener) {
		super(context, new ProductsCursorWrapper(cursor), false);
		this.listener = listener;
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
		view.setTag(new ViewHolder(view));
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		((ViewHolder) view.getTag()).bind(ProductSupplierPair.fromCursor(cursor), cursor.getPosition());
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		return super.swapCursor(new ProductsCursorWrapper(newCursor));
	}

	class ViewHolder {

		@BindView(R.id.product_image)
		ImageView imageView;

		@BindView(R.id.product_name)
		TextView nameTextView;

		@BindView(R.id.product_price)
		TextView priceTextView;

		@BindView(R.id.product_quantity)
		TextView quantityTextView;

		@BindView(R.id.product_increment)
		ImageButton incrementButton;

		@BindView(R.id.product_decrement)
		ImageButton decrementButton;

		private final View view;
		private final Context context;

		private ViewHolder(@NonNull final View view) {
			this.view = view;
			this.context = view.getContext();
			ButterKnife.bind(this, view);
		}

		private void bind(@NonNull final ProductSupplierPair pair, final int position) {

			FileUtils.loadImageFromStorage(context, pair.getProduct().getName(), imageView);
			nameTextView.setText(pair.getProduct().getName());
			priceTextView.setText(StringUtils.toString(pair.getProduct().getPrice()));

			if (pair.getProduct().getQuantity() < 0)
				pair.getProduct().setQuantity(0);

			displayQuantity(pair.getProduct().getQuantity());

			incrementButton.setOnClickListener(v -> increaseQuantity(pair, 1));
			decrementButton.setOnClickListener(v -> decreaseQuantity(pair, 1));

			incrementButton.setOnLongClickListener(v -> {

				DialogUtils.showNumberPickerDialog(context,
						context.getString(R.string.increase_quantity_by, pair.getProduct().getName()),
						R.string.increase,
						(int) (Integer.MAX_VALUE - pair.getProduct().getQuantity()),
						number -> increaseQuantity(pair, number)
				);

				return true;
			});

			decrementButton.setOnLongClickListener(v -> {

				DialogUtils.showNumberPickerDialog(context,
						context.getString(R.string.decrease_quantity_by, pair.getProduct().getName()),
						R.string.decrease,
						(int) pair.getProduct().getQuantity(),
						number -> decreaseQuantity(pair, number)
				);

				return true;
			});

			view.setOnClickListener(v -> listener.onClick(ProductsCursorAdapter.this, pair, position));
		}

		private void displayQuantity(final double quantity) {
			decrementButton.setEnabled(quantity >= 1);
			quantityTextView.setText(StringUtils.toString(quantity));
		}

		private void increaseQuantity(@NonNull final ProductSupplierPair pair, @IntRange(from = 1, to = Integer.MAX_VALUE) final int value) {
			pair.getProduct().setQuantity(pair.getProduct().getQuantity() + value);
			displayQuantity(pair.getProduct().getQuantity());
			InventorialDatabase.getInstance().updateProduct(context, pair.getProduct().toContentValues(pair.getSupplier().getId()), pair.getProduct().getId());
		}

		private void decreaseQuantity(@NonNull final ProductSupplierPair pair, @IntRange(from = 1, to = Integer.MAX_VALUE) final int value) {

			pair.getProduct().setQuantity(pair.getProduct().getQuantity() - value);

			if (pair.getProduct().getQuantity() < 0)
				pair.getProduct().setQuantity(0);

			displayQuantity(pair.getProduct().getQuantity());
			InventorialDatabase.getInstance().updateProduct(context, pair.getProduct().toContentValues(pair.getSupplier().getId()), pair.getProduct().getId());
		}
	}

	@FunctionalInterface
	public interface OnItemClickListener {
		void onClick(@NonNull ProductsCursorAdapter adapter, @NonNull ProductSupplierPair pair, int position);
	}
}
