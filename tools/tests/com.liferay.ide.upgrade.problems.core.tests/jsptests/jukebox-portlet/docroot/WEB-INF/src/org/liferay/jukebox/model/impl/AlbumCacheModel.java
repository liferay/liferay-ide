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

package org.liferay.jukebox.model.impl;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import org.liferay.jukebox.model.Album;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Album in entity cache.
 *
 * @author Julio Camarero
 * @see Album
 * @generated
 */
public class AlbumCacheModel implements CacheModel<Album>, Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(31);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", albumId=");
		sb.append(albumId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", artistId=");
		sb.append(artistId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", year=");
		sb.append(year);
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusByUserName=");
		sb.append(statusByUserName);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Album toEntityModel() {
		AlbumImpl albumImpl = new AlbumImpl();

		if (uuid == null) {
			albumImpl.setUuid(StringPool.BLANK);
		}
		else {
			albumImpl.setUuid(uuid);
		}

		albumImpl.setAlbumId(albumId);
		albumImpl.setCompanyId(companyId);
		albumImpl.setGroupId(groupId);
		albumImpl.setUserId(userId);

		if (userName == null) {
			albumImpl.setUserName(StringPool.BLANK);
		}
		else {
			albumImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			albumImpl.setCreateDate(null);
		}
		else {
			albumImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			albumImpl.setModifiedDate(null);
		}
		else {
			albumImpl.setModifiedDate(new Date(modifiedDate));
		}

		albumImpl.setArtistId(artistId);

		if (name == null) {
			albumImpl.setName(StringPool.BLANK);
		}
		else {
			albumImpl.setName(name);
		}

		albumImpl.setYear(year);
		albumImpl.setStatus(status);
		albumImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			albumImpl.setStatusByUserName(StringPool.BLANK);
		}
		else {
			albumImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			albumImpl.setStatusDate(null);
		}
		else {
			albumImpl.setStatusDate(new Date(statusDate));
		}

		albumImpl.resetOriginalValues();

		return albumImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();
		albumId = objectInput.readLong();
		companyId = objectInput.readLong();
		groupId = objectInput.readLong();
		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		artistId = objectInput.readLong();
		name = objectInput.readUTF();
		year = objectInput.readInt();
		status = objectInput.readInt();
		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(albumId);
		objectOutput.writeLong(companyId);
		objectOutput.writeLong(groupId);
		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);
		objectOutput.writeLong(artistId);

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}

		objectOutput.writeInt(year);
		objectOutput.writeInt(status);
		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);
	}

	public String uuid;
	public long albumId;
	public long companyId;
	public long groupId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long artistId;
	public String name;
	public int year;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;
}