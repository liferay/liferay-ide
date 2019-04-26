/** 
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved. 
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
  
package com.liferay.portal.kernel.scripting; 
  
import com.liferay.portal.kernel.util.FileUtil; 
import com.liferay.portal.kernel.scripting.Scripting;  
import java.io.File; 
import java.io.IOException; 
  
import java.util.Map; 
import java.util.Set; 
  
/** 
 * @author Alberto Montero 
 * @author Brian Wing Shun Chan 
 */
public abstract class RemovedAbilitySpecifyClassLoadersInScriptingExecutorTest implements ScriptingExecutor { 
  
    public void clearCache() { 
    } 
  
    public Map<String, Object> eval( 
            Set<String> allowedClasses, Map<String, Object> inputObjects, 
            Set<String> outputNames, File scriptFile, 
            ClassLoader... classloaders) 
        throws ScriptingException { 
  
        try { 
            String script = FileUtil.read(scriptFile); 
  
            return eval( 
                allowedClasses, inputObjects, outputNames, script, 
                classloaders); 
        } 
        catch (IOException ioe) { 
            throw new ScriptingException(ioe); 
        } 
    } 
  
}