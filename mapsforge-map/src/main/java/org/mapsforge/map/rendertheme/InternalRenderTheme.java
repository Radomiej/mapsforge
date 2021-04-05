/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.rendertheme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.common.io.Resources;

/**
 * Enumeration of all internal rendering themes.
 */
public enum InternalRenderTheme implements XmlRenderTheme {
	/**
	 * A render-theme similar to the OpenStreetMap Osmarender style.
	 * 
	 * @see <a href="http://wiki.openstreetmap.org/wiki/Osmarender">Osmarender</a>
	 */
	OSMARENDER("/osmarender/", "osmarender.xml");

	private final String absolutePath;
	private final String file;

	private InternalRenderTheme(String absolutePath, String file) {
		this.absolutePath = absolutePath;
		this.file = file;
	}

	@Override
	public XmlRenderThemeMenuCallback getMenuCallback() {
		return null;
	}
	/**
	 * @return the prefix for all relative resource paths.
	 */
	@Override
	public String getRelativePathPrefix() {
		return this.absolutePath;
	}

	@Override
	public InputStream getRenderThemeAsStream() {
		return getFileStream(this.absolutePath + this.file);
	}
	
	public static InputStream getFileStream(String fileName) {
		URL resourceUrl = Resources.getResource(fileName);
		try {
			return Resources.asByteSource(resourceUrl).openStream();
		} catch (IOException e) {
			System.err.println("Asset not found: " + fileName);
			return new InputStream() {
				@Override
				public int read() throws IOException {
					return -1;
				}
			};
		}
	}
}
