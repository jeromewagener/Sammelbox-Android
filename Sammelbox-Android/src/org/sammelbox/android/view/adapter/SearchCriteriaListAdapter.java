package org.sammelbox.android.view.adapter;

import java.util.List;

import org.sammelbox.R;
import org.sammelbox.android.model.querybuilder.QueryComponent;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/** A list container adapter to show album items */
public class SearchCriteriaListAdapter extends ArrayAdapter<QueryComponent>{
	private final Activity context;
	private List<QueryComponent> queryComponents;
	
	public SearchCriteriaListAdapter(Activity context, List<QueryComponent> queryComponents) {
		super(context, R.layout.search_criteria_list_item, queryComponents);
		this.context = context;
		this.queryComponents = queryComponents;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.search_criteria_list_item, null, true);
		
		TextView itemField = (TextView) rowView.findViewById(R.id.searchCriteriaItemField);
		itemField.setText(queryComponents.get(position).getFieldName() + " ");
		
		TextView operator = (TextView) rowView.findViewById(R.id.searchCriteriaOperator);
		operator.setText(queryComponents.get(position).getOperator().toSqlOperator() + " ");
		
		TextView value = (TextView) rowView.findViewById(R.id.searchCriteriaValue);
		value.setText(queryComponents.get(position).getValue() + " ");
		
		return rowView;
	}
}
