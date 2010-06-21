package com.liferay.ide.eclipse.project.core;

import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public abstract class AbstractProjectDefinition implements IProjectDefinition {

	protected String displayName;

	protected IFacetedProjectTemplate facetedProjectTemplate;

	protected String facetedProjectTemplateId;

	protected String facetId;

	protected IProjectFacet projectFacet;

	protected String shortName;

	public AbstractProjectDefinition() {
		super();
	}

	public String getDisplayName() {
		return displayName;
	}

	public IProjectFacet getFacet() {
		return this.projectFacet;
	}

	public IFacetedProjectTemplate getFacetedProjectTemplate() {
		return facetedProjectTemplate;
	}

	public String getFacetedProjectTemplateId() {
		return facetedProjectTemplateId;
	}

	public String getFacetId() {
		return facetId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setFacetedProjectTemplateId(String facetedProjectTemplateId) {
		this.facetedProjectTemplateId = facetedProjectTemplateId;
		this.facetedProjectTemplate = ProjectFacetsManager.getTemplate(facetedProjectTemplateId);
	}

	public void setFacetId(String facetId) {
		this.facetId = facetId;
		this.projectFacet = ProjectFacetsManager.getProjectFacet(facetId);
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
