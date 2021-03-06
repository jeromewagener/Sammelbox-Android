/** -----------------------------------------------------------------
 *    Sammelbox: Collection Manager - A free and open-source collection manager for Windows & Linux
 *    Copyright (C) 2011 Jerome Wagener & Paul Bicheler
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ** ----------------------------------------------------------------- */

package org.sammelbox.android.controller.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sammelbox.android.controller.AppState;
import org.sammelbox.android.controller.filesystem.XmlStorageWrapper;
import org.sammelbox.android.model.querybuilder.QueryBuilder;
import org.sammelbox.android.model.querybuilder.QueryBuilderException;
import org.sammelbox.android.model.querybuilder.QueryComponent;

import android.content.Context;

public final class SavedSearchManager {
	private static Map<String, List<SavedSearch>> albumNamesToSavedSearches = new HashMap<String, List<SavedSearch>>();
	
	private SavedSearchManager() {
		// not needed
	}
	
	/** Initializes saved searches without notifying any attached observers */
	public static void initialize(Context context) {
		SavedSearchManager.loadSavedSearches(context);
	}
	
	/** Returns the complete list of saved searches names for all available albums */
	public static List<String> getSavedSearchesNames() {
		List<String> allSavedSearchesNames = new LinkedList<String>();
		
		for (String albumName : albumNamesToSavedSearches.keySet()) {
			for (SavedSearch savedSearch : albumNamesToSavedSearches.get(albumName)) {
				allSavedSearchesNames.add(savedSearch.name);
			}
		}
		
		return allSavedSearchesNames;
	}
	
	/** Returns the list of saved searches for a specific album */
	public static List<SavedSearch> getSavedSearches(String albumName) {
		List<SavedSearch> savedSearches = albumNamesToSavedSearches.get(albumName);
		
		if (savedSearches != null) {
			return savedSearches;
		}
		
		return new LinkedList<SavedSearch>();
	}
	
	public static String[] getSavedSearchesNamesArray(String albumName) {
		List<SavedSearch> savedSearches = getSavedSearches(albumName);
		
		String[] savedSearchesNamesArray = new String[savedSearches.size()];
		
		for (int i=0; i<savedSearches.size(); i++) {
			savedSearchesNamesArray[i] = savedSearches.get(i).getName();
		}
		
		return savedSearchesNamesArray;
	}
	
	private static void loadSavedSearches(Context context) {
		albumNamesToSavedSearches = XmlStorageWrapper.retrieveSavedSearches();
		
		for (String albumName : albumNamesToSavedSearches.keySet()) {
			boolean found = false;
			for (String albumNameFromDB : AppState.getAlbumNameToTableName(context).keySet()) {
				if (albumNameFromDB.equals(albumName)) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				albumNamesToSavedSearches.remove(albumName);
			}
		}
	}
	
	public static class SavedSearch {
		private String name;
		private String album;
		private String orderByField;
		private boolean orderAscending;
		private List<QueryComponent> queryComponents;
		private boolean connectedByAnd;
				
		public SavedSearch(String name, String album, String orderByField, boolean orderAscending, 
				List<QueryComponent> queryComponents, boolean connectedByAnd) {
			this.name = name;
			this.album = album;
			this.orderByField = orderByField;
			this.orderAscending = orderAscending;
			this.queryComponents = queryComponents;
			this.connectedByAnd = connectedByAnd;
		}
				
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getAlbum() {
			return album;
		}

		public void setAlbum(String album) {
			this.album = album;
		}

		public String getOrderByField() {
			return orderByField;
		}

		public void setOrderByField(String orderByField) {
			this.orderByField = orderByField;
		}

		public List<QueryComponent> getQueryComponents() {
			return queryComponents;
		}

		public void setQueryComponents(List<QueryComponent> queryComponents) {
			this.queryComponents = queryComponents;
		}

		public boolean isConnectedByAnd() {
			return connectedByAnd;
		}

		public void setConnectedByAnd(boolean connectedByAnd) {
			this.connectedByAnd = connectedByAnd;
		}

		public boolean isOrderAscending() {
			return orderAscending;
		}

		public void setOrderAscending(boolean orderAscending) {
			this.orderAscending = orderAscending;
		}
		
		public String getSQLQueryString(Context context) throws QueryBuilderException {
			if (this.getOrderByField() == null || this.getOrderByField().isEmpty()) {
				return QueryBuilder.buildQuery(this.getQueryComponents(), 
						this.isConnectedByAnd(), this.getAlbum(), context);
			} else {
				return QueryBuilder.buildQuery(this.getQueryComponents(), 
						this.isConnectedByAnd(), this.getAlbum(), 
						this.getOrderByField(), this.isOrderAscending(), context);
			}
		}
	}

	public static String getSqlQueryBySavedSearchName(String albumName, String savedSearchName, Context context) throws QueryBuilderException {
		List<SavedSearch> savedSearches = albumNamesToSavedSearches.get(albumName);
		
		for (SavedSearch savedSearch : savedSearches) {
			if (savedSearch.getName().equals(savedSearchName)) {
				if (savedSearch.getOrderByField() == null || savedSearch.getOrderByField().isEmpty()) {
					return QueryBuilder.buildQuery(savedSearch.getQueryComponents(), 
							savedSearch.isConnectedByAnd(), savedSearch.getAlbum(), context);
				} else {
					return QueryBuilder.buildQuery(savedSearch.getQueryComponents(), 
							savedSearch.isConnectedByAnd(), savedSearch.getAlbum(), 
							savedSearch.getOrderByField(), savedSearch.isOrderAscending(), context);
				}
			}
		}
		
		return null;
	}

	public static SavedSearch getSavedSearchByName(String savedSearchName) {
		Collection<List<SavedSearch>> savedSearchesLists = albumNamesToSavedSearches.values();
		
		for (List<SavedSearch> savedSearches : savedSearchesLists) {
			for (SavedSearch savedSearch : savedSearches) {
				if (savedSearch.getName().equals(savedSearchName)) {
					return savedSearch;
				}
			}
		}
		
		return null;
	}
	
	public static SavedSearch getSavedSearchByName(String albumName, String savedSearchName) {
		List<SavedSearch> savedSearches = albumNamesToSavedSearches.get(albumName);
		
		for (SavedSearch savedSearch : savedSearches) {
			if (savedSearch.getName().equals(savedSearchName)) {
				return savedSearch;
			}
		}
		
		return null;
	}
	
	public static boolean hasAlbumSavedSearches(String albumName) {
		List<SavedSearch> savedSearches = albumNamesToSavedSearches.get(albumName);
		
		if (savedSearches != null) {
			for (SavedSearch savedSearch : savedSearches) {
				if (savedSearch.getAlbum().equals(albumName)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
