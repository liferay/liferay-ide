/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Lock;
import com.liferay.portal.service.LockLocalServiceUtil;

import java.util.Date;

public class LockProtectedAction<T> {

	public LockProtectedAction(
		Class<?> clazz, String lockKey, long timeout, long retryDelay) {

		_className = clazz.getName();
		_lockKey = lockKey;
		_timeout = timeout;
		_retryDelay = retryDelay;
	}

	public T getReturnValue() {
		return _returnValue;
	}

	public void performAction() throws PortalException, SystemException {
		Lock lock = null;

		while (true) {
			try {
				lock = LockLocalServiceUtil.lock(
					_className, _lockKey, _lockKey);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to acquire lock. Retrying.");
				}

				continue;
			}

			if (lock.isNew()) {
				try {
					_returnValue = performProtectedAction();
				}
				finally {
					LockLocalServiceUtil.unlock(_className, _lockKey, _lockKey);
				}

				break;
			}

			Date createDate = lock.getCreateDate();

			if ((System.currentTimeMillis() - createDate.getTime()) >=
					_timeout) {

				LockLocalServiceUtil.unlock(
					_className, _lockKey, lock.getOwner());

				if (_log.isWarnEnabled()) {
					_log.warn("Removed lock " + lock + " due to timeout");
				}
			}
			else {
				try {
					Thread.sleep(_retryDelay);
				}
				catch (InterruptedException ie) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Interrupted while waiting to reacquire lock", ie);
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	protected T performProtectedAction()
		throws PortalException, SystemException {

		return null;
	}

	private static Log _log = LogFactoryUtil.getLog(LockProtectedAction.class);

	private String _className;
	private String _lockKey;
	private long _retryDelay;
	private T _returnValue;
	private long _timeout;

}