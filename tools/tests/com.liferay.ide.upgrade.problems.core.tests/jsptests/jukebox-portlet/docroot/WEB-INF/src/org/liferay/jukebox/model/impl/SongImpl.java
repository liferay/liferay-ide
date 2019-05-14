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
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Repository;
import com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.util.AudioProcessorUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;

import java.util.List;

import org.liferay.jukebox.util.Constants;

/**
 * The extended model implementation for the Song service. Represents a row in the &quot;jukebox_Song&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.liferay.jukebox.model.Song} interface.
 * </p>
 *
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 */
public class SongImpl extends SongBaseImpl {

	public String getImageURL(ThemeDisplay themeDisplay)
		throws SystemException {

		return themeDisplay.getPortalURL() +
			"/jukebox-portlet/images/song.png";
	}

	public String getLyricsURL(ThemeDisplay themeDisplay)
		throws PortalException, SystemException {

		FileEntry fileEntry = getFileEntry(
			themeDisplay, Constants.LYRICS_FOLDER_NAME);

		if (fileEntry == null) {
			return StringPool.BLANK;
		}

		return DLUtil.getPreviewURL(
			fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
			StringPool.BLANK);
	}

	public String getSongURL(ThemeDisplay themeDisplay, String audioContainer)
		throws PortalException, SystemException {

		FileEntry fileEntry = getFileEntry(
			themeDisplay, Constants.SONGS_FOLDER_NAME);

		if (fileEntry == null) {
			return StringPool.BLANK;
		}

		if (!AudioProcessorUtil.hasAudio(fileEntry.getLatestFileVersion())) {
			return StringPool.BLANK;
		}

		if (Validator.isNull(audioContainer)) {
			audioContainer = "mp3";
		}

		String queryString = "&audioPreview=1&type=" + audioContainer;

		return DLUtil.getPreviewURL(
			fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
			queryString);
	}

	protected FileEntry getFileEntry(
			ThemeDisplay themeDisplay, String folderName)
		throws SystemException {

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				getGroupId(), Constants.JUKEBOX_PORTLET_REPOSITORY);

		if (repository == null) {
			return null;
		}

		try {
			Folder folder = PortletFileRepositoryUtil.getPortletFolder(
				0, repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(getSongId()), null);

			Folder songFolder = PortletFileRepositoryUtil.getPortletFolder(
				0, repository.getRepositoryId(), folder.getFolderId(),
				folderName, null);

			List<FileEntry> fileEntries =
				PortletFileRepositoryUtil.getPortletFileEntries(
					themeDisplay.getScopeGroupId(), songFolder.getFolderId());

			if (fileEntries.isEmpty()) {
				return null;
			}

			return fileEntries.get(0);
		}
		catch (Exception e) {
			return null;
		}
	}

}