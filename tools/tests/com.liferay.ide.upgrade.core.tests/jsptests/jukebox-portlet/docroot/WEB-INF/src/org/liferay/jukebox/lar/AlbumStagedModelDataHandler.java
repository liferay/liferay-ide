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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.service.ServiceContext;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.liferay.portlet.documentlibrary.NoSuchFileException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.store.DLStoreUtil;
import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;
import org.liferay.jukebox.service.ArtistLocalServiceUtil;

/**
 * @author Mate Thurzo
 */
public class AlbumStagedModelDataHandler
	extends BaseStagedModelDataHandler<Album> {

	public static final String[] CLASS_NAMES = {Album.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException, SystemException {

		Album album = AlbumLocalServiceUtil.fetchAlbumByUuidAndGroupId(
			uuid, groupId);

		if (album != null) {
			AlbumLocalServiceUtil.deleteAlbum(album);
		}
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(Album album) {
		return album.getName();
	}

	@Override
	public int[] getExportableStatuses() {
		return new int[] {WorkflowConstants.STATUS_APPROVED};
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Album album)
		throws Exception {

		Artist artist = ArtistLocalServiceUtil.getArtist(album.getArtistId());

		Element albumElement = portletDataContext.getExportDataElement(album);

		if (portletDataContext.getBooleanParameter(
				JukeboxPortletDataHandler.NAMESPACE, "artists")) {

			// Artists are selected to export, making sure this album's artist
			// is going to be exported as well

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, album, artist,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
		else {

			// Artists are not exported - adding missing reference element to
			// validate

			portletDataContext.addReferenceElement(
				album, albumElement, artist,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
		}

		if (album.hasCustomImage()) {
			FileEntry fileEntry = album.getCustomImage();

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, album, Album.class, fileEntry,
				FileEntry.class, PortletDataContext.REFERENCE_TYPE_WEAK);
		}

		portletDataContext.addClassedModel(
			albumElement, ExportImportPathUtil.getModelPath(album), album);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Album album)
		throws Exception {

		long userId = portletDataContext.getUserId(album.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			album);

		String artistPath = ExportImportPathUtil.getModelPath(
			portletDataContext, Artist.class.getName(), album.getArtistId());

		Artist artist = (Artist)portletDataContext.getZipEntryAsObject(
			artistPath);

		if (artist != null) {
			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, album, Artist.class, album.getArtistId());
		}

		Map<Long, Long> artistIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Artist.class);

		long artistId = MapUtil.getLong(
			artistIds, album.getArtistId(), album.getArtistId());

		Album importedAlbum = null;

		if (portletDataContext.isDataStrategyMirror()) {
			Album existingAlbum =
				AlbumLocalServiceUtil.fetchAlbumByUuidAndGroupId(
					album.getUuid(), portletDataContext.getScopeGroupId());

			if (existingAlbum == null) {
				serviceContext.setUuid(album.getUuid());

				importedAlbum = AlbumLocalServiceUtil.addAlbum(
					userId, artistId, album.getName(), album.getYear(), null,
					serviceContext);
			}
			else {
				importedAlbum = AlbumLocalServiceUtil.updateAlbum(
					userId, existingAlbum.getAlbumId(), artistId,
					album.getName(), album.getYear(), null, serviceContext);
			}
		}
		else {
			importedAlbum = AlbumLocalServiceUtil.addAlbum(
				userId, artistId, album.getName(), album.getYear(), null,
				serviceContext);
		}

		Element albumElement =
			portletDataContext.getImportDataStagedModelElement(album);

		List<Element> attachmentElements =
			portletDataContext.getReferenceDataElements(
				albumElement, FileEntry.class,
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

				importedAlbum = AlbumLocalServiceUtil.updateAlbum(
					userId, importedAlbum.getAlbumId(),
					importedAlbum.getArtistId(), importedAlbum.getName(),
					importedAlbum.getYear(), inputStream, serviceContext);
			}
			finally {
				StreamUtil.cleanUp(inputStream);
			}
		}

		portletDataContext.importClassedModel(album, importedAlbum);
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext, Album album)
		throws Exception {

		long userId = portletDataContext.getUserId(album.getUserUuid());

		Album existingAlbum = AlbumLocalServiceUtil.fetchAlbumByUuidAndGroupId(
			album.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingAlbum == null) || !existingAlbum.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = existingAlbum.getTrashHandler();

		if (trashHandler.isRestorable(existingAlbum.getAlbumId())) {
			trashHandler.restoreTrashEntry(userId, existingAlbum.getAlbumId());
		}
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
		AlbumStagedModelDataHandler.class);

}