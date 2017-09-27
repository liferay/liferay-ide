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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link org.liferay.jukebox.service.http.ArtistServiceSoap}.
 *
 * @author Julio Camarero
 * @see org.liferay.jukebox.service.http.ArtistServiceSoap
 * @generated
 */
public class ArtistSoap implements Serializable {
	public static ArtistSoap toSoapModel(Artist model) {
		ArtistSoap soapModel = new ArtistSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setArtistId(model.getArtistId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setStatus(model.getStatus());
		soapModel.setStatusByUserId(model.getStatusByUserId());
		soapModel.setStatusByUserName(model.getStatusByUserName());
		soapModel.setStatusDate(model.getStatusDate());
		soapModel.setName(model.getName());
		soapModel.setBio(model.getBio());

		return soapModel;
	}

	public static ArtistSoap[] toSoapModels(Artist[] models) {
		ArtistSoap[] soapModels = new ArtistSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static ArtistSoap[][] toSoapModels(Artist[][] models) {
		ArtistSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new ArtistSoap[models.length][models[0].length];
		}
		else {
			soapModels = new ArtistSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static ArtistSoap[] toSoapModels(List<Artist> models) {
		List<ArtistSoap> soapModels = new ArrayList<ArtistSoap>(models.size());

		for (Artist model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new ArtistSoap[soapModels.size()]);
	}

	public ArtistSoap() {
	}

	public long getPrimaryKey() {
		return _artistId;
	}

	public void setPrimaryKey(long pk) {
		setArtistId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getArtistId() {
		return _artistId;
	}

	public void setArtistId(long artistId) {
		_artistId = artistId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
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

	public int getStatus() {
		return _status;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public long getStatusByUserId() {
		return _statusByUserId;
	}

	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;
	}

	public String getStatusByUserName() {
		return _statusByUserName;
	}

	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;
	}

	public Date getStatusDate() {
		return _statusDate;
	}

	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getBio() {
		return _bio;
	}

	public void setBio(String bio) {
		_bio = bio;
	}

	private String _uuid;
	private long _artistId;
	private long _companyId;
	private long _groupId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
	private String _name;
	private String _bio;
}