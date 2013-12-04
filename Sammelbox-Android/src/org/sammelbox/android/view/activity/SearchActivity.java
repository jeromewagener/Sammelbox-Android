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
import org.sammelbox.android.view.SearchCriteriaList;

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
import android.widget.Toast;

public class SearchActivity extends Activity {
	private List<QueryComponent> queryComponents = new ArrayList<QueryComponent>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		final Spinner comboSelectAlbum = (Spinner) findViewById(R.id.comboSelectAlbum);		
		comboSelectAlbum.setFocusable(true); 
		comboSelectAlbum.setFocusableInTouchMode(true);
		comboSelectAlbum.requestFocus();
		
		final Map<String, String> albumNameToTableNameMapping = GlobalState.getAlbumNameToTableName(this);
		
		// Album selection spinner (combobox)
		ArrayAdapter<String> albumListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
				new ArrayList<String>(albumNameToTableNameMapping.keySet()));
		comboSelectAlbum.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateAlbumItemFieldSelectionSpinner(albumNameToTableNameMapping,
						albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		albumListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		comboSelectAlbum.setAdapter(albumListAdapter);
		
		// Album item field selection spinner (combobox)
		updateAlbumItemFieldSelectionSpinner(albumNameToTableNameMapping, 
				albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
		final Spinner comboSelectAlbumItemField = (Spinner) findViewById(R.id.comboSelectAlbumItemField);
		comboSelectAlbumItemField.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateOperatorSelectionSpinner(SearchActivity.this, (String) comboSelectAlbumItemField.getSelectedItem(), 
						albumNameToTableNameMapping.get((String) comboSelectAlbum.getSelectedItem()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
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
					boolean connectByAnd = true;
					if (queryComponents.isEmpty()) {
						queryComponents.add(new QueryComponent(
								(String) comboSelectAlbumItemField.getSelectedItem(),
								QueryBuilder.getQueryOperator(((String) ((Spinner) findViewById(R.id.comboSelectOperator)).getSelectedItem())),
								((EditText) findViewById(R.id.edtSearchValue)).getText().toString()));
					} else {
						RadioButton radioConnectByAnd = (RadioButton) findViewById(R.id.radioConnectByAnd);
						if (radioConnectByAnd.isChecked()) {
							connectByAnd = true;
						} else {
							connectByAnd = false;
						}
					}
					
					String rawSqlQuery = QueryBuilder.buildQuery(queryComponents, connectByAnd, (String) comboSelectAlbum.getSelectedItem(), SearchActivity.this);
					
					Toast.makeText(SearchActivity.this, rawSqlQuery, Toast.LENGTH_LONG).show();
					
					GlobalState.setSelectedAlbum((String) comboSelectAlbum.getSelectedItem());
					GlobalState.setSimplifiedAlbumItemResultSet(DatabaseQueryOperation.getAlbumItems(SearchActivity.this, 
							DatabaseWrapper.executeRawSQLQuery(DatabaseWrapper.getSQLiteDatabase(SearchActivity.this), rawSqlQuery)));
					
					Intent openAlbumItemListToBrowse = new Intent(SearchActivity.this, AlbumItemBrowserActivity.class);
	                startActivity(openAlbumItemListToBrowse);
				}
				
				return true;
			}
		});
		
		Button btnAddSearchCriteria = (Button) findViewById(R.id.btnAddCriteria);
		btnAddSearchCriteria.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					queryComponents.add(new QueryComponent(
							(String) comboSelectAlbumItemField.getSelectedItem(),
							QueryBuilder.getQueryOperator(((String) ((Spinner) findViewById(R.id.comboSelectOperator)).getSelectedItem())),
							((EditText) findViewById(R.id.edtSearchValue)).getText().toString()));
					
					TextView lblSearchCriteria = (TextView) findViewById(R.id.lblSearchCriteria);
					lblSearchCriteria.setVisibility(View.VISIBLE);
					
					ListView listSearchCriteria = (ListView) findViewById(R.id.listSearchCriteria);
					listSearchCriteria.setVisibility(View.VISIBLE);				
					SearchCriteriaList adapter = new SearchCriteriaList(SearchActivity.this, queryComponents);
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
				SearchCriteriaList adapter = new SearchCriteriaList(SearchActivity.this, queryComponents);
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
				DatabaseWrapper.getSQLiteDatabase(this), this, albumTable);
		
		Spinner comboSelectAlbumItemField = (Spinner) findViewById(R.id.comboSelectAlbumItemField);
		ArrayAdapter<String> albumItemFieldListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
				new ArrayList<String>(fieldNameToTypeMapping.keySet()));
		
		albumItemFieldListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		comboSelectAlbumItemField.setAdapter(albumItemFieldListAdapter);
	}
	
	private void updateOperatorSelectionSpinner(Context context, String fieldName, String albumTableName) {
		FieldType fieldType = DatabaseQueryOperation.retrieveFieldnameToFieldTypeMapping(
				DatabaseWrapper.getSQLiteDatabase(this), context, albumTableName).get(fieldName);
		
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

}
