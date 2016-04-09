/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.roster.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.expando.kernel.model.ExpandoBridge;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Club}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Club
 * @generated
 */
@ProviderType
public class ClubWrapper implements Club, ModelWrapper<Club> {
	public ClubWrapper(Club club) {
		_club = club;
	}

	@Override
	public Class<?> getModelClass() {
		return Club.class;
	}

	@Override
	public String getModelClassName() {
		return Club.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("clubId", getClubId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long clubId = (Long)attributes.get("clubId");

		if (clubId != null) {
			setClubId(clubId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public java.lang.Object clone() {
		return new ClubWrapper((Club)_club.clone());
	}

	@Override
	public int compareTo(com.liferay.roster.model.Club club) {
		return _club.compareTo(club);
	}

	/**
	* Returns the club ID of this club.
	*
	* @return the club ID of this club
	*/
	@Override
	public long getClubId() {
		return _club.getClubId();
	}

	/**
	* Returns the create date of this club.
	*
	* @return the create date of this club
	*/
	@Override
	public Date getCreateDate() {
		return _club.getCreateDate();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _club.getExpandoBridge();
	}

	/**
	* Returns the modified date of this club.
	*
	* @return the modified date of this club
	*/
	@Override
	public Date getModifiedDate() {
		return _club.getModifiedDate();
	}

	/**
	* Returns the name of this club.
	*
	* @return the name of this club
	*/
	@Override
	public java.lang.String getName() {
		return _club.getName();
	}

	/**
	* Returns the primary key of this club.
	*
	* @return the primary key of this club
	*/
	@Override
	public long getPrimaryKey() {
		return _club.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _club.getPrimaryKeyObj();
	}

	/**
	* Returns the uuid of this club.
	*
	* @return the uuid of this club
	*/
	@Override
	public java.lang.String getUuid() {
		return _club.getUuid();
	}

	@Override
	public int hashCode() {
		return _club.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _club.isCachedModel();
	}

	@Override
	public boolean isEscapedModel() {
		return _club.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _club.isNew();
	}

	@Override
	public void persist() {
		_club.persist();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_club.setCachedModel(cachedModel);
	}

	/**
	* Sets the club ID of this club.
	*
	* @param clubId the club ID of this club
	*/
	@Override
	public void setClubId(long clubId) {
		_club.setClubId(clubId);
	}

	/**
	* Sets the create date of this club.
	*
	* @param createDate the create date of this club
	*/
	@Override
	public void setCreateDate(Date createDate) {
		_club.setCreateDate(createDate);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {
		_club.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_club.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_club.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	* Sets the modified date of this club.
	*
	* @param modifiedDate the modified date of this club
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_club.setModifiedDate(modifiedDate);
	}

	/**
	* Sets the name of this club.
	*
	* @param name the name of this club
	*/
	@Override
	public void setName(java.lang.String name) {
		_club.setName(name);
	}

	@Override
	public void setNew(boolean n) {
		_club.setNew(n);
	}

	/**
	* Sets the primary key of this club.
	*
	* @param primaryKey the primary key of this club
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_club.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_club.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	* Sets the uuid of this club.
	*
	* @param uuid the uuid of this club
	*/
	@Override
	public void setUuid(java.lang.String uuid) {
		_club.setUuid(uuid);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<com.liferay.roster.model.Club> toCacheModel() {
		return _club.toCacheModel();
	}

	@Override
	public com.liferay.roster.model.Club toEscapedModel() {
		return new ClubWrapper(_club.toEscapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _club.toString();
	}

	@Override
	public com.liferay.roster.model.Club toUnescapedModel() {
		return new ClubWrapper(_club.toUnescapedModel());
	}

	@Override
	public java.lang.String toXmlString() {
		return _club.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ClubWrapper)) {
			return false;
		}

		ClubWrapper clubWrapper = (ClubWrapper)obj;

		if (Validator.equals(_club, clubWrapper._club)) {
			return true;
		}

		return false;
	}

	@Override
	public Club getWrappedModel() {
		return _club;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _club.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _club.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_club.resetOriginalValues();
	}

	private final Club _club;
}