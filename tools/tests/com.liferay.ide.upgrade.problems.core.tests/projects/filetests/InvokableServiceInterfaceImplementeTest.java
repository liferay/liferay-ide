package com.test

import com.liferay.portal.kernel.service.InvokableService;
import com.liferay.portal.kernel.service.InvokableLocalService;

public class InvokableServiceTest implements InvokableService {
	@Override
	public Object invokeMethod(String name, String[] parameterTypes, Object[] arguments) throws Throwable{
		return null;
	}
}