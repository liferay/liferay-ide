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

import org.liferay.jukebox.model.Artist;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Artist in entity cache.
 *
 * @author Julio Camarero
 * @see Artist
 * @generated
 */
public class ArtistCacheModel implements CacheModel<Artist>, Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(29);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", artistId=");
		sb.append(artistId);
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
		sb.append(", status=");
		sb.append(status);
		sb.append(", statusByUserId=");
		sb.append(statusByUserId);
		sb.append(", statusByUserName=");
		sb.append(statusByUserName);
		sb.append(", statusDate=");
		sb.append(statusDate);
		sb.append(", name=");
		sb.append(name);
		sb.append(", bio=");
		sb.append(bio);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Artist toEntityModel() {
		ArtistImpl artistImpl = new ArtistImpl();

		if (uuid == null) {
			artistImpl.setUuid(StringPool.BLANK);
		}
		else {
			artistImpl.setUuid(uuid);
		}

		artistImpl.setArtistId(artistId);
		artistImpl.setCompanyId(companyId);
		artistImpl.setGroupId(groupId);
		artistImpl.setUserId(userId);

		if (userName == null) {
			artistImpl.setUserName(StringPool.BLANK);
		}
		else {
			artistImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			artistImpl.setCreateDate(null);
		}
		else {
			artistImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			artistImpl.setModifiedDate(null);
		}
		else {
			artistImpl.setModifiedDate(new Date(modifiedDate));
		}

		artistImpl.setStatus(status);
		artistImpl.setStatusByUserId(statusByUserId);

		if (statusByUserName == null) {
			artistImpl.setStatusByUserName(StringPool.BLANK);
		}
		else {
			artistImpl.setStatusByUserName(statusByUserName);
		}

		if (statusDate == Long.MIN_VALUE) {
			artistImpl.setStatusDate(null);
		}
		else {
			artistImpl.setStatusDate(new Date(statusDate));
		}

		if (name == null) {
			artistImpl.setName(StringPool.BLANK);
		}
		else {
			artistImpl.setName(name);
		}

		if (bio == null) {
			artistImpl.setBio(StringPool.BLANK);
		}
		else {
			artistImpl.setBio(bio);
		}

		artistImpl.resetOriginalValues();

		return artistImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();
		artistId = objectInput.readLong();
		companyId = objectInput.readLong();
		groupId = objectInput.readLong();
		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		status = objectInput.readInt();
		statusByUserId = objectInput.readLong();
		statusByUserName = objectInput.readUTF();
		statusDate = objectInput.readLong();
		name = objectInput.readUTF();
		bio = objectInput.readUTF();
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

		objectOutput.writeLong(artistId);
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
		objectOutput.writeInt(status);
		objectOutput.writeLong(statusByUserId);

		if (statusByUserName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(statusByUserName);
		}

		objectOutput.writeLong(statusDate);

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (bio == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(bio);
		}
	}

	public String uuid;
	public long artistId;
	public long companyId;
	public long groupId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public int status;
	public long statusByUserId;
	public String statusByUserName;
	public long statusDate;
	public String name;
	public String bio;
}