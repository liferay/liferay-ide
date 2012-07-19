package com.liferay.ide.templates.core;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;

public class TemplateModel {

	protected String bundleId;
	protected String id;
	protected String name;
	protected String resource;
	protected String templateFolder;
	protected VelocityEngine engine;
	private TemplateVariable[] vars;

	public TemplateModel(
		String bundleId, String id, String name, String resource, String templateFolder, VelocityEngine velocityEngine,
		TemplateVariable[] vars) {

		super();
		this.bundleId = bundleId;
		this.id = id;
		this.name = name;
		this.resource = resource;
		this.templateFolder = templateFolder;
		this.engine = velocityEngine;
		this.vars = vars;
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

	public String[] getRequiredVarNames() {
		List<String> reqVarNames = new ArrayList<String>();

		if (!CoreUtil.isNullOrEmpty(vars)) {
			for (TemplateVariable var : vars) {
				if (var.isRequired()) {
					reqVarNames.add(var.getName());
				}
			}
		}

		return reqVarNames.toArray(new String[0]);
	}

}
