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

package com.liferay.ide.upgrade.plan.core;

import java.io.File;

import java.util.UUID;

import org.eclipse.core.runtime.Adapters;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class UpgradeProblem implements Comparable<UpgradeProblem> {

	public static final long DEFAULT_MARKER_ID = -1;

	public static final String MARKER_ATTRIBUTE_AUTOCORRECTCONTEXT = "upgradeProblem.autoCorrectContext";

	public static final String MARKER_ATTRIBUTE_HTML = "upgradeProblem.html";

	public static final String MARKER_ATTRIBUTE_RESOLVED = "upgradeProblem.resolved";

	public static final String MARKER_ATTRIBUTE_SECTION = "upgradeProblem.section";

	public static final String MARKER_ATTRIBUTE_STATUS = "upgradeProblem.status";

	public static final String MARKER_ATTRIBUTE_SUMMARY = "upgradeProblem.summary";

	public static final String MARKER_ATTRIBUTE_TICKET = "upgradeProblem.ticket";

	public static final String MARKER_ATTRIBUTE_TIMESTAMP = "upgradeProblem.timestamp";

	public static final String MARKER_ATTRIBUTE_TYPE = "upgradeProblem.type";

	public static final int MARKER_ERROR = 2;

	public static final String MARKER_TYPE = "com.liferay.ide.upgrade.plan.core.upgradeProblemMarker";

	public static final int MARKER_WARNING = 1;

	public static final int STATUS_IGNORE = 2;

	public static final int STATUS_NOT_RESOLVED = 0;

	public static final int STATUS_RESOLVED = 1;

	public UpgradeProblem() {
	}

	public UpgradeProblem(
		String title, String summary, String type, String ticket, String version, File resource, int lineNumber,
		int startOffset, int endOffset, String html, String autoCorrectContext, int status, long markerId,
		int markerType) {

		this(
			UUID.randomUUID().toString(), title, summary, type, ticket, version, resource, lineNumber, startOffset,
			endOffset, html, autoCorrectContext, status, markerId, markerType);
	}

	public UpgradeProblem(
		String uuid, String title, String summary, String type, String ticket, String version, File resource,
		int lineNumber, int startOffset, int endOffset, String html, String autoCorrectContext, int status,
		long markerId, int markerType) {

		_uuid = uuid;
		_title = title;
		_summary = summary;
		_type = type;
		_ticket = ticket;
		_version = version;
		_resource = resource;
		_lineNumber = lineNumber;
		_startOffset = startOffset;
		_endOffset = endOffset;
		_html = html;
		_autoCorrectContext = autoCorrectContext;
		_status = status;
		_markerId = markerId;
		_markerType = markerType;
	}

	public boolean equals(Object object) {
		if ((object instanceof UpgradeProblem) == false) {
			return false;
		}

		UpgradeProblem other = Adapters.adapt(object, UpgradeProblem.class);

		if (_endOffset != other._endOffset) {
			return false;
		}

		if (_resource == null) {
			if (other._resource != null) {
				return false;
			}
		}
		else if (!_resource.equals(other._resource)) {
			return false;
		}

		if (_lineNumber != other._lineNumber) {
			return false;
		}

		if (_startOffset != other._startOffset) {
			return false;
		}

		if (_isNullOrEmpty(_summary)) {
			if (_isNotNullOrEmpty(other._summary)) {
				return false;
			}
		}
		else if (!_summary.equals(other._summary)) {
			return false;
		}

		if (_isNullOrEmpty(_ticket)) {
			if (_isNotNullOrEmpty(other._ticket)) {
				return false;
			}
		}
		else if (!_ticket.equals(other._ticket)) {
			return false;
		}

		if (_isNullOrEmpty(_title)) {
			if (_isNotNullOrEmpty(other._title)) {
				return false;
			}
		}
		else if (!_title.equals(other._title)) {
			return false;
		}

		if (_isNullOrEmpty(_type)) {
			if (_isNotNullOrEmpty(other._type)) {
				return false;
			}
		}
		else if (!_type.equals(other._type)) {
			return false;
		}

		if (_isNullOrEmpty(_version)) {
			if (_isNotNullOrEmpty(other._version)) {
				return false;
			}
		}
		else if (!_version.equals(other._version)) {
			return false;
		}

		if (_isNullOrEmpty(_autoCorrectContext)) {
			if (_isNotNullOrEmpty(other._autoCorrectContext)) {
				return false;
			}
		}
		else if (!_autoCorrectContext.equals(other._autoCorrectContext)) {
			return false;
		}

		return true;
	}

	public String getAutoCorrectContext() {
		return _autoCorrectContext;
	}

	public int getEndOffset() {
		return _endOffset;
	}

	public String getHtml() {
		return _html;
	}

	public int getLineNumber() {
		return _lineNumber;
	}

	public long getMarkerId() {
		return _markerId;
	}

	public int getMarkerType() {
		return _markerType;
	}

	public File getResource() {
		return _resource;
	}

	public int getStartOffset() {
		return _startOffset;
	}

	public int getStatus() {
		return _status;
	}

	public String getSummary() {
		return _summary;
	}

	public String getTicket() {
		return _ticket;
	}

	public String getTitle() {
		return _title;
	}

	public String getType() {
		return _type;
	}

	public String getUuid() {
		return _uuid;
	}

	public String getVersion() {
		return _version;
	}

	public int hashCode() {
		int hash = 31;

		hash = 31 * hash + ((_autoCorrectContext != null) ? _autoCorrectContext.hashCode() : 0);
		hash = 31 * hash + ((_html != null) ? _html.hashCode() : 0);
		hash = 31 * hash + Integer.hashCode(_endOffset);
		hash = 31 * hash + Integer.hashCode(_lineNumber);
		hash = 31 * hash + Integer.hashCode(_startOffset);
		hash = 31 * hash + ((_resource != null) ? _resource.hashCode() : 0);
		hash = 31 * hash + ((_summary != null) ? _summary.hashCode() : 0);
		hash = 31 * hash + ((_title != null) ? _title.hashCode() : 0);
		hash = 31 * hash + ((_ticket != null) ? _ticket.hashCode() : 0);
		hash = 31 * hash + ((_type != null) ? _type.hashCode() : 0);
		hash = 31 * hash + ((_version != null) ? _version.hashCode() : 0);

		return hash;
	}

	public void setAutoCorrectContext(String autoCorrectContext) {
		_autoCorrectContext = autoCorrectContext;
	}

	public void setEndOffset(int endOffset) {
		_endOffset = endOffset;
	}

	public void setLineNumber(int lineNumber) {
		_lineNumber = lineNumber;
	}

	public void setMarkerId(long markerId) {
		_markerId = markerId;
	}

	public void setMarkerType(int markerType) {
		_markerType = markerType;
	}

	public void setResource(File resource) {
		_resource = resource;
	}

	public void setStartOffset(int startOffset) {
		_startOffset = startOffset;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public void setSummary(String summary) {
		_summary = summary;
	}

	public void setTicket(String ticket) {
		_ticket = ticket;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public void setVersion(String version) {
		_version = version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		String lineSparator = System.getProperty("line.separator");

		builder.append("Problem summary : " + _summary + lineSparator);
		builder.append("	File location is :" + _resource.getAbsolutePath() + lineSparator);
		builder.append("	Ticket is :" + _ticket + lineSparator);
		builder.append("	Line Number is :" + _lineNumber + lineSparator);
		builder.append("	Title is :" + _title + lineSparator);
		builder.append("	Version is :" + _version + lineSparator);
		builder.append("	Type is :" + _type + lineSparator);
		builder.append("	Status is :" + _status + lineSparator);
		builder.append("	StartOffset is :" + _startOffset + lineSparator);
		builder.append("	EndOffset is :" + _endOffset + lineSparator);

		return builder.toString();
	}

	private static boolean _isNotNullOrEmpty(String val) {
		return !_isNullOrEmpty(val);
	}

	private static boolean _isNullOrEmpty(String val) {
		if ((val == null) || val.equals("")) {
			return true;
		}

		String s = val.trim();

		if (s.equals("")) {
			return true;
		}

		return false;
	}

	private String _autoCorrectContext;
	private int _endOffset;
	private String _html;
	private int _lineNumber;
	private long _markerId;
	private int _markerType;
	private File _resource;
	private int _startOffset;
	private int _status;
	private String _summary;
	private String _ticket;
	private String _title;
	private String _type;
	private String _uuid;
	private String _version = "7.0";

	@Override
	public int compareTo(UpgradeProblem other) {
		return this.toString().compareTo(other.toString());
	}

}