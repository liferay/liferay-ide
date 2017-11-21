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

package com.liferay.ide.kaleo.core.op.internal;

import static org.eclipse.sapphire.modeling.Status.createErrorStatus;
import static org.eclipse.sapphire.modeling.Status.createOkStatus;

import com.liferay.ide.kaleo.core.model.User;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class UserValidationService extends ValidationService {

	@Override
	public Status compute() {
		User user = context(User.class);

		if (user != null) {
			int count = 0;

			Value<Integer> id = user.getUserId();

			boolean userId = false;

			if (id.content() != null) {
				userId = true;
			}

			Value<String> userScreenName = user.getScreenName();

			boolean screenName = false;

			if (userScreenName.content() != null) {
				screenName = true;
			}

			Value<String> userEmailAddress = user.getEmailAddress();

			boolean emailAddress = false;

			if (userEmailAddress.content() != null) {
				emailAddress = true;
			}

			if (userId) {
				count++;
			}

			if (screenName) {
				count++;
			}

			if (emailAddress) {
				count++;
			}

			if (count > 1) {
				return createErrorStatus("Only specify one of the three user fields.");
			}
		}

		return createOkStatus();
	}

	@Override
	public void dispose() {
		User user = context(User.class);

		if (user != null) {
			user.detach(_listener);
		}
	}

	@Override
	protected void initValidationService() {
		User user = context(User.class);

		if (user != null) {
			_listener = new FilteredListener<PropertyContentEvent>() {

				@Override
				protected void handleTypedEvent(PropertyContentEvent event) {
					refresh();
				}

			};

			user.attach(_listener, "*");
		}
	}

	private Listener _listener;

}