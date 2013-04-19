<#assign appendIndent="">
<#macro printLayout this type="">
<#if type == "ie">
<#assign rowElement="table" trBegin="\n"+appendIndent+"\t\t<tr>" trEnd=appendIndent+"</tr>\n\t\t" columnElement="td" columnNewline="">
<#else>
<#assign rowElement="div" trBegin="" trEnd="" columnElement="div" columnNewline="\n">
</#if>
<#assign rowCounter=0>
<#if !this.getRows().isEmpty()>
<#list this.getRows() as row>
<#if (rowCounter > 0)>

</#if>
<#assign rowCounter = rowCounter + 1>
		${appendIndent}<${rowElement} class="${row.className}">${trBegin}
<#list row.getColumns() as col>
<#if row.getColumns().size() == 1>
<#assign columnContentDescriptor = " portlet-column-content-only" columnDescriptor = " portlet-column-only">
<#elseif (row.getColumns().size() > 1)>
<#if col.isFirst()>
<#assign columnContentDescriptor = " portlet-column-content-first" columnDescriptor = " portlet-column-first">
<#elseif col.isLast()>${columnNewline}<#rt>
<#assign columnContentDescriptor = " portlet-column-content-last" columnDescriptor = " portlet-column-last">
<#else>${columnNewline}<#rt>
<#assign columnContentDescriptor = "" columnDescriptor = "">
</#if>
</#if>
			${appendIndent}<${columnElement} class="aui-w${col.getWeight()} portlet-column${columnDescriptor}"<#if !(col.numId==0)> id="column-${col.numId}"</#if>>
<#if !col.getRows().isEmpty()>
<#assign appendIndent = stack.push(appendIndent) + "\t\t">
<@printLayout this=col type=type/>
<#assign appendIndent = stack.pop()>
<#if type=="ie">
<#assign trEnd=appendIndent+"</tr>\n\t\t">
</#if>
<#else>
				${appendIndent}$processor.processColumn("column-${col.numId}", "portlet-column-content${columnContentDescriptor}")
</#if>
			${appendIndent}</${columnElement}>
</#list>
		${trEnd}${appendIndent}</${rowElement}>
</#list>
</#if>
</#macro>
<#if (root.getRows().size() >= 0)>
<div class="${templateName}" id="${root.id}" role="${root.role}">
	#if ($browserSniffer.isIe($request) && $browserSniffer.getMajorVersion($request) < 8)
<@printLayout this=root type="ie"/>
	#else
<@printLayout this=root/>
	#end
</div><#rt>
</#if>
