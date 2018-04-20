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
 * This class is used by SOAP remote services, specifically {@link com.liferay.roster.service.http.ClubServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.roster.service.http.ClubServiceSoap
 * @generated
 */
@ProviderType
public class ClubSoap implements Serializable {
	public static ClubSoap toSoapModel(Club model) {
		ClubSoap soapModel = new ClubSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setClubId(model.getClubId());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setName(model.getName());

		return soapModel;
	}

	public static ClubSoap[] toSoapModels(Club[] models) {
		ClubSoap[] soapModels = new ClubSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static ClubSoap[][] toSoapModels(Club[][] models) {
		ClubSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new ClubSoap[models.length][models[0].length];
		}
		else {
			soapModels = new ClubSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static ClubSoap[] toSoapModels(List<Club> models) {
		List<ClubSoap> soapModels = new ArrayList<ClubSoap>(models.size());

		for (Club model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new ClubSoap[soapModels.size()]);
	}

	public ClubSoap() {
	}

	public long getPrimaryKey() {
		return _clubId;
	}

	public void setPrimaryKey(long pk) {
		setClubId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getClubId() {
		return _clubId;
	}

	public void setClubId(long clubId) {
		_clubId = clubId;
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

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	private String _uuid;
	private long _clubId;
	private Date _createDate;
	private Date _modifiedDate;
	private String _name;
}