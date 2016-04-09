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
 * This class is used by SOAP remote services, specifically {@link com.liferay.roster.service.http.RosterServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.roster.service.http.RosterServiceSoap
 * @generated
 */
@ProviderType
public class RosterSoap implements Serializable {
	public static RosterSoap toSoapModel(Roster model) {
		RosterSoap soapModel = new RosterSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setRosterId(model.getRosterId());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setClubId(model.getClubId());
		soapModel.setName(model.getName());

		return soapModel;
	}

	public static RosterSoap[] toSoapModels(Roster[] models) {
		RosterSoap[] soapModels = new RosterSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static RosterSoap[][] toSoapModels(Roster[][] models) {
		RosterSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new RosterSoap[models.length][models[0].length];
		}
		else {
			soapModels = new RosterSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static RosterSoap[] toSoapModels(List<Roster> models) {
		List<RosterSoap> soapModels = new ArrayList<RosterSoap>(models.size());

		for (Roster model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new RosterSoap[soapModels.size()]);
	}

	public RosterSoap() {
	}

	public long getPrimaryKey() {
		return _rosterId;
	}

	public void setPrimaryKey(long pk) {
		setRosterId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getRosterId() {
		return _rosterId;
	}

	public void setRosterId(long rosterId) {
		_rosterId = rosterId;
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

	public long getClubId() {
		return _clubId;
	}

	public void setClubId(long clubId) {
		_clubId = clubId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	private String _uuid;
	private long _rosterId;
	private Date _createDate;
	private Date _modifiedDate;
	private long _clubId;
	private String _name;
}