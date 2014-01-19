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

public class XmlParsingException extends Exception {
	private static final long serialVersionUID = 7309071797276673929L;

	public XmlParsingException() {
		super();
	}
	
	public XmlParsingException(String message) {
		super(message);
	}
	
	public XmlParsingException(Throwable throwable) {
		super(throwable);
	}
	
	public XmlParsingException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
