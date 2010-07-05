package com.liferay.ide.eclipse.templates.core;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;


public class TemplateOperation implements ITemplateOperation {

	protected TemplateModel model;
	protected IFile outputFile;
	protected VelocityContext context;
	protected Template template;

	public TemplateOperation(TemplateModel model) {
		super();
		this.model = model;
	}

	public void setOutputFile(IFile file) {
		this.outputFile = file;
	}

	public VelocityContext getContext() {
		if (context == null) {
			context = createContext();
		}

		return context;
	}

	protected VelocityContext createContext() {
		return new VelocityContext();
	}

	public void execute(IProgressMonitor monitor)
		throws Exception {
		if (!canExecute()) {
			return;
		}

		StringWriter writer = new StringWriter();
		getTemplate().merge(getContext(), writer);
		String result = writer.toString().replaceAll("_#_", "\\$");

		if (this.outputFile.exists()) {
			this.outputFile.setContents(new ByteArrayInputStream(result.getBytes()), true, true, monitor);
		}
		else {
			this.outputFile.create(new ByteArrayInputStream(result.getBytes()), true, monitor);
		}
	}

	protected Template getTemplate()
		throws Exception {

		if (this.model == null) {
			return null;
		}

		if (template == null) {
			template = this.model.getEngine().getTemplate(this.model.getResource());
		}

		return template;
	}

	public boolean canExecute() {
		try {
			return this.outputFile != null && this.model != null && getTemplate() != null;
		}
		catch (Exception e) {
			return false;
		}
	}


}
