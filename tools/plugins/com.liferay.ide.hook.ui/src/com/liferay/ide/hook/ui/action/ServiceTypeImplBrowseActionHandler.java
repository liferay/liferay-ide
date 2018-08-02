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

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.hook.core.model.ServiceWrapper;
import com.liferay.ide.hook.ui.HookUI;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.BrowseActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Gregory Amerson
 */
public final class ServiceTypeImplBrowseActionHandler extends BrowseActionHandler {

	public static final String ID = "ServiceTypeImpl.Browse.Java.Type";

	@Override
	public String browse(Presentation context) {
		Element element = getModelElement();
		Property property = property();
		IProject project = element.adapt(IProject.class);

		try {
			IJavaSearchScope scope = null;

			TypeSelectionExtension extension = null;

			if ("type".equals(_kind)) {
				scope = SearchEngine.createJavaSearchScope(new IJavaProject[] {JavaCore.create(project)});

				extension = new TypeSelectionExtension() {

					@Override
					public ITypeInfoFilterExtension getFilterExtension() {
						return new ITypeInfoFilterExtension() {

							public boolean select(ITypeInfoRequestor typeInfoRequestor) {
								if (StringUtil.startsWith(typeInfoRequestor.getPackageName(), "com.liferay") &&
									StringUtil.endsWith(typeInfoRequestor.getTypeName(), "Service")) {

									return true;
								}

								return false;
							}

						};
					}

				};
			}
			else if ("impl".equals(_kind)) {
				String serviceType = _getServiceType(element);

				if (serviceType != null) {
					String wrapperType = serviceType + "Wrapper";

					IJavaProject javaProject = JavaCore.create(project);

					scope = SearchEngine.createHierarchyScope(javaProject.findType(wrapperType));
				}
				else {
					Shell shell = ((SwtPresentation)context).shell();

					MessageDialog.openInformation(shell, Msgs.serviceImplBrowse, Msgs.validServiceTypeProperty);

					return null;
				}
			}

			Shell shell = ((SwtPresentation)context).shell();

			SelectionDialog dlg = JavaUI.createTypeDialog(
				shell, null, scope, _browseDialogStyle, false, StringPool.DOUBLE_ASTERISK, extension);

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

		_kind = def.getParam("kind");

		if ("type".equals(_kind)) {
			_browseDialogStyle = IJavaElementSearchConstants.CONSIDER_INTERFACES;
		}
		else if ("impl".equals(_kind)) {
			_browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES;
		}
	}

	private String _getServiceType(Element element) {
		String retval = null;

		ServiceWrapper service = element.nearest(ServiceWrapper.class);

		JavaTypeName javaTypeName = SapphireUtil.getContent(service.getServiceType(), false);

		if (javaTypeName != null) {
			retval = javaTypeName.qualified();
		}

		return retval;
	}

	private int _browseDialogStyle;
	private String _kind;

	private static class Msgs extends NLS {

		public static String select;
		public static String serviceImplBrowse;
		public static String validServiceTypeProperty;

		static {
			initializeMessages(ServiceTypeImplBrowseActionHandler.class.getName(), Msgs.class);
		}

	}

}