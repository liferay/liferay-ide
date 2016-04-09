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
 * This class is a wrapper for {@link Roster}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Roster
 * @generated
 */
@ProviderType
public class RosterWrapper implements Roster, ModelWrapper<Roster> {
	public RosterWrapper(Roster roster) {
		_roster = roster;
	}

	@Override
	public Class<?> getModelClass() {
		return Roster.class;
	}

	@Override
	public String getModelClassName() {
		return Roster.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("rosterId", getRosterId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("clubId", getClubId());
		attributes.put("name", getName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long rosterId = (Long)attributes.get("rosterId");

		if (rosterId != null) {
			setRosterId(rosterId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long clubId = (Long)attributes.get("clubId");

		if (clubId != null) {
			setClubId(clubId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}
	}

	@Override
	public java.lang.Object clone() {
		return new RosterWrapper((Roster)_roster.clone());
	}

	@Override
	public int compareTo(com.liferay.roster.model.Roster roster) {
		return _roster.compareTo(roster);
	}

	/**
	* Returns the club ID of this roster.
	*
	* @return the club ID of this roster
	*/
	@Override
	public long getClubId() {
		return _roster.getClubId();
	}

	/**
	* Returns the create date of this roster.
	*
	* @return the create date of this roster
	*/
	@Override
	public Date getCreateDate() {
		return _roster.getCreateDate();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _roster.getExpandoBridge();
	}

	/**
	* Returns the modified date of this roster.
	*
	* @return the modified date of this roster
	*/
	@Override
	public Date getModifiedDate() {
		return _roster.getModifiedDate();
	}

	/**
	* Returns the name of this roster.
	*
	* @return the name of this roster
	*/
	@Override
	public java.lang.String getName() {
		return _roster.getName();
	}

	/**
	* Returns the primary key of this roster.
	*
	* @return the primary key of this roster
	*/
	@Override
	public long getPrimaryKey() {
		return _roster.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _roster.getPrimaryKeyObj();
	}

	/**
	* Returns the roster ID of this roster.
	*
	* @return the roster ID of this roster
	*/
	@Override
	public long getRosterId() {
		return _roster.getRosterId();
	}

	/**
	* Returns the uuid of this roster.
	*
	* @return the uuid of this roster
	*/
	@Override
	public java.lang.String getUuid() {
		return _roster.getUuid();
	}

	@Override
	public int hashCode() {
		return _roster.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _roster.isCachedModel();
	}

	@Override
	public boolean isEscapedModel() {
		return _roster.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _roster.isNew();
	}

	@Override
	public void persist() {
		_roster.persist();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_roster.setCachedModel(cachedModel);
	}

	/**
	* Sets the club ID of this roster.
	*
	* @param clubId the club ID of this roster
	*/
	@Override
	public void setClubId(long clubId) {
		_roster.setClubId(clubId);
	}

	/**
	* Sets the create date of this roster.
	*
	* @param createDate the create date of this roster
	*/
	@Override
	public void setCreateDate(Date createDate) {
		_roster.setCreateDate(createDate);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {
		_roster.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_roster.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_roster.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	* Sets the modified date of this roster.
	*
	* @param modifiedDate the modified date of this roster
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_roster.setModifiedDate(modifiedDate);
	}

	/**
	* Sets the name of this roster.
	*
	* @param name the name of this roster
	*/
	@Override
	public void setName(java.lang.String name) {
		_roster.setName(name);
	}

	@Override
	public void setNew(boolean n) {
		_roster.setNew(n);
	}

	/**
	* Sets the primary key of this roster.
	*
	* @param primaryKey the primary key of this roster
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_roster.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_roster.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	* Sets the roster ID of this roster.
	*
	* @param rosterId the roster ID of this roster
	*/
	@Override
	public void setRosterId(long rosterId) {
		_roster.setRosterId(rosterId);
	}

	/**
	* Sets the uuid of this roster.
	*
	* @param uuid the uuid of this roster
	*/
	@Override
	public void setUuid(java.lang.String uuid) {
		_roster.setUuid(uuid);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<com.liferay.roster.model.Roster> toCacheModel() {
		return _roster.toCacheModel();
	}

	@Override
	public com.liferay.roster.model.Roster toEscapedModel() {
		return new RosterWrapper(_roster.toEscapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _roster.toString();
	}

	@Override
	public com.liferay.roster.model.Roster toUnescapedModel() {
		return new RosterWrapper(_roster.toUnescapedModel());
	}

	@Override
	public java.lang.String toXmlString() {
		return _roster.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof RosterWrapper)) {
			return false;
		}

		RosterWrapper rosterWrapper = (RosterWrapper)obj;

		if (Validator.equals(_roster, rosterWrapper._roster)) {
			return true;
		}

		return false;
	}

	@Override
	public Roster getWrappedModel() {
		return _roster;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _roster.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _roster.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_roster.resetOriginalValues();
	}

	private final Roster _roster;
}