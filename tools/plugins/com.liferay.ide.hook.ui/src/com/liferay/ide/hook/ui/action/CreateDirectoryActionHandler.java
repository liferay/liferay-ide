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

package com.liferay.ide.hook.ui.action;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.ui.HookUI;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.sapphire.ui.forms.PropertyEditorCondition;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;

/**
 * @author Gregory Amerson
 */
public class CreateDirectoryActionHandler extends PropertyEditorActionHandler {

	public CreateDirectoryActionHandler() {
	}

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		Element element = getModelElement();
		ValueProperty property = (ValueProperty)property().definition();

		Listener listener = new FilteredListener<PropertyEvent>() {

			@Override
			public void handleTypedEvent(PropertyEvent event) {
				refreshEnablementState();
			}

		};

		element.attach(listener, property.name());

		attach(
			new Listener() {

				@Override
				public void handle(Event event) {
					if (event instanceof DisposeEvent) {
						element.detach(listener, property.name());
					}
				}

			});
	}

	public static class Condition extends PropertyEditorCondition {

		@Override
		protected boolean evaluate(PropertyEditorPart part) {
			Property property = part.property();
			Element element = part.getModelElement();

			PropertyDef propertyDef = property.definition();

			if ((propertyDef instanceof ValueProperty) && (element != null) && propertyDef.isOfType(Path.class)) {
				ValidFileSystemResourceType typeAnnotation = propertyDef.getAnnotation(
					ValidFileSystemResourceType.class);

				if ((typeAnnotation != null) && (typeAnnotation.value() == FileSystemResourceType.FOLDER)) {
					return true;
				}
			}

			return false;
		}

	}

	@Override
	protected boolean computeEnablementState() {
		boolean enabled = super.computeEnablementState();

		if (enabled) {
			@SuppressWarnings("unchecked")
			Value<Path> value = (Value<Path>)getModelElement().property(property().definition());

			Path path = value.content();

			RelativePathService service = property().service(RelativePathService.class);

			Path absolutePath = service.convertToAbsolute(path);

			if (absolutePath != null) {
				File file = absolutePath.toFile();

				enabled = !file.exists();
			}
		}

		return enabled;
	}

	@Override
	protected Object run(Presentation context) {
		try {
			Element element = getModelElement();

			IProject project = element.adapt(IProject.class);

			CustomJspDir customJspDir = (CustomJspDir)element;

			Path customJspDirValue = SapphireUtil.getContent(customJspDir.getValue(), false);

			if (customJspDirValue == null) {
				customJspDirValue = SapphireUtil.getContent(customJspDir.getValue());

				customJspDir.setValue(customJspDirValue);
			}

			customJspDir.setValue(customJspDirValue);

			RelativePathService service = property().service(RelativePathService.class);

			Path absolutePath = service.convertToAbsolute(customJspDirValue);

			if (FileUtil.notExists(absolutePath)) {
				IWebProject webproject = LiferayCore.create(IWebProject.class, project);

				if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
					IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

					IFolder customJspFolder = defaultDocroot.getFolder(
						new org.eclipse.core.runtime.Path(customJspDirValue.toPortableString()));

					CoreUtil.makeFolders(customJspFolder);

					// force a refresh of validation

					customJspDir.setValue((Path)null);
					customJspDir.setValue(customJspDirValue);

					refreshEnablementState();
				}
			}
		}
		catch (Exception e) {
			HookUI.logError(e);
		}

		return null;
	}

}