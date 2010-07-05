package com.liferay.ide.eclipse.templates.core;

import org.apache.velocity.app.VelocityEngine;

public class TemplateModel {

	protected String bundleId;
	protected String id;
	protected String name;
	protected String resource;
	protected String templateFolder;
	protected VelocityEngine engine;

	public TemplateModel(
		String bundleId, String id, String name, String resource, String templateFolder, VelocityEngine velocityEngine) {
		super();
		this.bundleId = bundleId;
		this.id = id;
		this.name = name;
		this.resource = resource;
		this.templateFolder = templateFolder;
		this.engine = velocityEngine;
	}

	public VelocityEngine getEngine() {
		return engine;
	}

	public void setEngine(VelocityEngine engine) {
		this.engine = engine;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getResource() {
		return resource;
	}

	public String getBundleId() {
		return bundleId;
	}

}
