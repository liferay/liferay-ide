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
public class SongSearch extends SearchContainer<AssetEntry> {

	static List<String> headerNames = new ArrayList<String>();

	static {
		headerNames.add("artist");
		headerNames.add("title");
	}

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-results";

	public SongSearch(
		PortletRequest portletRequest, int delta, PortletURL iteratorURL) {

		super(
			portletRequest, new SongDisplayTerms(portletRequest),
			new SongSearchTerms(portletRequest), DEFAULT_CUR_PARAM, delta,
			iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		SongDisplayTerms displayTerms = (SongDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			SongDisplayTerms.ALBUM, displayTerms.getAlbum());
		iteratorURL.setParameter(
			SongDisplayTerms.ARTIST, displayTerms.getArtist());
		iteratorURL.setParameter(
			SongDisplayTerms.TITLE, displayTerms.getTitle());
	}

	public SongSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		this(portletRequest, DEFAULT_DELTA, iteratorURL);
	}

}