package com.liferay.ide.project.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;


public interface SelectProjectsOp extends Element
{
    ElementType TYPE = new ElementType( SelectProjectsOp.class );


    // *** SelectedProjects ***

    @Type( base = SelectedProject.class )
    @Label( standard = "selected project" )
    ListProperty PROP_SELECTED_PROJECTS = new ListProperty( TYPE, "SelectedProjects" );

    ElementList<SelectedProject> getSelectedProjects();




}
