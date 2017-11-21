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

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Value;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class TransitionTargetListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property property = event.property();

		Transition transition = property.nearest(Transition.class);

		if (transition != null) {
			ReferenceValue<String, Node> targe = transition.getTarget();

			Value<String> transitionName = transition.getName();

			if ((targe.content() != null) && (transitionName.content(false) == null)) {
				String targetName = targe.content();

				String defaultName = targetName;

				Set<String> existingNames = new HashSet<>();

				CanTransition camtransition = transition.nearest(CanTransition.class);

				for (Transition t : camtransition.getTransitions()) {
					Value<String> tName = t.getName();

					if (tName.content() != null) {
						existingNames.add(tName.content());
					}
				}

				int count = 1;

				while (existingNames.contains(defaultName)) {
					defaultName = targetName + "_" + count++;
				}

				transition.setName(defaultName);
			}
		}
	}

}