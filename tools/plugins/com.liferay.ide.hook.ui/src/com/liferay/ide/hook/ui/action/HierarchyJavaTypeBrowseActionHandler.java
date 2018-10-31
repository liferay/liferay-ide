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

import com.liferay.ide.hook.ui.HookUI;

import java.util.EnumSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.java.JavaTypeConstraintService;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.BrowseActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Gregory Amerson
 */
public final class HierarchyJavaTypeBrowseActionHandler extends BrowseActionHandler {

	public static final String ID = "Hierarchy.Browse.Java.Type";

	@Override
	public String browse(Presentation context) {
		Element element = getModelElement();

		Property property = property();

		IProject project = element.adapt(IProject.class);

		try {
			JavaTypeConstraintService typeService = property.service(JavaTypeConstraintService.class);

			EnumSet<JavaTypeKind> kinds = EnumSet.noneOf(JavaTypeKind.class);

			kinds.addAll(typeService.kinds());

			int browseDialogStyle = IJavaElementSearchConstants.CONSIDER_ALL_TYPES;
			int count = kinds.size();

			if (count == 1) {
				Iterator<JavaTypeKind> iterator = kinds.iterator();

				JavaTypeKind kind = iterator.next();

				switch (kind) {
					case CLASS:
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES;

						break;
					case ABSTRACT_CLASS:
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES;

						break;
					case INTERFACE:
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_INTERFACES;

						break;
					case ANNOTATION:
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;

						break;
					case ENUM:
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_ENUMS;

						break;
					default:
						throw new IllegalStateException();
				}
			}
			else if (count == 2) {
				if (kinds.contains(JavaTypeKind.CLASS) || kinds.contains(JavaTypeKind.ABSTRACT_CLASS)) {
					if (kinds.contains(JavaTypeKind.INTERFACE)) {
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES;
					}
					else if (kinds.contains(JavaTypeKind.ENUM)) {
						browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS;
					}
				}
			}

			IJavaSearchScope scope = null;

			IJavaProject javaProject = JavaCore.create(project);

			IType type = javaProject.findType(_typeName);

			if (type != null) {
				scope = SearchEngine.createHierarchyScope(type);
			}

			SwtPresentation swt = (SwtPresentation)context;

			SelectionDialog dlg = JavaUI.createTypeDialog(
				swt.shell(), null, scope, browseDialogStyle, false, _filter, null);

			PropertyDef propertyDef = property.definition();

			String title = propertyDef.getLabel(true, CapitalizationType.TITLE_STYLE, false);

			dlg.setTitle(Msgs.select + title);

			if (dlg.open() == SelectionDialog.OK) {
				Object[] results = dlg.getResult();

				assert (results != null) && (results.length == 1);

				if (results[0] instanceof IType) {
					return ((IType)results[0]).getFullyQualifiedName();
				}
			}
		}
		catch (JavaModelException jme) {
			HookUI.logError(jme);
		}

		return null;
	}

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		setId(ID);

		_typeName = def.getParam("type");
		_filter = def.getParam("filter");
	}

	private String _filter;
	private String _typeName;

	private static class Msgs extends NLS {

		public static String select;

		static {
			initializeMessages(HierarchyJavaTypeBrowseActionHandler.class.getName(), Msgs.class);
		}

	}

}