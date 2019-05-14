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

package org.liferay.jukebox.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Repository;
import com.liferay.portal.model.User;
import com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetLinkConstants;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.model.TrashVersion;

import java.io.InputStream;

import java.util.Date;
import java.util.List;

import org.liferay.jukebox.AlbumNameException;
import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Song;
import org.liferay.jukebox.service.base.AlbumLocalServiceBaseImpl;
import org.liferay.jukebox.util.Constants;

/**
 * The implementation of the album local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.liferay.jukebox.service.AlbumLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 * @see org.liferay.jukebox.service.base.AlbumLocalServiceBaseImpl
 * @see org.liferay.jukebox.service.AlbumLocalServiceUtil
 */
public class AlbumLocalServiceImpl extends AlbumLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	public Album addAlbum(
			long userId, long artistId, String name, int year,
			InputStream inputStream, ServiceContext serviceContext)
		throws PortalException, SystemException {

		long groupId = serviceContext.getScopeGroupId();

		User user = userPersistence.findByPrimaryKey(userId);

		Date now = new Date();

		validate(name);

		long albumId = counterLocalService.increment();

		Album album = albumPersistence.create(albumId);

		album.setUuid(serviceContext.getUuid());
		album.setGroupId(groupId);
		album.setCompanyId(user.getCompanyId());
		album.setUserId(user.getUserId());
		album.setUserName(user.getFullName());
		album.setCreateDate(serviceContext.getCreateDate(now));
		album.setModifiedDate(serviceContext.getModifiedDate(now));
		album.setArtistId(artistId);
		album.setName(name);
		album.setYear(year);
		album.setExpandoBridgeAttributes(serviceContext);

		albumPersistence.update(album);

		if (inputStream != null) {
			PortletFileRepositoryUtil.addPortletFileEntry(
				groupId, userId, Album.class.getName(), album.getAlbumId(),
				Constants.JUKEBOX_PORTLET_REPOSITORY,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, inputStream,
				String.valueOf(album.getAlbumId()), StringPool.BLANK, true);
		}

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addEntryResources(
				album, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addEntryResources(
				album, serviceContext.getGroupPermissions(),
				serviceContext.getGuestPermissions());
		}

		// Message boards

		mbMessageLocalService.addDiscussionMessage(
			userId, album.getUserName(), groupId, Album.class.getName(),
			albumId, WorkflowConstants.ACTION_PUBLISH);

		// Asset

		updateAsset(
			userId, album, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		return album;
	}

	@Override
	public void addEntryResources(
			Album album, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException, SystemException {

		resourceLocalService.addResources(
			album.getCompanyId(), album.getGroupId(), album.getUserId(),
			Album.class.getName(), album.getAlbumId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			Album album, String[] groupPermissions, String[] guestPermissions)
		throws PortalException, SystemException {

		resourceLocalService.addModelResources(
			album.getCompanyId(), album.getGroupId(), album.getUserId(),
			Album.class.getName(), album.getAlbumId(), groupPermissions,
			guestPermissions);
	}

	@Indexable(type = IndexableType.DELETE)
	public Album deleteAlbum(long albumId)
		throws PortalException, SystemException {

		Album album = albumPersistence.findByPrimaryKey(albumId);

		List<Song> songs = songLocalService.getSongsByAlbumId(albumId);

		for (Song song : songs) {
			songLocalService.deleteSong(song.getSongId());
		}

		try {
			PortletFileRepositoryUtil.deletePortletFileEntry(
				album.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(albumId));
		}
		catch (Exception e) {
		}

		return albumPersistence.remove(albumId);
	}

	public void deleteAlbums(long groupId)
		throws PortalException, SystemException {

		List<Album> albums = getAlbums(groupId);

		for (Album album : albums) {
			albumLocalService.deleteAlbum(album.getAlbumId());
		}
	}

	public List<Album> getAlbums(long groupId) throws SystemException {
		return albumPersistence.findByGroupId(groupId);
	}

	public List<Album> getAlbums(long groupId, int start, int end)
		throws SystemException {

		return albumPersistence.findByGroupId(groupId, start, end);
	}

	public List<Album> getAlbumsByArtistId(long artistId)
		throws SystemException {

		return albumPersistence.findByArtistId(artistId);
	}

	public int getAlbumsCount(long groupId) throws SystemException {
		return albumPersistence.countByGroupId(groupId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Album moveAlbumToTrash(long userId, long albumId)
		throws PortalException, SystemException {

		ServiceContext serviceContext = new ServiceContext();

		// Folder

		User user = userPersistence.findByPrimaryKey(userId);
		Date now = new Date();

		Album album = albumPersistence.findByPrimaryKey(albumId);

		int oldStatus = album.getStatus();

		album.setModifiedDate(serviceContext.getModifiedDate(now));
		album.setStatus(WorkflowConstants.STATUS_IN_TRASH);
		album.setStatusByUserId(user.getUserId());
		album.setStatusByUserName(user.getFullName());
		album.setStatusDate(serviceContext.getModifiedDate(now));

		albumPersistence.update(album);

		// Asset

		assetEntryLocalService.updateVisible(
			Album.class.getName(), album.getAlbumId(), false);

		// Trash

		TrashEntry trashEntry = trashEntryLocalService.addTrashEntry(
			userId, album.getGroupId(), Album.class.getName(),
			album.getAlbumId(), album.getUuid(), null, oldStatus, null, null);

		// Folders and entries

		List<Song> songs = songLocalService.getSongsByAlbumId(
			album.getAlbumId());

		moveDependentsToTrash(songs, trashEntry.getEntryId());

		return album;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Album restoreAlbumFromTrash(long userId, long albumId)
		throws PortalException, SystemException {

		ServiceContext serviceContext = new ServiceContext();

		// Folder

		User user = userPersistence.findByPrimaryKey(userId);
		Date now = new Date();

		Album album = albumPersistence.findByPrimaryKey(albumId);

		TrashEntry trashEntry = trashEntryLocalService.getEntry(
			Album.class.getName(), albumId);

		album.setModifiedDate(serviceContext.getModifiedDate(now));
		album.setStatus(trashEntry.getStatus());
		album.setStatusByUserId(user.getUserId());
		album.setStatusByUserName(user.getFullName());
		album.setStatusDate(serviceContext.getModifiedDate(now));

		albumPersistence.update(album);

		assetEntryLocalService.updateVisible(
			Album.class.getName(), album.getAlbumId(), true);

		// Songs

		List<Song> songs = songLocalService.getSongsByAlbumId(
			album.getGroupId(), album.getAlbumId(),
			WorkflowConstants.STATUS_IN_TRASH);

		restoreDependentsFromTrash(songs, trashEntry.getEntryId());

		// Trash

		trashEntryLocalService.deleteEntry(trashEntry.getEntryId());

		return album;
	}

	@Indexable(type = IndexableType.REINDEX)
	public Album updateAlbum(
			long userId, long albumId, long artistId, String name, int year,
			InputStream inputStream, ServiceContext serviceContext)
		throws PortalException, SystemException {

		// Event

		User user = userPersistence.findByPrimaryKey(userId);

		validate(name);

		Album album = albumPersistence.findByPrimaryKey(albumId);

		album.setModifiedDate(serviceContext.getModifiedDate(null));
		album.setArtistId(artistId);
		album.setName(name);
		album.setYear(year);
		album.setExpandoBridgeAttributes(serviceContext);

		albumPersistence.update(album);

		if (inputStream != null) {
			Repository repository =
				PortletFileRepositoryUtil.fetchPortletRepository(
					serviceContext.getScopeGroupId(),
					Constants.JUKEBOX_PORTLET_REPOSITORY);

			if (repository != null) {
				try {
					PortletFileRepositoryUtil.deletePortletFileEntry(
						repository.getRepositoryId(),
						DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						String.valueOf(album.getAlbumId()));
				}
				catch (Exception e) {
					if (_log.isDebugEnabled()) {
						_log.debug("Cannot delete album cover");
					}
				}
			}

			PortletFileRepositoryUtil.addPortletFileEntry(
				serviceContext.getScopeGroupId(), userId, Album.class.getName(),
				album.getAlbumId(), Constants.JUKEBOX_PORTLET_REPOSITORY,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, inputStream,
				String.valueOf(album.getAlbumId()), StringPool.BLANK, true);
		}

		// Asset

		updateAsset(
			userId, album, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		return album;
	}

	public void updateAsset(
			long userId, Album album, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException, SystemException {

		AssetEntry assetEntry = assetEntryLocalService.updateEntry(
			userId, album.getGroupId(), album.getCreateDate(),
			album.getModifiedDate(), Album.class.getName(), album.getAlbumId(),
			album.getUuid(), 0, assetCategoryIds, assetTagNames, true, null,
			null, null, ContentTypes.TEXT_HTML, album.getName(), null, null,
			null, null, 0, 0, null, false);

		assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	protected void moveDependentsToTrash(List<Song> songs, long trashEntryId)
		throws PortalException, SystemException {

		for (Song song : songs) {

			// Entry

			if (song.isInTrash()) {
				continue;
			}

			int oldStatus = song.getStatus();

			song.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			songPersistence.update(song);

			// Trash

			int status = oldStatus;

			if (oldStatus == WorkflowConstants.STATUS_PENDING) {
				status = WorkflowConstants.STATUS_DRAFT;
			}

			if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
				trashVersionLocalService.addTrashVersion(
					trashEntryId, Song.class.getName(), song.getSongId(),
					status, null);
			}

			// Asset

			assetEntryLocalService.updateVisible(
				Song.class.getName(), song.getSongId(), false);

			// Indexer

			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				Song.class);

			indexer.reindex(song);
		}
	}

	protected void restoreDependentsFromTrash(
			List<Song> songs, long trashEntryId)
		throws PortalException, SystemException {

		for (Song song : songs) {

			// Song

			TrashEntry trashEntry = trashEntryLocalService.fetchEntry(
				Song.class.getName(), song.getSongId());

			if (trashEntry != null) {
				continue;
			}

			TrashVersion trashVersion = trashVersionLocalService.fetchVersion(
				trashEntryId, Song.class.getName(), song.getSongId());

			int oldStatus = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				oldStatus = trashVersion.getStatus();
			}

			song.setStatus(oldStatus);

			songPersistence.update(song);

			// Trash

			if (trashVersion != null) {
				trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Asset

			if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
				assetEntryLocalService.updateVisible(
					Song.class.getName(), song.getSongId(), true);
			}

			// Indexer

			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				Song.class);

			indexer.reindex(song);
		}
	}

	protected void validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new AlbumNameException();
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		AlbumLocalServiceImpl.class);

}