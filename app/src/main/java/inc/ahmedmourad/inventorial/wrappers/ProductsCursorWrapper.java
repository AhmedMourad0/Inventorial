package inc.ahmedmourad.inventorial.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ProductsCursorWrapper extends CursorWrapper {

	public ProductsCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		final int index = super.getColumnIndex(columnName);
		return index != -1 ? index : 0;
	}
}
