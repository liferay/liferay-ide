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

package com.liferay.ide.kaleo.ui.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.ui.util.UIUtil;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.swt.widgets.Display;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class UploadWorkflowFileJob extends Job {

	public UploadWorkflowFileJob(IKaleoConnection kaleoConnection, IFile workflowFile, Runnable runnable) {
		super("Uploading new workflow draft definition");

		_kaleoConnection = kaleoConnection;
		_workflowFile = workflowFile;
		_runnable = runnable;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String errorMsgs = KaleoUtil.checkWorkflowDefinitionForErrors(_workflowFile);

			if (!CoreUtil.empty(errorMsgs)) {
				UIUtil.async(
					new Runnable() {

						public void run() {
							MessageDialog.openError(
								Display.getDefault().getActiveShell(), "Upload Kaleo Workflow",
								"Unable to upload kaleo workflow:\n\n" + errorMsgs);
						}

					});

				return Status.OK_STATUS;
			}

			JSONObject def = _kaleoConnection.getKaleoDefinitions().getJSONObject(0);

			int companyId = def.getInt("companyId");
			long groupId = def.getLong("groupId");

			long userId = _kaleoConnection.getUserByEmailAddress().getLong("userId");

			String content = CoreUtil.readStreamToString(_workflowFile.getContents());

			try(InputStream inputStream =_workflowFile.getContents()){
				RootXmlResource rootXmlResource = new RootXmlResource(new XmlResourceStore(inputStream));

				WorkflowDefinition workflowDefinition = WorkflowDefinition.TYPE.instantiate(
					rootXmlResource).nearest(WorkflowDefinition.class);

				String portalLocale = "en_US";

				try {
					portalLocale = _kaleoConnection.getPortalLocale(userId);
				}
				catch (Exception e) {
				}

				String name = workflowDefinition.getName().content();

				String titleMap = KaleoUtil.createJSONTitleMap(name, portalLocale);

				JSONArray drafts = _kaleoConnection.getKaleoDraftWorkflowDefinitions();

				/*
				 * go through to see if the file that is being uploaded has any
				 * existing drafts
				 */
				JSONObject existingDraft = null;

				if ((drafts != null) && (drafts.length() > 0)) {
					for (int i = 0; i < drafts.length(); i++) {
						JSONObject draft = drafts.getJSONObject(i);

						String draftName = draft.getString("name");

						if ((name != null) && name.equals(draftName)) {
							if (existingDraft == null) {
								existingDraft = draft;
							}
							else {
								boolean draftVersion = false;

								if (draft.getInt("draftVersion") > existingDraft.getInt("draftVersion")) {
									draftVersion = true;
								}

								boolean version = false;

								if (draft.getInt("version") > existingDraft.getInt("version")) {
									version = true;
								}

								if (draftVersion || version) {
									existingDraft = draft;
								}
							}
						}
					}
				}

				if (existingDraft != null) {
					_kaleoConnection.updateKaleoDraftDefinition(
						name, titleMap, content, existingDraft.getInt("version"), existingDraft.getInt("draftVersion"),
						companyId, userId);
				}

				_kaleoConnection.publishKaleoDraftDefinition(
					name, titleMap, content, companyId + "", userId + "", groupId + "");
			}

		}
		catch (Exception e) {
			return KaleoUI.createErrorStatus("Error uploading new kaleo workflow file.", e);
		}

		if (_runnable != null) {
			_runnable.run();
		}

		return Status.OK_STATUS;
	}

	private IKaleoConnection _kaleoConnection;
	private Runnable _runnable;
	private IFile _workflowFile;

}