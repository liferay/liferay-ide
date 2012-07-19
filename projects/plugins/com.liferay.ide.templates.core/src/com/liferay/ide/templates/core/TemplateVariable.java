package com.liferay.ide.templates.core;

import com.liferay.ide.core.util.CoreUtil;


public class TemplateVariable {

	private String name;

	private boolean required;

	public TemplateVariable(String varName, String reqVal) {
		this.name = varName;

		if (CoreUtil.isNullOrEmpty(reqVal)) {
			this.required = Boolean.FALSE;
		}
		else {
			this.required = Boolean.parseBoolean(reqVal);
		}
	}

	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}

}
