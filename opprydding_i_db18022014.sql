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

-- Kjell begynt her 19/2-14

-- flytte Bakgrunn - sjøkart > Olje og gass til Næringsaktivitet > Olje og gass
-- select * from kartbilder_no where  alternate_title ilike '%Olje og gass%' --> kartbilder_id = 57
-- select * from kartbilder where kartbilder_id=57 --> hovedtemaer_id = 34 (Bakgrunn sjø, ht_id=28 for Næringsaktivitet.
update kartbilder set hovedtemaer_id=28 where kartbilder_id=57 and hovedtemaer_id = 34;

-- rename  MAREANO og forvaltningsplaner > MAREANO oversiktskart
-- select * from hovedtemaer_no where  alternate_title ilike '%MAREANO og forvaltningsplaner%'; --> hovedtemaer_id = 29
update hovedtemaer_no set alternate_title = 'MAREANO oversiktskart' where hovedtemaer_id=29;
update hovedtemaer_en set alternate_title = 'MAREANO main maps' where hovedtemaer_id=29;

-- slette MAREANO oversiktskart > MAREANO-oversiktskart og Forvaltningsplanomr.
-- Behold Forvaltningsplanomr. under Særlige verdifulle områder (kartbilder_id=158 og kartlag_id=10)
-- select * from kartlag_no where  alternate_title ilike '%Forvaltningsplano%'; --> kartlag_id=10
-- select * from kartbilder_kartlag where kartlag_id=10 --> kartbilder_id 2 og 158
-- select * from kartbilder_kartlag where kartbilder_id=2; -- Forvaltningsplanomr.
delete from kartbilder_kartlag where kartbilder_id=2;
-- select * from kartbilder_no where kartbilder_id=2; -- MAREANO-oversiktskart
delete from kartbilder_no where kartbilder_id=2;
		-- KLARER IKKE Å SLETTE laget  MAREANO-oversiktskart.  JO, gikk med dette!!
-- select * from kartbilder_en where  alternate_title ilike '%MAREANO-oversiktskart%'; --> kartbilder_id = 2
-- select * from kartbilder where kartbilder_id=2; --> hovedtemaer_id = 29
delete from kartbilder where kartbilder_id=2 and hovedtemaer_id = 29;

-- Flytter årlige stasjoner opp fra Utvikling til MAREANO oversikt.
-- flytte Utvikling - Årlige stasjoner > MAREANO oversiktskart
-- select * from kartbilder_no where  alternate_title ilike '%MAREANO-stasjoner årlig%'; --> kartbilder_id = 214
-- select * from kartbilder where kartbilder_id=214; --> hovedtemaer_id = 20 for Utvikling, ht_id=29 for MAREANO oversikt.
update kartbilder set hovedtemaer_id=29 where kartbilder_id=214 and hovedtemaer_id = 20;

-- flytte MAREANO oversiktskart > Leteområder olje og gass til Næringsaktivitet > Leteområder olje og gass
-- select * from kartbilder_no where  alternate_title ilike '%Leteområder olje og gass%'; --> kartbilder_id = 157
-- select * from kartbilder where kartbilder_id=157; --> hovedtemaer_id = 29 MAREANO oversiktskart, ht_id=28 for Næringsaktivitet.
update kartbilder set hovedtemaer_id=28 where kartbilder_id=157 and hovedtemaer_id = 29;

-- opprett et hovedtema  Vannmasser	 
insert into hovedtemaer (hovedtemaer_id,generic_title) values (37,'Vannmasser');
insert into hovedtemaer_no (hovedtemaer_id, title, alternate_title) values (37,'Vannmasser','Vannmasser');
insert into hovedtemaer_en (hovedtemaer_id, title, alternate_title) values (37,'Watermass','Watermass');


-- opprett et hovedtema  Naturtyper	 
insert into hovedtemaer (hovedtemaer_id,generic_title) values (38,'Naturtyper');
insert into hovedtemaer_no (hovedtemaer_id, title, alternate_title) values (38,'Naturtyper','Naturtyper');
insert into hovedtemaer_en (hovedtemaer_id, title, alternate_title) values (38,'Habitats','Habitats');

-- rename  hovedtema Artsmangfold og naturtyper > Artsmangfold
-- select * from hovedtemaer_no where  alternate_title ilike '%Artsmangfold og naturtyper%'; --> hovedtemaer_id = 23
update hovedtemaer_no set alternate_title = 'Artsmangfold' where hovedtemaer_id=23;
update hovedtemaer_en set alternate_title = 'Biodiversity' where hovedtemaer_id=23;


-- flytte Havbunn > Havstrømmer -> Vannmasser > Havstrømmer
-- select * from kartbilder_no where  alternate_title ilike '%Havstrømmer%'; --> kartbilder_id = 26
-- select * from kartbilder where kartbilder_id=26; --> hovedtemaer_id = 6 Havbunn, ht_id=37 for Vannmasser.
update kartbilder set hovedtemaer_id=37 where kartbilder_id=26 and hovedtemaer_id = 6;


-- flytte Artsmangfold > Sårbare naturtyper -> Naturtyper > Sårbare naturtyper
-- select * from kartbilder_no where  alternate_title ilike '%Sårbare naturtyper%'; --> kartbilder_id = 138 og 209
-- select * from kartbilder where kartbilder_id=138; --> hovedtemaer_id = 20. Under utvikling. KAN FJERNES.
-- select * from kartbilder where kartbilder_id=209; --> hovedtemaer_id = 23 Artsmangfold, ht_id=38 for Naturtype.
update kartbilder set hovedtemaer_id=38 where kartbilder_id=209 and hovedtemaer_id = 23;


slett begge Naturtyper > Sårbare naturtyper > Sårbare naturtyper undersøkelseområder
-- select * from kartlag_no where alternate_title ilike '%Sårbare naturtyper undersøkel%'; -> 443   (2 forekomster)
-- select * from kartbilder_kartlag where kartlag_id=443;
delete from kartbilder_kartlag where kartlag_id=443;

slett begge Naturtyper > Sårbare naturtyper > Harbunnskorallskog
-- select * from kartlag_no where alternate_title ilike '%Harbunnskorallskog%'; -> 341   (1 forekomster)
-- select * from kartbilder_kartlag where kartlag_id=341;
delete from kartbilder_kartlag where kartlag_id=341;

slett begge Naturtyper > Sårbare naturtyper > Svampsamfunn
-- select * from kartlag_no wherealternate_title ilike '%Svampsamfunn%'; -> 343   (1 forekomster)
-- select * from kartbilder_kartlag where kartlag_id=343;
delete from kartbilder_kartlag where kartlag_id=343;

slett begge Naturtyper > Sårbare naturtyper > Glassvamp (Hex
-- select * from kartlag_no where alternate_title ilike '%Glassvamp (Hex%'; -> 340   (1 forekomster)
-- select * from kartbilder_kartlag where kartlag_id=340;
delete from kartbilder_kartlag where kartlag_id=340;

slett begge Naturtyper > Sårbare naturtyper > Umbellula bestand
-- select * from kartlag_no where alternate_title ilike '%Umbellula bestand%'; -> 344   (1 forekomster)
-- select * from kartbilder_kartlag where kartlag_id=344;
delete from kartbilder_kartlag where kartlag_id=344;

slett begge Naturtyper > Sårbare naturtyper > Bløtbunnskorallskog (Radi
-- select * from kartlag_no where alternate_title ilike '%Bløtbunnskorallskog (Radi%'; -> 339   (1 forekomster)
-- select * from kartbilder_kartlag where kartlag_id=339;
delete from kartbilder_kartlag where kartlag_id=339;

slett begge Naturtyper > Sårbare naturtyper > Sjøfjærbunn      - duplikat
-- select * from kartlag_no where alternate_title ilike '%Sjøfjærbunn%'; -> 342   (1 forekomster) (Behold -> 469)
-- select * from kartbilder_kartlag where kartlag_id=342;
delete from kartbilder_kartlag where kartlag_id=342;


-- flytte Artsmangfold > Biotoper - Tromsøflaket -> Naturtyper > Biotoper - Tromsøflaket
-- select * from kartbilder_no where  alternate_title ilike '%Biotoper - Tromsø%'; --> kartbilder_id = 115
-- select * from kartbilder where kartbilder_id=115; --> hovedtemaer_id = 23 Artsmangfold, ht_id=38 for Naturtype.
update kartbilder set hovedtemaer_id=38 where kartbilder_id=115 and hovedtemaer_id = 23;

-- flytte Artsmangfold > Biotoper - TromsII/NordlandVII -> Naturtyper > Biotoper - TromsII/NordlandVII
-- select * from kartbilder_no where  alternate_title ilike '%Biotoper - TromsII%'; --> kartbilder_id = 176
-- select * from kartbilder where kartbilder_id=176; --> hovedtemaer_id = 23 Artsmangfold, ht_id=38 for Naturtype.
update kartbilder set hovedtemaer_id=38 where kartbilder_id=176 and hovedtemaer_id = 23;

-- flytte Artsmangfold > Biotoper - Midtnorsk sokkel -> Naturtyper > Biotoper - Midtnorsk sokkel
-- select * from kartbilder_no where  alternate_title ilike '%Biotoper - Midtnorsk sok%'; --> kartbilder_id = 213
-- select * from kartbilder where kartbilder_id=213; --> hovedtemaer_id = 23 Artsmangfold, ht_id=38 for Naturtype.
update kartbilder set hovedtemaer_id=38 where kartbilder_id=213 and hovedtemaer_id = 23;

-- flytte Artsmangfold > Biotoper - NordlandVI -> Naturtyper > Biotoper - Nordland VI
-- select * from kartbilder_no where  alternate_title ilike '%Biotoper - Nordland VI%'; --> kartbilder_id = 210
-- select * from kartbilder where kartbilder_id=210; --> hovedtemaer_id = 23 Artsmangfold, ht_id=38 for Naturtype.
update kartbilder set hovedtemaer_id=38 where kartbilder_id=210 and hovedtemaer_id = 23;

-- flytte Artsmangfold > Fiskesamfunn -> Andre kart > Fiskesamfunn
-- select * from kartbilder_no where  alternate_title ilike '%Fiskesamfunn%'; --> kartbilder_id = 126
-- select * from kartbilder where kartbilder_id=126; --> hovedtemaer_id = 23 Artsmangfold, ht_id=30 for Andre kart.
update kartbilder set hovedtemaer_id=30 where kartbilder_id=126 and hovedtemaer_id = 23;

-- rename  hovedtema Geologi > Kjemiske analyser
-- select * from hovedtemaer_no where  alternate_title ilike '%Geologi%'; --> hovedtemaer_id = 27
update hovedtemaer_no set alternate_title = 'Kjemiske analyser' where hovedtemaer_id=27;
update hovedtemaer_en set alternate_title = 'Chemical analysis' where hovedtemaer_id=27;


update hovedtemaer set sort = 300 where hovedtemaer_id=27;  --->  ny sorteringsrekkefølge for Kjemiske analyser
update hovedtemaer set sort = 425 where hovedtemaer_id=35;  --->  ny sorteringsrekkefølge for MAREANO bilder



slett begge MAREANO bilder > Bilder av sediment > Kornstørrelse (regional)
-- select * from kartlag_no where alternate_title ilike '%Kornstørrelse (regi%'; -> kartlag_id=11 
-- select * from kartbilder_kartlag where kartlag_id=11;   -->  kartbilder_id= 13, 119, 174
-- EG KLARER IKKE Å FINNE RIKTIG LAG.


slett begge MAREANO bilder > Bilder av sediment > Marine områder
-- select * from kartlag_no where alternate_title ilike '%Marine områder'; -> kartlag_id=14 
-- select * from kartbilder_kartlag where kartlag_id=14;   -->  kartbilder_id= ein heil haug
-- EG KLARER IKKE Å FINNE RIKTIG LAG.


-- Noe verdier som mangler under Kjemiske analyser, men som finne under Miljøkjemi. Burde vært flyttet for lengst!!

-- flytte Miljøkjemi og forurensning > PAH 16 (miljø..) > PAH 16 -> Kjemiske analyser > Organiske stoffer > PAH 16
-- select * from kartlag_no where alternate_title ilike '%PAH 16'; -> kartlag_id=264
-- select * from kartbilder_no where  alternate_title ilike '%Organiske stoffer%'; --> kartbilder_id = 108
-- select * from kartbilder_kartlag where kartlag_id=264;   -->  kartbilder_id=123 for PAH 16
-- select * from kartbilder_kartlag where kartbilder_id=108;   -->  kartbilder_id=108 finner alle Organsike stoffer
update kartbilder_kartlag set kartbilder_id=108 where kartbilder_id=123 and kartlag_id=264;

-- flytte Miljøkjemi og forurensning > BDE209-nivåer > BDE209-nivåer -> Kjemiske analyser > Organiske stoffer > BDE209-nivåer
-- select * from kartlag_no where alternate_title ilike '%BDE209-nivåer'; -> kartlag_id=461
-- select * from kartbilder_no where  alternate_title ilike '%Organiske stoffer%'; --> kartbilder_id = 108
-- select * from kartbilder_kartlag where kartlag_id=461;   -->  kartbilder_id=207 for BDE209-nivåer
-- select * from kartbilder_kartlag where kartbilder_id=108;   -->  kartbilder_id=108 finner alle Organsike stoffer
update kartbilder_kartlag set kartbilder_id=108 where kartbilder_id=207 and kartlag_id=461;

-- flytte Miljøkjemi og forurensning > Summerte PBDE-nivåer > Summerte PBDE-nivåer -> Kjemiske analyser > Organiske stoffer > Summerte PBDE-nivåer
-- select * from kartlag_no where alternate_title ilike '%Summerte PBDE-nivåer'; -> kartlag_id=462
-- select * from kartbilder_no where  alternate_title ilike '%Organiske stoffer%'; --> kartbilder_id = 108
-- select * from kartbilder_kartlag where kartlag_id=462;   -->  kartbilder_id=208 for Summerte PBDE-nivåer
-- select * from kartbilder_kartlag where kartbilder_id=108;   -->  kartbilder_id=108 finner alle Organsike stoffer
update kartbilder_kartlag set kartbilder_id=108 where kartbilder_id=208 and kartlag_id=462;

-- flytte Bakgrunn, sjøkart > Dybdedata - koter - Andre kart (id 30) > Dybdedata - koter
-- select * from kartbilder_no where  alternate_title like '%Dybdedata - koter%'; --> kartbilder_id=49
update kartbilder set hovedtemaer_id=30 where kartbilder_id=49 and hovedtemaer_id=34;

-- flytte Bakgrunn, sjøkart > D-celler >  D-celler - Generelle kart (id 30) >  D-celler
-- select * from kartbilder_no where  alternate_title like '%D-celler%'; --> kartbilder_id=135
-- select * from kartbilder where  kartbilder_id=135; --> kartbilder_id=135



insert into kartbilder (kartbilder_id, hovedtemaer_id) values (215, 36);
-- select * from kartbilder where  kartbilder_id=215;

--flytte Bakgrunn, sjøkart > D-celler > D-celler til Generelle kart
--select * from kartbilder where hovedtemaer_id=34 -->135
--select * from kartbilder_kartlag where kartbilder_id=135 -->328
--select * from kartlag_no where kartlag_id=328
update kartbilder_kartlag set kartbilder_id = 215 where kartbilder_id=135 and kartlag_id=328;
--fjerne Bakgrunn, sjøkart
-- select * from kartbilder where hovedtemaer_id=34 --> 135
-- select * from kartbilder_kartlag where kartbilder_id=135

delete from kartbilder_en where kartbilder_id=135;
delete from kartbilder_no where kartbilder_id=135;
delete from kartbilder where kartbilder_id=135;

--select * from kartbilder where hovedtemaer_id=34
delete from hovedtemaer_en where hovedtemaer_id=34
delete from hovedtemaer_no where hovedtemaer_id=34
delete from hovedtemaer where hovedtemaer_id=34

--lagt til sjøkart som bakgrunnskart og slettet den fra Dybdekart
--select * from kartbilder where hovedtemaer_id=3 and kartbilder_id=7
--select * from kartbilder_en where kartbilder_id=7
delete from kartbilder_en where kartbilder_id=7;
delete from kartbilder_no where kartbilder_id=7;
delete from kartbilder where kartbilder_id=7;

-- opprett et kartbilde  Andre kart > Annen geokjemi	 
--select * from kartbilder order by kartbilder_id -->
insert into kartbilder (kartbilder_id, hovedtemaer_id) values (217, 30);
insert into kartbilder_no (kartbilder_id, title, alternate_title) values (217,'Annen geokjemi','Annen geokjemi');
insert into kartbilder_en (kartbilder_id, title, alternate_title) values (217,'Annen geokjemi','Annen geokjemi');

-- flytte Kvikksølv-nivåer;rasterkart, Bly-nivåer; rasterkart, Barium-nivåer; rasterkart
--select * from kartlag_no where alternate_title ilike '%rasterkart%' --> 219 232 231
--select * from kartbilder_kartlag where kartlag_id=219 --> 103, 105
--select * from kartbilder_no where kartbilder_id=103 --> kartbilde blynivå;rasterkart
--select * from kartbilder_no where kartbilder_id=105 --> Tungmetaller

update kartbilder_kartlag set kartbilder_id=217 where kartbilder_id=105 and kartlag_id=219
--select * from kartbilder_kartlag where kartlag_id=232 --> 106, 109
--select * from kartbilder_no where kartbilder_id=109 --> Barium-nivåer, rasterkart
--select * from kartbilder_no where kartbilder_id=106 --> Andre kjemiske element
update kartbilder_kartlag set kartbilder_id=217 where kartbilder_id=106 and kartlag_id=232
--select * from kartbilder_kartlag where kartlag_id=231 --> 104, 105
--select * from kartbilder_no where kartbilder_id=104 --> kvikksølv-nivåer
--select * from kartbilder_no where kartbilder_id=105 -->Tungmetaller
update kartbilder_kartlag set kartbilder_id=217 where kartbilder_id=105 and kartlag_id=231

-- slettet kartbilde PBDE-209-nivåer
--select * from kartbilder_no where alternate_title ilike '%BDE209-nivåer%' -->207
delete from kartbilder_en where kartbilder_id=207
delete from kartbilder_no where kartbilder_id=207
delete from kartbilder where kartbilder_id=207