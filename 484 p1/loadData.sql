INSERT INTO Users (user_id, first_name, last_name, year_of_birth, month_of_birth, day_of_birth, gender)
SELECT DISTINCT user_id, first_name, last_name, year_of_birth, month_of_birth, day_of_birth, gender
FROM PROJECT1.Public_User_Information
WHERE user_id IS NOT NULL AND first_name IS NOT NULL AND last_name IS NOT NULL;

INSERT INTO Cities (city_name, state_name, country_name)
SELECT current_city, current_state, current_country
FROM PROJECT1.Public_User_Information
UNION    
SELECT hometown_city, hometown_state, hometown_country
FROM PROJECT1.Public_User_Information
UNION 
SELECT event_city, event_state, event_country
FROM PROJECT1.Public_Event_Information;

INSERT INTO User_Current_Cities (user_id, current_city_id)
SELECT DISTINCT PROJECT1.Public_User_Information.user_id, Cities.city_id
FROM PROJECT1.Public_User_Information, Cities
WHERE PROJECT1.Public_User_Information.current_country = Cities.country_name AND PROJECT1.Public_User_Information.current_state = Cities.state_name AND PROJECT1.Public_User_Information.current_city = Cities.city_name;

INSERT INTO User_Hometown_Cities (user_id, hometown_city_id)
SELECT DISTINCT PROJECT1.Public_User_Information.user_id, Cities.city_id
FROM PROJECT1.Public_User_Information, Cities
WHERE PROJECT1.Public_User_Information.hometown_city = Cities.city_name AND PROJECT1.Public_User_Information.hometown_state = Cities.state_name AND PROJECT1.Public_User_Information.hometown_country = Cities.country_name;

INSERT INTO Programs (institution, concentration, degree)
SELECT DISTINCT institution_name, program_concentration, program_degree
FROM PROJECT1.Public_User_Information
WHERE institution_name IS NOT NULL AND program_concentration IS NOT NULL AND program_degree IS NOT NULL;

INSERT INTO Education (user_id, program_id, program_year)
SELECT DISTINCT PROJECT1.Public_User_Information.user_id, Programs.program_id, PROJECT1.Public_User_Information.program_year
FROM PROJECT1.Public_User_Information, Programs
WHERE PROJECT1.Public_User_Information.program_concentration = Programs.concentration AND PROJECT1.Public_User_Information.program_degree = Programs.degree AND PROJECT1.Public_User_Information.institution_name = Programs.institution;

INSERT INTO Friends (user1_id, user2_id)
SELECT user1_id, user2_id
FROM PROJECT1.Public_Are_Friends;

INSERT INTO User_Events (event_id, event_creator_id, event_name, event_tagline, event_description, event_host, event_type, event_subtype, event_address, event_city_id, event_start_time, event_end_time)
SELECT DISTINCT PROJECT1.Public_Event_Information.event_id, PROJECT1.Public_Event_Information.event_creator_id, PROJECT1.Public_Event_Information.event_name, PROJECT1.Public_Event_Information.event_tagline, PROJECT1.Public_Event_Information.event_description, PROJECT1.Public_Event_Information.event_host, PROJECT1.Public_Event_Information.event_type, PROJECT1.Public_Event_Information.event_subtype, PROJECT1.Public_Event_Information.event_address, Cities.city_id, PROJECT1.Public_Event_Information.event_start_time, PROJECT1.Public_Event_Information.event_end_time
FROM PROJECT1.Public_Event_Information, Cities, Users
WHERE PROJECT1.Public_Event_Information.event_creator_id = Users.user_id AND PROJECT1.Public_Event_Information.event_country = Cities.country_name AND PROJECT1.Public_Event_Information.event_state = Cities.state_name AND PROJECT1.Public_Event_Information.event_city = Cities.city_name;

SET AUTOCOMMIT OFF;

INSERT INTO Albums (album_id, album_owner_id, album_name, album_created_time, album_modified_time, album_link, album_visibility, cover_photo_id)
SELECT DISTINCT album_id, owner_id, album_name, album_created_time, album_modified_time, album_link, album_visibility, cover_photo_id
FROM PROJECT1.Public_Photo_Information;

INSERT INTO Photos (photo_id, album_id, photo_caption, photo_created_time, photo_modified_time, photo_link)
SELECT DISTINCT photo_id, album_id, photo_caption, photo_created_time, photo_modified_time, photo_link
FROM PROJECT1.Public_Photo_Information;

COMMIT;
SET AUTOCOMMIT ON;

INSERT INTO Tags (tag_photo_id, tag_subject_id, tag_created_time, tag_x, tag_y)
SELECT DISTINCT photo_id, tag_subject_id, tag_created_time, tag_x_coordinate, tag_y_coordinate 
FROM PROJECT1.Public_Tag_Information;