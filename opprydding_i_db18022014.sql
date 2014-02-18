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

--rename Oversiktskart, dybdeforhold til Dybdedata havområder 
--select * from kartbilder_no where  alternate_title like '%Oversiktskart, dybdeforhold%' --> 120
update kartbilder_no set alternate_title = 'Dybdedata havområder' where kartbilder_id=120;
update kartbilder_en set alternate_title = 'Depth Data seas' where kartbilder_id=120;

-- bytt navn på hovedtema Havbunn og vannmasser til Havbunn
--select * from hovedtemaer_no where  alternate_title like '%Havbunn og vannmasser%'  --> 6
update hovedtemaer_no set alternate_title = 'Havbunn' where hovedtemaer_id=6;
update hovedtemaer_en set alternate_title = 'Seabed' where hovedtemaer_id=6;

-- opprett et kartbilde  Havbunn > Terrengmodell havbunn (skyggerelieff)	 
insert into kartbilder (kartbilder_id, hovedtemaer_id) values (216, 6);
insert into kartbilder_no (kartbilder_id, title, alternate_title) values (216,'Terrengmodell havbunn (detalj)','Terrengmodell havbunn (detalj)');
insert into kartbilder_en (kartbilder_id, title, alternate_title) values (216,'Terrain Model seabed (details)','Terrain Model seabed (details)');

-- slett alle Skyggerelieff, fokusområder (tidligere skyggerelieff detaljert) utenom en som 
-- flyttes til havbunn > Terrengmodell havbunn 
--select * from kartbilder_no where  alternate_title like '%Terrengmodell havbunn (sk%' -->66
-- select * from kartbilder_kartlag where kartbilder_id=66 and kartlag_id=321;
update kartbilder_kartlag set kartbilder_id=216 where kartbilder_id=66 and kartlag_id=321;

--flytte dybdedata > Detaljerte dybdedata til Andre kart (id 30)
--select * from kartbilder_no where  alternate_title like '%Detaljerte dybdedata%' --> 48
update kartbilder set hovedtemaer_id=30 where kartbilder_id=48;

-- Fjern Dybdedata > Enkeltstråle ekkolodd 
--select * from kartlag_no where  alternate_title ilike '%Enkeltstråle ekkolodd%' --> 191
delete from kartbilder_kartlag where kartlag_id=191 and kartbilder_id!=66;

-- flytt Bakgrunn, sjø > Sjøgrenser til Dybdekart
--select * from kartlag_no where  alternate_title ilike '%Sjøgrenser%' --> 329
--select * from kartbilder_kartlag where kartlag_id=329 --> kartbilde_id 136
--select * from hovedtemaer_no where  alternate_title ilike '%Dybdekart%' --> 3
update kartbilder set hovedtemaer_id=3 where kartbilder_id=136;
-- rename kartbilde Sjøgrenser til Maritime grenser
update kartbilder_no set alternate_title='Maritime grenser' where kartbilder_id=136;
update kartbilder_en set alternate_title='Maritime boundaries' where kartbilder_id=136;
-- rename kartlag Sjøgrenser > Sjøgrenser til Martitime grenser > Martitime grenser
update kartlag_no set alternate_title='Maritime grenser' where kartlag_id=329;
update kartlag_en set alternate_title='Maritime boundaries' where kartlag_id=329;

-- flytte Artsmangfold og naturtyper > Landskap til Havbunn
--select * from kartbilder_no where  alternate_title ilike '%Landskap%' --> 145 (Marine landskap)
update kartbilder set hovedtemaer_id=6 where kartbilder_id=145;

-- slett skyggerelieff detaljert
--select * from kartlag_no where  alternate_title ilike '%Skyggerelieff detaljert%' --> 432
--select * from kartbilder_kartlag where kartlag_id=432 
delete from kartbilder_kartlag where kartlag_id=432;

--Slett Havbunn>Landskap>Marine områder og Dybdekoter(regional)
--select * from kartbilder_no where kartbilder_id=145
--select * from kartbilder_kartlag where kartbilder_id=145 -> 14, 421, 351
--select * from kartbilder_kartlag where kartlag_id=421 and kartbilder_id=145;
delete from kartbilder_kartlag where kartlag_id=14 and kartbilder_id=145;
delete from kartbilder_kartlag where kartlag_id=421 and kartbilder_id=145;