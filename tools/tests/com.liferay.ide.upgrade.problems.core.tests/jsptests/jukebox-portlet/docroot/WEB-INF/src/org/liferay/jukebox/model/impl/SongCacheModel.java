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

import org.liferay.jukebox.model.Song;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Song in entity cache.
 *
 * @author Julio Camarero
 * @see Song
 * @generated
 */
public class SongCacheModel implements CacheModel<Song>, Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(31);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", songId=");
		sb.append(songId);
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
		sb.append(", albumId=");
		sb.append(albumId);
		sb.append(", name=");
		sb.append(name);
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
	public Song toEntityModel() {
		SongImpl songImpl = new SongImpl();

		if (uuid == null) {
			songImpl.setUuid(StringPool.BLANK);
		}
		else {
			songImpl.setUuid(uuid);
		}

		songImpl.setSongId(songId);
		songImpl.setCompanyId(companyId);
		songImpl.setGroupId(groupId);
		songImpl.setUserId(userId);

		if (userName == null) {
			songImpl.setUserName(StringPool.BLANK);
		}
		else {
			songImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			songImpl.setCreateDate(null);
		}
		else {
			songImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			songImpl.setModifiedDate(null);
		}
		else {
			songImpl.setModifiedDate(new Date(modifiedDate));
		}

		songImpl.setArtistId(artistId);
		songImpl.setAlbumId(albumId);

		if (name == null) {
			songImpl.setName(StringPool.BLANK);
		}
		else {
			songImpl.setName(name);
		}

		songImpl.setStatus(status);
		songImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			songImpl.setStatusByUserName(StringPool.BLANK);
		}
		else {
			songImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			songImpl.setStatusDate(null);
		}
		else {
			songImpl.setStatusDate(new Date(statusDate));
		}

		songImpl.resetOriginalValues();

		return songImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();
		songId = objectInput.readLong();
		companyId = objectInput.readLong();
		groupId = objectInput.readLong();
		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		artistId = objectInput.readLong();
		albumId = objectInput.readLong();
		name = objectInput.readUTF();
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

		objectOutput.writeLong(songId);
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
		objectOutput.writeLong(albumId);

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}

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
	public long songId;
	public long companyId;
	public long groupId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long artistId;
	public long albumId;
	public String name;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;
}