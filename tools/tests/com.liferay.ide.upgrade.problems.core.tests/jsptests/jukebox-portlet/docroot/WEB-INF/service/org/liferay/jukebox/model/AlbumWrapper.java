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

package org.liferay.jukebox.model;

import com.liferay.portal.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Album}.
 * </p>
 *
 * @author Julio Camarero
 * @see Album
 * @generated
 */
public class AlbumWrapper implements Album, ModelWrapper<Album> {
	public AlbumWrapper(Album album) {
		_album = album;
	}

	@Override
	public Class<?> getModelClass() {
		return Album.class;
	}

	@Override
	public String getModelClassName() {
		return Album.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("albumId", getAlbumId());
		attributes.put("companyId", getCompanyId());
		attributes.put("groupId", getGroupId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("artistId", getArtistId());
		attributes.put("name", getName());
		attributes.put("year", getYear());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long albumId = (Long)attributes.get("albumId");

		if (albumId != null) {
			setAlbumId(albumId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long artistId = (Long)attributes.get("artistId");

		if (artistId != null) {
			setArtistId(artistId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer year = (Integer)attributes.get("year");

		if (year != null) {
			setYear(year);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	/**
	* Returns the primary key of this album.
	*
	* @return the primary key of this album
	*/
	@Override
	public long getPrimaryKey() {
		return _album.getPrimaryKey();
	}

	/**
	* Sets the primary key of this album.
	*
	* @param primaryKey the primary key of this album
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_album.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the uuid of this album.
	*
	* @return the uuid of this album
	*/
	@Override
	public java.lang.String getUuid() {
		return _album.getUuid();
	}

	/**
	* Sets the uuid of this album.
	*
	* @param uuid the uuid of this album
	*/
	@Override
	public void setUuid(java.lang.String uuid) {
		_album.setUuid(uuid);
	}

	/**
	* Returns the album ID of this album.
	*
	* @return the album ID of this album
	*/
	@Override
	public long getAlbumId() {
		return _album.getAlbumId();
	}

	/**
	* Sets the album ID of this album.
	*
	* @param albumId the album ID of this album
	*/
	@Override
	public void setAlbumId(long albumId) {
		_album.setAlbumId(albumId);
	}

	/**
	* Returns the company ID of this album.
	*
	* @return the company ID of this album
	*/
	@Override
	public long getCompanyId() {
		return _album.getCompanyId();
	}

	/**
	* Sets the company ID of this album.
	*
	* @param companyId the company ID of this album
	*/
	@Override
	public void setCompanyId(long companyId) {
		_album.setCompanyId(companyId);
	}

	/**
	* Returns the group ID of this album.
	*
	* @return the group ID of this album
	*/
	@Override
	public long getGroupId() {
		return _album.getGroupId();
	}

	/**
	* Sets the group ID of this album.
	*
	* @param groupId the group ID of this album
	*/
	@Override
	public void setGroupId(long groupId) {
		_album.setGroupId(groupId);
	}

	/**
	* Returns the user ID of this album.
	*
	* @return the user ID of this album
	*/
	@Override
	public long getUserId() {
		return _album.getUserId();
	}

	/**
	* Sets the user ID of this album.
	*
	* @param userId the user ID of this album
	*/
	@Override
	public void setUserId(long userId) {
		_album.setUserId(userId);
	}

	/**
	* Returns the user uuid of this album.
	*
	* @return the user uuid of this album
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _album.getUserUuid();
	}

	/**
	* Sets the user uuid of this album.
	*
	* @param userUuid the user uuid of this album
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_album.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this album.
	*
	* @return the user name of this album
	*/
	@Override
	public java.lang.String getUserName() {
		return _album.getUserName();
	}

	/**
	* Sets the user name of this album.
	*
	* @param userName the user name of this album
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_album.setUserName(userName);
	}

	/**
	* Returns the create date of this album.
	*
	* @return the create date of this album
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _album.getCreateDate();
	}

	/**
	* Sets the create date of this album.
	*
	* @param createDate the create date of this album
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_album.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this album.
	*
	* @return the modified date of this album
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _album.getModifiedDate();
	}

	/**
	* Sets the modified date of this album.
	*
	* @param modifiedDate the modified date of this album
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_album.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the artist ID of this album.
	*
	* @return the artist ID of this album
	*/
	@Override
	public long getArtistId() {
		return _album.getArtistId();
	}

	/**
	* Sets the artist ID of this album.
	*
	* @param artistId the artist ID of this album
	*/
	@Override
	public void setArtistId(long artistId) {
		_album.setArtistId(artistId);
	}

	/**
	* Returns the name of this album.
	*
	* @return the name of this album
	*/
	@Override
	public java.lang.String getName() {
		return _album.getName();
	}

	/**
	* Sets the name of this album.
	*
	* @param name the name of this album
	*/
	@Override
	public void setName(java.lang.String name) {
		_album.setName(name);
	}

	/**
	* Returns the year of this album.
	*
	* @return the year of this album
	*/
	@Override
	public int getYear() {
		return _album.getYear();
	}

	/**
	* Sets the year of this album.
	*
	* @param year the year of this album
	*/
	@Override
	public void setYear(int year) {
		_album.setYear(year);
	}

	/**
	* Returns the status of this album.
	*
	* @return the status of this album
	*/
	@Override
	public int getStatus() {
		return _album.getStatus();
	}

	/**
	* Sets the status of this album.
	*
	* @param status the status of this album
	*/
	@Override
	public void setStatus(int status) {
		_album.setStatus(status);
	}

	/**
	* Returns the status by user ID of this album.
	*
	* @return the status by user ID of this album
	*/
	@Override
	public long getStatusByUserId() {
		return _album.getStatusByUserId();
	}

	/**
	* Sets the status by user ID of this album.
	*
	* @param statusByUserId the status by user ID of this album
	*/
	@Override
	public void setStatusByUserId(long statusByUserId) {
		_album.setStatusByUserId(statusByUserId);
	}

	/**
	* Returns the status by user uuid of this album.
	*
	* @return the status by user uuid of this album
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getStatusByUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _album.getStatusByUserUuid();
	}

	/**
	* Sets the status by user uuid of this album.
	*
	* @param statusByUserUuid the status by user uuid of this album
	*/
	@Override
	public void setStatusByUserUuid(java.lang.String statusByUserUuid) {
		_album.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	* Returns the status by user name of this album.
	*
	* @return the status by user name of this album
	*/
	@Override
	public java.lang.String getStatusByUserName() {
		return _album.getStatusByUserName();
	}

	/**
	* Sets the status by user name of this album.
	*
	* @param statusByUserName the status by user name of this album
	*/
	@Override
	public void setStatusByUserName(java.lang.String statusByUserName) {
		_album.setStatusByUserName(statusByUserName);
	}

	/**
	* Returns the status date of this album.
	*
	* @return the status date of this album
	*/
	@Override
	public java.util.Date getStatusDate() {
		return _album.getStatusDate();
	}

	/**
	* Sets the status date of this album.
	*
	* @param statusDate the status date of this album
	*/
	@Override
	public void setStatusDate(java.util.Date statusDate) {
		_album.setStatusDate(statusDate);
	}

	/**
	* Returns the trash entry created when this album was moved to the Recycle Bin. The trash entry may belong to one of the ancestors of this album.
	*
	* @return the trash entry created when this album was moved to the Recycle Bin
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.portlet.trash.model.TrashEntry getTrashEntry()
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _album.getTrashEntry();
	}

	/**
	* Returns the class primary key of the trash entry for this album.
	*
	* @return the class primary key of the trash entry for this album
	*/
	@Override
	public long getTrashEntryClassPK() {
		return _album.getTrashEntryClassPK();
	}

	/**
	* Returns the trash handler for this album.
	*
	* @return the trash handler for this album
	*/
	@Override
	public com.liferay.portal.kernel.trash.TrashHandler getTrashHandler() {
		return _album.getTrashHandler();
	}

	/**
	* Returns <code>true</code> if this album is in the Recycle Bin.
	*
	* @return <code>true</code> if this album is in the Recycle Bin; <code>false</code> otherwise
	*/
	@Override
	public boolean isInTrash() {
		return _album.isInTrash();
	}

	/**
	* Returns <code>true</code> if the parent of this album is in the Recycle Bin.
	*
	* @return <code>true</code> if the parent of this album is in the Recycle Bin; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean isInTrashContainer() {
		return _album.isInTrashContainer();
	}

	/**
	* @deprecated As of 6.1.0, replaced by {@link #isApproved()}
	*/
	@Override
	public boolean getApproved() {
		return _album.getApproved();
	}

	/**
	* Returns <code>true</code> if this album is approved.
	*
	* @return <code>true</code> if this album is approved; <code>false</code> otherwise
	*/
	@Override
	public boolean isApproved() {
		return _album.isApproved();
	}

	/**
	* Returns <code>true</code> if this album is denied.
	*
	* @return <code>true</code> if this album is denied; <code>false</code> otherwise
	*/
	@Override
	public boolean isDenied() {
		return _album.isDenied();
	}

	/**
	* Returns <code>true</code> if this album is a draft.
	*
	* @return <code>true</code> if this album is a draft; <code>false</code> otherwise
	*/
	@Override
	public boolean isDraft() {
		return _album.isDraft();
	}

	/**
	* Returns <code>true</code> if this album is expired.
	*
	* @return <code>true</code> if this album is expired; <code>false</code> otherwise
	*/
	@Override
	public boolean isExpired() {
		return _album.isExpired();
	}

	/**
	* Returns <code>true</code> if this album is inactive.
	*
	* @return <code>true</code> if this album is inactive; <code>false</code> otherwise
	*/
	@Override
	public boolean isInactive() {
		return _album.isInactive();
	}

	/**
	* Returns <code>true</code> if this album is incomplete.
	*
	* @return <code>true</code> if this album is incomplete; <code>false</code> otherwise
	*/
	@Override
	public boolean isIncomplete() {
		return _album.isIncomplete();
	}

	/**
	* Returns <code>true</code> if this album is pending.
	*
	* @return <code>true</code> if this album is pending; <code>false</code> otherwise
	*/
	@Override
	public boolean isPending() {
		return _album.isPending();
	}

	/**
	* Returns <code>true</code> if this album is scheduled.
	*
	* @return <code>true</code> if this album is scheduled; <code>false</code> otherwise
	*/
	@Override
	public boolean isScheduled() {
		return _album.isScheduled();
	}

	/**
	* Returns the container model ID of this album.
	*
	* @return the container model ID of this album
	*/
	@Override
	public long getContainerModelId() {
		return _album.getContainerModelId();
	}

	/**
	* Sets the container model ID of this album.
	*
	* @param containerModelId the container model ID of this album
	*/
	@Override
	public void setContainerModelId(long containerModelId) {
		_album.setContainerModelId(containerModelId);
	}

	/**
	* Returns the container name of this album.
	*
	* @return the container name of this album
	*/
	@Override
	public java.lang.String getContainerModelName() {
		return _album.getContainerModelName();
	}

	/**
	* Returns the parent container model ID of this album.
	*
	* @return the parent container model ID of this album
	*/
	@Override
	public long getParentContainerModelId() {
		return _album.getParentContainerModelId();
	}

	/**
	* Sets the parent container model ID of this album.
	*
	* @param parentContainerModelId the parent container model ID of this album
	*/
	@Override
	public void setParentContainerModelId(long parentContainerModelId) {
		_album.setParentContainerModelId(parentContainerModelId);
	}

	@Override
	public boolean isNew() {
		return _album.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_album.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _album.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_album.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _album.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _album.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_album.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _album.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_album.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_album.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_album.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new AlbumWrapper((Album)_album.clone());
	}

	@Override
	public int compareTo(org.liferay.jukebox.model.Album album) {
		return _album.compareTo(album);
	}

	@Override
	public int hashCode() {
		return _album.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<org.liferay.jukebox.model.Album> toCacheModel() {
		return _album.toCacheModel();
	}

	@Override
	public org.liferay.jukebox.model.Album toEscapedModel() {
		return new AlbumWrapper(_album.toEscapedModel());
	}

	@Override
	public org.liferay.jukebox.model.Album toUnescapedModel() {
		return new AlbumWrapper(_album.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _album.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _album.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_album.persist();
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry getCustomImage()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _album.getCustomImage();
	}

	@Override
	public java.lang.String getImageURL(
		com.liferay.portal.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _album.getImageURL(themeDisplay);
	}

	@Override
	public boolean hasCustomImage()
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _album.hasCustomImage();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AlbumWrapper)) {
			return false;
		}

		AlbumWrapper albumWrapper = (AlbumWrapper)obj;

		if (Validator.equals(_album, albumWrapper._album)) {
			return true;
		}

		return false;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return _album.getStagedModelType();
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public Album getWrappedAlbum() {
		return _album;
	}

	@Override
	public Album getWrappedModel() {
		return _album;
	}

	@Override
	public void resetOriginalValues() {
		_album.resetOriginalValues();
	}

	private Album _album;
}