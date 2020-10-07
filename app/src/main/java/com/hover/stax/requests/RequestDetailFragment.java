package com.hover.stax.requests;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.amplitude.api.Amplitude;
import com.hover.stax.R;
import com.hover.stax.utils.DateUtils;
import com.hover.stax.utils.UIHelper;
import com.hover.stax.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestDetailFragment extends Fragment {
	final public static String TAG = "RequestDetailFragment";

	private RequestDetailViewModel viewModel;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(this).get(RequestDetailViewModel.class);
		JSONObject data = new JSONObject();
		try { data.put("id", getArguments().getInt("id"));
		} catch (JSONException e) { }
		Amplitude.getInstance().logEvent(getString(R.string.visit_screen, getString(R.string.visit_request_detail)), data);
		return inflater.inflate(R.layout.fragment_request_detail, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		viewModel.getRequest().observe(getViewLifecycleOwner(), request -> {
			if (request != null) {
				setUpSummary(view, request);
				setUpResendBtn(view, request);
			}
		});

		viewModel.setRequest(getArguments().getInt("id"));
	}

	private void setUpSummary(View view, Request request) {
		((TextView) view.findViewById(R.id.title)).setText(request.getDescription(view.getContext()));
		TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.recipient_cell, null);
		tv.setText(request.recipient);
		((LinearLayout) view.findViewById(R.id.recipientValueList)).addView(tv);
		((TextView) view.findViewById(R.id.dateValue)).setText(DateUtils.humanFriendlyDate(request.date_sent));

		view.findViewById(R.id.amountValue).setVisibility(request.amount == null || request.amount.isEmpty() ? View.GONE : View.VISIBLE);
		((TextView) view.findViewById(R.id.amountValue)).setText(Utils.formatAmount(request.amount));

		view.findViewById(R.id.noteRow).setVisibility(request.note == null || request.note.isEmpty() ? View.GONE : View.VISIBLE);
		((TextView) view.findViewById(R.id.noteValue)).setText(request.note);

		((Button) view.findViewById(R.id.cancel_btn)).setOnClickListener((View.OnClickListener) btn -> showConfirmDialog());
	}

	private void showConfirmDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.StaxDialog))
			.setTitle(R.string.cancel_request_head)
			.setMessage(R.string.cancel_request_msg)
			.setNegativeButton(R.string.back, (DialogInterface.OnClickListener) (dialog, whichButton) -> {})
			.setPositiveButton(R.string.cancel_request_btn, (DialogInterface.OnClickListener) (dialog, whichButton) -> {
				viewModel.deleteRequest();
				UIHelper.flashMessage(getContext(), getString(R.string.cancel_request_success));
				NavHostFragment.findNavController(RequestDetailFragment.this).navigate(R.id.navigation_home);
			}).create();
		alertDialog.show();
	}

	private void setUpResendBtn(View view, Request request) {
		((Button) view.findViewById(R.id.resend_btn)).setOnClickListener((View.OnClickListener) btn -> {

		});
	}
}
