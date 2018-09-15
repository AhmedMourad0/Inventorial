package inc.ahmedmourad.inventorial.adapters.spinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inc.ahmedmourad.inventorial.R;
import inc.ahmedmourad.inventorial.model.pojo.Supplier;

public class SuppliersArrayAdapter extends BaseAdapter {

	private final List<Supplier> suppliers = new ArrayList<>();

	private final Context context;

	public SuppliersArrayAdapter(@NonNull final Context context) {
		this.context = context;
		addDefaultItem();
	}

	@Override
	public int getCount() {
		return suppliers.size();
	}

	@Override
	public Object getItem(int position) {
		return suppliers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return createView(position, convertView, parent);
	}

	@Override
	public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return createView(position, convertView, parent);
	}

	@NonNull
	private View createView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {

		final View view;

		if (position == 0) {

			final DefaultViewHolder viewHolder;

			if (convertView == null || !DefaultViewHolder.isInstance(convertView.getTag())) {

				view = LayoutInflater.from(context).inflate(R.layout.item_spinner_supplier_default, parent, false);
				viewHolder = new DefaultViewHolder(view);

				view.setTag(viewHolder);

			} else {
				view = convertView;
				viewHolder = (DefaultViewHolder) view.getTag();
			}

			viewHolder.bind(getCount());

			return view;
		}

		final Supplier supplier = (Supplier) getItem(position);

		final SupplierViewHolder viewHolder;

		if (convertView == null || !SupplierViewHolder.isInstance(convertView.getTag())) {

			view = LayoutInflater.from(context).inflate(R.layout.item_spinner_supplier, parent, false);
			viewHolder = new SupplierViewHolder(view);

			view.setTag(viewHolder);

		} else {
			view = convertView;
			viewHolder = (SupplierViewHolder) view.getTag();
		}

		viewHolder.bind(supplier);

		return view;
	}

	public void clear() {
		suppliers.clear();
		addDefaultItem();
		notifyDataSetChanged();
	}

	private void addDefaultItem() {
		suppliers.add(null);
		notifyDataSetChanged();
	}

	public void addAll(@NonNull final List<Supplier> suppliers) {
		this.suppliers.addAll(suppliers);
		notifyDataSetChanged();
	}

	static abstract class ViewHolder<T> {

		private ViewHolder(@NonNull final View view) {
			ButterKnife.bind(this, view);
		}

		@SuppressWarnings("unused")
		abstract void bind(final T param);

		abstract int getType();
	}

	static final class SupplierViewHolder extends ViewHolder<Supplier> {

		static final int TYPE_SUPPLIER = 1;

		@BindView(R.id.spinner_supplier_text)
		TextView nameTextView;

		@BindView(R.id.spinner_supplier_phone_number)
		TextView phoneNumberTextView;

		private SupplierViewHolder(@NonNull final View view) {
			super(view);
		}

		static boolean isInstance(@NonNull final Object o) {
			return ((ViewHolder) o).getType() == TYPE_SUPPLIER;
		}

		@Override
		void bind(@Nullable final Supplier supplier) {

			if (supplier == null)
				return;

			nameTextView.setText(supplier.getName());
			phoneNumberTextView.setText(supplier.getPhoneNumber());
		}

		@Override
		int getType() {
			return TYPE_SUPPLIER;
		}
	}

	static final class DefaultViewHolder extends ViewHolder<Integer> {

		static final int TYPE_DEFAULT = 0;

		@BindView(R.id.spinner_supplier_text)
		TextView textView;

		private DefaultViewHolder(@NonNull final View view) {
			super(view);
		}

		static boolean isInstance(@NonNull final Object o) {
			return ((ViewHolder) o).getType() == TYPE_DEFAULT;
		}

		@Override
		void bind(@NonNull final Integer itemsCount) {
			if (itemsCount > 1)
				textView.setText(R.string.no_supplier_selected);
			else
				textView.setText(R.string.no_suppliers_found);
		}

		@Override
		int getType() {
			return TYPE_DEFAULT;
		}
	}
}
