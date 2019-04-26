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

package com.liferay.test;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;


public class DLAppServiceUtilTest {

    public void moveFileEntryFromTrash() {
        DLAppServiceUtil.moveFileEntryFromTrash(null);
    }

    public void moveFileEntryToTrash() {
        DLAppServiceUtil.moveFileEntryToTrash(null);
    }

    public void moveFileShortcutFromTrash() {
        DLAppServiceUtil.moveFileShortcutFromTrash(null);
    }

    public void moveFileShortcutToTrash() {
        DLAppServiceUtil.moveFileShortcutToTrash(null);
    }

    public void moveFolderFromTrash() {
        DLAppServiceUtil.moveFolderFromTrash(null);
    }

    public void moveFolderToTrash() {
        DLAppServiceUtil.moveFolderToTrash(null);
    }

    public void restoreFileEntryFromTrash() {
        DLAppServiceUtil.restoreFileEntryFromTrash(null);
    }

    public void restoreFileShortcutFromTrash() {
        DLAppServiceUtil.restoreFileShortcutFromTrash(null);
    }

    public void restoreFolderFromTrash() {
        DLAppServiceUtil.restoreFolderFromTrash(null);
    }

    public void moveFileEntryToTrash() {
        DLAppLocalServiceUtil.moveFileEntryToTrash(null);
    }

    public void restoreFileEntryFromTrash() {
        DLAppLocalServiceUtil.restoreFileEntryFromTrash(null);
    }

}
