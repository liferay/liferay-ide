package com.liferay.ide.project.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;


public class NewElementWizardAction extends NewWizardAction
{
    protected String validProjectTypes = null;

    public NewElementWizardAction( IConfigurationElement element )
    {
        super( element );
        setValidProjectTypes(getValidProjectTypesFromConfig(fConfigurationElement));
    }

    public String getValidProjectTypes() {
        return validProjectTypes;
    }

    private String getValidProjectTypesFromConfig(IConfigurationElement config) {
        IConfigurationElement[] classElements = config.getChildren();

        if (classElements.length > 0) {
            for (IConfigurationElement classElement : classElements) {
                IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

                for (IConfigurationElement paramElement : paramElements) {
                    if (ATT_VALID_PROJECT_TYPES.equals(paramElement.getAttribute(TAG_NAME))) {
                        return paramElement.getAttribute(TAG_VALUE);
                    }
                }
            }
        }

        return null;
    }

    public void run() {
        IProject[] projects = CoreUtil.getAllProjects();
        boolean hasValidProjectTypes = false;

        for( IProject project : projects ) {
            if( ProjectUtil.isLiferayFacetedProject( project ) ) {
                Set<IProjectFacetVersion> facets = ProjectUtil.getFacetedProject( project ).getProjectFacets();

                if( validProjectTypes != null && facets != null)
                {
                    String[] validTypes = validProjectTypes.split( StringPool.COMMA );
                    for( String validProjectType : validTypes )
                    {
                        for( IProjectFacetVersion facet : facets )
                        {
                            String id = facet.getProjectFacet().getId();
                            if(id.startsWith( "liferay." ) && id.equals( "liferay." + validProjectType )) {  //$NON-NLS-1$//$NON-NLS-2$
                                hasValidProjectTypes = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(hasValidProjectTypes) {
            super.run();
        }
        else {
            Shell shell = getShell();
            Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion( shell, Msgs.newElement,
                Msgs.noSuitableLiferayProjects );

            if(openNewLiferayProjectWizard) {
                Action[] actions = NewPluginProjectDropDownAction.getNewProjectActions();

                if (actions.length > 0) {
                    actions[0].run();
                    this.run();
                }
            }
        }
    }

    public void setValidProjectTypes(String validProjectTypes) {
        this.validProjectTypes = validProjectTypes;
    }

    private static class Msgs extends NLS
    {
        public static String newElement;
        public static String noSuitableLiferayProjects;

        static
        {
            initializeMessages( NewElementWizardAction.class.getName(), Msgs.class );
        }
    }
}
