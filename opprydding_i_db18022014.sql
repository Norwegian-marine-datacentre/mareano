-- slette Gebco skyggerelieff
-- select * from kartlag_no where title='GEBCO Skyggerelieff' -> 309, 245 (425 - gråtoner)
delete from kartbilder_kartlag where kartlag_id=309;
delete from kartbilder_kartlag where kartlag_id=245;
delete from kartbilder_kartlag where kartlag_id=425;

-- slett 1;"Enkelt bakgrunnskart"
delete from kartbilder_kartlag where kartlag_id=1;

-- Slette tomt kartbilde under bagrunn, landkart
-- select * from kartbilder_no where kartbilder_id=11
delete from kartbilder_no where kartbilder_id=11;
delete from kartbilder_en where kartbilder_id=11;
delete from kartbilder where kartbilder_id=11;

	-- select * from kartbilder where hovedtemaer_id=1

-- slett Detaljert bakgrunnskart
-- select * from kartlag_no where alternate_title like'%Detaljert bakgrunnskart%' -> 259
delete from kartbilder_kartlag where kartlag_id=259;

-- Slett satelittbilder også etter detaljertbakgrunnskart er fjernet
-- select * from kartbilder where kartbilder_id=10; 
delete from kartbilder_kartlag where kartbilder_id=10;
delete from kartbilder_no where kartbilder_id=10;
delete from kartbilder_en where kartbilder_id=10;
delete from kartbilder where kartbilder_id=10;

-- slette bakgrunnskart
-- select * from kartlag_no where title like '%Europa%' -> 350
delete from kartbilder_kartlag where kartlag_id=350;

-- adding general map picture
insert into hovedtemaer(hovedtemaer_id) values (36);
insert into hovedtemaer_no (hovedtemaer_id,title, alternate_title,modified) values (36,'generelle','generelle','2012-03-07 14:26:17');
insert into hovedtemaer_en (hovedtemaer_id,title, alternate_title,modified) values (36,'generelle','generelle','2012-03-07 14:26:17');

insert into kartbilder (kartbilder_id, hovedtemaer_id) values (215, 36);
insert into kartbilder_en (kartbilder_id, title, alternate_title) values (215,'generelle','generelle');
insert into kartbilder_no (kartbilder_id, title, alternate_title) values (215,'generelle','generelle');

--legger til Hav, navn - stedsnavn - Rutenett
insert into kartbilder_kartlag values(215,293,18815,29999,'2013-01-03 11:45:48');
insert into kartbilder_kartlag values(215,22,18815,29998,'2013-01-03 11:45:48');
insert into kartbilder_kartlag values(215,257,18815,29997,'2013-01-03 11:45:48');

delete from kartbilder_kartlag where kartlag_id=293 and kartbilder_id!=215; --hav, navn
delete from kartbilder_kartlag where kartlag_id=22 and kartbilder_id!=215; --10 graders rutenett
delete from kartbilder_kartlag where kartlag_id=257 and kartbilder_id!=215; --stedsnavn, fra topografisk norgeskart
delete from kartbilder_kartlag where kartlag_id=405 and kartbilder_id!=215; --stedsnavn, fra topografisk kart

-- slette bakgrunnskart, land hovedtema og kartbilde:
-- select * from kartbilder where hovedtemaer_id=1
delete from kartbilder_kartlag where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;
delete from kartbilder_no where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;
delete from kartbilder_en where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;
delete from kartbilder where kartbilder_id=1 or kartbilder_id=10 or kartbilder_id=11;

delete from hovedtemaer_no where hovedtemaer_id=1;
delete from hovedtemaer_en where hovedtemaer_id=1;
delete from hovedtemaer where hovedtemaer_id=1;

--NY ENDRING FRA FORRIGE FIL
----select * from kartbilder_no where  alternate_title like '%Havbunn, skyggerelieff%' ->66
delete from kartbilder_kartlag where kartlag_id=311 and kartbilder_id!=66;
update kartbilder_no set alternate_title = 'Terrengmodell havbunn (skyggerelieff)' where kartbilder_id=66
update kartbilder_en set alternate_title = 'Terrain Model seabed (shaded relief)' where kartbilder_id=66
