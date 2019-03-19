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

package com.liferay.ide.upgrade.plan.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 */
public class ServicesLookup {

	public static <T> List<T> getOrderedServices(BundleContext bundleContext, Class<T> clazz, String filter) {
		try {
			Collection<ServiceReference<T>> serviceReferenceCollection = bundleContext.getServiceReferences(
				clazz, filter);

			List<ServiceReference<T>> serviceReferenceList = new ArrayList<>(serviceReferenceCollection);

			Collections.sort(
				serviceReferenceList,
				(srLeft, srRight) -> {
					try {
						Dictionary<String, Object> srLeftProperties = srLeft.getProperties();

						Object srLeftOrder = srLeftProperties.get("order");

						try {
							double srLeftDouble = Double.parseDouble(srLeftOrder.toString());

							Dictionary<String, Object> srRightProperties = srRight.getProperties();

							Object srRightOrder = srRightProperties.get("order");

							double srRightDouble = Double.parseDouble(srRightOrder.toString());

							return Double.compare(srLeftDouble, srRightDouble);
						}
						catch (NumberFormatException nfe) {
						}
					}
					catch (Throwable t) {
					}

					return -1;
				});

			Stream<ServiceReference<T>> stream = serviceReferenceList.stream();

			return stream.map(
				bundleContext::getServiceObjects
			).map(
				ServiceObjects::getService
			).collect(
				Collectors.toList()
			);
		}
		catch (InvalidSyntaxException ise) {
		}

		return Collections.emptyList();
	}

	public static <T> T getSingleService(Class<T> serviceClass) {
		return getSingleService(serviceClass, null);
	}

	public static <T> T getSingleService(Class<T> serviceClass, String filter) {
		Bundle bundle = FrameworkUtil.getBundle(ServicesLookup.class);

		BundleContext bundleContext = bundle.getBundleContext();

		try {
			Collection<ServiceReference<T>> serviceReferences = bundleContext.getServiceReferences(
				serviceClass, filter);

			if (!serviceReferences.isEmpty()) {
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