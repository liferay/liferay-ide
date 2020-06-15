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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.WorkspaceProductInfo;
import com.liferay.ide.project.core.modules.BladeCLI;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Ethan Sun
 */
public class ProductVersionPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		if (_op != null) {
			SapphireUtil.detachListener(_op.property(NewLiferayWorkspaceOp.PROP_PRODUCT_CATEGORY), _listener);

			SapphireUtil.detachListener(_op.property(NewLiferayWorkspaceOp.PROP_SHOW_ALL_VERSION_PRODUCT), _listener);
		}

		super.dispose();
	}

	@Override
	protected void compute(Set<String> values) {
		if (ListUtil.isNotEmpty(_workspaceProducts)) {
			String category = get(_op.getProductCategory());

			List<String> productVersionList = _productInfo.getProductVersionList(category, _workspaceProducts);

			values.addAll(productVersionList);
		}
	}

	@Override
	protected void initPossibleValuesService() {
		_op = context(NewLiferayWorkspaceOp.class);

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				Job refreshWorkspaceProductJob = new Job("") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							String category = get(_op.getProductCategory());

							String version = get(_op.getProductVersion());

							boolean showAll = get(_op.getShowAllVersionProduct());

							_workspaceProducts = BladeCLI.getInitPromotedWorkspaceProduct(showAll);

							List<String> productVersionsList = _productInfo.getProductVersionList(
								category, _workspaceProducts);

							if (Objects.nonNull(version) && !productVersionsList.contains(version) &&
								ListUtil.isNotEmpty(productVersionsList)) {

								productVersionsList.sort(String::compareTo);

								_op.setProductVersion(productVersionsList.get(productVersionsList.size() - 1));
							}

							refresh();
						}
						catch (Exception e) {
							e.printStackTrace();
						}

						return Status.OK_STATUS;
					}

				};

				refreshWorkspaceProductJob.setSystem(true);

				refreshWorkspaceProductJob.schedule();
			}

		};

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_PRODUCT_CATEGORY), _listener);

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_SHOW_ALL_VERSION_PRODUCT), _listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private NewLiferayWorkspaceOp _op;
	private WorkspaceProductInfo _productInfo = WorkspaceProductInfo.getInstance();
	private String[] _workspaceProducts;

}