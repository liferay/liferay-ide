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

package org.liferay.jukebox.util;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletURL;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.model.Song;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;
import org.liferay.jukebox.service.ArtistLocalServiceUtil;
import org.liferay.jukebox.service.SongLocalServiceUtil;
import org.liferay.jukebox.service.permission.SongPermission;
import org.liferay.jukebox.service.persistence.SongActionableDynamicQuery;

/**
 * @author Eudaldo Alonso
 */
public class SongIndexer extends BaseIndexer {

	public static final String[] CLASS_NAMES = {Song.class.getName()};

	public static final String PORTLET_ID = PortletKeys.SONGS;

	public SongIndexer() {
		setPermissionAware(true);
	}

	@Override
	public void addRelatedEntryFields(Document document, Object obj)
			throws Exception {

		if (obj instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)obj;

			Song song = SongLocalServiceUtil.getSong(
				GetterUtil.getLong(dlFileEntry.getTitle()));

			document.addKeyword(
				Field.CLASS_NAME_ID,
				PortalUtil.getClassNameId(Song.class.getName()));
			document.addKeyword(Field.CLASS_PK, song.getSongId());
			document.addKeyword(Field.RELATED_ENTRY, true);
		}
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getPortletId() {
		return PORTLET_ID;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		return SongPermission.contains(
			permissionChecker, entryClassPK, ActionKeys.VIEW);
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		addStatus(contextQuery, searchContext);
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		if (searchContext.getAttributes() == null) {
			return;
		}

		addSearchTerm(searchQuery, searchContext, Field.TITLE, true);
		addSearchTerm(searchQuery, searchContext, "album", true);
		addSearchTerm(searchQuery, searchContext, "artist", true);
	}

	@Override
	protected void doDelete(Object obj) throws Exception {
		Song song = (Song)obj;

		deleteDocument(song.getCompanyId(), song.getSongId());
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		Song song = (Song)obj;

		Document document = getBaseModelDocument(PORTLET_ID, song);

		document.addDate(Field.MODIFIED_DATE, song.getModifiedDate());
		document.addText(Field.TITLE, song.getName());

		Album album = AlbumLocalServiceUtil.getAlbum(song.getAlbumId());

		document.addText("album", album.getName());
		document.addKeyword("albumId", album.getAlbumId());

		Artist artist = ArtistLocalServiceUtil.getArtist(song.getArtistId());

		document.addText("artist", artist.getName());
		document.addKeyword("artistId", artist.getArtistId());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		Summary summary = createSummary(document);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(Object obj) throws Exception {
		Song song = (Song)obj;

		Document document = getDocument(song);

		SearchEngineUtil.updateDocument(
			getSearchEngineId(), song.getCompanyId(), document);
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		Song song = SongLocalServiceUtil.getSong(classPK);

		doReindex(song);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		reindexEntries(companyId);
	}

	@Override
	protected String getPortletId(SearchContext searchContext) {
		return PORTLET_ID;
	}

	protected void reindexEntries(long companyId)
		throws PortalException, SystemException {

		final Collection<Document> documents = new ArrayList<Document>();

		ActionableDynamicQuery actionableDynamicQuery =
			new SongActionableDynamicQuery() {

			@Override
			protected void addCriteria(DynamicQuery dynamicQuery) {
			}

			@Override
			protected void performAction(Object object) throws PortalException {
				Song song = (Song)object;

				Document document = getDocument(song);

				documents.add(document);
			}

		};

		actionableDynamicQuery.setCompanyId(companyId);

		actionableDynamicQuery.performActions();

		SearchEngineUtil.updateDocuments(
			getSearchEngineId(), companyId, documents);
	}

}