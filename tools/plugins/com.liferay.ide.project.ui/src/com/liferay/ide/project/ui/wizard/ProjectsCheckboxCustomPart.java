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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.sapphire.ElementList;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Simon Jiang
 */
public abstract class ProjectsCheckboxCustomPart extends AbstractCheckboxCustomPart {

	public class ProjectCheckboxElement extends CheckboxElement {
		public ProjectCheckboxElement(String name, String context, String location) {
			super(name, context);

			this.location = location;
		}

		public final String location;

	}

	public class SDKImportProjectsLabelProvider
		extends ElementLabelProvider implements IColorProvider, IStyledLabelProvider {

		public SDKImportProjectsLabelProvider() {
			_color_registry.put(_GREY_COLOR, new RGB(128, 128, 128));
			_greyed_styler = StyledString.createColorRegistryStyler(_GREY_COLOR, null);
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof ProjectCheckboxElement) {
				final String projectLocation = ((ProjectCheckboxElement)element).location;

				ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir(projectLocation);

				if (projectRecord != null) {
					IPath location = projectRecord.getProjectLocation();

					String suffix = ProjectUtil.getLiferayPluginType(location.toPortableString());

					return getImageRegistry().get(suffix);
				}
			}

			return null;
		}

		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof ProjectCheckboxElement) {
				final String srcLableString = ((ProjectCheckboxElement)element).context;
				final String projectName = ((ProjectCheckboxElement)element).name;

				final StyledString styled = new StyledString(projectName);

				return StyledCellLabelProvider.styleDecoratedString(srcLableString, _greyed_styler, styled);
			}

			return new StyledString(((ProjectCheckboxElement)element).context);
		}

		@Override
		protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
			imageRegistry.put(
				PluginType.portlet.name(),
				ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/portlet.png"));
			imageRegistry.put(
				PluginType.hook.name(),
				ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/hook.png"));
			imageRegistry.put(
				PluginType.layouttpl.name(),
				ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/layout.png"));
			imageRegistry.put(
				PluginType.servicebuilder.name(),
				ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/portlet.png"));
			imageRegistry.put(
				PluginType.ext.name(), ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/ext.png"));
			imageRegistry.put(
				PluginType.theme.name(),
				ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/theme.png"));
			imageRegistry.put(
				PluginType.web.name(), ProjectUI.imageDescriptorFromPlugin(ProjectUI.PLUGIN_ID, "/icons/e16/web.png"));
		}

		private static final String _GREY_COLOR = "sdk import projects";

		private final ColorRegistry _color_registry = JFaceResources.getColorRegistry();
		private final Styler _greyed_styler;

	}

	@Override
	protected void checkAndUpdateCheckboxElement() {
		List<ProjectCheckboxElement> checkboxElementList = getInitItemsList();

		checkboxElements = checkboxElementList.toArray(new ProjectCheckboxElement[checkboxElementList.size()]);

		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					checkBoxViewer.setInput(checkboxElements);

					ElementList<ProjectNamedItem> selectedElements = getSelectedElements();

					Iterator<ProjectNamedItem> iterator = selectedElements.iterator();

					while (iterator.hasNext()) {
						NamedItem projectItem = iterator.next();

						for (CheckboxElement checkboxElement : checkboxElements) {
							if (checkboxElement.name.equals(SapphireUtil.getContent(projectItem.getName()))) {
								checkBoxViewer.setChecked(checkboxElement, true);

								break;
							}
						}
					}

					updateValidation();
				}

			});
	}

	protected abstract ElementList<ProjectNamedItem> getCheckboxList();

	protected abstract List<ProjectCheckboxElement> getInitItemsList();

	protected abstract ElementList<ProjectNamedItem> getSelectedElements();

	@Override
	protected void handleCheckStateChangedEvent(CheckStateChangedEvent event) {
		if (checkBoxViewer.equals(event.getSource())) {
			final Object element = event.getElement();

			if (element instanceof CheckboxElement) {
				checkBoxViewer.setGrayed(element, false);
			}

			ElementList<ProjectNamedItem> selectedElements = getSelectedElements();

			if (selectedElements != null) {
				selectedElements.clear();

				for (ProjectCheckboxElement checkboxElement : checkboxElements) {
					if (checkBoxViewer.getChecked(checkboxElement)) {
						final ProjectNamedItem newProjectItem = selectedElements.insert();

						newProjectItem.setName(checkboxElement.name);
						newProjectItem.setExtDesc(checkboxElement.context);
						newProjectItem.setLocation(checkboxElement.location);
					}
				}
			}

			updateValidation();
		}
	}

	@Override
	protected void handleDeSelectAllEvent() {
		for (CheckboxElement checkboxElement : checkboxElements) {
			checkBoxViewer.setChecked(checkboxElement, false);
		}

		getCheckboxList().clear();
		updateValidation();
	}

	@Override
	protected void handleSelectAllEvent() {
		getCheckboxList().clear();
		ElementList<ProjectNamedItem> projectItems = getCheckboxList();

		for (ProjectCheckboxElement checkboxElement : checkboxElements) {
			checkBoxViewer.setChecked(checkboxElement, true);
			ProjectNamedItem projectItem = projectItems.insert();

			projectItem.setName(checkboxElement.name);
			projectItem.setExtDesc(checkboxElement.context);
			projectItem.setLocation(checkboxElement.location);
		}

		updateValidation();
	}

	protected ProjectCheckboxElement[] checkboxElements;

}