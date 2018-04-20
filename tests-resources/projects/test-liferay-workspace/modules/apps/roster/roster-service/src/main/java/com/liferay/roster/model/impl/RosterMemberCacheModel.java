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

import com.liferay.roster.model.RosterMember;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing RosterMember in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see RosterMember
 * @generated
 */
@ProviderType
public class RosterMemberCacheModel implements CacheModel<RosterMember>,
	Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof RosterMemberCacheModel)) {
			return false;
		}

		RosterMemberCacheModel rosterMemberCacheModel = (RosterMemberCacheModel)obj;

		if (rosterMemberId == rosterMemberCacheModel.rosterMemberId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, rosterMemberId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", rosterMemberId=");
		sb.append(rosterMemberId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", rosterId=");
		sb.append(rosterId);
		sb.append(", contactId=");
		sb.append(contactId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public RosterMember toEntityModel() {
		RosterMemberImpl rosterMemberImpl = new RosterMemberImpl();

		if (uuid == null) {
			rosterMemberImpl.setUuid(StringPool.BLANK);
		}
		else {
			rosterMemberImpl.setUuid(uuid);
		}

		rosterMemberImpl.setRosterMemberId(rosterMemberId);

		if (createDate == Long.MIN_VALUE) {
			rosterMemberImpl.setCreateDate(null);
		}
		else {
			rosterMemberImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			rosterMemberImpl.setModifiedDate(null);
		}
		else {
			rosterMemberImpl.setModifiedDate(new Date(modifiedDate));
		}

		rosterMemberImpl.setRosterId(rosterId);
		rosterMemberImpl.setContactId(contactId);

		rosterMemberImpl.resetOriginalValues();

		return rosterMemberImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		rosterMemberId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		rosterId = objectInput.readLong();

		contactId = objectInput.readLong();
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

		objectOutput.writeLong(rosterMemberId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(rosterId);

		objectOutput.writeLong(contactId);
	}

	public String uuid;
	public long rosterMemberId;
	public long createDate;
	public long modifiedDate;
	public long rosterId;
	public long contactId;
}