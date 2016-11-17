# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table cause_of_dispute (
  id                            bigint not null,
  cause_of_dispute_text         varchar(255),
  constraint pk_cause_of_dispute primary key (id)
);
create sequence cause_of_dispute_seq;

create table company_name (
  id                            bigint not null,
  strike_id                     bigint not null,
  company_name_text             varchar(255),
  constraint pk_company_name primary key (id)
);
create sequence company_name_seq;

create table identity_element (
  id                            bigint not null,
  identity_element_text         varchar(255),
  constraint pk_identity_element primary key (id)
);
create sequence identity_element_seq;

create table occupation (
  id                            bigint not null,
  strike_id                     bigint not null,
  occupation_text               varchar(255),
  constraint pk_occupation primary key (id)
);
create sequence occupation_seq;

create table occupation_hisco (
  id                            bigint not null,
  occupation_hisco_text         varchar(255),
  constraint pk_occupation_hisco primary key (id)
);
create sequence occupation_hisco_seq;

create table sector (
  id                            bigint not null,
  sector_name                   varchar(255),
  constraint pk_sector primary key (id)
);
create sequence sector_seq;

create table strike (
  id                            bigint not null,
  year_start                    integer,
  month_start                   integer,
  day_start                     integer,
  month_end                     integer,
  day_end                       integer,
  duration                      integer,
  days_lost                     integer,
  participants_involved         integer,
  year_of_article               integer,
  month_of_article              integer,
  day_of_article                integer,
  country                       varchar(255),
  location                      varchar(255),
  labour_relation               varchar(255),
  workers_situation             varchar(255),
  company_ownership             varchar(255),
  company_ownership_situated    varchar(255),
  companies_involved            varchar(255),
  type_of_action                varchar(255),
  type_of_organisation          varchar(255),
  dominant_gender               varchar(255),
  nature_of_strike              varchar(255),
  outcome_of_strike             varchar(255),
  description                   varchar(255),
  author_information            varchar(255),
  source                        varchar(255),
  article_upload                longvarbinary,
  constraint pk_strike primary key (id)
);
create sequence strike_seq;

create table strike_sector (
  strike_id                     bigint not null,
  sector_id                     bigint not null,
  constraint pk_strike_sector primary key (strike_id,sector_id)
);

create table strike_cause_of_dispute (
  strike_id                     bigint not null,
  cause_of_dispute_id           bigint not null,
  constraint pk_strike_cause_of_dispute primary key (strike_id,cause_of_dispute_id)
);

create table strike_occupation_hisco (
  strike_id                     bigint not null,
  occupation_hisco_id           bigint not null,
  constraint pk_strike_occupation_hisco primary key (strike_id,occupation_hisco_id)
);

create table strike_identity_element (
  strike_id                     bigint not null,
  identity_element_id           bigint not null,
  constraint pk_strike_identity_element primary key (strike_id,identity_element_id)
);

create table strike_strike_definition (
  strike_id                     bigint not null,
  strike_definition_id          bigint not null,
  constraint pk_strike_strike_definition primary key (strike_id,strike_definition_id)
);

create table strike_definition (
  id                            bigint not null,
  strike_definition_text        varchar(255),
  constraint pk_strike_definition primary key (id)
);
create sequence strike_definition_seq;

alter table company_name add constraint fk_company_name_strike_id foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_company_name_strike_id on company_name (strike_id);

alter table occupation add constraint fk_occupation_strike_id foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_occupation_strike_id on occupation (strike_id);

alter table strike_sector add constraint fk_strike_sector_strike foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_strike_sector_strike on strike_sector (strike_id);

alter table strike_sector add constraint fk_strike_sector_sector foreign key (sector_id) references sector (id) on delete restrict on update restrict;
create index ix_strike_sector_sector on strike_sector (sector_id);

alter table strike_cause_of_dispute add constraint fk_strike_cause_of_dispute_strike foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_strike_cause_of_dispute_strike on strike_cause_of_dispute (strike_id);

alter table strike_cause_of_dispute add constraint fk_strike_cause_of_dispute_cause_of_dispute foreign key (cause_of_dispute_id) references cause_of_dispute (id) on delete restrict on update restrict;
create index ix_strike_cause_of_dispute_cause_of_dispute on strike_cause_of_dispute (cause_of_dispute_id);

alter table strike_occupation_hisco add constraint fk_strike_occupation_hisco_strike foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_strike_occupation_hisco_strike on strike_occupation_hisco (strike_id);

alter table strike_occupation_hisco add constraint fk_strike_occupation_hisco_occupation_hisco foreign key (occupation_hisco_id) references occupation_hisco (id) on delete restrict on update restrict;
create index ix_strike_occupation_hisco_occupation_hisco on strike_occupation_hisco (occupation_hisco_id);

alter table strike_identity_element add constraint fk_strike_identity_element_strike foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_strike_identity_element_strike on strike_identity_element (strike_id);

alter table strike_identity_element add constraint fk_strike_identity_element_identity_element foreign key (identity_element_id) references identity_element (id) on delete restrict on update restrict;
create index ix_strike_identity_element_identity_element on strike_identity_element (identity_element_id);

alter table strike_strike_definition add constraint fk_strike_strike_definition_strike foreign key (strike_id) references strike (id) on delete restrict on update restrict;
create index ix_strike_strike_definition_strike on strike_strike_definition (strike_id);

alter table strike_strike_definition add constraint fk_strike_strike_definition_strike_definition foreign key (strike_definition_id) references strike_definition (id) on delete restrict on update restrict;
create index ix_strike_strike_definition_strike_definition on strike_strike_definition (strike_definition_id);


# --- !Downs

alter table company_name drop constraint if exists fk_company_name_strike_id;
drop index if exists ix_company_name_strike_id;

alter table occupation drop constraint if exists fk_occupation_strike_id;
drop index if exists ix_occupation_strike_id;

alter table strike_sector drop constraint if exists fk_strike_sector_strike;
drop index if exists ix_strike_sector_strike;

alter table strike_sector drop constraint if exists fk_strike_sector_sector;
drop index if exists ix_strike_sector_sector;

alter table strike_cause_of_dispute drop constraint if exists fk_strike_cause_of_dispute_strike;
drop index if exists ix_strike_cause_of_dispute_strike;

alter table strike_cause_of_dispute drop constraint if exists fk_strike_cause_of_dispute_cause_of_dispute;
drop index if exists ix_strike_cause_of_dispute_cause_of_dispute;

alter table strike_occupation_hisco drop constraint if exists fk_strike_occupation_hisco_strike;
drop index if exists ix_strike_occupation_hisco_strike;

alter table strike_occupation_hisco drop constraint if exists fk_strike_occupation_hisco_occupation_hisco;
drop index if exists ix_strike_occupation_hisco_occupation_hisco;

alter table strike_identity_element drop constraint if exists fk_strike_identity_element_strike;
drop index if exists ix_strike_identity_element_strike;

alter table strike_identity_element drop constraint if exists fk_strike_identity_element_identity_element;
drop index if exists ix_strike_identity_element_identity_element;

alter table strike_strike_definition drop constraint if exists fk_strike_strike_definition_strike;
drop index if exists ix_strike_strike_definition_strike;

alter table strike_strike_definition drop constraint if exists fk_strike_strike_definition_strike_definition;
drop index if exists ix_strike_strike_definition_strike_definition;

drop table if exists cause_of_dispute;
drop sequence if exists cause_of_dispute_seq;

drop table if exists company_name;
drop sequence if exists company_name_seq;

drop table if exists identity_element;
drop sequence if exists identity_element_seq;

drop table if exists occupation;
drop sequence if exists occupation_seq;

drop table if exists occupation_hisco;
drop sequence if exists occupation_hisco_seq;

drop table if exists sector;
drop sequence if exists sector_seq;

drop table if exists strike;
drop sequence if exists strike_seq;

drop table if exists strike_sector;

drop table if exists strike_cause_of_dispute;

drop table if exists strike_occupation_hisco;

drop table if exists strike_identity_element;

drop table if exists strike_strike_definition;

drop table if exists strike_definition;
drop sequence if exists strike_definition_seq;

