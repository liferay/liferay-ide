/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.plan.ui.internal;

import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 */
public class ServiceReferenceLookup {

	public static <T> T getSingleService(Class<T> serviceClass, String filter) {
		Bundle bundle = FrameworkUtil.getBundle(ServiceReferenceLookup.class);

		BundleContext bundleContext = bundle.getBundleContext();

		try {
			Collection<ServiceReference<T>> serviceReferences = bundleContext.getServiceReferences(
				serviceClass, filter);

			if (serviceReferences.size() > 0) {
				Iterator<ServiceReference<T>> iterator = serviceReferences.iterator();

				ServiceReference<T> serviceReference = iterator.next();

				return bundleContext.getService(serviceReference);
			}
		}
		catch (InvalidSyntaxException ise) {
		}

		return null;
	}

}