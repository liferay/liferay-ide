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

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Moved Shopping File Uploads Portlet Properties to OSGi Configuration",
		"problem.summary=Moved Shopping File Uploads Portlet Properties to OSGi Configuration",
		"problem.tickets=LPS-69210",
		"problem.section=#moved-shopping-file-uploads-portlet-properties-to-osgi-configuration", "version=7.0"
	},
	service = FileMigrator.class
)
public class ShoppingFileUploadsPortletProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("shopping.cart.min.qty.multiple");
		properties.add("shopping.category.forward.to.car");
		properties.add("shopping.category.show.special.items");
		properties.add("shopping.item.show.availabilit");
		properties.add("shopping.image.small.max.size");
		properties.add("shopping.image.medium.max.size");
		properties.add("shopping.image.large.max.size");
		properties.add("shopping.image.extensions");
		properties.add("shopping.email.from.name");
		properties.add("shopping.email.from.address");
		properties.add("shopping.email.order.confirmation.enabled");
		properties.add("shopping.email.order.confirmation.subject");
		properties.add("shopping.email.order.confirmation.body");
		properties.add("shopping.email.order.shipping.enabled");
		properties.add("shopping.email.order.shipping.subject");
		properties.add("shopping.email.order.shipping.body");
		properties.add("shopping.order.comments.enabled");
	}

}