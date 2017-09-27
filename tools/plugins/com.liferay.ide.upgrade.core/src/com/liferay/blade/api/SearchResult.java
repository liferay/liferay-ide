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

/**
 * @author Gregory Amerson
 */
public class SearchResult{

	public final int endLine;
	public final int endOffset;
	public final File file;
	public final String searchContext;
	public final boolean fullMatch;
	public final int startLine;
	public final int startOffset;
	public String autoCorrectContext;

	public SearchResult(File file, int startOffset, int endOffset,
			int startLine, int endLine, boolean fullMatch) {
		this.file = file;
		this.searchContext = null;
		this.fullMatch = fullMatch;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public SearchResult(File file, String searchContext, int startOffset, int endOffset,
			int startLine, int endLine, boolean fullMatch) {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchResult other = (SearchResult) obj;
		if (autoCorrectContext == null) {
			if (other.autoCorrectContext != null)
				return false;
		}
		if (endLine != other.endLine)
			return false;
		if (endOffset != other.endOffset)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (fullMatch != other.fullMatch)
			return false;
		if (startLine != other.startLine)
			return false;
		if (startOffset != other.startOffset)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
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

}