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

package com.liferay.blade.api;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * @author Gregory Amerson
 */
public interface Migration {

	int DETAIL_LONG = 1 << 2;

	int DETAIL_SHORT = 1 << 1;

	public List<Problem> findProblems(File projectDir, ProgressMonitor monitor);

	public List<Problem> findProblems(Set<File> files, ProgressMonitor monitor);

	public void reportProblems(List<Problem> problems, int detail, String format, Object... args);

}