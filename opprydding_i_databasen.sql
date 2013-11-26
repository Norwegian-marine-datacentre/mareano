
-- slette Gebco skyggerelieff
--select * from kartlag_no where title='GEBCO Skyggerelieff' -> 309, 245 (425)
delete from kartbilder_kartlag where kartlag_id=309
delete from kartbilder_kartlag where kartlag_id=245

-- Slette tomt kartbilde under bagrunn, landkart
--select * from kartbilder where hovedtemaer_id=1
--select * from kartbilder_no where kartbilder_id=11
delete from kartbilder_no where kartbilder_id=11;
delete from kartbilder_en where kartbilder_id=11;
delete from kartbilder where kartbilder_id=11;

-- Slett satelittbilder også etter detaljertbakgrunnskart er fjernet
delete from kartbilder_no where kartbilder_id=10;
delete from kartbilder_en where kartbilder_id=10;
delete from kartbilder where kartbilder_id=10;

-- slette bakgrunnskart
--select * from kartlag_no where title like '%Europa%' -> 350
delete from kartbilder_kartlag where kartlag_id=350

-- slett Detaljert bakgrunnskart
--select * from kartlag_no where alternate_title like'%Detaljert bakgrunnskart%' -> 259
delete from kartbilder_kartlag where kartlag_id=259


-- adding general map picture

--insert into hovedtemaer_no (hovedtemaer_id,title, alternate_title,modified) values (36,'generelle','generelle','2012-03-07 14:26:17')
--insert into hovedtemaer_en (hovedtemaer_id,title, alternate_title,modified) values (36,'generelle','generelle','2012-03-07 14:26:17')

--insert into kartbilder_en (kartbilder_id, title, alternate_title) values (213,'general','general')
--insert into kartbilder_no (kartbilder_id, title, alternate_title) values (213,'generelle','generelle')

--insert into kartbilder_kartlag values(213,293,18815,29999,'2013-01-03 11:45:48')