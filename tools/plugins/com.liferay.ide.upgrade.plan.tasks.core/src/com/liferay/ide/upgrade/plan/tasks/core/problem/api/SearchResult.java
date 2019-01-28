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

package com.liferay.ide.upgrade.plan.tasks.core.problem.api;

import java.io.File;

/**
 * @author Gregory Amerson
 */
public class SearchResult {

	public SearchResult(File file, int startOffset, int endOffset, int startLine, int endLine, boolean fullMatch) {
		this.file = file;
		searchContext = null;
		this.fullMatch = fullMatch;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public SearchResult(
		File file, String searchContext, int startOffset, int endOffset, int startLine, int endLine,
		boolean fullMatch) {

		this.file = file;
		this.searchContext = searchContext;
		this.fullMatch = fullMatch;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.startLine = startLine;
		this.endLine = endLine;
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

		SearchResult other = (SearchResult)obj;

		if (autoCorrectContext == null) {
			if (other.autoCorrectContext != null) {
				return false;
			}
		}

		if (endLine != other.endLine) {
			return false;
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

		if (fullMatch != other.fullMatch) {
			return false;
		}

		if (startLine != other.startLine) {
			return false;
		}

		if (startOffset != other.startOffset) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((autoCorrectContext == null) ? 0 : autoCorrectContext.hashCode());
		result = prime * result + endLine;
		result = prime * result + endOffset;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + (fullMatch ? 1231 : 1237);
		result = prime * result + startLine;
		result = prime * result + startOffset;

		return result;
	}

	public String autoCorrectContext;
	public final int endLine;
	public final int endOffset;
	public final File file;
	public final boolean fullMatch;
	public final String searchContext;
	public final int startLine;
	public final int startOffset;

}