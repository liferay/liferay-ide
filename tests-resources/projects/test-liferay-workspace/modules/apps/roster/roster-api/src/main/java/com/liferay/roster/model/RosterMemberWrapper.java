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
 * This class is a wrapper for {@link RosterMember}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RosterMember
 * @generated
 */
@ProviderType
public class RosterMemberWrapper implements RosterMember,
	ModelWrapper<RosterMember> {
	public RosterMemberWrapper(RosterMember rosterMember) {
		_rosterMember = rosterMember;
	}

	@Override
	public Class<?> getModelClass() {
		return RosterMember.class;
	}

	@Override
	public String getModelClassName() {
		return RosterMember.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("rosterMemberId", getRosterMemberId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("rosterId", getRosterId());
		attributes.put("contactId", getContactId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long rosterMemberId = (Long)attributes.get("rosterMemberId");

		if (rosterMemberId != null) {
			setRosterMemberId(rosterMemberId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long rosterId = (Long)attributes.get("rosterId");

		if (rosterId != null) {
			setRosterId(rosterId);
		}

		Long contactId = (Long)attributes.get("contactId");

		if (contactId != null) {
			setContactId(contactId);
		}
	}

	@Override
	public java.lang.Object clone() {
		return new RosterMemberWrapper((RosterMember)_rosterMember.clone());
	}

	@Override
	public int compareTo(com.liferay.roster.model.RosterMember rosterMember) {
		return _rosterMember.compareTo(rosterMember);
	}

	/**
	* Returns the contact ID of this roster member.
	*
	* @return the contact ID of this roster member
	*/
	@Override
	public long getContactId() {
		return _rosterMember.getContactId();
	}

	/**
	* Returns the create date of this roster member.
	*
	* @return the create date of this roster member
	*/
	@Override
	public Date getCreateDate() {
		return _rosterMember.getCreateDate();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _rosterMember.getExpandoBridge();
	}

	/**
	* Returns the modified date of this roster member.
	*
	* @return the modified date of this roster member
	*/
	@Override
	public Date getModifiedDate() {
		return _rosterMember.getModifiedDate();
	}

	/**
	* Returns the primary key of this roster member.
	*
	* @return the primary key of this roster member
	*/
	@Override
	public long getPrimaryKey() {
		return _rosterMember.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _rosterMember.getPrimaryKeyObj();
	}

	/**
	* Returns the roster ID of this roster member.
	*
	* @return the roster ID of this roster member
	*/
	@Override
	public long getRosterId() {
		return _rosterMember.getRosterId();
	}

	/**
	* Returns the roster member ID of this roster member.
	*
	* @return the roster member ID of this roster member
	*/
	@Override
	public long getRosterMemberId() {
		return _rosterMember.getRosterMemberId();
	}

	/**
	* Returns the uuid of this roster member.
	*
	* @return the uuid of this roster member
	*/
	@Override
	public java.lang.String getUuid() {
		return _rosterMember.getUuid();
	}

	@Override
	public int hashCode() {
		return _rosterMember.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _rosterMember.isCachedModel();
	}

	@Override
	public boolean isEscapedModel() {
		return _rosterMember.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _rosterMember.isNew();
	}

	@Override
	public void persist() {
		_rosterMember.persist();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_rosterMember.setCachedModel(cachedModel);
	}

	/**
	* Sets the contact ID of this roster member.
	*
	* @param contactId the contact ID of this roster member
	*/
	@Override
	public void setContactId(long contactId) {
		_rosterMember.setContactId(contactId);
	}

	/**
	* Sets the create date of this roster member.
	*
	* @param createDate the create date of this roster member
	*/
	@Override
	public void setCreateDate(Date createDate) {
		_rosterMember.setCreateDate(createDate);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {
		_rosterMember.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_rosterMember.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_rosterMember.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	* Sets the modified date of this roster member.
	*
	* @param modifiedDate the modified date of this roster member
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_rosterMember.setModifiedDate(modifiedDate);
	}

	@Override
	public void setNew(boolean n) {
		_rosterMember.setNew(n);
	}

	/**
	* Sets the primary key of this roster member.
	*
	* @param primaryKey the primary key of this roster member
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_rosterMember.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_rosterMember.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	* Sets the roster ID of this roster member.
	*
	* @param rosterId the roster ID of this roster member
	*/
	@Override
	public void setRosterId(long rosterId) {
		_rosterMember.setRosterId(rosterId);
	}

	/**
	* Sets the roster member ID of this roster member.
	*
	* @param rosterMemberId the roster member ID of this roster member
	*/
	@Override
	public void setRosterMemberId(long rosterMemberId) {
		_rosterMember.setRosterMemberId(rosterMemberId);
	}

	/**
	* Sets the uuid of this roster member.
	*
	* @param uuid the uuid of this roster member
	*/
	@Override
	public void setUuid(java.lang.String uuid) {
		_rosterMember.setUuid(uuid);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<com.liferay.roster.model.RosterMember> toCacheModel() {
		return _rosterMember.toCacheModel();
	}

	@Override
	public com.liferay.roster.model.RosterMember toEscapedModel() {
		return new RosterMemberWrapper(_rosterMember.toEscapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _rosterMember.toString();
	}

	@Override
	public com.liferay.roster.model.RosterMember toUnescapedModel() {
		return new RosterMemberWrapper(_rosterMember.toUnescapedModel());
	}

	@Override
	public java.lang.String toXmlString() {
		return _rosterMember.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof RosterMemberWrapper)) {
			return false;
		}

		RosterMemberWrapper rosterMemberWrapper = (RosterMemberWrapper)obj;

		if (Validator.equals(_rosterMember, rosterMemberWrapper._rosterMember)) {
			return true;
		}

		return false;
	}

	@Override
	public RosterMember getWrappedModel() {
		return _rosterMember;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _rosterMember.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _rosterMember.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_rosterMember.resetOriginalValues();
	}

	private final RosterMember _rosterMember;
}