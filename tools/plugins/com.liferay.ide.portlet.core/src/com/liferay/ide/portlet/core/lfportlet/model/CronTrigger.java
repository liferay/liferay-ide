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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

/**
 * @author Simon Jiang
 */
public interface CronTrigger extends ICronTrigger {

	public ElementType TYPE = new ElementType(CronTrigger.class);

	public ElementHandle<ICronTrigger> getCronTrigger();

	@Required
	@Type(base = ICronTrigger.class, possible = {PropertyCronTrigger.class, CronTriggerValueTrigger.class})
	@XmlElementBinding(mappings = {
		@XmlElementBinding.Mapping(element = "property-key", type = PropertyCronTrigger.class),
		@XmlElementBinding.Mapping(element = "cron-trigger-value", type = CronTriggerValueTrigger.class)
	},
		path = "")
	public ElementProperty PROP_CRON_TRIGGER = new ElementProperty(TYPE, "CronTrigger");

}