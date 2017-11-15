<#assign appendIndent ="" />
<#if ((root.getClassName().content())?exists)>
<#assign className = root.getClassName().content() />
<#else>
<#assign className ="" />
</#if>
<#macro printLayout this type="">
<#if stringUtil.equals(type, "ie")>
<#assign rowElement ="table" trBegin="\n"+appendIndent+"\t\t<tr>" trEnd= appendIndent+"</tr>\n\t\t" columnElement="td" columnNewline="" />
<#else>
<#assign rowElement ="div" trBegin="" trEnd="" columnElement="div" columnNewline="\n" />
</#if>
<#assign rowCounter = 0 />
<#if (this.getPortletLayouts().size() > 0)>
<#list this.getPortletLayouts() as row>
<#if (rowCounter > 0)>

</#if>
<#assign rowCounter = rowCounter + 1 />
		${appendIndent}<${rowElement} class="${row.getClassName().content()}">${trBegin}
<#list row.getPortletColumns() as col>
<#if (!col.getFirst().content())&&(!col.getOnly().content())>
${columnNewline}<#rt>
</#if>
			${appendIndent}<${columnElement} class="aui-w${col.getWeight().content()} portlet-column<#if (col.getColumnDescriptor().content()?exists)> ${col.getColumnDescriptor().content()}</#if>"<#if !(col.getNumId().content()=="N/A")> id="column-${col.getNumId().content()}"</#if>>
<#if (col.getPortletLayouts().size() > 0)>
<#assign appendIndent = stack.push(appendIndent) + "\t\t" />
<@printLayout
	this=col
	type=type
/>
<#assign appendIndent = stack.pop() />
<#if type=="ie">
<#assign trEnd = appendIndent+"</tr>\n\t\t" />
</#if>
<#else>
				${appendIndent}$processor.processColumn("column-${col.getNumId().content()}", "portlet-column-content<#if (col.getColumnContentDescriptor().content()?exists)> ${col.getColumnContentDescriptor().content()}</#if>")
</#if>
			${appendIndent}</${columnElement}>
</#list>
		${trEnd}${appendIndent}</${rowElement}>
</#list>
</#if>
</#macro>
<#if (root.getPortletLayouts().size() >= 0)>
<div class="${className}" id="${root.getId().content()}" role="${root.getRole().content()}">
	#if ($browserSniffer.isIe($request) && $browserSniffer.getMajorVersion($request) < 8)
<@printLayout
	this=root
	type="ie"
/>
	#else
<@printLayout this=root />
	#end
</div><#rt>
</#if>