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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component
public class UpgradePlannerService implements UpgradePlanner {

	@Override
	public void addListener(UpgradeListener upgradeListener) {
		synchronized (this) {
			if (_upgradeListeners.contains(upgradeListener)) {
				return;
			}

			_upgradeListeners.add(upgradeListener);

			for (UpgradeEvent upgradeEvent : _upgradeEvents) {
				try {
					upgradeListener.onUpgradeEvent(upgradeEvent);
				}
				catch (Exception e) {
					UpgradePlanCorePlugin.logError("onUpgradeEvent error", e);
				}
			}
		}
	}

	public void dispatch(UpgradeEvent upgradeEvent) {
		Collection<UpgradeListener> upgradeListeners;

		synchronized (this) {
			upgradeListeners = Collections.unmodifiableCollection(_upgradeListeners);

			_upgradeEvents.add(upgradeEvent);
		}

		for (UpgradeListener upgradeListener : upgradeListeners) {
			try {
				upgradeListener.onUpgradeEvent(upgradeEvent);
			}
			catch (Exception e) {
				UpgradePlanCorePlugin.logError("onUpgradeEvent error", e);
			}
		}
	}

	@Override
	public void removeListener(UpgradeListener upgradeListener) {
		synchronized (this) {
			_upgradeListeners.remove(upgradeListener);
		}
	}

	@Override
	public UpgradePlan startUpgradePlan(String name) {
		UpgradePlan upgradePlan = new StandardUpgradePlan(name);

		UpgradeEvent upgradeEvent = new UpgradePlanStartedEvent(upgradePlan);

		dispatch(upgradeEvent);

		return upgradePlan;
	}

	private final List<UpgradeEvent> _upgradeEvents = new ArrayList<>();
	private final Set<UpgradeListener> _upgradeListeners = new LinkedHashSet<>();

}