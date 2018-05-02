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

package com.liferay.ide.portlet.ui.navigator.actions;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.navigator.PortletNode;
import com.liferay.ide.portlet.ui.navigator.PortletResourcesRootNode;
import com.liferay.ide.portlet.ui.navigator.PortletsNode;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentNodeList;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentNodePart;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentOutline;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class OpenPortletResourceAction extends BaseSelectionListenerAction {

	public OpenPortletResourceAction() {
		super(_ACTION_MESSAGE);
	}

	@Override
	public void run() {
		if (isEnabled()) {
			IFile file = initEditorPart();

			if ((file != null) && file.exists()) {
				editorPart = openEditor(file);

				if ((editorPart != null) && selectedNode instanceof PortletNode) {
					selectAndRevealItem(editorPart);
					openPortletJavaClass(file);
				}
			}
		}
	}

	/**
	 * @param file
	 * @return
	 */
	protected IEditorDescriptor findEditor(IFile file) {
		IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();
		IContentType contentType = IDE.getContentType(file);

		IEditorDescriptor editorDescriptor = registry.getDefaultEditor(file.getName(), contentType);

		if (editorDescriptor == null) {
			return null; // no editor associated...
		}

		return editorDescriptor;
	}

	protected IFile initEditorPart() {
		IFile file = null;

		if (selectedNode instanceof PortletsNode) {
			PortletsNode portletsNode = (PortletsNode)selectedNode;

			PortletResourcesRootNode rootNode = portletsNode.getParent();

			file = ProjectUtil.getPortletXmlFile(rootNode.getProject());
		}
		else if (selectedNode instanceof PortletNode) {
			PortletNode portletNode = (PortletNode)selectedNode;

			PortletResourcesRootNode rootNode = portletNode.getParent().getParent();

			file = ProjectUtil.getPortletXmlFile(rootNode.getProject());
		}

		// Check to see if the editor part is already open

		if ((editorPart == null) && (file != null)) {
			IWorkbench workbench = PlatformUI.getWorkbench();

			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

			IEditorReference[] editorReferences = page.getEditorReferences();

			for (IEditorReference iEditorReference : editorReferences) {
				if (file.getName().equals(iEditorReference.getName())) {
					editorPart = iEditorReference.getEditor(false);
				}
			}
		}

		return file;
	}

	protected IEditorPart openEditor(IFile file) {
		IEditorDescriptor editorDescriptor = findEditor(file);
		IEditorPart editorPart = null;

		if (editorDescriptor != null) {
			IWorkbench workbench = PlatformUI.getWorkbench();

			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

			try {
				editorPart = page.findEditor(new FileEditorInput(file));

				if (editorPart == null) {
					editorPart = page.openEditor(new FileEditorInput(file), editorDescriptor.getId());
				}
			}
			catch (Exception e) {
				MessageDialog.openError(page.getWorkbenchWindow().getShell(), Msgs.errorOpeningFile, e.getMessage());
			}
		}

		return editorPart;
	}

	/**
	 * @param file
	 */
	protected void openPortletJavaClass(IFile file) {
		Element modelElement = ((PortletNode)selectedNode).getModel();

		if (modelElement instanceof Portlet) {
			Portlet portlet = (Portlet)modelElement;

			JavaTypeName portletClassFile = portlet.getPortletClass().content();

			Runnable run = new Runnable() {

				public void run() {
					IJavaProject project = JavaCore.create(file.getProject());

					String fullyQualifiedName = portletClassFile.qualified();

					try {
						IType type = project.findType(fullyQualifiedName);

						if ((type != null) && type.exists()) {
							IResource resource = type.getResource();

							if (resource instanceof IFile) {
								IFile javaFile = (IFile)resource;

								IEditorDescriptor editorDescriptor = findEditor(javaFile);

								IEditorPart editorPart = null;

								if (editorDescriptor != null) {
									IWorkbench workbench = PlatformUI.getWorkbench();

									IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

									try {
										editorPart = page.findEditor(new FileEditorInput(javaFile));

										if (editorPart == null) {
											editorPart = page.openEditor(
												new FileEditorInput(javaFile), editorDescriptor.getId());
										}
									}
									catch (Exception e) {
										MessageDialog.openError(
											page.getWorkbenchWindow().getShell(), Msgs.errorOpeningFile,
											e.getMessage());
									}
								}
							}
						}
					}
					catch (JavaModelException jme) {
						PortletUIPlugin.logError(jme);
					}
				}

			};

			Display.getDefault().asyncExec(run);
		}
	}

	/**
	 * @param editor
	 *            TODO: need to work on to fix to reveal the selected node
	 */
	protected void selectAndRevealItem(IEditorPart editorPart) {
		if (this.editorPart instanceof SapphireEditor) {
			SapphireEditorForXml editor = (SapphireEditorForXml)editorPart;

			PortletNode portletNavigatorNode = (PortletNode)selectedNode;

			Element selectedModelElement = portletNavigatorNode.getModel();

			if (selectedModelElement != null) {
				MasterDetailsEditorPage mdepDetailsEditorPage = (MasterDetailsEditorPage)editor.getActivePageInstance();

				if (mdepDetailsEditorPage != null) {
					MasterDetailsContentOutline contentOutline = mdepDetailsEditorPage.outline();

					MasterDetailsContentNodePart rootNode = contentOutline.getRoot();

					if (rootNode != null) {
						MasterDetailsContentNodeList nodes = rootNode.nodes();

						MasterDetailsContentNodePart portletAppNode = nodes.visible().get(0);

						MasterDetailsContentNodePart portletsNode = portletAppNode.findNode(_PORTLETS_NODE_LABEL);

						// TODO: Performance Check ???, cant we not have the shared model ?

						if (portletsNode != null) {
							if (selectedModelElement instanceof Portlet) {
								Portlet selectedPortlet = (Portlet)selectedModelElement;

								for (MasterDetailsContentNodePart childNode : portletsNode.nodes().visible()) {
									String selectedPortletName = selectedPortlet.getPortletName().content();

									if (childNode.getModelElement() instanceof Portlet) {
										Portlet mpContentNodePortlet = (Portlet)childNode.getModelElement();

										String mpContentNodePortletName =
											mpContentNodePortlet.getPortletName().content();

										if (selectedPortletName.equals(mpContentNodePortletName)) {
											childNode.select();
											childNode.setExpanded(true);

											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.size() == 1) {
			selectedNode = selection.getFirstElement();

			if (editorPart == null) {
				initEditorPart();
			}

			if ((editorPart != null) && selectedNode instanceof PortletNode) {
				selectAndRevealItem(editorPart);
			}

			return true;
		}

		return false;
	}

	protected IEditorPart editorPart;
	protected Object selectedNode;

	private static final String _ACTION_MESSAGE = Msgs.openPortletConfigurationFile;

	private static final String _PORTLETS_NODE_LABEL = Msgs.portlets;

	private static class Msgs extends NLS {

		public static String errorOpeningFile;
		public static String openPortletConfigurationFile;
		public static String portlets;

		static {
			initializeMessages(OpenPortletResourceAction.class.getName(), Msgs.class);
		}

	}

}