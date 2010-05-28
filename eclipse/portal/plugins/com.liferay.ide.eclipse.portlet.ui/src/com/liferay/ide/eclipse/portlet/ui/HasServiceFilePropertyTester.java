package com.liferay.ide.eclipse.portlet.ui;

import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.IPortalConstants;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;


public class HasServiceFilePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject) {
			IProject project = (IProject) receiver;

			boolean isLiferayProject = ProjectUtil.isLiferayProject(project);

			if (isLiferayProject) {
				IFolder docroot = PortletUtil.getDocroot(project);

				if (docroot != null && docroot.exists()) {
					IFile serviceFile = docroot.getFile("WEB-INF/" + IPortalConstants.LIFERAY_SERVICE_BUILDER_XML_FILE);

					if (serviceFile.exists()) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
