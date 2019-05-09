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

package com.liferay.ide.upgrade.plan.ui.internal.steps;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.upgrade.plan.core.UpgradeStep;
import com.liferay.ide.upgrade.plan.ui.Disposable;
import com.liferay.ide.upgrade.plan.ui.Perform;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Gregory Amerson
 */
public interface UpgradeItem extends Disposable, ISelectionProvider {

	public default ImageHyperlink createImageHyperlink(
		FormToolkit formToolkit, Composite parentComposite, Image image, Object data, String linkText, String jobText,
		Perform perform, UpgradeStep upgradeStep) {

		ImageHyperlink imageHyperlink = formToolkit.createImageHyperlink(parentComposite, SWT.NULL);

		imageHyperlink.setData(data);
		imageHyperlink.setEnabled(true);
		imageHyperlink.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		imageHyperlink.setImage(image);
		imageHyperlink.setText(linkText);
		imageHyperlink.setToolTipText(linkText);

		imageHyperlink.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					Job job = new Job(jobText) {

						@Override
						public boolean belongsTo(Object family) {
							return family.equals(LiferayCore.LIFERAY_JOB_FAMILY);
						}

						@Override
						protected IStatus run(IProgressMonitor progressMonitor) {
							return perform.apply(progressMonitor);
						}

					};

					job.schedule();
				}

			});

		return imageHyperlink;
	}

	public default ImageHyperlink createNoneJobImageHyperlink(
		FormToolkit formToolkit, Composite parentComposite, Image image, Object data, String linkText, String jobText,
		Perform perform, UpgradeStep upgradeStep) {

		ImageHyperlink imageHyperlink = formToolkit.createImageHyperlink(parentComposite, SWT.NULL);

		imageHyperlink.setData(data);
		imageHyperlink.setEnabled(true);
		imageHyperlink.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		imageHyperlink.setImage(image);
		imageHyperlink.setText(linkText);
		imageHyperlink.setToolTipText(linkText);

		imageHyperlink.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					perform.apply(null);
				}

			});

		return imageHyperlink;
	}

}