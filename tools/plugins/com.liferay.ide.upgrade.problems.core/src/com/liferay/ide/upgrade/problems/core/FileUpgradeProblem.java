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

package com.liferay.ide.upgrade.problems.core;

import java.io.File;

import java.util.UUID;

/**
 * @author Gregory Amerson
 */
public class FileUpgradeProblem {

	public static final long DEFAULT_MARKER_ID = -1;

	public static final String MARKER_ATTRIBUTE_AUTOCORRECTCONTEXT = "fileUpgradeProblem.autoCorrectContext";

	public static final String MARKER_ATTRIBUTE_RESOLVED = "fileUpgradeProblem.resolved";

	public static final String MARKER_ATTRIBUTE_SECTION = "fileUpgradeProblem.section";

	public static final String MARKER_ATTRIBUTE_SUMMARY = "fileUpgradeProblem.summary";

	public static final String MARKER_ATTRIBUTE_TICKET = "fileUpgradeProblem.ticket";

	public static final String MARKER_ATTRIBUTE_TIMESTAMP = "fileUpgradeProblem.timestamp";

	public static final String MARKER_ATTRIBUTE_TYPE = "fileUpgradeProblem.type";

	public static final int MARKER_ERROR = 2;

	public static final String MARKER_TYPE = "com.liferay.ide.upgrade.plan.core.FileUpgradeProblemMarker";

	public static final int MARKER_WARNING = 1;

	public static final int STATUS_IGNORE = 2;

	public static final int STATUS_NOT_RESOLVED = 0;

	public static final int STATUS_RESOLVED = 1;

	public FileUpgradeProblem() {
	}

	public FileUpgradeProblem(
		String title, String summary, String type, String ticket, String version, File file, int lineNumber,
		int startOffset, int endOffset, String html, String autoCorrectContext, int status, long markerId,
		int markerType) {

		this(
			UUID.randomUUID().toString(), title, summary, type, ticket, version, file, lineNumber, startOffset,
			endOffset, html, autoCorrectContext, status, markerId, markerType);
	}

	public FileUpgradeProblem(
		String uuid, String title, String summary, String type, String ticket, String version, File file,
		int lineNumber, int startOffset, int endOffset, String html, String autoCorrectContext, int status,
		long markerId, int markerType) {

		this.uuid = uuid;
		this.title = title;
		this.summary = summary;
		this.type = type;
		this.ticket = ticket;
		this.version = version;
		this.file = file;
		this.lineNumber = lineNumber;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.html = html;
		this.autoCorrectContext = autoCorrectContext;
		this.status = status;
		this.markerId = markerId;
		this.markerType = markerType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		FileUpgradeProblem other = (FileUpgradeProblem)obj;

		if (uuid.equals(other.uuid)) {
			return true;
		}

		if (endOffset != other.endOffset) {
			return false;
		}

		if (file == null) {
			if (other.file != null) {
				return false;
			}
		}
		else if (!file.equals(other.file)) {
			return false;
		}

		if (lineNumber != other.lineNumber) {
			return false;
		}

		if (number != other.number) {
			return false;
		}

		if (startOffset != other.startOffset) {
			return false;
		}

		if (summary == null) {
			if (other.summary != null) {
				return false;
			}
		}
		else if (!summary.equals(other.summary)) {
			return false;
		}

		if (ticket == null) {
			if (other.ticket != null) {
				return false;
			}
		}
		else if (!ticket.equals(other.ticket)) {
			return false;
		}

		if (title == null) {
			if (other.title != null) {
				return false;
			}
		}
		else if (!title.equals(other.title)) {
			return false;
		}

		if (type == null) {
			if (other.type != null) {
				return false;
			}
		}
		else if (!type.equals(other.type)) {
			return false;
		}

		if (version == null) {
			if (other.version != null) {
				return false;
			}
		}
		else if (!version.equals(other.version)) {
			return false;
		}

		if (autoCorrectContext == null) {
			if (other.autoCorrectContext != null) {
				return false;
			}
		}
		else if (!autoCorrectContext.equals(other.autoCorrectContext)) {
			return false;
		}

		if (status != other.status) {
			return false;
		}

		if (markerId != other.markerId) {
			return false;
		}

		if (markerType != other.markerType) {
			return false;
		}

		return true;
	}

	public String getAutoCorrectContext() {
		return autoCorrectContext;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public File getFile() {
		return file;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public long getMarkerId() {
		return markerId;
	}

	public int getMarkerType() {
		return markerType;
	}

	public int getNumber() {
		return number;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getStatus() {
		return status;
	}

	public String getSummary() {
		return summary;
	}

	public String getTicket() {
		return ticket;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public String getUuid() {
		return uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setAutoCorrectContext(String autoCorrectContext) {
		this.autoCorrectContext = autoCorrectContext;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setMarkerId(long markerId) {
		this.markerId = markerId;
	}

	public void setMarkerType(int markerType) {
		this.markerType = markerType;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String autoCorrectContext;
	public int endOffset;
	public File file;
	public String html;
	public int lineNumber;
	public long markerId;
	public int markerType;
	public int number;
	public int startOffset;
	public int status;
	public String summary;
	public String ticket;
	public String title;
	public String type;
	public String uuid;
	public String version = "7.0";

}