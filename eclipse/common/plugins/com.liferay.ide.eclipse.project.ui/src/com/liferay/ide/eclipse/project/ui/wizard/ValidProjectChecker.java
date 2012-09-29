package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.project.ui.action.NewPluginProjectDropDownAction;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;


public class ValidProjectChecker
{
    private static final String ATT_ID = "id";

    private static final String ATT_NAME = "name";

    private static final String ATT_VALID_PROJECT_TYPES = "validProjectTypes";

    private static final String TAG_NEW_WIZARDS = "newWizards";

    private static final String TAG_PARAMETER = "parameter";

    private static final String TAG_VALUE = "value";

    private static final String TAG_WIZARD = "wizard";

    protected boolean isJsfPortlet = false;

    protected String validProjectTypes = null;

    protected String wizardId = null;

    public ValidProjectChecker( String wizardId )
    {
        this.wizardId = wizardId;
        init();
    }

    public void checkValidProjectTypes() {
        IProject[] projects = CoreUtil.getAllProjects();
        boolean hasValidProjectTypes = false;

        boolean hasJsfFacet = false;

        for( IProject project : projects ) {
            if( ProjectUtil.isLiferayProject( project ) ) {
                Set<IProjectFacetVersion> facets = ProjectUtil.getFacetedProject( project ).getProjectFacets();

                if( validProjectTypes != null && facets != null )
                {
                    String[] validTypes = validProjectTypes.split( "," );
                    for( String validProjectType : validTypes )
                    {
                        for( IProjectFacetVersion facet : facets )
                        {
                            String id = facet.getProjectFacet().getId();
                            if( isJsfPortlet && id.equals( "jst.jsf" )) {
                                hasJsfFacet = true;
                            }
                            if( id.startsWith( "liferay." ) && id.equals( "liferay." + validProjectType )) {
                                hasValidProjectTypes = true;
                            }
                        }
                    }
                }
            }
        }
        if(isJsfPortlet) {
            hasValidProjectTypes = hasJsfFacet && hasValidProjectTypes;
        }

        if(hasValidProjectTypes) {

        }
        else {
            Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion( Display.getDefault().getActiveShell(), "New Element",
                "There are no suitable Liferay projects available for this new element.\nDo you want"
                + " to open the \'New Liferay Project\' wizard now?" );

            if(openNewLiferayProjectWizard) {
                Action[] actions = NewPluginProjectDropDownAction.getNewProjectActions();

                if (actions.length > 0) {
                    actions[0].run();
                    this.checkValidProjectTypes();
                }
            }
        }
    }

    private String getValidProjectTypesFromConfig(IConfigurationElement config) {
        IConfigurationElement[] classElements = config.getChildren();

        if (classElements.length > 0) {
            for (IConfigurationElement classElement : classElements) {
                IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);

                for (IConfigurationElement paramElement : paramElements) {
                    if (ATT_VALID_PROJECT_TYPES.equals(paramElement.getAttribute(ATT_NAME))) {
                        return paramElement.getAttribute(TAG_VALUE);
                    }
                }
            }
        }

        return null;
    }

    protected void init()
    {
        if(wizardId != null && wizardId.equals( "com.liferay.ide.eclipse.portlet.jsf.ui.wizard.portlet" )) {
            setJsfPortlet( true );
        }
        
        IExtensionPoint extensionPoint =
                        Platform.getExtensionRegistry().getExtensionPoint(PlatformUI.PLUGIN_ID, TAG_NEW_WIZARDS);
        if (extensionPoint != null) {
            IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

            for (IConfigurationElement element : elements) {
                if (element.getName().equals(TAG_WIZARD) && element.getAttribute( ATT_ID ).equals( wizardId ) ) {
                    // getValidProjectTypesFromConfig( element )!=null && isLiferayArtifactWizard(element, "liferay_artifact")
                    setValidProjectTypes( getValidProjectTypesFromConfig( element ) );
                    break;
                }
            }
        }
    }

    public void setJsfPortlet( boolean isJsfPortlet )
    {
        this.isJsfPortlet = isJsfPortlet;
    }

    public void setValidProjectTypes(String validProjectTypes) {
        this.validProjectTypes = validProjectTypes;
    }
}
