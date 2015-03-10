package com.blinkedup.yourtype;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CustomAdapter extends ParseQueryAdapter<ParseObject> {

	public CustomAdapter(Context context) {
		// Use the QueryFactory to construct a PQA that will only show
		// Todos marked as high-pri
		super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
			public ParseQuery create() {
				ParseQuery query = new ParseQuery("Credit");
				query.whereEqualTo("isActive", true);
				return query;
			}
		});
	}

	// Customize the layout by overriding getItemView
	@Override
	public View getItemView(ParseObject object, View v, ViewGroup parent) {
		if (v == null) {
			v = View.inflate(getContext(), R.layout.urgent_item, null);
		}

		super.getItemView(object, v, parent);

		// Add and download the image
	//	ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
	//	ParseFile imageFile = object.getParseFile("image");
	//	if (imageFile != null) {
	//		todoImage.setParseFile(imageFile);
	//		todoImage.loadInBackground();
	//	}

		// Add the Reference Id
		TextView titleTextView = (TextView) v.findViewById(R.id.text1);
		titleTextView.setText(object.getObjectId());
		
		// Add the Credits left
		
				TextView titleTextView2 = (TextView) v.findViewById(R.id.text2);
				
				titleTextView2.setText(object.getInt("creditsLeft"));
	
		
		// Add the title view
				TextView titleTextView3 = (TextView) v.findViewById(R.id.text3);
				titleTextView3.setText(object.getString("payType"));

		// Add a reminder of how long this item has been outstanding
		TextView timestampView = (TextView) v.findViewById(R.id.timestamp);
		timestampView.setText(object.getCreatedAt().toString());
		return v;
	}

}
