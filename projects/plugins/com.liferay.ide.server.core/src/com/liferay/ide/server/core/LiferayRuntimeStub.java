package com.liferay.ide.server.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public class LiferayRuntimeStub implements ILiferayRuntimeStub {

	protected String runtimeStubTypeId;
	protected boolean isDefault;
	protected String name;

	public String getRuntimeStubTypeId() {
		return this.runtimeStubTypeId;
	}

	public String getName() {
		return this.name;
	}

	public IStatus validate() {
		return Status.OK_STATUS;
	}

	public void setRuntimeTypeId( String runtimeTypeId ) {
		this.runtimeStubTypeId = runtimeTypeId;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public void setDefault( boolean isDefault ) {
		this.isDefault = isDefault;
	}

}
