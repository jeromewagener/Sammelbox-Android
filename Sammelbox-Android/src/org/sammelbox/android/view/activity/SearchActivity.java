package org.sammelbox.android.view.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sammelbox.R;
import org.sammelbox.android.GlobalState;
import org.sammelbox.android.controller.DatabaseQueryOperation;
import org.sammelbox.android.controller.DatabaseWrapper;
import org.sammelbox.android.model.FieldType;
import org.sammelbox.android.model.querybuilder.QueryBuilder;
import org.sammelbox.android.model.querybuilder.QueryComponent;
import org.sammelbox.android.view.adapter.SearchCriteriaListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchActivity extends Activity {
	private List<QueryComponent> queryComponents = new ArrayList<QueryComponent>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// hide title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// default stuff
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		
		final Spinner comboSelectAlbum = (Spinner) findViewById(R.id.comboSelectAlbum);		
		final Map<String, String> albumNameToTableNameMapping = GlobalState.getAlbumNameToTableName(this);
		
		// Album selection spinner (combobox)
		ArrayAdapter<String> albumListAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, 
				new ArrayList<String>(albumNameToTableNameMapping.keySet()));
		albumListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		comboSelectAlbum.setAdapter(albumListAdapter);
		comboSelectAlbum.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateAlbumItemFieldSelectionSpinner(albumNameToTableNameMapping,
						albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		// Album item field selection spinner (combobox)
		updateAlbumItemFieldSelectionSpinner(albumNameToTableNameMapping, 
				albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
		final Spinner comboSelectAlbumItemField = (Spinner) findViewById(R.id.comboSelectAlbumItemField);
		comboSelectAlbumItemField.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateOperatorSelectionSpinner(SearchActivity.this, (String) comboSelectAlbumItemField.getSelectedItem(), 
						albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		// Operator selection spinner (combobox)
		updateOperatorSelectionSpinner(this, (String) comboSelectAlbumItemField.getSelectedItem(), 
				albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
		
		// Search button
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (queryComponents.isEmpty()) {
						queryComponents.add(getQueryComponentFromView());
					}
					
					RadioButton radioConnectByAnd = (RadioButton) findViewById(R.id.radioConnectByAnd);
					String rawSqlQuery = QueryBuilder.buildQuery(
							queryComponents, radioConnectByAnd.isChecked(), 
							(String) comboSelectAlbum.getSelectedItem(), SearchActivity.this);
					
					// clear value to search
					((EditText) findViewById(R.id.edtSearchValue)).getText().clear();
					
					GlobalState.setSelectedAlbum((String) comboSelectAlbum.getSelectedItem());
					GlobalState.setSimplifiedAlbumItemResultSet(DatabaseQueryOperation.getAlbumItems(SearchActivity.this, 
							DatabaseWrapper.executeRawSQLQuery(DatabaseWrapper.getSQLiteDatabaseInstance(SearchActivity.this), rawSqlQuery)));
					
					Intent openAlbumItemListToBrowse = new Intent(SearchActivity.this, AlbumItemBrowserActivity.class);
	                startActivity(openAlbumItemListToBrowse);
				}
				
				return true;
			}
		});
		
		Button btnAddSearchCriteria = (Button) findViewById(R.id.btnAddCriteria);
		btnAddSearchCriteria.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View parent, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					EditText edtSearchValue = ((EditText) findViewById(R.id.edtSearchValue));
					if (edtSearchValue.getText().toString().isEmpty()) {
						edtSearchValue.requestFocus();
					}
					
					queryComponents.add(getQueryComponentFromView());
					
					TextView lblSearchCriteria = (TextView) findViewById(R.id.lblSearchCriteria);
					lblSearchCriteria.setVisibility(View.VISIBLE);
					
					ListView listSearchCriteria = (ListView) findViewById(R.id.listSearchCriteria);
					listSearchCriteria.setVisibility(View.VISIBLE);				
					SearchCriteriaListAdapter adapter = new SearchCriteriaListAdapter(SearchActivity.this, queryComponents);
					listSearchCriteria.setAdapter(adapter);
					
					TextView lblSelectCriteriaConnector = (TextView) findViewById(R.id.lblSelectCriteriaConnector);
					lblSelectCriteriaConnector.setVisibility(View.VISIBLE);
					RadioGroup radioSearchAndOrConnector = (RadioGroup) findViewById(R.id.radioSearchAndOrConnector);
					radioSearchAndOrConnector.setVisibility(View.VISIBLE);
										
					return true;
				} else {
					return false;
				}
				
			}
		});
		
		ListView listSearchCriteria = (ListView) findViewById(R.id.listSearchCriteria);
		listSearchCriteria.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				queryComponents.remove(position);
				
				ListView listSearchCriteria = (ListView) findViewById(R.id.listSearchCriteria);
				SearchCriteriaListAdapter adapter = new SearchCriteriaListAdapter(SearchActivity.this, queryComponents);
				listSearchCriteria.setAdapter(adapter);
				
				if (queryComponents.isEmpty()) {
					listSearchCriteria.setVisibility(View.GONE);		
					
					TextView lblSearchCriteria = (TextView) findViewById(R.id.lblSearchCriteria);
					lblSearchCriteria.setVisibility(View.GONE);

					TextView lblSelectCriteriaConnector = (TextView) findViewById(R.id.lblSelectCriteriaConnector);
					lblSelectCriteriaConnector.setVisibility(View.GONE);
					RadioGroup radioSearchAndOrConnector = (RadioGroup) findViewById(R.id.radioSearchAndOrConnector);
					radioSearchAndOrConnector.setVisibility(View.GONE);
				}
			}
		});
	}

	private void updateAlbumItemFieldSelectionSpinner(Map<String, String> albumNameToTableNameMapping, String albumTable) {
		Map<String, FieldType> fieldNameToTypeMapping = DatabaseQueryOperation.retrieveFieldnameToFieldTypeMapping(
				DatabaseWrapper.getSQLiteDatabaseInstance(this), this, albumTable);
		
		Spinner comboSelectAlbumItemField = (Spinner) findViewById(R.id.comboSelectAlbumItemField);
		ArrayAdapter<String> albumItemFieldListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
				new ArrayList<String>(fieldNameToTypeMapping.keySet()));
		
		albumItemFieldListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		comboSelectAlbumItemField.setAdapter(albumItemFieldListAdapter);
	}
	
	private void updateOperatorSelectionSpinner(Context context, String fieldName, String albumTableName) {
		FieldType fieldType = DatabaseQueryOperation.retrieveFieldnameToFieldTypeMapping(
				DatabaseWrapper.getSQLiteDatabaseInstance(this), context, albumTableName).get(fieldName);
		
		ArrayList<String> operators = new ArrayList<String>();
		if (fieldType.equals(FieldType.TEXT) || fieldType.equals(FieldType.URL) || fieldType.equals(FieldType.OPTION)) {
			operators.addAll(Arrays.asList(QueryBuilder.toTextOperatorStringArray()));			
		} else if (fieldType.equals(FieldType.DECIMAL) || fieldType.equals(FieldType.INTEGER) || fieldType.equals(FieldType.STAR_RATING)) {
			operators.addAll(Arrays.asList(QueryBuilder.toNumberOperatorStringArray()));
		} else {
			operators.add("no operators for this fieldtype supported"); // TODO
		}
		
		Spinner comboSelectOperator = (Spinner) findViewById(R.id.comboSelectOperator);
		ArrayAdapter<String> operatorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
		
		operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		comboSelectOperator.setAdapter(operatorAdapter);
	}
	
	private QueryComponent getQueryComponentFromView() {
		return new QueryComponent(
				(String) ((Spinner) findViewById(R.id.comboSelectAlbumItemField)).getSelectedItem(),
				QueryBuilder.getQueryOperator(((String) ((Spinner) findViewById(R.id.comboSelectOperator)).getSelectedItem())),
				((EditText) findViewById(R.id.edtSearchValue)).getText().toString());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

}
