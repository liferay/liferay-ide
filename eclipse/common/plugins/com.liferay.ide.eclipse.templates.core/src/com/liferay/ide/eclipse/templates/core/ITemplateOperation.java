package com.liferay.ide.eclipse.templates.core;

import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ITemplateOperation {

	public void setOutputFile(IFile file);

	public void execute(IProgressMonitor monitor)
		throws Exception;

	public boolean canExecute();

	public VelocityContext getContext();

}
