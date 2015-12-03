/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kay-Uwe Graw - initial API and implementation

 *******************************************************************************/

package com.liferay.ide.ui.tests.swtbot.page;

/**
 * interface for a page object representing a wizard
 *
 * @author Kay-Uwe Graw &lt;kugraw [at] web [dot] de&gt;
 */
public interface IWizardPageObject extends ICancelPageObject
{

    /**
     * click the finish button
     */
    public void finish();

    /**
     * click the back button
     */
    public void back();

    /**
     * click the next button
     */
    public void next();

    /**
     * set the current title of the wizard the title of a wizard can change during the input of the data and the page
     * object must know the correct current shell title otherwise the finish method will wait for the wrong shell to be
     * closed
     *
     * @param title
     *            - the new title of the wizard represented by the page object
     */
    public void setTitle( String title );
}
