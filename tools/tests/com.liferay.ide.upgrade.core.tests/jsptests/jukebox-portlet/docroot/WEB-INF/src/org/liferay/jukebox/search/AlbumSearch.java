/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.liferay.jukebox.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portlet.asset.model.AssetEntry;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

/**
 * @author Eudaldo Alonso
 */
public class AlbumSearch extends SearchContainer<AssetEntry> {

	static List<String> headerNames = new ArrayList<String>();

	static {
		headerNames.add("title");
		headerNames.add("year");
	}

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-results";

	public AlbumSearch(
		PortletRequest portletRequest, int delta, PortletURL iteratorURL) {

		super(
			portletRequest, new AlbumDisplayTerms(portletRequest),
			new AlbumSearchTerms(portletRequest), DEFAULT_CUR_PARAM, delta,
			iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		AlbumDisplayTerms displayTerms = (AlbumDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			AlbumDisplayTerms.ARTIST, displayTerms.getTitle());
		iteratorURL.setParameter(
			AlbumDisplayTerms.TITLE, displayTerms.getTitle());
		iteratorURL.setParameter(
			AlbumDisplayTerms.YEAR, String.valueOf(displayTerms.getYear()));
	}

	public AlbumSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		this(portletRequest, DEFAULT_DELTA, iteratorURL);
	}

}