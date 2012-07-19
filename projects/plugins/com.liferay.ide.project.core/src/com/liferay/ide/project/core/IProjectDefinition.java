package com.liferay.ide.project.core;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

public interface IProjectDefinition {

	public static final String ID = "com.liferay.ide.project.core.liferayProjects";

	public String getDisplayName();

	public IProjectFacet getFacet();

	public IFacetedProjectTemplate getFacetedProjectTemplate();

	public String getFacetId();

	public int getMenuIndex();

	public String getShortName();

	public void setupNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject);

}
