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

package com.liferay.ide.server.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.internal.commons.notifications.ui.popup.NotificationPopup;
import org.eclipse.mylyn.internal.commons.notifications.ui.popup.PopupNotificationSink;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ImportGlobalSettingsNotificationSink extends PopupNotificationSink {

	public ImportGlobalSettingsNotificationSink() {
	}

	@Override
	public void showPopup() {
		if (_popup != null) {
			_popup.close();
		}

		IWorkbench workbench = PlatformUI.getWorkbench();

		Shell shell = new Shell(workbench.getDisplay());

		_popup = new NotificationPopup(shell) {

			@Override
			protected void createContentArea(Composite parent) {
				super.createContentArea(parent);

				_hookHyperlink(parent);
			}

			@Override
			protected String getPopupShellTitle() {
				return "Liferay IDE Notification";
			}

			private void _hookHyperlink(Composite parent) {
				Hyperlink hyperlink = null;

				for (Control child : parent.getChildren()) {
					if (child instanceof Hyperlink) {
						hyperlink = (Hyperlink)child;

						hyperlink.addHyperlinkListener(
							new IHyperlinkListener() {

								public void linkActivated(HyperlinkEvent e) {
									if (_popup != null) {
										_popup.closeFade();
									}
								}

								public void linkEntered(HyperlinkEvent e) {
								}

								public void linkExited(HyperlinkEvent e) {
								}

							});

						return;
					}
					else if (child instanceof Composite) {
						_hookHyperlink((Composite)child);
					}
				}
			}

		};

		_popup.setFadingEnabled(isAnimationsEnabled());

		List<AbstractNotification> toDisplay = new ArrayList<>(getNotifications());

		Collections.sort(toDisplay);

		_popup.setContents(toDisplay);

		getNotifications().clear();

		_popup.setBlockOnOpen(false);
		_popup.setDelayClose(60 * 1000);
		_popup.open();
	}

	private NotificationPopup _popup;

}