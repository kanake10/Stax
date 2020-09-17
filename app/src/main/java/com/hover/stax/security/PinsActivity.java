package com.hover.stax.security;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricConstants;
import androidx.biometric.BiometricPrompt;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.amplitude.api.Amplitude;
import com.hover.stax.R;
import com.hover.stax.utils.UIHelper;

public class PinsActivity extends AppCompatActivity implements PinEntryAdapter.UpdateListener {

	private PinsViewModel pinViewModel;
	private PinEntryAdapter pinEntryAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pins_entry_layout);
		pinViewModel = new ViewModelProvider(this).get(PinsViewModel.class);

		RecyclerView pinRecyclerView = findViewById(R.id.pin_recyclerView);
		pinViewModel.getSelectedChannels().observe(this, channels -> {
			pinRecyclerView.setLayoutManager(UIHelper.setMainLinearManagers(this));
			pinRecyclerView.setHasFixedSize(true);
			pinEntryAdapter = new PinEntryAdapter(channels, this);
			pinRecyclerView.setAdapter(pinEntryAdapter);
		});

		findViewById(R.id.choose_serves_cancel).setOnClickListener(view -> {
			Amplitude.getInstance().logEvent(getString(R.string.skipped_pin_entry));
			setResult(RESULT_CANCELED);
			finish();
		});

		findViewById(R.id.continuePinButton).setOnClickListener(view -> {
			new BiometricFingerprint().startFingerPrint(this, authenticationCallback);

		});
	}
	void doSavePins() {
		Amplitude.getInstance().logEvent(getString(R.string.completed_pin_entry));
		pinViewModel.savePins(this);
		setResult(RESULT_OK);
		finish();
	}

	private BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
		@Override
		public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
			super.onAuthenticationError(errorCode, errString);
			if(errorCode == BiometricConstants.ERROR_NO_BIOMETRICS) {
				Amplitude.getInstance().logEvent(getString(R.string.biometrics_not_matched));
				doSavePins();
			} else Amplitude.getInstance().logEvent(getString(R.string.biometrics_not_setup));
		}

		@Override
		public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
			super.onAuthenticationSucceeded(result);
			Amplitude.getInstance().logEvent(getString(R.string.biometrics_succeeded));
			doSavePins();

		}

		@Override
		public void onAuthenticationFailed() {
			super.onAuthenticationFailed();
			Amplitude.getInstance().logEvent(getString(R.string.biometrics_failed));
		}
	};

	public void onUpdate(int id, String pin) {
		pinViewModel.setPin(id, pin);
	}
}
