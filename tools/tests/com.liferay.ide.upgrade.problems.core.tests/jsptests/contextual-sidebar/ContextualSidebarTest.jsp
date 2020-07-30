<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
--%>

<liferay-frontend:info-bar>
  <liferay-frontend:info-bar-buttons>
    <liferay-frontend:info-bar-sidenav-toggler-button
      icon="info-circle"
      label="my info"
    />
  </liferay-frontend:info-bar-buttons>
</liferay-frontend:info-bar>

<div class="closed container-fluid-1280 sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
    <liferay-frontend:sidebar-panel>
      <div>
      <h2>sidebar content</h2>
      <p>Here is some content</p>
      </div>
    </liferay-frontend:sidebar-panel>
    <liferay-frontend:contextual-sidebar>
    </liferay-frontend:contextual-sidebar>
</div>