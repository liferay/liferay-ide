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

package com.liferay.roster.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import com.liferay.roster.model.Roster;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Roster in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see Roster
 * @generated
 */
@ProviderType
public class RosterCacheModel implements CacheModel<Roster>, Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof RosterCacheModel)) {
			return false;
		}

		RosterCacheModel rosterCacheModel = (RosterCacheModel)obj;

		if (rosterId == rosterCacheModel.rosterId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, rosterId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", rosterId=");
		sb.append(rosterId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", clubId=");
		sb.append(clubId);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Roster toEntityModel() {
		RosterImpl rosterImpl = new RosterImpl();

		if (uuid == null) {
			rosterImpl.setUuid(StringPool.BLANK);
		}
		else {
			rosterImpl.setUuid(uuid);
		}

		rosterImpl.setRosterId(rosterId);

		if (createDate == Long.MIN_VALUE) {
			rosterImpl.setCreateDate(null);
		}
		else {
			rosterImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			rosterImpl.setModifiedDate(null);
		}
		else {
			rosterImpl.setModifiedDate(new Date(modifiedDate));
		}

		rosterImpl.setClubId(clubId);

		if (name == null) {
			rosterImpl.setName(StringPool.BLANK);
		}
		else {
			rosterImpl.setName(name);
		}

		rosterImpl.resetOriginalValues();

		return rosterImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		rosterId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		clubId = objectInput.readLong();
		name = objectInput.readUTF();
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

		objectOutput.writeLong(rosterId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(clubId);

		if (name == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public String uuid;
	public long rosterId;
	public long createDate;
	public long modifiedDate;
	public long clubId;
	public String name;
}