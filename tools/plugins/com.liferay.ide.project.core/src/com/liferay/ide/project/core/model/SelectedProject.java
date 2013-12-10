package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.ProjectNamesPossibleValuesService;
import com.liferay.ide.project.core.model.internal.ProjectReferenceService;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;


public interface SelectedProject extends Element
{
    ElementType TYPE = new ElementType( SelectedProject.class );

    @Reference( target = IProject.class )
    @Services
    (
        {
            @Service( impl = ProjectReferenceService.class ),
            @Service( impl = ProjectNamesPossibleValuesService.class )
        }
    )
    @Label( standard = "&project" )
    @Required
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

}
