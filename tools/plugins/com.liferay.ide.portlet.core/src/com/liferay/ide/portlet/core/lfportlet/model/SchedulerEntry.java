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

package com.liferay.ide.portlet.core.lfportlet.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

/**
 * @author Simon Jiang
 */
@Image(path = "images/elcl16/dates.gif")
public interface SchedulerEntry extends Element {

	public ElementType TYPE = new ElementType(SchedulerEntry.class);

	public ElementHandle<ITrigger> getPortletTrigger();

	public Value<String> getSchedulerDescription();

	public ReferenceValue<JavaTypeName, JavaType> getSchedulerEventListenerClass();

	public void setSchedulerDescription(String value);

	public void setSchedulerEventListenerClass(JavaTypeName value);

	public void setSchedulerEventListenerClass(String value);

	@Required
	@Type(base = ITrigger.class, possible = {CronTrigger.class, SimpleTrigger.class})
	@XmlElementBinding(mappings = {
		@XmlElementBinding.Mapping(element = "cron", type = CronTrigger.class),
		@XmlElementBinding.Mapping(element = "simple", type = SimpleTrigger.class)
	}, path = "trigger")
	public ElementProperty PROP_PORTLET_TRIGGER = new ElementProperty(TYPE, "PortletTrigger");

	@Label(standard = "SchedulerDescription")
	@Required
	@XmlBinding(path = "scheduler-description")
	public ValueProperty PROP_SCHEDULER_DESCRIPTION = new ValueProperty(TYPE, "SchedulerDescription");

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.messaging.MessageListener")
	@Label(standard = "SchedulerEventListenerClass")
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "scheduler-event-listener-class")
	public ValueProperty PROP_SCHEDULER_EVENT_LISTENER_CLASS = new ValueProperty(TYPE, "SchedulerEventListenerClass");

}