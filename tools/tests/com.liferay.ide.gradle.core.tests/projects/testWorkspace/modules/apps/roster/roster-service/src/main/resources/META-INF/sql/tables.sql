create table ROSTER_Club (
	uuid_ VARCHAR(75) null,
	clubId LONG not null primary key,
	createDate DATE null,
	modifiedDate DATE null,
	name VARCHAR(75) null
);

create table ROSTER_Roster (
	uuid_ VARCHAR(75) null,
	rosterId LONG not null primary key,
	createDate DATE null,
	modifiedDate DATE null,
	clubId LONG,
	name VARCHAR(75) null
);

create table ROSTER_RosterMember (
	uuid_ VARCHAR(75) null,
	rosterMemberId LONG not null primary key,
	createDate DATE null,
	modifiedDate DATE null,
	rosterId LONG,
	contactId LONG
);