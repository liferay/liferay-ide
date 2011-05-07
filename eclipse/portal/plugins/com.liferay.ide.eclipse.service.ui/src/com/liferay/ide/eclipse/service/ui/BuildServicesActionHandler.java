package com.liferay.ide.eclipse.service.ui;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.job.BuildServiceJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;


public class BuildServicesActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(SapphireRenderingContext context) {
		IFile file = context.getPart().getModelElement().adapt(IFile.class);

		if (file != null && file.exists()) {
			BuildServiceJob job = PortletCore.createBuildServiceJob(file);

			job.schedule();
		}

		return null;
	}

}
