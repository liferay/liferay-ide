package com.liferay.ide.velocity.ui;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

/**
 */
public class VeloContextType extends TemplateContextType
{

    /** This context's id */
	public static final String XML_CONTEXT_TYPE = "com.liferay.ide.velocity.ui.contextType1"; //$NON-NLS-1$

    /**
     * Creates a new XML context type.
     */
    public VeloContextType()
    {
        addGlobalResolvers();
    }

    private void addGlobalResolvers()
    {
        addResolver(new GlobalTemplateVariables.Cursor());
        addResolver(new GlobalTemplateVariables.WordSelection());
        
        addResolver(new GlobalTemplateVariables.LineSelection());
        addResolver(new GlobalTemplateVariables.Dollar());
        addResolver(new GlobalTemplateVariables.Date());
        addResolver(new GlobalTemplateVariables.Year());
        addResolver(new GlobalTemplateVariables.Time());
        addResolver(new GlobalTemplateVariables.User());
    }
}
