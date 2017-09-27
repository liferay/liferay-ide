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

package org.liferay.jukebox.model.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Repository;
import com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.util.DLUtil;

import org.liferay.jukebox.util.Constants;

/**
 * The extended model implementation for the Artist service. Represents a row in the &quot;jukebox_Artist&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.liferay.jukebox.model.Artist} interface.
 * </p>
 *
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 */
public class ArtistImpl extends ArtistBaseImpl {

	public FileEntry getCustomImage() throws SystemException {
		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
			getGroupId(), Constants.JUKEBOX_PORTLET_REPOSITORY);

		if (repository == null) {
			return null;
		}

		try {
			return PortletFileRepositoryUtil.getPortletFileEntry(
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(getArtistId()));
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getImageURL(ThemeDisplay themeDisplay)
		throws PortalException, SystemException {

		FileEntry fileEntry = getCustomImage();

		if (fileEntry != null) {
			return DLUtil.getPreviewURL(
				fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
				StringPool.BLANK);
		}
		else {
			return themeDisplay.getPortalURL() +
				"/jukebox-portlet/images/singer2.jpeg";
		}
	}

	public boolean hasCustomImage() throws PortalException, SystemException {
		FileEntry fileEntry = getCustomImage();

		if (fileEntry != null) {
			return true;
		}

		return false;
	}

}