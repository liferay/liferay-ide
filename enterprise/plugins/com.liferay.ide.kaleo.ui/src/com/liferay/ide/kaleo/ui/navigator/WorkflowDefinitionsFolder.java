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

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;
import com.liferay.ide.server.core.ILiferayServer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionsFolder {

	public WorkflowDefinitionsFolder(ICommonContentExtensionSite site, IServer server) {
		_site = site;
		_input = server;
		KaleoCore.updateKaleoConnectionSettings(_getLiferayServer());
		_scheduleCacheDefinitionsJob();
	}

	public void clearCache() {
		_cachedDefinitions = null;
	}

	public Object[] getChildren() {
		if (_input.getServerState() != IServer.STATE_STARTED) {
			return null;
		}

		if (_getLiferayServer() == null) {
			return null;
		}

		if (_cachedDefinitions == null) {
			_scheduleCacheDefinitionsJob();

			return new Object[] {_createLoadingNode()};
		}

		return _cachedDefinitions;
	}

	public IServer getParent() {
		return _input;
	}

	public IStatus getStatus() {
		return _currentStatus;
	}

	private Object _createLoadingNode() {
		WorkflowDefinitionEntry node = new WorkflowDefinitionEntry();

		node.setLoadingNode(true);

		return node;
	}

	private ILiferayServer _getLiferayServer() {
		Object object = _input.loadAdapter(ILiferayServer.class, null);

		return (ILiferayServer)object;
	}

	private boolean _same(JSONObject json1, JSONObject json2, String field) throws JSONException {
		if ((json1 != null) && (json2 != null) && (json1.get(field) != null) &&
			json1.get(field).equals(json2.get(field))) {

			return true;
		}

		return false;
	}

	private void _scheduleCacheDefinitionsJob() {
		if (_cacheDefinitionsJob == null) {
			_cacheDefinitionsJob = new Job("Loading kaleo workflows...") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					_currentStatus = KaleoUI.createInfoStatus("Loading kaleo workflows...");

					_site.getService().update();

					IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection(_getLiferayServer());

					List<WorkflowDefinitionEntry> definitionEntries = new ArrayList<>();

					IStatus errorStatus = null;

					try {
						JSONArray kaleoDefinitions = kaleoConnection.getKaleoDefinitions();
						JSONArray kaleoDraftDefinitions = kaleoConnection.getKaleoDraftWorkflowDefinitions();

						for (int i = 0; i < kaleoDefinitions.length(); i++) {
							JSONObject definition = (JSONObject)kaleoDefinitions.get(i);

							/*
							 * if( definition.has( "active" ) &&
							 * !definition.getBoolean( "active" ) ) { continue;
							 * }
							 */

							/*
							 * kaleoConnection.getLatestKaleoDraftDefinition(
							 * definition.getString( "name" ),
							 * definition.getString( "version" ) );
							 */
							WorkflowDefinitionEntry definitionEntry = WorkflowDefinitionEntry.createFromJsObject(
								definition);

							definitionEntry.setParent(WorkflowDefinitionsFolder.this);

							if (kaleoDraftDefinitions != null) {
								for (int j = 0; j < kaleoDraftDefinitions.length(); j++) {
									JSONObject draftDefinition = kaleoDraftDefinitions.getJSONObject(j);

									if (_same(definition, draftDefinition, "name") &&
										_same(definition, draftDefinition, "version")) {

										int draftVersion = draftDefinition.getInt("draftVersion");

										if (definitionEntry.getDraftVersion() < draftVersion) {
											if (draftDefinition.has("title")) {
												definitionEntry.setTitle(draftDefinition.getString("title"));
											}

											if (draftDefinition.has("titleMap")) {
												definitionEntry.setTitleMap(draftDefinition.getString("titleMap"));
											}
											else {
												definitionEntry.setTitleMap(
													KaleoUtil.createJSONTitleMap(definitionEntry.getTitle()));
											}

											definitionEntry.setCompanyId(draftDefinition.getLong("companyId"));
											definitionEntry.setContent(draftDefinition.getString("content"));
											definitionEntry.setDraftVersion(draftDefinition.getInt("draftVersion"));

											String titleCurrentValue = draftDefinition.getString("titleCurrentValue");

											definitionEntry.setTitleCurrentValue(titleCurrentValue);

											definitionEntry.setUserId(draftDefinition.getLong("userId"));
											definitionEntry.setGroupId(draftDefinition.getLong("groupId"));
											definitionEntry.setLocation(
												kaleoConnection.getHost() + ":" + kaleoConnection.getHttpPort());
										}
									}
								}
							}

							definitionEntries.add(definitionEntry);
						}

						if (kaleoDraftDefinitions != null) {
							for (int i = 0; i < kaleoDraftDefinitions.length(); i++) {
								JSONObject draftDefinition = kaleoDraftDefinitions.getJSONObject(i);
								WorkflowDefinitionEntry draftEntry = null;

								for (WorkflowDefinitionEntry entry : definitionEntries) {
									if (entry.getName().equals(draftDefinition.getString("name"))) {
										if (entry.getVersion() == draftDefinition.getInt("version")) {
											draftEntry = entry;

											if (entry.getDraftVersion() < draftDefinition.getInt("draftVersion")) {
												entry.setCompanyId(draftDefinition.getLong("companyId"));
												entry.setContent(draftDefinition.getString("content"));
												entry.setDraftVersion(draftDefinition.getInt("draftVersion"));
												entry.setTitleCurrentValue(
													draftDefinition.getString("titleCurrentValue"));
												entry.setUserId(draftDefinition.getLong("userId"));
												entry.setLocation(
													kaleoConnection.getHost() + ":" + kaleoConnection.getHttpPort());
											}
										}
									}
								}

								if (draftEntry == null) {
									/*
									 * draftEntry = WorkflowDefinitionEntry.
									 * createFromJSONObject( draftDefinition );
									 * draftEntry.setParent(
									 * WorkflowDefinitionsFolder.this );
									 * draftEntry.setDraftVersion(
									 * draftDefinition.getInt( "draftVersion" )
									 * ); definitionEntries.add( draftEntry );
									 */
								}
							}
						}

						_cachedDefinitions = definitionEntries.toArray(new WorkflowDefinitionEntry[0]);
					}
					catch (Exception e) {
						errorStatus = KaleoUI.createErrorStatus(e);
					}

					if (errorStatus != null) {
						_currentStatus = errorStatus;
					}
					else {
						_currentStatus = null;
					}

					_site.getService().update();

					_cacheDefinitionsJob = null;

					return Status.OK_STATUS;
				}

			};

			_cacheDefinitionsJob.schedule();
		}
	}

	private WorkflowDefinitionEntry[] _cachedDefinitions;
	private Job _cacheDefinitionsJob;
	private IStatus _currentStatus;
	private IServer _input;
	private ICommonContentExtensionSite _site;

}