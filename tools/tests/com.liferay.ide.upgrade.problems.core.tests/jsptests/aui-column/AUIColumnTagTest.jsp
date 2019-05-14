<aui:layout cssClass="calendar-booking-invitations">
					<aui:column columnWidth="<%= (calendarBooking != null) ? 25 : 50 %>" first="<%= true %>">
						<label class="field-label">
							<liferay-ui:message key="pending" /> (<span id="<portlet:namespace />pendingCounter"><%= pendingCalendarsJSONArray.length() %></span>)
						</label>

						<div class="calendar-portlet-calendar-list" id="<portlet:namespace />calendarListPending"></div>
					</aui:column>
</aui:layout>