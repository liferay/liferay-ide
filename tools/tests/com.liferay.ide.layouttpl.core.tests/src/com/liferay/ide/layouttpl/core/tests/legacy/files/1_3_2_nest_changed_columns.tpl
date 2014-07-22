<div class="columns-1-3-2-nest-changed" id="main-content" role="main">
	#if ($browserSniffer.isIe($request) && $browserSniffer.getMajorVersion($request) < 8)
		<table class="portlet-layout">
		<tr>
			<td class="aui-w66 portlet-column portlet-column-first" id="column-1">
				$processor.processColumn("column-1", "portlet-column-content portlet-column-content-first")
			</td>
			<td class="aui-w33 portlet-column portlet-column-last" id="column-2">
				$processor.processColumn("column-2", "portlet-column-content portlet-column-content-last")
			</td>
		</tr>
		</table>

		<table class="portlet-layout">
		<tr>
			<td class="aui-w66 portlet-column portlet-column-first">
				<table class="portlet-layout">
				<tr>
					<td class="aui-w80 portlet-column portlet-column-first" id="column-3">
						$processor.processColumn("column-3", "portlet-column-content portlet-column-content-first")
					</td>
					<td class="aui-w20 portlet-column portlet-column-last" id="column-4">
						$processor.processColumn("column-4", "portlet-column-content portlet-column-content-last")
					</td>
				</tr>
				</table>

				<table class="portlet-layout">
				<tr>
					<td class="aui-w50 portlet-column portlet-column-first" id="column-5">
						$processor.processColumn("column-5", "portlet-column-content portlet-column-content-first")
					</td>
					<td class="aui-w50 portlet-column portlet-column-last">
						<table class="portlet-layout">
						<tr>
							<td class="aui-w90 portlet-column portlet-column-first" id="column-6">
								$processor.processColumn("column-6", "portlet-column-content portlet-column-content-first")
							</td>
							<td class="aui-w10 portlet-column portlet-column-last" id="column-7">
								$processor.processColumn("column-7", "portlet-column-content portlet-column-content-last")
							</td>
						</tr>
						</table>
					</td>
				</tr>
				</table>
			</td>
			<td class="aui-w33 portlet-column portlet-column-last" id="column-8">
				$processor.processColumn("column-8", "portlet-column-content portlet-column-content-last")
			</td>
		</tr>
		</table>
	#else
		<div class="portlet-layout">
			<div class="aui-w66 portlet-column portlet-column-first" id="column-1">
				$processor.processColumn("column-1", "portlet-column-content portlet-column-content-first")
			</div>

			<div class="aui-w33 portlet-column portlet-column-last" id="column-2">
				$processor.processColumn("column-2", "portlet-column-content portlet-column-content-last")
			</div>
		</div>

		<div class="portlet-layout">
			<div class="aui-w66 portlet-column portlet-column-first">
				<div class="portlet-layout">
					<div class="aui-w80 portlet-column portlet-column-first" id="column-3">
						$processor.processColumn("column-3", "portlet-column-content portlet-column-content-first")
					</div>

					<div class="aui-w20 portlet-column portlet-column-last" id="column-4">
						$processor.processColumn("column-4", "portlet-column-content portlet-column-content-last")
					</div>
				</div>

				<div class="portlet-layout">
					<div class="aui-w50 portlet-column portlet-column-first" id="column-5">
						$processor.processColumn("column-5", "portlet-column-content portlet-column-content-first")
					</div>

					<div class="aui-w50 portlet-column portlet-column-last">
						<div class="portlet-layout">
							<div class="aui-w90 portlet-column portlet-column-first" id="column-6">
								$processor.processColumn("column-6", "portlet-column-content portlet-column-content-first")
							</div>

							<div class="aui-w10 portlet-column portlet-column-last" id="column-7">
								$processor.processColumn("column-7", "portlet-column-content portlet-column-content-last")
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="aui-w33 portlet-column portlet-column-last" id="column-8">
				$processor.processColumn("column-8", "portlet-column-content portlet-column-content-last")
			</div>
		</div>
	#end
</div>
