/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Moved Shopping File Uploads Portlet Properties to OSGi Configuration",
		"problem.summary=Moved Shopping File Uploads Portlet Properties to OSGi Configuration",
		"problem.tickets=LPS-69210",
		"problem.section=#moved-shopping-file-uploads-portlet-properties-to-osgi-configuration",
		"implName=ShoppingFileUploadsPortletProperties"
	}, 
	service = FileMigrator.class
)
public class ShoppingFileUploadsPortletProperties
	extends PropertiesFileMigrator {

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
