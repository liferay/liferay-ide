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

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;
import com.liferay.blade.upgrade.liferay70.XMLFileMigrator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.service.component.annotations.Component;
import org.w3c.dom.Text;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=xml",
		"problem.summary=The classes from package com.liferay.util.bridges.mvc in util-bridges.jar were moved to a new package com.liferay.portal.kernel.portlet.bridges.mvc in portal-service.jar.",
		"problem.tickets=LPS-50156",
		"problem.title=Moved MVCPortlet, ActionCommand and ActionCommandCache from util-bridges.jar to portal-service.jar",
		"problem.section=#moved-mvcportlet-actioncommand-and-actioncommandcache-from-util-bridges-jar",
		"implName=MVCPortletClassInPortletXML",
		"auto.correct=portlet-xml-portlet-class"
	},
	service = {
		AutoMigrator.class,
		FileMigrator.class
	}
)
@SuppressWarnings("restriction")
public class MVCPortletClassInPortletXML extends XMLFileMigrator implements AutoMigrator {

	private static final String KEY = "portlet-xml-portlet-class";

	@Override
	protected List<SearchResult> searchFile(File file, XMLFile xmlFileChecker) {

		if (!"portlet.xml".equals(file.getName())) {
			return Collections.emptyList();
		}

		final List<SearchResult> results = new ArrayList<>();

		Collection<SearchResult> tags = xmlFileChecker.findElement(
			"portlet-class", "com.liferay.util.bridges.mvc.MVCPortlet");

		for (SearchResult tagResult : tags ) {
			tagResult.autoCorrectContext = KEY;
		}

		results.addAll(tags);

		return results;
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int corrected = 0;
		IFile xmlFile = getXmlFile(file);
		IDOMModel xmlModel = null;

		if (xmlFile != null) {
			try {
				xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(xmlFile);

				List<IDOMElement> elementsToCorrect = new ArrayList<>();

				for (Problem problem : problems) {
					if (KEY.equals(problem.autoCorrectContext)) {
						IndexedRegion region = xmlModel.getIndexedRegion(problem.startOffset);

						if (region instanceof IDOMElement) {
							IDOMElement element = (IDOMElement) region;

							elementsToCorrect.add(element);
						}
					}
				}

				for (IDOMElement element : elementsToCorrect)  {
					xmlModel.aboutToChangeModel();

					removeChildren(element);

					Text textContent = element.getOwnerDocument().createTextNode(
							"com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet");

					element.appendChild(textContent);

					xmlModel.changedModel();

					corrected++;
				}

				xmlModel.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (xmlModel != null) {
					xmlModel.releaseFromEdit();
				}
			}
		}

        if( corrected > 0 && !xmlFile.getLocation().toFile().equals( file ) )
        {
            try(InputStream xmlFileContent = xmlFile.getContents())
            {
                Files.copy( xmlFileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING );
            }
            catch( IOException | CoreException e )
            {
                throw new AutoMigrateException( "Error writing corrected file.", e );
            }
        }

		return corrected;
	}

	private void removeChildren(IDOMElement element) {
		while (element.hasChildNodes()) {
			element.removeChild(element.getFirstChild());
		}
	}

}