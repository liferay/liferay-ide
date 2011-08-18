package com.liferay.ide.eclipse.server.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.model.RuntimeDelegate;


public interface IRuntimeDelegateValidator {

	String ID = "com.liferay.ide.eclipse.server.core.runtimeDelegateValidators";

	IStatus validateRuntimeDelegate(RuntimeDelegate runtimeDelegate);

	String getRuntimeTypeId();

}
