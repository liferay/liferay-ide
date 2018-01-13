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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.core.model.NotificationTransport;

import java.util.List;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 */
public class NotificationTypesDerivedValueService extends DerivedValueService {

	@Override
	protected String compute() {
		StringBuilder data = new StringBuilder();

		List<NotificationTransport> transports = context(Notification.class).getNotificationTransports();

		for (NotificationTransport transport : transports) {
			if (data.length() != 0) {
				data.append(',');
			}

			data.append(transport.getNotificationTransport().content());
		}

		return data.toString();
	}

	@Override
	protected void initDerivedValueService() {
		FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			public void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		context(Notification.class).attach(listener, "NotificationTransports");
		context(Notification.class).attach(listener, "NotificationTransports/*");
	}

}