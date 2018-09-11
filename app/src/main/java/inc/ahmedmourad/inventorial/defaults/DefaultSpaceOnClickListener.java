package inc.ahmedmourad.inventorial.defaults;

import com.luseen.spacenavigation.SpaceOnClickListener;

public interface DefaultSpaceOnClickListener extends SpaceOnClickListener {

	@Override
	default void onCentreButtonClick() {

	}

	@Override
	default void onItemClick(int itemIndex, String itemName) {

	}

	@Override
	default void onItemReselected(int itemIndex, String itemName) {

	}
}
