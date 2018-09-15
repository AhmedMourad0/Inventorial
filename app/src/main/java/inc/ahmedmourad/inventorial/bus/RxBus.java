package inc.ahmedmourad.inventorial.bus;

import android.support.annotation.NonNull;

import com.jakewharton.rxrelay2.PublishRelay;

public final class RxBus {

	public static final int STATE_IDLE = 0;
	public static final int STATE_IN_PROGRESS = 1;

	private static final RxBus INSTANCE = new RxBus();

	private final PublishRelay<Integer> publishingStateRelay = PublishRelay.create();

	private int currentState = STATE_IDLE;

	@NonNull
	public static RxBus getInstance() {
		return INSTANCE;
	}

	private RxBus() {
		// To prevent instantiation outside the class
	}

	public void setCurrentState(final int state) {
		currentState = state;
		publishingStateRelay.accept(state);
	}

	@NonNull
	public PublishRelay<Integer> getCurrentStateRelay() {
		return publishingStateRelay;
	}

	public int getCurrentState() {
		return currentState;
	}
}
