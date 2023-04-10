INSERT INTO lab_users (id, os_username, os_password)
VALUES (1, 'user1', 'pass1'),
       (2, 'user2', 'pass2'),
       (3, 'user3', 'pass3'),
       (4, 'user4', 'pass4');

INSERT INTO labs (id, lab_name, app_name)
VALUES (1, 'labname1', 'appName1'),
       (2, 'labname2', 'appName2'),
       (3, 'labname3', 'appName3');

-- INSERT INTO bookings (id, lab_id, user_id, start_date_time, end_date_time, status)
-- VALUES ()