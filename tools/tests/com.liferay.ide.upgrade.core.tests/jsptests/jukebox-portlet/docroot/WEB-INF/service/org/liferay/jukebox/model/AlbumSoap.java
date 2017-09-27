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
 * This class is used by SOAP remote services, specifically {@link org.liferay.jukebox.service.http.AlbumServiceSoap}.
 *
 * @author Julio Camarero
 * @see org.liferay.jukebox.service.http.AlbumServiceSoap
 * @generated
 */
public class AlbumSoap implements Serializable {
	public static AlbumSoap toSoapModel(Album model) {
		AlbumSoap soapModel = new AlbumSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setAlbumId(model.getAlbumId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setArtistId(model.getArtistId());
		soapModel.setName(model.getName());
		soapModel.setYear(model.getYear());
		soapModel.setStatus(model.getStatus());
		soapModel.setStatusByUserId(model.getStatusByUserId());
		soapModel.setStatusByUserName(model.getStatusByUserName());
		soapModel.setStatusDate(model.getStatusDate());

		return soapModel;
	}

	public static AlbumSoap[] toSoapModels(Album[] models) {
		AlbumSoap[] soapModels = new AlbumSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static AlbumSoap[][] toSoapModels(Album[][] models) {
		AlbumSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new AlbumSoap[models.length][models[0].length];
		}
		else {
			soapModels = new AlbumSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static AlbumSoap[] toSoapModels(List<Album> models) {
		List<AlbumSoap> soapModels = new ArrayList<AlbumSoap>(models.size());

		for (Album model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new AlbumSoap[soapModels.size()]);
	}

	public AlbumSoap() {
	}

	public long getPrimaryKey() {
		return _albumId;
	}

	public void setPrimaryKey(long pk) {
		setAlbumId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getAlbumId() {
		return _albumId;
	}

	public void setAlbumId(long albumId) {
		_albumId = albumId;
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

	public long getArtistId() {
		return _artistId;
	}

	public void setArtistId(long artistId) {
		_artistId = artistId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public int getYear() {
		return _year;
	}

	public void setYear(int year) {
		_year = year;
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

	private String _uuid;
	private long _albumId;
	private long _companyId;
	private long _groupId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _artistId;
	private String _name;
	private int _year;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
}