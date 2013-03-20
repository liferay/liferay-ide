<#macro printLayout type="">
<#if type == "ie">
<#assign rowElement="table" trBegin="\n\t\t<tr>" trEnd="</tr>\n\t\t" columnElement="td" columnNewline="">
<#else>
<#assign rowElement="div" trBegin="" trEnd="" columnElement="div" columnNewline="\n">
</#if>
<#assign rowCounter=0>
<#list root.getRows() as row>
<#if (rowCounter > 0)>

</#if>
<#assign rowCounter = rowCounter + 1>
		<${rowElement} class="${row.className}">${trBegin}
<#if row.getColumns().size() == 1>
<#assign columnOnly=row.getColumns().get(0)>
			<${columnElement} class="portlet-column portlet-column-only" id="column-${columnOnly.numId}">
				$processor.processColumn("column-${columnOnly.numId}", "portlet-column-content portlet-column-content-only")
			</${columnElement}>
<#elseif (row.getColumns().size() > 1)>
<#list row.getColumns() as col>
<#if col.isFirst()>
			<${columnElement} class="aui-w${col.getWeight()} portlet-column portlet-column-first" id="column-${col.numId}">
				$processor.processColumn("column-${col.numId}", "portlet-column-content portlet-column-content-first")
<#elseif col.isLast()>${columnNewline}<#rt>
			<${columnElement} class="aui-w${col.getWeight()} portlet-column portlet-column-last" id="column-${col.numId}">
				$processor.processColumn("column-${col.numId}", "portlet-column-content portlet-column-content-last")
<#else>${columnNewline}<#rt>
			<${columnElement} class="aui-w${col.getWeight()} portlet-column" id="column-${col.numId}">
				$processor.processColumn("column-${col.numId}", "portlet-column-content")
</#if>
			</${columnElement}>
</#list>
</#if>
		${trEnd}</${rowElement}>
</#list>
</#macro>
<#if (root.getRows().size() > 0)>
<div class="${templateName}" id="${root.id}" role="${root.role}">
	#if ($browserSniffer.isIe($request) && $browserSniffer.getMajorVersion($request) < 8)
	<@printLayout type="ie"/>
	#else
	<@printLayout/>
	#end
</div>
</#if>
