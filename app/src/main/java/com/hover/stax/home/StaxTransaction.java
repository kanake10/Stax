package com.hover.stax.home;

import android.content.Context;
import android.util.Log;

import com.hover.sdk.transactions.Transaction;
import com.hover.stax.R;
import com.hover.stax.actions.Action;
import com.hover.stax.models.StaxDate;
import com.hover.stax.utils.DateUtils;
import com.hover.stax.utils.Utils;

import org.json.JSONException;

import java.util.Calendar;

public class StaxTransaction {
	private String description, amount, uuid, actionId;
	private StaxDate staxDate;
	private boolean showDate = false;

	public StaxTransaction(Transaction sdkTrans, String lastTime, Context c) throws JSONException {
		uuid = sdkTrans.uuid;
		actionId = sdkTrans.actionId;
		amount = (sdkTrans.myType.equals(Action.ME2ME) ? "" : "-") + Utils.formatAmount(sdkTrans.parsed_variables.getString(Action.AMOUNT_KEY));
		description = setDescription(sdkTrans, c);
		staxDate = convertToStaxDate(sdkTrans.updatedTimestamp);

		String concatenatedDate = staxDate.getYear()+staxDate.getMonth()+staxDate.getDayOfMonth();
		if (!lastTime.equals(concatenatedDate)) showDate = true;
	}

	private String setDescription(Transaction t, Context c) {
		String recipient;
		switch (t.myType) {
			case Action.AIRTIME:
				Log.e("STAX", (t.fromInstitutionName.equals("null") + ""));
				String sender = t.fromInstitutionName != null ? t.fromInstitutionName : t.toInstitutionName; // Something wrong, from shouldn't be null and to should be, but it is backwards
				Log.e("STAX", sender);
				recipient = t.input_extras.optString(Action.PHONE_KEY, "myself");
				return c.getString(R.string.transaction_descrip_airtime, sender, (recipient.equals("") ? "myself" : recipient));
			case Action.P2P:
				recipient = t.input_extras.optString(Action.PHONE_KEY, t.toInstitutionName);
				return c.getString(R.string.transaction_descrip_money, t.fromInstitutionName, recipient);
			case Action.ME2ME:
				return c.getString(R.string.transaction_descrip_money, t.fromInstitutionName, t.toInstitutionName);
			default:
				return "Other";
		}
	}

	public String getDateString() { return staxDate.getYear()+staxDate.getMonth()+staxDate.getDayOfMonth(); }

	private static StaxDate convertToStaxDate(long timestamp) {
		StaxDate staxDate = new StaxDate();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		staxDate.setMonth(DateUtils.monthNumToName(cal.get(Calendar.MONTH)));
		staxDate.setDayOfMonth(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
		staxDate.setYear(String.valueOf(cal.get(Calendar.YEAR)));
		return staxDate;
	}

	public StaxDate getStaxDate() {
		return staxDate;
	}

	public String getDescription() { return description; }

	public boolean isShowDate() {
		return showDate;
	}

	public String getAmount() {
		return amount;
	}
}
