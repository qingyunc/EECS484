CREATE VIEW View_User_Information AS
SELECT Users.user_id, first_name, last_name, year_of_birth, month_of_birth, day_of_birth, gender, current_city, current_state,
current_country, hometown_city, hometown_state, hometown_country, institution_name, program_year, program_concentration,
program_degree
FROM Users 
INNER JOIN User_Current_Cities ON Users.user_id = User_Current_Cities.user_id
INNER JOIN (SELECT city_id, city_name AS current_city, state_name AS current_state, country_name AS current_country FROM Cities) Cities ON User_Current_Cities.current_city_id = Cities.city_id
INNER JOIN User_Hometown_Cities ON Users.user_id = User_Hometown_Cities.user_id
INNER JOIN (SELECT city_id, city_name AS hometown_city, state_name AS hometown_state, country_name AS hometown_country FROM Cities) Cities ON User_Hometown_Cities.hometown_city_id = Cities.city_id
LEFT JOIN Education ON Users.user_id = Education.user_id
LEFT JOIN (SELECT program_id, institution AS institution_name, concentration AS program_concentration, degree AS program_degree FROM Programs) Programs ON Education.program_id = Programs.program_id;

CREATE VIEW  View_Are_Friends AS
SELECT user1_id, user2_id 
FROM Friends;

CREATE VIEW View_Photo_Information AS
SELECT Albums.album_id, owner_id, cover_photo_id, album_name, album_created_time, album_modified_time, album_link, album_visibility,
photo_id, photo_caption, photo_created_time, photo_modified_time, photo_link
FROM (SELECT album_id, album_owner_id as owner_id, album_name, album_created_time, album_modified_time, album_link, album_visibility, cover_photo_id FROM Albums) Albums
INNER JOIN Photos ON Albums.album_id = Photos.album_id;

CREATE VIEW View_Tag_Information AS
SELECT tag_photo_id "photo_id", tag_subject_id, tag_created_time, tag_x "tag_x_coordinate", tag_y "tag_y_coordinate"
FROM Tags;

CREATE VIEW View_Event_Information AS
SELECT event_id, event_creator_id, event_name, event_tagline, event_description, event_host, event_type, event_subtype, event_address, event_city, event_state,
event_country, event_start_time, event_end_time
FROM User_Events
INNER JOIN (SELECT city_id, city_name AS event_city, state_name AS event_state, country_name AS event_country FROM Cities) Cities ON User_Events.event_city_id = Cities.city_id;
