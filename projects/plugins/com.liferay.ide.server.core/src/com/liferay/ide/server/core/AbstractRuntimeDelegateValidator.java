package com.liferay.ide.server.core;



public abstract class AbstractRuntimeDelegateValidator implements IRuntimeDelegateValidator {

	protected String runtimeTypeId;

	public String getRuntimeTypeId() {
		return runtimeTypeId;
	}

	public void setRuntimeTypeId(String runtimeTypeId) {
		this.runtimeTypeId = runtimeTypeId;
	}

}
