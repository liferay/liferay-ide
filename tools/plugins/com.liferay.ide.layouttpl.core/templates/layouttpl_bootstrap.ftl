<#assign appendIndent="" layoutTplElement="div" layoutElement="div" columnElement="div" newLine="\n">
<#if (root.getClassName().content())?exists>
<#assign className=root.getClassName().content()>
<#else>
<#assign className="">
</#if>
<#macro printLayout this>
<#if (this.getPortletLayouts().size()>0)>
<#assign appendIndent=stack.push(appendIndent)+"\t">
<#assign rowCounter=0>
<#list this.getPortletLayouts() as row>
<#if (this.getPortletLayouts().indexOf(row)>0)>

</#if>
${appendIndent}<${layoutElement} class="${row.getClassName().content()}">
<#if (row.getPortletColumns().size()>0)>
<#assign appendIndent=stack.push(appendIndent)+"\t">
<#assign colCounter=0>
<#list row.getPortletColumns() as col>
<#if (row.getPortletColumns().indexOf(col)>0)>

</#if>
<#if root.getIs62().content()>
${appendIndent}<${columnElement} class="portlet-column<#if (col.getColumnDescriptor().content()?exists)> ${col.getColumnDescriptor().content()}</#if> span${col.getWeight().content()}"<#if !(col.getNumId().content()=="N/A")> id="column-${col.getNumId().content()}"</#if>>
<#else>
${appendIndent}<${columnElement} class="portlet-column<#if (col.getColumnDescriptor().content()?exists)> ${col.getColumnDescriptor().content()}</#if> col-md-${col.getWeight().content()}"<#if !(col.getNumId().content()=="N/A")> id="column-${col.getNumId().content()}"</#if>>
</#if>
<#if (col.getPortletLayouts().size()>0)>
<@printLayout this=col/>
<#else>
${appendIndent+"\t"}$processor.processColumn("column-${col.getNumId().content()}", "portlet-column-content<#if (col.getColumnContentDescriptor().content()?exists) > ${col.getColumnContentDescriptor().content()}</#if>")
</#if>
${appendIndent}</${columnElement}>
</#list>
<#assign appendIndent=stack.pop()>
</#if>
${appendIndent}</${layoutElement}>
</#list>
<#assign appendIndent=stack.pop()>
</#if>
</#macro>
<${layoutTplElement} class="${className}" id="${root.getId().content()}" role="${root.getRole().content()}">
<@printLayout this=root/>
</${layoutTplElement}>
