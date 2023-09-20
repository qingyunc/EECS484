CREATE TABLE Users(
    user_id INTEGER NOT NULL,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    year_of_birth INTEGER,
    month_of_birth INTEGER,
    day_of_birth INTEGER,
    gender VARCHAR2(100),
    PRIMARY KEY(user_id)
);

CREATE TABLE Friends(
    user1_id INTEGER NOT NULL,
    user2_id INTEGER NOT NULL,
    UNIQUE(user1_id, user2_id),
    FOREIGN KEY(user1_id) REFERENCES Users(user_id) ON delete cascade,
    FOREIGN KEY(user2_id) REFERENCES Users(user_id) ON delete cascade,
    CHECK(user1_id < user2_id)
);

CREATE TABLE Cities(
    city_id INTEGER NOT NULL,
    city_name VARCHAR2(100) NOT NULL,
    state_name VARCHAR2(100) NOT NULL,
    country_name VARCHAR2(100) NOT NULL,
    UNIQUE(city_name, state_name, country_name),
    PRIMARY KEY(city_id)
);

CREATE TABLE User_Current_Cities(
    user_id INTEGER NOT NULL,
    current_city_id INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON delete cascade,
    FOREIGN KEY(current_city_id) REFERENCES Cities(city_id) ON delete cascade,
    UNIQUE(user_id)
);

CREATE TABLE User_Hometown_Cities(
    user_id INTEGER NOT NULL,
    hometown_city_id INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON delete cascade,
    FOREIGN KEY(hometown_city_id) REFERENCES Cities(city_id) ON delete cascade,
    UNIQUE(user_id)
);

CREATE TABLE Programs(
    program_id INTEGER NOT NULL,
    institution VARCHAR2(100) NOT NULL,
    concentration VARCHAR2(100) NOT NULL,
    degree VARCHAR2(100) NOT NULL,
    UNIQUE(institution, concentration, degree),
    PRIMARY KEY(program_id)
);

CREATE TABLE Education(
    user_id INTEGER NOT NULL,
    program_id INTEGER NOT NULL,
    program_year INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON delete cascade,
    FOREIGN KEY(program_id) REFERENCES Programs(program_id) ON delete cascade,
    UNIQUE(user_id, program_id)
);

CREATE TABLE Messages(
    message_id INTEGER NOT NULL,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    message_content VARCHAR2(2000) NOT NULL,
    sent_time TIMESTAMP NOT NULL,
    FOREIGN KEY(sender_id) REFERENCES Users(user_id) ON delete cascade,
    FOREIGN KEY(receiver_id) REFERENCES Users(user_id) ON delete cascade,
    PRIMARY KEY(message_id)
);

CREATE TABLE User_Events(
    event_id INTEGER NOT NULL,
    event_creator_id INTEGER NOT NULL,
    event_name VARCHAR2(100) NOT NULL,
    event_tagline VARCHAR2(100),
    event_description VARCHAR2(100),
    event_host VARCHAR2(100),
    event_type VARCHAR2(100),
    event_subtype VARCHAR2(100),
    event_address VARCHAR2(100),
    event_city_id INTEGER NOT NULL,
    event_start_time TIMESTAMP,
    event_end_time TIMESTAMP,
    FOREIGN KEY(event_creator_id) REFERENCES Users(user_id) ON delete cascade,
    FOREIGN KEY(event_city_id) REFERENCES Cities(city_id) ON delete cascade,
    PRIMARY KEY(event_id),
    UNIQUE(event_creator_id, event_id),
    UNIQUE(event_city_id, event_id)
);

CREATE TABLE Participants(
    event_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    confirmation VARCHAR2(100) NOt NULL,
    FOREIGN KEY(event_id) REFERENCES User_Events(event_id) ON delete cascade,
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON delete cascade,
    UNIQUE(event_id, user_id),
    CHECK(confirmation IN ('Attending', 'Unsure', 'Declines', 'Not_Replied'))
);

--According to Piazza, at least one relationship is not required

CREATE TABLE Photos(
    photo_id INTEGER NOT NULL,
    album_id INTEGER NOT NULL,
    photo_caption VARCHAR2(2000),
    photo_created_time TIMESTAMP NOT NULL,
    photo_modified_time TIMESTAMP,
    photo_link VARCHAR2(2000) NOT NULL,
    PRIMARY KEY(photo_id)
);

CREATE TABLE Albums(
    album_id INTEGER NOT NULL,
    album_name VARCHAR2(100) NOT NULL,
    album_owner_id INTEGER NOT NULL,
    album_created_time TIMESTAMP NOT NULL,
    album_modified_time TIMESTAMP,
    album_link VARCHAR2(2000) NOT NULL,
    album_visibility VARCHAR2(100) NOT NULL,
    cover_photo_id INTEGER NOT NULL,
    FOREIGN KEY(album_owner_id) REFERENCES Users(user_id) ON delete cascade,
    PRIMARY KEY(album_id),
    CHECK(album_visibility IN ('Everyone', 'Friends','Friends_Of_Friends', 'Myself'))
);

Alter TABLE Photos
    ADD CONSTRAINT photo_album_id_fk
    FOREIGN KEY(album_id) REFERENCES Albums(album_id) DEFERRABLE INITIALLY DEFERRED;

Alter TABLE Albums
    ADD CONSTRAINT album_photo_id_fk
    FOREIGN KEY(cover_photo_id) REFERENCES Photos(photo_id) DEFERRABLE INITIALLY DEFERRED;

CREATE TABLE Tags(
    tag_photo_id INTEGER NOT NULL,
    tag_subject_id INTEGER NOT NULL,
    tag_created_time TIMESTAMP NOT NULL,
    tag_x NUMBER NOT NULL,
    tag_y NUMBER NOT NULL,
    FOREIGN KEY(tag_photo_id) REFERENCES Photos(photo_id) ON delete cascade,
    FOREIGN KEY(tag_subject_id) REFERENCES Users(user_id) ON delete cascade,
    UNIQUE(tag_photo_id, tag_subject_id)
);

CREATE SEQUENCE city_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TRIGGER City_id_trigger
    BEFORE insert ON Cities
    FOR EACH ROW
        begin
          SELECT city_seq.nextval INTO :NEW.city_id FROM dual;
        end;
/

CREATE SEQUENCE program_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TRIGGER Program_id_trigger
    BEFORE insert ON Programs
    FOR EACH ROW
        begin
          SELECT program_seq.nextval INTO :NEW.program_id FROM dual;
        end;
/

CREATE TRIGGER Order_Friend_Pairs
    BEFORE insert ON Friends
    FOR EACH ROW
        DECLARE temp INTEGER;
        begin
          IF :NEW.user1_id > :NEW.user2_id THEN temp := :NEW.user2_id;
            :NEW.user2_id := :NEW.user1_id;
            :NEW.user1_id := temp;
          END IF;
        end;
/