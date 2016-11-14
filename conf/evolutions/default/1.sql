# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

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
  company_names                 varchar(255),
  company_ownership             varchar(255),
  company_ownership_situated    varchar(255),
  companies_involved            varchar(255),
  type_of_action                varchar(255),
  type_of_organisation          varchar(255),
  dominant_gender               varchar(255),
  case_of_dispute               varchar(255),
  nature_of_strike              varchar(255),
  outcome_of_strike             varchar(255),
  description                   varchar(255),
  author_information            varchar(255),
  sources                       varchar(255),
  occupations                   varchar(255),
  strike_identities             varchar(255),
  strike_definitions            varchar(255),
  article_upload                longvarbinary,
  constraint pk_strike primary key (id)
);
create sequence strike_seq;


# --- !Downs

drop table if exists sector;
drop sequence if exists sector_seq;

drop table if exists strike;
drop sequence if exists strike_seq;

