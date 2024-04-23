insert into user
(created_at, email,modified_at, name, password, profile_image, profile_text, role)
values (now(), 'admin@localhost.com',  now(), 'host', '$2a$10$0Y6WozbD2Ot0YH5J86XfROIN3W.gEG0IPl/3wpoNo0SuMWI2pj1Tu', null, null, 'ROLE_USER');

insert into follow (created_at, follower_id, following_id) values (now(), 2, 1);
delete from follow where follow_id =3;