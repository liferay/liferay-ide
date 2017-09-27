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

package com.liferay.blade.test.deprecatedmethods;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.test.apichanges.APITestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class DeprecatedMethodsTest extends APITestBase {

    @Override
    public int getExpectedNumber() {
        return 60;
    }

    @Override
    public String getImplClassName() {
        return "DeprecatedMethodsMigrator";
    }

    @Override
    public File getTestFile() {
        return new File("projects/deprecated-methods-test/PortalMockFactory.java");
    }

    final File deprecatedMethods61TestFile = new File("projects/deprecated-methods-test/AssetVocabularyServiceSoap.java");

    @Test
    public void deprecatedMethods61TestFile() throws Exception {
        FileMigrator fmigrator = context.getService(fileMigrators[0]);

        List<Problem> problems = fmigrator.analyze(deprecatedMethods61TestFile);

        context.ungetService(fileMigrators[0]);

        assertNotNull(problems);
        assertEquals(4, problems.size());
    }

    final File deprecatedMethodsNoneVersionTestFile = new File("projects/deprecated-methods-test/WebServerServlet.java");

    @Test
    public void deprecatedMethodsNoneVersionTestFile() throws Exception {
        FileMigrator fmigrator = context.getService(fileMigrators[0]);

        List<Problem> problems = fmigrator.analyze(deprecatedMethodsNoneVersionTestFile);

        context.ungetService(fileMigrators[0]);

        assertNotNull(problems);
        assertEquals(2, problems.size());
    }

}
