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

package com.liferay.ide.kaleo.ui.diagram;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Scriptable;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ActionsDiagramNodeEditHandlerFactory extends SapphireActionHandlerFactory {

	@Override
	public List<SapphireActionHandler> create() {
		ListFactory<SapphireActionHandler> factory = ListFactory.start();

		Element element = getElement();

		if (element == null) {
			return factory.result();
		}

		ElementList<Action> actions = getActions();

		if (_listener == null) {
			_listener = new FilteredListener<PropertyEvent>() {

				@Override
				public void handleTypedEvent(PropertyEvent event) {
					broadcast(new Event());
				}

			};
		}

		element.attach(_listener, getListPropertyName());

		for (Action action : actions) {
			action.getName().attach(_listener);

			factory.add(
				new ScriptableOpenActionHandler() {

					@Override
					public void init(SapphireAction sapphireAction, ActionHandlerDef def) {
						super.init(sapphireAction, def);

						String name = action.getName().content(true);

						setLabel(empty(name) ? "<null>" : name);

						addImage(Action.TYPE.image());
					}

					@Override
					protected Scriptable scriptable(Presentation context) {
						return action;
					}

				});
		}

		return factory.result();
	}

	@Override
	public void dispose() {
		super.dispose();

		Element element = getElement();

		element.detach(_listener, getListPropertyName());
	}

	protected ElementList<Action> getActions() {
		ElementList<Action> actions = null;

		ActionTimer actionTimer = getModelElement().nearest(ActionTimer.class);

		if (actionTimer != null) {
			actions = actionTimer.getActions();
		}

		return actions;
	}

	protected Element getElement() {
		return getModelElement().nearest(ActionTimer.class);
	}

	protected String getListPropertyName() {
		return ActionTimer.PROP_ACTIONS.name();
	}

	private Listener _listener;

}