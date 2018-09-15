package inc.ahmedmourad.inventorial.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.BaseColumns;

public class ProductsCursorWrapper extends CursorWrapper {

	public ProductsCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) {

		if (columnName.equals(BaseColumns._ID)) {
			final int index = super.getColumnIndex(columnName);
			return index >= 0 ? index : 0;
		}

		return super.getColumnIndexOrThrow(columnName);
	}
}
