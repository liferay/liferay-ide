/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class ServiceUtil
{
  private static Map<Object, ServiceReference<?>> services = new IdentityHashMap<Object, ServiceReference<?>>();

  private ServiceUtil()
  {
  }

  public static <T> T getServiceOrNull(BundleContext bundleContext, Class<T> serviceClass)
  {
    try
    {
      return getService(bundleContext, serviceClass);
    }
    catch (MissingServiceException ex)
    {
      return null;
    }
  }

  public static <T> T getService(BundleContext bundleContext, Class<T> serviceClass) throws MissingServiceException
  {
    String serviceName = serviceClass.getName();
    ServiceReference<?> serviceRef = bundleContext.getServiceReference(serviceName);
    if (serviceRef == null)
    {
      throw new MissingServiceException("Missing OSGi service " + serviceName);
    }

    @SuppressWarnings("unchecked")
    T service = (T)bundleContext.getService(serviceRef);
    if (service == null)
    {
      throw new MissingServiceException("Missing OSGi service " + serviceName);
    }

    services.put(service, serviceRef);
    return service;
  }

  public static void ungetService(BundleContext bundleContext, Object service)
  {
    if (service != null)
    {
      ServiceReference<?> serviceRef = services.remove(service);
      if (serviceRef != null)
      {
        bundleContext.ungetService(serviceRef);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MissingServiceException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    public MissingServiceException(String s)
    {
      super(s);
    }
  }
}
