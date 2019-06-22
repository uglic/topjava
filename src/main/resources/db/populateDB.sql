DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'), -- id 100_000 --
       ('Admin', 'admin@gmail.com', 'admin'); -- id 100_001 --

INSERT INTO user_roles (role, user_id)
VALUES ('ROLE_USER', 100000),
       ('ROLE_ADMIN', 100001);

insert into meals(date_time, description, calories, user_id)
values (timestamp with time zone '2019-06-20 07:50+03:00', 'Клиент откушал раз', 100, 100000), -- id 100_002 --
       (timestamp with time zone '2019-06-20 09:10+03:00', 'Клиент откушал два', 210, 100000), -- id 100_003 --
       (timestamp with time zone '2019-06-21 11:20+03:00', 'Клиент откушал три', 320, 100000), -- id 100_004 --
       (timestamp with time zone '2019-06-20 11:05+03:00', 'Админ ел как один', 120, 100001),  -- id 100_005 --
       (timestamp with time zone '2019-06-20 15:10+03:00', 'Админ ел за двоих', 230, 100001),  -- id 100_006 --
       (timestamp with time zone '2019-06-21 7:50+03:00', 'Админ ел за троих', 360, 100001);  -- id 100_007 --
