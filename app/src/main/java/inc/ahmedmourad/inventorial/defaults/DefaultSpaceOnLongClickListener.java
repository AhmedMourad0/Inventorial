package inc.ahmedmourad.inventorial.defaults;

import com.luseen.spacenavigation.SpaceOnLongClickListener;

public interface DefaultSpaceOnLongClickListener extends SpaceOnLongClickListener {

	@Override
	default void onCentreButtonLongClick() {

	}

	@Override
	default void onItemLongClick(int itemIndex, String itemName) {

	}
}
