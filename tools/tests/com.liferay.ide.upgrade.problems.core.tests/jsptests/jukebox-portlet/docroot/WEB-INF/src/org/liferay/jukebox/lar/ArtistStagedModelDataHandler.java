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

package org.liferay.jukebox.lar;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.portal.kernel.lar.ExportImportPathUtil;
import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.service.ServiceContext;

import java.io.InputStream;
import java.util.List;

import com.liferay.portlet.documentlibrary.NoSuchFileException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.store.DLStoreUtil;
import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.service.ArtistLocalServiceUtil;

/**
 * @author Mate Thurzo
 */
public class ArtistStagedModelDataHandler
	extends BaseStagedModelDataHandler<Artist> {

	public static final String[] CLASS_NAMES = {Artist.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException, SystemException {

		Artist artist = ArtistLocalServiceUtil.fetchArtistByUuidAndGroupId(
			uuid, groupId);

		if (artist != null) {
			ArtistLocalServiceUtil.deleteArtist(artist);
		}
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(Artist artist) {
		return artist.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Artist artist)
		throws Exception {

		Element artistElement = portletDataContext.getExportDataElement(artist);

		if (artist.hasCustomImage()) {
			FileEntry fileEntry = artist.getCustomImage();

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, artist, Artist.class, fileEntry,
				FileEntry.class, PortletDataContext.REFERENCE_TYPE_WEAK);
		}

		portletDataContext.addClassedModel(
			artistElement, ExportImportPathUtil.getModelPath(artist), artist);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Artist artist)
		throws Exception {

		long userId = portletDataContext.getUserId(artist.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			artist);

		Artist importedArtist = null;

		if (portletDataContext.isDataStrategyMirror()) {
			Artist existingArtist =
				ArtistLocalServiceUtil.fetchArtistByUuidAndGroupId(
					artist.getUuid(), portletDataContext.getScopeGroupId());

			if (existingArtist == null) {
				serviceContext.setUuid(artist.getUuid());

				importedArtist = ArtistLocalServiceUtil.addArtist(
					userId, artist.getName(), artist.getBio(), null,
					serviceContext);
			}
			else {
				importedArtist = ArtistLocalServiceUtil.updateArtist(
					userId, existingArtist.getArtistId(), artist.getName(),
					artist.getBio(), null, serviceContext);
			}
		}
		else {
			importedArtist = ArtistLocalServiceUtil.addArtist(
				userId, artist.getName(), artist.getBio(), null,
				serviceContext);
		}

		Element artistElement =
			portletDataContext.getImportDataStagedModelElement(artist);

		List<Element> attachmentElements =
			portletDataContext.getReferenceDataElements(
				artistElement, FileEntry.class,
				PortletDataContext.REFERENCE_TYPE_WEAK);

		for (Element attachmentElement : attachmentElements) {
			String path = attachmentElement.attributeValue("path");

			FileEntry fileEntry =
				(FileEntry)portletDataContext.getZipEntryAsObject(path);

			InputStream inputStream = null;

			try {
				String binPath = attachmentElement.attributeValue("bin-path");

				if (Validator.isNull(binPath) &&
					portletDataContext.isPerformDirectBinaryImport()) {

					try {
						inputStream = _getContentStream(fileEntry);
					}
					catch (NoSuchFileException nsfe) {
					}
				}
				else {
					inputStream =
						portletDataContext.getZipEntryAsInputStream(binPath);
				}

				if (inputStream == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to import attachment for file entry " +
								fileEntry.getFileEntryId());
					}

					continue;
				}

				importedArtist = ArtistLocalServiceUtil.updateArtist(
					userId, importedArtist.getArtistId(),
					importedArtist.getName(), importedArtist.getBio(),
					inputStream, serviceContext);
			}
			finally {
				StreamUtil.cleanUp(inputStream);
			}
		}

		portletDataContext.importClassedModel(artist, importedArtist);
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, Artist artist)
		throws Exception {

		long userId = portletDataContext.getUserId(artist.getUserUuid());

		Artist existingArtist =
			ArtistLocalServiceUtil.fetchArtistByUuidAndGroupId(
				artist.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingArtist == null) || !existingArtist.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = existingArtist.getTrashHandler();

		if (trashHandler.isRestorable(existingArtist.getArtistId())) {
			trashHandler.restoreTrashEntry(
				userId, existingArtist.getArtistId());
		}
	}

	@Override
	protected boolean validateMissingReference(
			String uuid, long companyId, long groupId)
		throws Exception {

		Artist artist = ArtistLocalServiceUtil.fetchArtistByUuidAndGroupId(
			uuid, groupId);

		if (artist == null) {
			return false;
		}

		return true;
	}

	private InputStream _getContentStream(FileEntry fileEntry)
		throws PortalException, SystemException {

		long repositoryId = DLFolderConstants.getDataRepositoryId(
			fileEntry.getRepositoryId(), fileEntry.getFolderId());

		String name = ((DLFileEntry)fileEntry.getModel()).getName();

		InputStream is = DLStoreUtil.getFileAsStream(
			fileEntry.getCompanyId(), repositoryId, name,
			fileEntry.getVersion());

		if (is == null) {
			is = new UnsyncByteArrayInputStream(new byte[0]);
		}

		return is;
	}

	private static Log _log = LogFactoryUtil.getLog(
		ArtistStagedModelDataHandler.class);

}