package com.hover.stax.requests;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hover.stax.R;
import com.hover.stax.contacts.StaxContact;
import com.hover.stax.utils.EditStagedFragment;
import com.hover.stax.utils.UIHelper;

public class EditRequestFragment extends EditStagedFragment implements RecipientAdapter.UpdateListener {

	protected NewRequestViewModel requestViewModel;

	private RecyclerView recipientInputList;
	private EditText amountInput, noteInput;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		stagedViewModel = new ViewModelProvider(requireActivity()).get(NewRequestViewModel.class);
		requestViewModel = (NewRequestViewModel) stagedViewModel;
		View view = inflater.inflate(R.layout.fragment_edit_request, container, false);
		init(view);
		return view;
	}

	@Override
	protected void init(View view) {
		recipientInputList = view.findViewById(R.id.recipient_list);
		recipientInputList.setLayoutManager(UIHelper.setMainLinearManagers(getContext()));
		RecipientAdapter recipientAdapter = new RecipientAdapter(requestViewModel.getRecipients().getValue(), requestViewModel.getRecentContacts().getValue(), this);
		recipientInputList.setAdapter(recipientAdapter);

		amountInput = view.findViewById(R.id.amount_input);
		amountInput.setText(requestViewModel.getAmount().getValue());
		noteInput = view.findViewById(R.id.note_input);
		noteInput.setText(requestViewModel.getNote().getValue());

		super.init(view);
	}

	protected void save() {
		for (int c = 0; c < requestViewModel.getRecipients().getValue().size(); c++)
			if (!requestViewModel.getRecipients().getValue().get(c).toString().equals(
					((TextView) recipientInputList.getChildAt(c).findViewById(R.id.recipient_autocomplete)).getText().toString())) {
				StaxContact contact = new StaxContact(((TextView)recipientInputList.getChildAt(c).findViewById(R.id.recipient_autocomplete)).getText().toString());
				requestViewModel.onUpdate(c, contact);
			}

		if (!amountInput.getText().toString().isEmpty())
			requestViewModel.setAmount(amountInput.getText().toString());
		if (!noteInput.getText().toString().isEmpty())
			requestViewModel.setNote(noteInput.getText().toString());
		NavHostFragment.findNavController(this).navigate(R.id.navigation_new);
	}

	@Override
	public void onUpdate(int pos, StaxContact recipient) { }

	@Override
	public void onClickContact(int index, Context c) { contactPicker(index, c); }

	protected void onContactSelected(int requestCode, StaxContact contact) {
		((AutoCompleteTextView) recipientInputList.getChildAt(requestCode).findViewById(R.id.recipient_autocomplete)).setText(contact.toString());
		requestViewModel.onUpdate(requestCode, contact);
	}
}
