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

package com.liferay.ide.kaleo.ui.navigator;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.ui.util.KaleoUtil;

import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionEntry {

	public static WorkflowDefinitionEntry createFromJsObject(JSONObject objectDefinition) {
		WorkflowDefinitionEntry node = new WorkflowDefinitionEntry();

		try {
			node.setName(objectDefinition.getString("name"));

			if (objectDefinition.has("title")) {
				node.setTitle(objectDefinition.getString("title"));
			}

			if (objectDefinition.has("titleMap")) {
				node.setTitleMap(objectDefinition.getString("titleMap"));
			}
			else if (!empty(node.getTitle())) {
				node.setTitleMap(KaleoUtil.createJSONTitleMap(node.getTitle()));
			}

			node.setVersion(objectDefinition.getInt("version"));

			if (objectDefinition.has("draftVersion")) {
				node.setDraftVersion(objectDefinition.getInt("draftVersion"));
			}

			if (objectDefinition.has("companyId")) {
				node.setCompanyId(objectDefinition.getLong("companyId"));
			}

			if (objectDefinition.has("userId")) {
				node.setUserId(objectDefinition.getLong("userId"));
			}

			if (objectDefinition.has("groupId")) {
				node.setGroupId(objectDefinition.getLong("groupId"));
			}

			node.setContent(objectDefinition.getString("content"));
		}
		catch (Exception e) {
		}

		return node;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && obj instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry input = (WorkflowDefinitionEntry)obj;

			return _inforEquals(input);
		}

		return false;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public String getContent() {
		return _content;
	}

	public int getDraftVersion() {
		return _draftVersion;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getLocation() {
		return _location;
	}

	public String getName() {
		if (isLoadingNode()) {
			return "Loading kaleo workflows...";
		}

		return _name;
	}

	public WorkflowDefinitionsFolder getParent() {
		return _parent;
	}

	public String getTitle() {
		return _title;
	}

	public String getTitleCurrentValue() {
		return _titleCurrentValue;
	}

	public String getTitleMap() {
		return _titleMap;
	}

	public long getUserId() {
		return _userId;
	}

	public int getVersion() {
		return _version;
	}

	public boolean isLoadingNode() {
		return _loadingNode;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setContent(String content) {
		_content = content;
	}

	public void setDraftVersion(int draftVersion) {
		_draftVersion = draftVersion;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setLoadingNode(boolean loadingNode) {
		_loadingNode = loadingNode;
	}

	public void setLocation(String loc) {
		_location = loc;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setParent(WorkflowDefinitionsFolder parent) {
		_parent = parent;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTitleCurrentValue(String titleCurrentValue) {
		_titleCurrentValue = titleCurrentValue;
	}

	public void setTitleMap(String title) {
		_titleMap = title;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setVersion(int version) {
		_version = version;
	}

	private boolean _inforEquals(WorkflowDefinitionEntry input) {
		boolean infoEqual = false;

		boolean idEqual = false;

		if (_companyId == input.getCompanyId()) {
			idEqual = true;
		}

		boolean contentEqual = false;

		if (_content == null) {
			contentEqual = input.getContent() == null;
		}
		else {
			contentEqual = _content.equals(input.getContent());
		}

		boolean draftVersionEqual = false;

		if (_draftVersion == input.getDraftVersion()) {
			draftVersionEqual = true;
		}

		boolean groupIdEqual = false;

		if (_groupId == input.getGroupId()) {
			groupIdEqual = true;
		}

		boolean locationEqual = false;

		if (_location == null) {
			locationEqual = input.getLocation() == null;
		}
		else {
			locationEqual = _location.equals(input.getLocation());
		}

		boolean nameEqual = false;

		if (_name == null) {
			nameEqual = input.getName() == null;
		}
		else {
			nameEqual = _name.equals(input.getName());
		}

		if (idEqual && contentEqual && draftVersionEqual && groupIdEqual && locationEqual && nameEqual) {
			boolean parentEqual = false;

			if (_parent == null) {
				parentEqual = input.getParent() == null;
			}
			else {
				parentEqual = _parent.equals(input.getParent());
			}

			boolean titleEqual = false;

			if (_title == null) {
				titleEqual = input.getTitle() == null;
			}
			else {
				titleEqual = _title.equals(input.getTitle());
			}

			boolean titleCurrentValueEqual = false;

			if (_titleCurrentValue == null) {
				titleCurrentValueEqual = input.getTitleCurrentValue() == null;
			}
			else {
				titleCurrentValueEqual = _titleCurrentValue.equals(input.getTitleCurrentValue());
			}

			boolean titleMapEqual = false;

			if (_titleMap == null) {
				titleMapEqual = input.getTitleMap() == null;
			}
			else {
				titleMapEqual = _titleMap.equals(input.getTitleMap());
			}

			boolean userIdEqual = false;

			if (_userId == input.getUserId()) {
				userIdEqual = true;
			}

			boolean versionEqual = false;

			if (_version == input.getVersion()) {
				versionEqual = true;
			}

			infoEqual =
				parentEqual && titleEqual && titleCurrentValueEqual && titleMapEqual && userIdEqual && versionEqual;
		}

		return infoEqual;
	}

	private long _companyId = -1;
	private String _content;
	private int _draftVersion = -1;
	private long _groupId = -1;
	private boolean _loadingNode;
	private String _location;
	private String _name;
	private WorkflowDefinitionsFolder _parent;
	private String _title;
	private String _titleCurrentValue;
	private String _titleMap;
	private long _userId = -1;
	private int _version = -1;

}