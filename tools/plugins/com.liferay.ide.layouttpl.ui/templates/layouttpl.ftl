<#if (root.getRows().size() > 0)>
<div class="${templateName}" id="${root.id}" role="${root.role}">
<#list root.getRows() as row>
<#if row.getColumns().size() == 1>
<#assign columnOnly = row.getColumns().get(0)>
	<div class="${row.className}">
		<div class="portlet-column portlet-column-only" id="column-${columnOnly.numId}">
			$processor.processColumn("column-${columnOnly.numId}", "portlet-column-content portlet-column-content-only")
		</div>
	</div>
<#elseif (row.getColumns().size() > 1)>
	<div class="${row.className}">
<#list row.getColumns() as col>
<#if col.isFirst()>
		<div class="aui-w${col.getWeight()} portlet-column portlet-column-first" id="column-${col.numId}">
			$processor.processColumn("column-${col.numId}", "portlet-column-content portlet-column-content-first")
<#elseif col.isLast()>
		<div class="aui-w${col.getWeight()} portlet-column portlet-column-last" id="column-${col.numId}">
			$processor.processColumn("column-${col.numId}", "portlet-column-content portlet-column-content-last")
<#else>
		<div class="aui-w${col.getWeight()} portlet-column" id="column-${col.numId}">
			$processor.processColumn("column-${col.numId}", "portlet-column-content")
</#if>
		</div>
</#list>
	</div>
</#if>
</#list>
</div>
</#if>
