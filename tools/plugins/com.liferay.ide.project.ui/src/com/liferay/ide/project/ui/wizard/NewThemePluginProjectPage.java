package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.jst.j2ee.internal.wizard.J2EEComponentFacetCreationWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class NewThemePluginProjectPage extends J2EEComponentFacetCreationWizardPage
    implements IPluginProjectDataModelProperties
{
    protected Combo parentCombo;
    protected Combo templateFrameworkCombo;

    public NewThemePluginProjectPage( NewPluginProjectWizard wizard, IDataModel model )
    {
        super( model, "theme.page" ); //$NON-NLS-1$

        setWizard( wizard );
        setImageDescriptor( wizard.getDefaultPageImageDescriptor() );
        setTitle( Msgs.liferayThemePluginProject );
        setDescription( Msgs.selectThemeOptions );
    }

    @Override
    public boolean canFlipToNextPage()
    {
        return false;
    }

    private void createTemplateFrameworkGroup( Composite parent )
    {
        SWTUtil.createLabel( parent, "Theme template framework:", 1 ); //$NON-NLS-1$

        templateFrameworkCombo = new Combo( parent, SWT.READ_ONLY );
        templateFrameworkCombo.setLayoutData( gdhfill() );
        synchHelper.synchCombo( templateFrameworkCombo, THEME_TEMPLATE_FRAMEWORK, null );
    }

    private void createThemeParentGroup( Composite parent )
    {
        SWTUtil.createLabel( parent, "Theme parent:", 1 ); //$NON-NLS-1$

        parentCombo = new Combo( parent, SWT.READ_ONLY );
        parentCombo.setLayoutData( gdhfill() );
        synchHelper.synchCombo( parentCombo, THEME_PARENT, null );
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite top = SWTUtil.createTopComposite( parent, 2 );

        createThemeParentGroup( top );

        createTemplateFrameworkGroup( top );

        return top;
    }

    protected IDataModel getModel()
    {
        return model;
    }

    @Override
    protected String getModuleFacetID()
    {
        return IModuleConstants.JST_WEB_MODULE;
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        String[] validationPropertyNames = { THEME_TEMPLATE_FRAMEWORK };

        return validationPropertyNames;
    }

    @Override
    public NewPluginProjectWizard getWizard()
    {
        return (NewPluginProjectWizard) super.getWizard();
    }

    private static class Msgs extends NLS
    {
        public static String liferayThemePluginProject;
        public static String selectThemeOptions;

        static
        {
            initializeMessages( NewThemePluginProjectPage.class.getName(), Msgs.class );
        }
    }
}
