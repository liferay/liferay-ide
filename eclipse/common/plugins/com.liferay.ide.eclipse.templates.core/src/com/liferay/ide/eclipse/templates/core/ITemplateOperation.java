package com.liferay.ide.eclipse.templates.core;

import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ITemplateOperation {

	public boolean canExecute();

	public void execute(IProgressMonitor monitor)
		throws Exception;

	public VelocityContext getContext();

	public void setOutputBuffer(StringBuffer buffer);

	public void setOutputFile(IFile file);

}
