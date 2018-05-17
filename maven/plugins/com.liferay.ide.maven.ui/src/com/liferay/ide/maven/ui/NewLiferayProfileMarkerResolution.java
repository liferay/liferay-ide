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

package com.liferay.ide.maven.ui;

import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.ui.action.NewLiferayProfileActionHandler;
import com.liferay.ide.project.ui.wizard.NewLiferayPluginProjectWizard;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.forms.DialogDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewLiferayProfileMarkerResolution extends AbstractProjectMarkerResolution {

	public NewLiferayProfileMarkerResolution() {
	}

	public String getLabel() {
		return "Create a new maven profile based on a Liferay runtime and attach it to the project.";
	}

	@Override
	protected int promptUser(IProject project, NewLiferayPluginProjectOp op) {
		NewLiferayProfile newLiferayProfile = op.getNewLiferayProfiles().insert();

		Reference<DialogDef> dialogRef = DefinitionLoader.sdef(
			NewLiferayPluginProjectWizard.class).dialog("NewLiferayProfile");

		SapphireDialog dialog = new SapphireDialog(UIUtil.getActiveShell(), newLiferayProfile, dialogRef);

		dialog.setBlockOnOpen(true);

		int result = dialog.open();

		if (result == SapphireDialog.OK) {
			IDOMModel domModel = null;

			try {
				IFile pomFile = project.getFile(IMavenConstants.POM_FILE_NAME);

				domModel = (IDOMModel)StructuredModelManager.getModelManager().getModelForEdit(pomFile);

				MavenUtil.createNewLiferayProfileNode(domModel.getDocument(), newLiferayProfile);

				domModel.save();
			}
			catch (Exception e) {
				LiferayMavenCore.logError("Unable to save new Liferay profiles to project pom.", e);
			}
			finally {
				if (domModel != null) {
					domModel.releaseFromEdit();
				}
			}

			NewLiferayProfileActionHandler.addToActiveProfiles(op, newLiferayProfile);
		}
		else {
			op.getNewLiferayProfiles().remove(newLiferayProfile);
		}

		return result;
	}

}