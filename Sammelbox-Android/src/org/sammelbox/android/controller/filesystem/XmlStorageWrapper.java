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

package org.sammelbox.android.controller.filesystem;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sammelbox.android.controller.FileSystemAccessWrapper;
import org.sammelbox.android.controller.FileSystemLocations;
import org.sammelbox.android.controller.managers.SavedSearchManager.SavedSearch;
import org.sammelbox.android.model.querybuilder.QueryComponent;
import org.sammelbox.android.model.querybuilder.QueryOperator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XmlStorageWrapper {
	private XmlStorageWrapper() {
		// not needed
	}
	
	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		
		if (node == null) {
			return null;
		}
		
		return node.getNodeValue();
	}
	
	public static Map<String, List<SavedSearch>> retrieveSavedSearches() {
		String savedSearchesXml = FileSystemAccessWrapper.readFileAsString(FileSystemLocations.SAVED_SEARCHES_XML_FILE);
		
		Map<String, List<SavedSearch>> albumNamesToSavedSearches = new HashMap<String, List<SavedSearch>>();
		
		if (savedSearchesXml.isEmpty()) {
			return albumNamesToSavedSearches;
		}
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(savedSearchesXml));

			Document document = documentBuilder.parse(inputSource);
			Node root = document.getFirstChild();

			if (!root.getNodeName().equals("savedSearches")) {
				throw new XmlParsingException("Invalid Saved Searches File");
			} else {
				NodeList viewNodes = document.getElementsByTagName("savedSearch");
				
				String name = "";
				String album = "";
				String orderByField = "";
				String orderAscending = "";
				
				for (int i = 0; i < viewNodes.getLength(); i++) {
					Node node = viewNodes.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						
						name = getValue("name", element);
						album = getValue("album", element);
						orderByField = getValue("orderByField", element);
						orderAscending = getValue("isOrderAscending", element);
						List<QueryComponent> queryComponents = new ArrayList<QueryComponent>();
						String connectedByAnd = getValue("isConnectedByAnd", element);
						
						NodeList queryComponentNodes = element.getElementsByTagName("queryComponent");
						for (int j=0; j<queryComponentNodes.getLength(); j++) {
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element queryComponentElement = (Element) queryComponentNodes.item(j);
								
								String fieldName = getValue("fieldName", queryComponentElement);
								String operator = getValue("operator", queryComponentElement);
								String value = getValue("value", queryComponentElement);
								
								queryComponents.add(new QueryComponent(fieldName, QueryOperator.valueOf(operator), value));
							}
						}
						
						if (albumNamesToSavedSearches.get(album) == null) {
							List<SavedSearch> albumViews = new LinkedList<SavedSearch>();
							albumViews.add(new SavedSearch(name, album, orderByField, Boolean.valueOf(orderAscending), queryComponents, Boolean.valueOf(connectedByAnd)));
							albumNamesToSavedSearches.put(album, albumViews);
						} else {
							List<SavedSearch> albumViews = albumNamesToSavedSearches.get(album);
							albumViews.add(new SavedSearch(name, album, orderByField, Boolean.valueOf(orderAscending), queryComponents, Boolean.valueOf(connectedByAnd)));
							albumNamesToSavedSearches.put(album, albumViews);
						}
					}
				}
			}
		} catch (ParserConfigurationException parserConfigurationException) {
			// TODO
		} catch (IOException ioException) {
			// TODO
		} catch (SAXException saxException) {
			// TODO
		} catch (XmlParsingException xmlParsingException) {
			// TODO
		}
		
		return albumNamesToSavedSearches;
	}
}
