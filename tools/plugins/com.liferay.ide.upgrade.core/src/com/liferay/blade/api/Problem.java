/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.api;

import java.io.File;
import java.util.UUID;

/**
 * @author Gregory Amerson
 */
public class Problem {

	public static final int STATUS_NOT_RESOLVED = 0;
	public static final int STATUS_RESOLVED = 1;
	public static final int STATUS_IGNORE = 2;
	public static final long DEFAULT_MARKER_ID = -1;
	public static final int MARKER_WARNING = 1;
	public static final int MARKER_ERROR = 2;

	public Problem() {
	}

	public Problem( String title, String summary, String type,
			String ticket, File file,
			int lineNumber, int startOffset, int endOffset,
			String html, String autoCorrectContext, int status, long markerId, int markerType ) {

		this( UUID.randomUUID().toString(), title, summary, type, ticket, file,
				lineNumber, startOffset, endOffset, html, autoCorrectContext,
				status, markerId, markerType );
	}

	public Problem( String uuid, String title, String summary, String type,
			String ticket, File file,
			int lineNumber, int startOffset, int endOffset,
			String html, String autoCorrectContext, int status, long markerId, int markerType) {

		this.uuid = uuid;
		this.title = title;
		this.summary = summary;
		this.type = type;
		this.ticket = ticket;
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

	public String uuid;
	public File file;
	public int lineNumber;
	public int number;
	public String summary;
	public String ticket;
	public String title;
	public String type;
	public int endOffset;
	public int startOffset;
	public String html;
	public String autoCorrectContext;
	public int status;
	public long markerId;
	public int markerType;

	public String getUuid() {
        return uuid;
   }

	public void setUuid(String uuid) {
	    this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAutoCorrectContext() {
		return autoCorrectContext;
	}

	public void setAutoCorrectContext(String autoCorrectContext) {
		this.autoCorrectContext = autoCorrectContext;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getMarkerId() {
		return markerId;
	}

	public void setMarkerId(long markerId) {
		this.markerId = markerId;
	}

	public int getMarkerType(){
		return markerType;
	}

	public void setMarkerType(int markerType) {
		this.markerType = markerType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Problem other = (Problem) obj;
		if (uuid.equals( other.uuid ))
		    return true;
		if (endOffset != other.endOffset)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (number != other.number)
			return false;
		if (startOffset != other.startOffset)
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (ticket == null) {
			if (other.ticket != null)
				return false;
		} else if (!ticket.equals(other.ticket))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (autoCorrectContext == null) {
			if (other.autoCorrectContext != null)
				return false;
		} else if (!autoCorrectContext.equals(other.autoCorrectContext))
			return false;
		if (status != other.status)
			return false;
		if (markerId != other.markerId)
			return false;
		if(markerType != other.markerType)
			return false;

		return true;
	}

}