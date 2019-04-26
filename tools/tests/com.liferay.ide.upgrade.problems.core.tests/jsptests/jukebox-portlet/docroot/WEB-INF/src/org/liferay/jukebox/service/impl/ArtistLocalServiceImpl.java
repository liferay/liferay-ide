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

import java.io.InputStream;

import java.util.Date;
import java.util.List;

import org.liferay.jukebox.ArtistNameException;
import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.service.base.ArtistLocalServiceBaseImpl;
import org.liferay.jukebox.util.Constants;

/**
 * The implementation of the artist local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.liferay.jukebox.service.ArtistLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 * @see org.liferay.jukebox.service.base.ArtistLocalServiceBaseImpl
 * @see org.liferay.jukebox.service.ArtistLocalServiceUtil
 */
public class ArtistLocalServiceImpl extends ArtistLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	public Artist addArtist(
			long userId, String name, String bio, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		long groupId = serviceContext.getScopeGroupId();

		User user = userPersistence.findByPrimaryKey(userId);

		Date now = new Date();

		validate(name);

		long artistId = counterLocalService.increment();

		Artist artist = artistPersistence.create(artistId);

		artist.setUuid(serviceContext.getUuid());
		artist.setGroupId(groupId);
		artist.setCompanyId(user.getCompanyId());
		artist.setUserId(user.getUserId());
		artist.setUserName(user.getFullName());
		artist.setCreateDate(serviceContext.getCreateDate(now));
		artist.setModifiedDate(serviceContext.getModifiedDate(now));
		artist.setName(name);
		artist.setBio(bio);
		artist.setExpandoBridgeAttributes(serviceContext);

		artistPersistence.update(artist);

		if (inputStream != null) {
			PortletFileRepositoryUtil.addPortletFileEntry(
				groupId, userId, Artist.class.getName(), artist.getArtistId(),
				Constants.JUKEBOX_PORTLET_REPOSITORY,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, inputStream,
				String.valueOf(artist.getArtistId()), StringPool.BLANK, true);
		}

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addEntryResources(
				artist, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addEntryResources(
				artist, serviceContext.getGroupPermissions(),
				serviceContext.getGuestPermissions());
		}

		// Message boards

		mbMessageLocalService.addDiscussionMessage(
			userId, artist.getUserName(), groupId, Artist.class.getName(),
			artistId, WorkflowConstants.ACTION_PUBLISH);

		// Asset

		updateAsset(
			userId, artist, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		return artist;
	}

	@Override
	public void addEntryResources(
			Artist artist, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException, SystemException {

		resourceLocalService.addResources(
			artist.getCompanyId(), artist.getGroupId(), artist.getUserId(),
			Artist.class.getName(), artist.getArtistId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			Artist artist, String[] groupPermissions, String[] guestPermissions)
		throws PortalException, SystemException {

		resourceLocalService.addModelResources(
			artist.getCompanyId(), artist.getGroupId(), artist.getUserId(),
			Artist.class.getName(), artist.getArtistId(), groupPermissions,
			guestPermissions);
	}

	@Indexable(type = IndexableType.DELETE)
	public Artist deleteArtist(long artistId)
		throws PortalException, SystemException {

		Artist artist = artistPersistence.findByPrimaryKey(artistId);

		List<Album> albums = albumLocalService.getAlbumsByArtistId(artistId);

		for (Album album : albums) {
			albumLocalService.deleteAlbum(album.getAlbumId());
		}

		try {
			PortletFileRepositoryUtil.deletePortletFileEntry(
				artist.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(artistId));
		}
		catch (Exception e) {
		}

		return artistPersistence.remove(artistId);
	}

	public void deleteArtists(long groupId)
		throws PortalException, SystemException {

		List<Artist> artists = getArtists(groupId);

		for (Artist artist : artists) {
			artistLocalService.deleteArtist(artist.getArtistId());
		}
	}

	public List<Artist> getArtists(long groupId) throws SystemException {
		return artistPersistence.findByGroupId(groupId);
	}

	public List<Artist> getArtists(long groupId, int start, int end)
		throws SystemException {

		return artistPersistence.findByGroupId(groupId, start, end);
	}

	public int getArtistsCount(long groupId) throws SystemException {
		return artistPersistence.countByGroupId(groupId);
	}

	@Indexable(type = IndexableType.REINDEX)
	public Artist updateArtist(
			long userId, long artistId, String name, String bio,
			InputStream inputStream, ServiceContext serviceContext)
		throws PortalException, SystemException {

		// Event

		User user = userPersistence.findByPrimaryKey(userId);

		validate(name);

		Artist artist = artistPersistence.findByPrimaryKey(artistId);

		artist.setModifiedDate(serviceContext.getModifiedDate(null));
		artist.setName(name);
		artist.setBio(bio);
		artist.setExpandoBridgeAttributes(serviceContext);

		artistPersistence.update(artist);

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
						String.valueOf(artist.getArtistId()));
				}
				catch (Exception e) {
					if (_log.isDebugEnabled()) {
						_log.debug("Cannot delete artist image");
					}
				}
			}

			PortletFileRepositoryUtil.addPortletFileEntry(
				serviceContext.getScopeGroupId(), userId,
				Artist.class.getName(), artist.getArtistId(),
				Constants.JUKEBOX_PORTLET_REPOSITORY,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, inputStream,
				String.valueOf(artist.getArtistId()), StringPool.BLANK, true);
		}

		// Asset

		updateAsset(
			userId, artist, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		return artist;
	}

	public void updateAsset(
			long userId, Artist artist, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException, SystemException {

		AssetEntry assetEntry = assetEntryLocalService.updateEntry(
			userId, artist.getGroupId(), artist.getCreateDate(),
			artist.getModifiedDate(), Artist.class.getName(),
			artist.getArtistId(), artist.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, null, null, null, ContentTypes.TEXT_HTML,
			artist.getName(), null, null, null, null, 0, 0, null, false);

		assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	protected void validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new ArtistNameException();
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ArtistLocalServiceImpl.class);

}