package com.liferay.ide.portlet.core.operation;

import java.util.*;
import com.liferay.ide.portlet.core.operation.*;
import org.eclipse.jst.j2ee.internal.common.operations.*;

/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/
 
/**
 * @author Cindy Li
 */
@SuppressWarnings({"restriction","unused"})
public class EntryTemplate
 {
  protected static String nl;
  public static synchronized EntryTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    EntryTemplate result = new EntryTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "package ";
  protected final String TEXT_2 = ";";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = NL + "import ";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL + NL;
  protected final String TEXT_7 = NL;
  protected final String TEXT_8 = "/**" + NL + " * Control panel entry class ";
  protected final String TEXT_9 = NL + " */" + NL + "public class ";
  protected final String TEXT_10 = " extends BaseControlPanelEntry {";
  protected final String TEXT_11 = NL + NL + "    /**" + NL + "     * Default constructor. " + NL + "     */" + NL + "    public ";
  protected final String TEXT_12 = "() {" + NL + "    }";
  protected final String TEXT_13 = NL + "       " + NL + "    /**" + NL + "     * @see ";
  protected final String TEXT_14 = "#";
  protected final String TEXT_15 = "(";
  protected final String TEXT_16 = ")" + NL + "     */" + NL + "    public ";
  protected final String TEXT_17 = "(";
  protected final String TEXT_18 = ") {" + NL + "        super(";
  protected final String TEXT_19 = ");" + NL + "    }";
  protected final String TEXT_20 = NL + NL + "    @Override" + NL + "    public boolean isVisible(PermissionChecker permissionChecker, Portlet portlet)" + NL + "            throws Exception {" + NL + "        return false;" + NL + "    }" + NL + "" + NL + "}";

   public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     CreateEntryTemplateModel model = (CreateEntryTemplateModel) argument; 
    
	if (model.getJavaPackageName() != null && model.getJavaPackageName().length() > 0) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append( model.getJavaPackageName() );
    stringBuffer.append(TEXT_2);
    
	}

    stringBuffer.append(TEXT_3);
     
	Collection<String> imports = model.getImports();
	for (String anImport : imports) { 

    stringBuffer.append(TEXT_4);
    stringBuffer.append( anImport );
    stringBuffer.append(TEXT_5);
     
	}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_10);
     
	if (!model.hasEmptySuperclassConstructor()) { 

    stringBuffer.append(TEXT_11);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_12);
     
	} 

	if (model.shouldGenSuperclassConstructors()) {
		List<Constructor> constructors = model.getConstructors();
		for (Constructor constructor : constructors) {
			if (constructor.isPublic() || constructor.isProtected()) { 

    stringBuffer.append(TEXT_13);
    stringBuffer.append( model.getSuperclassName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( model.getSuperclassName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append( constructor.getParamsForJavadoc() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( constructor.getParamsForDeclaration() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( constructor.getParamsForCall() );
    stringBuffer.append(TEXT_19);
    
			} 
		} 
	} 

    stringBuffer.append(TEXT_20);
    return stringBuffer.toString();
  }
}