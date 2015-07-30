package net.oilchem.communication.sms.view;

import net.oilchem.communication.sms.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SearchBar extends RelativeLayout {
	
	private EditText edit;
	private ImageView searchBtn;
	
	public SearchBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_searchbar, SearchBar.this);
		edit = (EditText) findViewById(R.id.view_searchbar_edittext_search);
		searchBtn = (ImageView) findViewById(R.id.view_searchbar_image_btn);
	}

	public EditText getEdit() {
		return edit;
	}

	public ImageView getSearchBtn() {
		return searchBtn;
	}

	public String getQuery() {
		return edit.getText().toString();
	}
	
}
