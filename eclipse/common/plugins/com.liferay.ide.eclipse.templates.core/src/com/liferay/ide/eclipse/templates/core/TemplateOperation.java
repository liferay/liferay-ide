package com.liferay.ide.eclipse.templates.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;


public class TemplateOperation implements ITemplateOperation {

	protected VelocityContext context;
	protected TemplateModel model;
	protected StringBuffer outputBuffer;
	protected IFile outputFile;
	protected Template template;

	public TemplateOperation(TemplateModel model) {
		super();
		this.model = model;
	}

	public boolean canExecute() {
		try {
			if ((this.outputFile == null && this.outputBuffer == null) || this.model == null || getTemplate() == null) {
				return false;
			}

			String[] names = model.getRequiredVarNames();

			if (!CoreUtil.isNullOrEmpty(names)) {
				for (String name : names) {
					if (!(getContext().containsKey(name))) {
						TemplatesCore.logError("Could not execute template operation: context var " + name +
							" not found.");
						return false;
					}
				}
			}

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public void execute(IProgressMonitor monitor)
		throws Exception {
		if (!canExecute()) {
			return;
		}

		StringWriter writer = new StringWriter();
		getTemplate().merge(getContext(), writer);
		String result = writer.toString();

		if (this.outputFile != null) {
			if (this.outputFile.exists()) {
				this.outputFile.setContents(new ByteArrayInputStream(result.getBytes()), true, true, monitor);
			}
			else {
				this.outputFile.create(new ByteArrayInputStream(result.getBytes()), true, monitor);
			}
		}
		else if (this.outputBuffer != null) {
			this.outputBuffer.delete(0, this.outputBuffer.length());
			this.outputBuffer.append(result);
		}
	}

	public VelocityContext getContext() {
		if (context == null) {
			context = createContext();
		}

		return context;
	}

	public void setOutputBuffer(StringBuffer buffer) {
		this.outputBuffer = buffer;
	}

	public void setOutputFile(IFile file) {
		this.outputFile = file;
	}

	protected VelocityContext createContext() {
		return new VelocityContext();
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
}
