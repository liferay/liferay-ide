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

package com.liferay.ide.project.ui.modules.fragment.action;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.modules.fragment.OverrideFilePath;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Joye Luo
 */
public class AddFilesFromOSGiBundleAction extends PropertyEditorActionHandler {

	public AddFilesFromOSGiBundleAction() {
	}

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		final Element element = getModelElement();

		final Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refreshEnablementState();
			}

		};

		element.attach(listener, NewModuleFragmentOp.PROP_HOST_OSGI_BUNDLE.name());
		attach(
			new Listener() {

				@Override
				public void handle(final Event event) {
					if (event instanceof DisposeEvent) {
						element.detach(listener, NewModuleFragmentOp.PROP_HOST_OSGI_BUNDLE.name());
					}
				}

			});
	}

	@Override
	protected boolean computeEnablementState() {
		boolean enabled = false;

		NewModuleFragmentOp op = getModelElement().nearest(NewModuleFragmentOp.class);

		String hostOsgiBundle = op.getHostOsgiBundle().content();

		if (hostOsgiBundle != null) {
			enabled = true;
		}

		return enabled;
	}

	@Override
	protected Object run(Presentation context) {
		Element modelElement = context.part().getModelElement();

		final NewModuleFragmentOp op = modelElement.nearest(NewModuleFragmentOp.class);

		final ElementList<OverrideFilePath> currentFiles = op.getOverrideFiles();

		final String projectName = op.getProjectName().content();

		final OSGiBundleFileSelectionDialog dialog = new OSGiBundleFileSelectionDialog(null, currentFiles, projectName);

		final String runtimeName = op.getLiferayRuntimeName().content();

		final IRuntime runtime = ServerUtil.getRuntime(runtimeName);

		final IPath tempLocation = ProjectCore.getDefault().getStateLocation();

		dialog.setTitle("Add files from OSGi bundle to override");

		String currentOSGiBundle = op.getHostOsgiBundle().content();

		if (!currentOSGiBundle.endsWith("jar")) {
			currentOSGiBundle = currentOSGiBundle + ".jar";
		}

		ServerUtil.getModuleFileFrom70Server(runtime, currentOSGiBundle, tempLocation);

		File module = tempLocation.append(currentOSGiBundle).toFile();

		if(FileUtil.exists(module)) {
			dialog.setInput(module);
		}

		if (dialog.open() == Window.OK) {
			Object[] selected = dialog.getResult();

			for (int i = 0; i < selected.length; i++) {
				OverrideFilePath file = op.getOverrideFiles().insert();

				file.setValue(selected[i].toString());
			}
		}

		return Status.createOkStatus();
	}

}