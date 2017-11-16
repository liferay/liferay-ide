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

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.util.PortletUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class CreatePortletAppResourceBundleActionHandler extends AbstractResourceBundleActionHandler {

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#init(org.eclipse.
	 * sapphire.ui.SapphireAction, ActionHandlerDef)
	 */
	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);
		Element element = getModelElement();
		Property property = property();
		listener = new FilteredListener<PropertyEvent>() {

			@Override
			protected void handleTypedEvent(PropertyEvent event) {
				refreshEnablementState();
			}

		};

		element.attach(listener, property.definition().name());

		Listener listen = new Listener() {

			@Override
			public void handle(Event event) {
				if (event instanceof DisposeEvent) {
					PropertyDef definition = property().definition();

					getModelElement().detach(listener, definition.name());
				}
			}

		};

		attach(listen);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.
	 * SapphireRenderingContext)
	 */
	@Override
	protected Object run(Presentation context) {
		Element element = getModelElement();

		IProject project = element.adapt(IProject.class);

		Property property = property();

		Value<Path> resourceBundle = element.property((ValueProperty)property.definition());

		String resourceBundleText = resourceBundle.text();

		int index = resourceBundleText.lastIndexOf(".");

		if (index == -1) {
			index = resourceBundleText.length();
		}

		String packageName = resourceBundleText.substring(0, index);

		String defaultRBFileName = PortletUtil.convertJavaToIoFileName(
			resourceBundleText, GenericResourceBundlePathService.RB_FILE_EXTENSION);

		IFolder rbSourecFolder = getResourceBundleFolderLocation(project, defaultRBFileName);

		IPath entryPath = rbSourecFolder.getLocation();

		if (getModelElement() instanceof PortletApp) {
			List<IFile> missingRBFiles = new ArrayList<>();
			StringBuilder rbFileBuffer = new StringBuilder("#Portlet Application Resource Bundle \n");
			IFile rbFile = wroot.getFileForLocation(entryPath.append(defaultRBFileName));

			missingRBFiles.add(rbFile);

			createFiles(context, project, packageName, missingRBFiles, rbFileBuffer);
			setEnabled(false);

			Property modelElement = getModelElement().property(property().definition());

			modelElement.refresh();
		}

		return null;
	}

}