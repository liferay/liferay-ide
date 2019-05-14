package com.test;

import com.liferay.portal.kernel.util.DefaultServiceLoaderCondition;
import com.liferay.portal.kernel.util.ServiceLoaderCondition;

public class DroppedSupportOfServiceLoaderConditionTest {
	public static void main(String[] args) {
		DefaultServiceLoaderCondition dslc = New DefaultServiceLoaderCondition();
		
		dslc.isLoad(new URL(""));
	}
}