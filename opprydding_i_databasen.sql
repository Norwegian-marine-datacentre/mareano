
-- slette Gebco skyggerelieff
-- select * from kartlag_no where title='GEBCO Skyggerelieff' -> 309, 245 (425)
delete from kartbilder_kartlag where kartlag_id=309;
delete from kartbilder_kartlag where kartlag_id=245;
delete from kartbilder_kartlag where kartlag_id=425;

-- slett 1;"Enkelt bakgrunnskart"
delete from kartbilder_kartlag where kartlag_id=1;

-- Slette tomt kartbilde under bagrunn, landkart
-- select * from kartbilder where hovedtemaer_id=1
-- select * from kartbilder_no where kartbilder_id=11
delete from kartbilder_no where kartbilder_id=11;
delete from kartbilder_en where kartbilder_id=11;
delete from kartbilder where kartbilder_id=11;

-- Slett satelittbilder også etter detaljertbakgrunnskart er fjernet
delete from kartbilder_kartlag where kartbilder_id=10;
delete from kartbilder_no where kartbilder_id=10;
delete from kartbilder_en where kartbilder_id=10;
delete from kartbilder where kartbilder_id=10;

-- slette bakgrunnskart
-- select * from kartlag_no where title like '%Europa%' -> 350
delete from kartbilder_kartlag where kartlag_id=350;

-- slett Detaljert bakgrunnskart
-- select * from kartlag_no where alternate_title like'%Detaljert bakgrunnskart%' -> 259
delete from kartbilder_kartlag where kartlag_id=259;


-- adding general map picture
insert into hovedtemaer(hovedtemaer_id) values (36);
insert into hovedtemaer_no (hovedtemaer_id,title, alternate_title,modified) values (36,'generelle','generelle','2012-03-07 14:26:17');
insert into hovedtemaer_en (hovedtemaer_id,title, alternate_title,modified) values (36,'generelle','generelle','2012-03-07 14:26:17');

insert into kartbilder (kartbilder_id) values (214);
insert into kartbilder_en (kartbilder_id, title, alternate_title) values (214,'generelle','generelle');
insert into kartbilder_no (kartbilder_id, title, alternate_title) values (214,'generelle','generelle');

insert into kartbilder_kartlag values(214,293,18815,29999,'2013-01-03 11:45:48');
insert into kartbilder_kartlag values(214,22,18815,29998,'2013-01-03 11:45:48');
insert into kartbilder_kartlag values(214,257,18815,29997,'2013-01-03 11:45:48');

delete from kartbilder_kartlag where kartlag_id=293 and kartbilder_id!=213;
delete from kartbilder_kartlag where kartlag_id=22 and kartbilder_id!=213;
delete from kartbilder_kartlag where kartlag_id=257 and kartbilder_id!=213;
delete from kartbilder_kartlag where kartlag_id=405 and kartbilder_id!=213;

-- slette bakgrunnskart, land hovedtema og kartbilde:
-- select * from kartbilder where hovedtemaer_id=1
delete from kartbilder_kartlag where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;
delete from kartbilder_no where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;
delete from kartbilder_en where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;
delete from kartbilder where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;

delete from hovedtemaer_no where hovedtemaer_id=1;
delete from hovedtemaer_en where hovedtemaer_id=1;
delete from hovedtemaer where hovedtemaer_id=1;

-- delete Mareano - oversiktskart - kartbilde
delete from kartbilder_kartlag where kartbilder_id=2;
delete from kartbilder_no where kartbilder_id=2;
delete from kartbilder_en where kartbilder_id=2;
delete from kartbilder where kartbilder_id=2;

-- move one Havbunn skyggerelieff to commons map and delete the rest
update kartbilder_kartlag set kartbilder_id=213 where kartbilder_id=27 and kartlag_id=311;
delete from kartbilder_kartlag where kartlag_id=311 and kartbilder_id!=213;

-- do the same for marine områder
update kartbilder_kartlag set kartbilder_id=213 where kartbilder_id=5 and kartlag_id=14;
delete from kartbilder_kartlag where kartlag_id=14 and kartbilder_id!=213;

-- flyttet Leiteområder for olje og gass fra mareano oversiktskart til næringsaktiviteter
update kartbilder set hovedtemaer_id=28 where kartbilder_id=157;

-- Slett vegnet fra detaljerte dybdedata
delete from kartbilder_kartlag where kartlag_id=255 and kartbilder_id=48; 
delete from kartbilder_kartlag where kartlag_id=256 and kartbilder_id=48;
delete from kartbilder_kartlag where kartlag_id=189 and kartbilder_id=48;
delete from kartbilder_kartlag where kartlag_id=253 and kartbilder_id=48;