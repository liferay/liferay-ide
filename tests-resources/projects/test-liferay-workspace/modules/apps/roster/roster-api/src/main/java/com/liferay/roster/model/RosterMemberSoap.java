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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.roster.service.http.RosterMemberServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.roster.service.http.RosterMemberServiceSoap
 * @generated
 */
@ProviderType
public class RosterMemberSoap implements Serializable {
	public static RosterMemberSoap toSoapModel(RosterMember model) {
		RosterMemberSoap soapModel = new RosterMemberSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setRosterMemberId(model.getRosterMemberId());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setRosterId(model.getRosterId());
		soapModel.setContactId(model.getContactId());

		return soapModel;
	}

	public static RosterMemberSoap[] toSoapModels(RosterMember[] models) {
		RosterMemberSoap[] soapModels = new RosterMemberSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static RosterMemberSoap[][] toSoapModels(RosterMember[][] models) {
		RosterMemberSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new RosterMemberSoap[models.length][models[0].length];
		}
		else {
			soapModels = new RosterMemberSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static RosterMemberSoap[] toSoapModels(List<RosterMember> models) {
		List<RosterMemberSoap> soapModels = new ArrayList<RosterMemberSoap>(models.size());

		for (RosterMember model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new RosterMemberSoap[soapModels.size()]);
	}

	public RosterMemberSoap() {
	}

	public long getPrimaryKey() {
		return _rosterMemberId;
	}

	public void setPrimaryKey(long pk) {
		setRosterMemberId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getRosterMemberId() {
		return _rosterMemberId;
	}

	public void setRosterMemberId(long rosterMemberId) {
		_rosterMemberId = rosterMemberId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public long getRosterId() {
		return _rosterId;
	}

	public void setRosterId(long rosterId) {
		_rosterId = rosterId;
	}

	public long getContactId() {
		return _contactId;
	}

	public void setContactId(long contactId) {
		_contactId = contactId;
	}

	private String _uuid;
	private long _rosterMemberId;
	private Date _createDate;
	private Date _modifiedDate;
	private long _rosterId;
	private long _contactId;
}