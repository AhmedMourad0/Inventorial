package inc.ahmedmourad.inventorial.defaults;

import android.view.View;
import android.widget.AdapterView;

public interface DefaultOnItemSelectedListener extends AdapterView.OnItemSelectedListener {

	@Override
	default void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	default void onNothingSelected(AdapterView<?> parent) {

	}
}
