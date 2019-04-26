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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletURL;

import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.service.ArtistLocalServiceUtil;
import org.liferay.jukebox.service.permission.ArtistPermission;
import org.liferay.jukebox.service.persistence.ArtistActionableDynamicQuery;

/**
 * @author Eudaldo Alonso
 */
public class ArtistIndexer extends BaseIndexer {

	public static final String[] CLASS_NAMES = {Artist.class.getName()};

	public static final String PORTLET_ID = PortletKeys.ARTISTS;

	public ArtistIndexer() {
		setPermissionAware(true);
	}

	@Override
	public void addRelatedEntryFields(Document document, Object obj)
		throws Exception {

		if (obj instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)obj;

			Artist artist = ArtistLocalServiceUtil.getArtist(
				GetterUtil.getLong(dlFileEntry.getTitle()));

			document.addKeyword(
				Field.CLASS_NAME_ID,
				PortalUtil.getClassNameId(Artist.class.getName()));
			document.addKeyword(Field.CLASS_PK, artist.getArtistId());
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

		return ArtistPermission.contains(
			permissionChecker, entryClassPK, ActionKeys.VIEW);
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		addStatus(contextQuery, searchContext);

		int artistId = GetterUtil.getInteger(
			searchContext.getAttribute("artistId"));

		if (artistId != 0) {
			contextQuery.addRequiredTerm("artistId", artistId);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		if (searchContext.getAttributes() == null) {
			return;
		}

		addSearchTerm(searchQuery, searchContext, Field.TITLE, true);
		addSearchTerm(searchQuery, searchContext, "bio", true);
	}

	@Override
	protected void doDelete(Object obj) throws Exception {
		Artist artist = (Artist)obj;

		deleteDocument(artist.getCompanyId(), artist.getArtistId());
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {
		Artist artist = (Artist)obj;

		Document document = getBaseModelDocument(PORTLET_ID, artist);

		document.addDate(Field.MODIFIED_DATE, artist.getModifiedDate());
		document.addText(Field.TITLE, artist.getName());
		document.addKeyword("artistId", artist.getArtistId());
		document.addText("bio", artist.getBio());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletURL portletURL) {

		Summary summary = createSummary(document);

		summary.setMaxContentLength(200);

		String title = document.get(Field.TITLE);

		String content = snippet;

		if (Validator.isNull(snippet)) {
			content = StringUtil.shorten(document.get("bio"), 200);
		}

		portletURL.setParameter("jspPage", "/html/artists/view_artist.jsp");
		portletURL.setParameter("artistId", document.get(Field.CLASS_PK));

		return new Summary(title, content, portletURL);
	}

	@Override
	protected void doReindex(Object obj) throws Exception {
		Artist artist = (Artist)obj;

		Document document = getDocument(artist);

		SearchEngineUtil.updateDocument(
			getSearchEngineId(), artist.getCompanyId(), document);
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		Artist artist = ArtistLocalServiceUtil.getArtist(classPK);

		doReindex(artist);
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
			new ArtistActionableDynamicQuery() {

			@Override
			protected void addCriteria(DynamicQuery dynamicQuery) {
			}

			@Override
			protected void performAction(Object object) throws PortalException {
				Artist artist = (Artist)object;

				Document document = getDocument(artist);

				documents.add(document);
			}

		};

		actionableDynamicQuery.setCompanyId(companyId);

		actionableDynamicQuery.performActions();

		SearchEngineUtil.updateDocuments(
			getSearchEngineId(), companyId, documents);
	}

}