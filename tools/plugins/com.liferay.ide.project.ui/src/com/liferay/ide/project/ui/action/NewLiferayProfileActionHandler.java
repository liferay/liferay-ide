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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.ui.wizard.NewLiferayPluginProjectWizard;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Gregory Amerson
 */
public class NewLiferayProfileActionHandler extends PropertyEditorActionHandler {

	public static void addToActiveProfiles(
		final NewLiferayPluginProjectOp op, final NewLiferayProfile newLiferayProfile) {

		// should append to current list

		String activeProfilesValue = SapphireUtil.getContent(op.getActiveProfilesValue());

		StringBuilder sb = new StringBuilder();

		if (CoreUtil.isNotNullOrEmpty(activeProfilesValue)) {
			sb.append(activeProfilesValue);
			sb.append(',');
		}

		sb.append(SapphireUtil.getContent(newLiferayProfile.getId()));

		op.setActiveProfilesValue(sb.toString());
	}

	@Override
	protected Object run(Presentation context) {
		if (context instanceof SwtPresentation) {
			SwtPresentation swt = (SwtPresentation)context;

			NewLiferayPluginProjectOp op = _op(context);

			ElementList<NewLiferayProfile> profiles = op.getNewLiferayProfiles();

			NewLiferayProfile newLiferayProfile = profiles.insert();

			DefinitionLoader loader = DefinitionLoader.sdef(NewLiferayPluginProjectWizard.class);

			SapphireDialog dialog = new SapphireDialog(
				swt.shell(), newLiferayProfile, loader.dialog("NewLiferayProfile"));

			dialog.setBlockOnOpen(true);

			int result = dialog.open();

			if (result == SapphireDialog.OK) {
				addToActiveProfiles(op, newLiferayProfile);
			}
			else {
				profiles.remove(newLiferayProfile);
			}
		}

		return null;
	}

	private NewLiferayPluginProjectOp _op(Presentation context) {
		SapphirePart sapphirePart = context.part();

		Element localModelElement = sapphirePart.getLocalModelElement();

		return localModelElement.nearest(NewLiferayPluginProjectOp.class);
	}

}