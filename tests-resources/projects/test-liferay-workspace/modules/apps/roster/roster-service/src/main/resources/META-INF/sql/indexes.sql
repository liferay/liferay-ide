create index IX_3CCD9864 on ROSTER_Club (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C897C98A on ROSTER_Roster (clubId);
create index IX_B496332F on ROSTER_Roster (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C23C9F99 on ROSTER_RosterMember (rosterId);
create index IX_84CBE8F5 on ROSTER_RosterMember (uuid_[$COLUMN_LENGTH:75$]);