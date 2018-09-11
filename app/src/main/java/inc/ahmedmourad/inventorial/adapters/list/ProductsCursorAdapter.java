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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.model.database.InventorialDatabase;
import inc.ahmedmourad.inventorial.model.pojo.Product;
import inc.ahmedmourad.inventorial.utils.BitmapUtils;
import inc.ahmedmourad.inventorial.utils.DialogUtils;
import inc.ahmedmourad.inventorial.utils.StringUtils;
import inc.ahmedmourad.inventorial.wrappers.ProductsCursorWrapper;

public class ProductsCursorAdapter extends CursorAdapter {

	public ProductsCursorAdapter(@NonNull final Context context) {
		super(context, null, false);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent);
		view.setTag(new ViewHolder(view));
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		((ViewHolder) view.getTag()).bind(Product.fromCursor(cursor));
	}

	@Override
	public Cursor swapCursor(final Cursor newCursor) {
		return super.swapCursor(new ProductsCursorWrapper(newCursor));
	}

	static final class ViewHolder {

		@BindView(R.id.product_image)
		CircleImageView imageView;

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

		private final Context context;

		private ViewHolder(@NonNull final View view) {
			context = view.getContext();
			ButterKnife.bind(this, view);
		}

		private void bind(@NonNull final Product product) {

			imageView.setImageBitmap(BitmapUtils.fromByteArray(product.getImage()));
			nameTextView.setText(product.getName());
			priceTextView.setText(StringUtils.toString(product.getPrice()));

			if (product.getQuantity() < 0)
				product.setQuantity(0);

			displayQuantity(product.getQuantity());

			incrementButton.setOnClickListener(v -> increaseQuantity(product, 1));
			decrementButton.setOnClickListener(v -> decreaseQuantity(product, 1));

			incrementButton.setOnLongClickListener(v -> {

				DialogUtils.showNumberPickerDialog(context,
						context.getString(R.string.increase_quantity_by, product.getName()),
						R.string.increase,
						Integer.MAX_VALUE,
						number -> increaseQuantity(product, number)
				);

				return true;
			});

			decrementButton.setOnLongClickListener(v -> {

				DialogUtils.showNumberPickerDialog(context,
						context.getString(R.string.decrease_quantity_by, product.getName()),
						R.string.decrease,
						(int) product.getQuantity(),
						number -> decreaseQuantity(product, number)
				);

				return true;
			});
		}

		private void displayQuantity(final double quantity) {
			decrementButton.setEnabled(quantity >= 1);
			quantityTextView.setText(StringUtils.toString(quantity));
		}

		private void increaseQuantity(@NonNull final Product product, @IntRange(from = 1, to = Integer.MAX_VALUE) final int value) {
			product.setQuantity(product.getQuantity() + value);
			InventorialDatabase.getInstance().updateProduct(context, product.toContentValues(), product.getId());
			displayQuantity(product.getQuantity());
		}

		private void decreaseQuantity(@NonNull final Product product, @IntRange(from = 1, to = Integer.MAX_VALUE) final int value) {

			product.setQuantity(product.getQuantity() - value);

			if (product.getQuantity() < 0)
				product.setQuantity(0);

			InventorialDatabase.getInstance().updateProduct(context, product.toContentValues(), product.getId());
			displayQuantity(product.getQuantity());
		}
	}
}
