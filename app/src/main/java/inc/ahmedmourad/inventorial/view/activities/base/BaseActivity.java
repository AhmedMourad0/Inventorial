package inc.ahmedmourad.inventorial.view.activities.base;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

	protected void displayUpButton(@NonNull final Toolbar toolbar) {

		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (toolbar.getNavigationIcon() != null)
			toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
	}
}
