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

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;
import com.liferay.ide.server.ui.PluginsCustomContentProvider;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class WorkflowDefinitionsCustomContentProvider extends PluginsCustomContentProvider {

	public WorkflowDefinitionsCustomContentProvider() {
	}

	public void dispose() {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IServer) {
			return null;
		}
		else if (parentElement instanceof WorkflowDefinitionsFolder) {
			return ((WorkflowDefinitionsFolder)parentElement).getChildren();
		}

		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof IWorkspaceRoot) {
			return null;
		}

		return null;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getPipelinedChildren(Object parent, Set currentChildren) {
		if (parent instanceof IServer) {
			IServer server = (IServer)parent;

			/*
			 * IServerListener listener = new IServerListener() {

			 *
			 *
			 * @Override public void serverChanged( ServerEvent event ) { try {
			 * WorkflowDefinitionsCustomContentProvider.checkApiStatuses.
			 * remove( server.getId() ); refreshUI( (NavigatorContentService)
			 * getConfig().getService(), server ); } catch( Exception e) {} } };
			 * server.addServerListener( listener );
			 */
			if (server.getServerState() == IServer.STATE_STARTED) {
				WorkflowDefinitionsFolder definitionsNode = _workflowDefinitionFolders.get(server.getId());

				if (definitionsNode == null) {
					IStatus checkApiStatus = _checkApiStatuses.get(server.getId());

					if (checkApiStatus == null) {
						Job checkJob = new Job("Checking for Kaleo Designer API...") {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection(server);

									KaleoCore.updateKaleoConnectionSettings(
										KaleoUtil.getLiferayServer(server, monitor), kaleoConnection);

									kaleoConnection.getKaleoDefinitions();
									kaleoConnection.getKaleoDraftWorkflowDefinitions();

									WorkflowDefinitionsCustomContentProvider.this._checkApiStatuses.put(
										server.getId(), Status.OK_STATUS);

									refreshUI((NavigatorContentService)getConfig().getService(), server);
								}
								catch (Exception e) {
									WorkflowDefinitionsCustomContentProvider.this._checkApiStatuses.put(
										server.getId(), KaleoUI.createErrorStatus(e));
								}

								return Status.OK_STATUS;
							}

						};

						checkJob.schedule();
					}
					else {
						if (checkApiStatus.isOK()) {
							_insertDefinitionsNode(server, currentChildren);
							WorkflowDefinitionsCustomContentProvider.this._checkApiStatuses.put(server.getId(), null);
						}
						else {
							KaleoUI.logInfo("Kaleo Workflow API unavailable.", checkApiStatus);
						}
					}
				}
				else {
					if (!currentChildren.contains(definitionsNode)) {
						currentChildren.add(definitionsNode);

						// make sure children are cached.

						definitionsNode.getChildren();
					}
				}
			}
			else {
				_workflowDefinitionFolders.put(server.getId(), null);
				_checkApiStatuses.put(server.getId(), null);
			}
		}
	}

	public Object getPipelinedParent(Object object, Object suggestedParent) {
		if (object instanceof WorkflowDefinitionsFolder) {
			return ((WorkflowDefinitionsFolder)object).getParent();

			/*
			 * if ( ProjectUtil.isLiferayProject( project ) &&
			 * (definitionsContentNode != null) ) { return
			 * definitionsContentNode; }
			 */
		}
		/*
		 * else if ( anObject instanceof DefinitionsContainer &&
		 * anObject.equals( definitionsContentNode ) ) { return
		 * definitionsContentNode.getParent(); }
		 */
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof WorkflowDefinitionsFolder;
	}

	public boolean hasChildren(Object element, boolean currentHasChildren) {
		if (element instanceof IServer) {
			/*
			 * higher priority extension should consider Properties File
			 * extension's result
			 */
			if (currentHasChildren) {
				return true;
			}

			IServer server = (IServer)element;

			WorkflowDefinitionsFolder definitionsNode = _workflowDefinitionFolders.get(server.getId());

			if (definitionsNode != null) {
				return true;
			}

			if (ServerUtil.isLiferayRuntime(server)) {
				if (server.getServerState() == IServer.STATE_STARTED) {
					return true;
				}
			}
		}

		return currentHasChildren;
	}

	public boolean interceptRefresh(PipelinedViewerUpdate viewerUpdate) {
		for (Object refreshTarget : viewerUpdate.getRefreshTargets()) {
			if (refreshTarget instanceof IServer) {
				_clearStatuses((IServer)refreshTarget);
			}
		}

		return false;
	}

	public boolean interceptUpdate(PipelinedViewerUpdate viewerUpdate) {
		for (Object updateTarget : viewerUpdate.getRefreshTargets()) {
			if (updateTarget instanceof IServer) {
				_clearStatuses((IServer)updateTarget);
			}
		}

		return false;
	}

	protected void refreshUI(NavigatorContentService s, IServer server) {
		UIUtil.async(
			new Runnable() {

				public void run() {
					try {
						CommonViewer viewer = (CommonViewer)s.getViewer();

						viewer.refresh(true);
						viewer.setExpandedState(server, true);
					}
					catch (Exception e) {
					}
				}

			});
	}

	protected static final Object[] EMPTY = {};

	private void _clearStatuses(IServer server) {
		if (ServerUtil.isLiferayRuntime(server)) {
			if (server.getServerState() != IServer.STATE_STARTED) {
				_checkApiStatuses.put(server.getId(), null);
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void _insertDefinitionsNode(IServer server, Set currentChildren) {
		WorkflowDefinitionsFolder node = new WorkflowDefinitionsFolder(getConfig(), server);

		_workflowDefinitionFolders.put(server.getId(), node);

		currentChildren.add(node);
	}

	private final Map<String, IStatus> _checkApiStatuses = new HashMap<>();
	private final Map<String, WorkflowDefinitionsFolder> _workflowDefinitionFolders = new HashMap<>();

}