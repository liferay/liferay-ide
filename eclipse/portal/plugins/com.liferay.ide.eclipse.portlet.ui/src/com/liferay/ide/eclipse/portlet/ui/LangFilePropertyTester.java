package com.liferay.ide.eclipse.portlet.ui;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;


public class LangFilePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IFile) {
			IFile file = (IFile) receiver;

			if (file.exists() && file.getName().endsWith(".properties")) {
				boolean isLiferayProject = ProjectUtil.isLiferayProject(file.getProject());

				return isLiferayProject;
			}
		}

		return false;
	}

}
