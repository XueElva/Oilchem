package net.oilchem.communication.sms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import net.oilchem.communication.sms.R;

public class SendBar extends RelativeLayout {

	private EditText edit;
	private Button sendBtn;

	public SendBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_sendbar, SendBar.this);
		edit = (EditText) findViewById(R.id.view_sendhbar_edittext);
		sendBtn = (Button) findViewById(R.id.view_sendbar_btn);
	}

	public EditText getEdit() {
		return edit;
	}

	public Button getSendBtn() {
		return sendBtn;
	}

	public String getQuery() {
		return edit.getText().toString();
	}
	
}
