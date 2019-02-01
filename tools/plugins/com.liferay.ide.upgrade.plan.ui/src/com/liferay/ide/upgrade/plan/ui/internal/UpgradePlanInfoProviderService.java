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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;

import java.util.NoSuchElementException;

import org.osgi.service.component.annotations.Component;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * @author Gregory Amerson
 */
@Component
public class UpgradePlanInfoProviderService implements UpgradeInfoProvider {

	public UpgradePlanInfoProviderService() {
		_promiseFactory = new PromiseFactory(null);
	}

	@Override
	public Promise<String> getDetail(Object element) {
		Deferred<String> deferred = _promiseFactory.deferred();

		if (element instanceof UpgradeTaskStep) {
			_doUpgradeTaskStepDetail((UpgradeTaskStep)element, deferred);
		}
		else {
			deferred.fail(new NoSuchElementException());
		}

		return deferred.getPromise();
	}

	@Override
	public String getLabel(Object element) {
		if (element instanceof UpgradeTaskStep) {
			return _doUpgradeTaskStepLabel((UpgradeTaskStep)element);
		}

		return null;
	}

	@Override
	public boolean provides(Object element) {
		return element instanceof UpgradeTaskStep;
	}

	private void _doUpgradeTaskStepDetail(UpgradeTaskStep upgradeTaskStep, Deferred<String> deferred) {
		final String detail;

		String url = upgradeTaskStep.getUrl();

		if (CoreUtil.isNotNullOrEmpty(url)) {
			detail = url;
		}
		else {
			StringBuffer sb = new StringBuffer();

			sb.append(upgradeTaskStep.getTitle());

			sb.append("<br />");

			sb.append(upgradeTaskStep.getDescription());

			detail = sb.toString();
		}

		deferred.resolve(detail);
	}

	private String _doUpgradeTaskStepLabel(UpgradeTaskStep upgradeTaskStep) {
		return upgradeTaskStep.getTitle();
	}

	private final PromiseFactory _promiseFactory;

}