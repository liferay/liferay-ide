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

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.PortletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SongDisplayTerms extends DisplayTerms {

	public static final String ALBUM = "album";

	public static final String ARTIST = "artist";

	public static final String GROUP_ID = "groupId";

	public static final String TITLE = "title";

	public SongDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		album = ParamUtil.getString(portletRequest, ALBUM);
		artist = ParamUtil.getString(portletRequest, ARTIST);
		groupId = ParamUtil.getLong(portletRequest, GROUP_ID);
		title = ParamUtil.getString(portletRequest, TITLE);
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public long getGroupId() {
		return groupId;
	}

	public String getTitle() {
		return title;
	}

	protected String album;
	protected String artist;
	protected long groupId;
	protected String title;

}