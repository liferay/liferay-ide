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

package com.liferay.ide.portlet.ui.action;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.ui.PortletUIPlugin;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.BrowseActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Simon Jiang
 */
public class HierarchyBrowseActionHandler extends BrowseActionHandler {

	public static final String ID = "Hierarchy.Browse.Java.Type";

	@Override
	public String browse(Presentation context) {
		Element element = getModelElement();

		Property property = property();

		IProject project = element.adapt(IProject.class);

		try {
			IJavaSearchScope scope = null;

			TypeSelectionExtension extension = null;

			String javaType = _getClassReferenceType(property);

			if (javaType != null) {
				IJavaProject javaProject = JavaCore.create(project);

				scope = SearchEngine.createHierarchyScope(javaProject.findType(javaType));
			}
			else {
				MessageDialog.openInformation(
					((SwtPresentation)context).shell(), Msgs.browseImplementation, Msgs.validClassImplProperty);

				return null;
			}

			SelectionDialog dlg = JavaUI.createTypeDialog(
				((SwtPresentation)context).shell(), null, scope, IJavaElementSearchConstants.CONSIDER_CLASSES, false,
				StringPool.DOUBLE_ASTERISK, extension);

			PropertyDef propertyDef = property.definition();

			String title = propertyDef.getLabel(true, CapitalizationType.TITLE_STYLE, false);

			dlg.setTitle(Msgs.select + title);

			if (dlg.open() == SelectionDialog.OK) {
				Object[] results = dlg.getResult();

				assert results != null && results.length == 1;

				if (results[0] instanceof IType) {
					return ((IType)results[0]).getFullyQualifiedName();
				}
			}
		}
		catch (JavaModelException jme) {
			PortletUIPlugin.logError(jme);
		}

		return null;
	}

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		setId(ID);
	}

	private String _getClassReferenceType(Property property) {
		PropertyDef propertyDef = property.definition();

		JavaTypeConstraint typeConstraint = propertyDef.getAnnotation(JavaTypeConstraint.class);

		String retval = Arrays.toString(typeConstraint.type());

		retval = retval.replaceAll("[\\[\\]\\s,]", "");

		return retval;
	}

	private static class Msgs extends NLS {

		public static String browseImplementation;
		public static String select;
		public static String validClassImplProperty;

		static {
			initializeMessages(HierarchyBrowseActionHandler.class.getName(), Msgs.class);
		}

	}

}