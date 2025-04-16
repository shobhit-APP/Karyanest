-- liquibase formatted sql

-- changeset shobh:1742204445843-1
CREATE TABLE addresses (id BIGINT AUTO_INCREMENT NOT NULL, area VARCHAR(255) NULL, city VARCHAR(255) NULL, country VARCHAR(255) NULL, district VARCHAR(255) NULL, latitude DOUBLE NULL, location_address VARCHAR(255) NULL, longitude DOUBLE NULL, nearby_landmarks VARCHAR(255) NULL, pincode VARCHAR(255) NULL, state VARCHAR(255) NULL, CONSTRAINT PK_ADDRESSES PRIMARY KEY (id));

-- changeset shobh:1742204445843-2
CREATE TABLE lead_notes (noteId BIGINT AUTO_INCREMENT NOT NULL, createdAt datetime NOT NULL, note TEXT NOT NULL, updatedAt datetime NOT NULL, leadId BIGINT NOT NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_LEAD_NOTES PRIMARY KEY (noteId));

-- changeset shobh:1742204445843-3
CREATE TABLE leads (Id BIGINT AUTO_INCREMENT NOT NULL, assignedBy VARCHAR(255) NULL, assignedTo VARCHAR(255) NULL, createdAt datetime NOT NULL, email VARCHAR(255) NOT NULL, isArchived BIT(1) NOT NULL, leadDetails TEXT NULL, message TEXT NULL, name VARCHAR(255) NOT NULL, phone VARCHAR(20) NOT NULL, source VARCHAR(255) NULL, status ENUM('CLOSED_DEAL', 'CONTACTED', 'INTERESTED', 'LOST_LEAD', 'NEW') NOT NULL, updatedAt datetime NOT NULL, admin_id BIGINT NULL, agent_id BIGINT NULL, property_id BIGINT NOT NULL, CONSTRAINT PK_LEADS PRIMARY KEY (Id), UNIQUE (email));

-- changeset shobh:1742204445843-4
CREATE TABLE properties (id BIGINT AUTO_INCREMENT NOT NULL, ageOfProperty VARCHAR(255) NULL, amenities TEXT NULL, area_size DECIMAL(10, 2) NOT NULL, area_unit ENUM('ACRES', 'HECTARES', 'SQ_FT', 'SQ_YARDS') NOT NULL, balconies INT NOT NULL, bathrooms INT NOT NULL, bedrooms INT NOT NULL, city VARCHAR(100) NOT NULL, constructionStatus ENUM('READY_TO_MOVE', 'UNDER_CONSTRUCTION') NOT NULL, country VARCHAR(100) NOT NULL, created_at datetime NULL, currency VARCHAR(10) NOT NULL, `description` TEXT NULL, electricityAvailability BIT(1) NOT NULL, facingDirection ENUM('EAST', 'NORTH', 'NORTH_EAST', 'NORTH_WEST', 'SOUTH', 'SOUTH_EAST', 'SOUTH_WEST', 'WEST') NULL, floorNumber INT NULL, furnishedStatus ENUM('FURNISHED', 'SEMI_FURNISHED', 'UNFURNISHED') NULL, latitude DECIMAL(38, 2) NULL, listing_type ENUM('FOR_RENT', 'FOR_SALE', 'LEASE') NOT NULL, locationAddress VARCHAR(255) NOT NULL, longitude DECIMAL(38, 2) NULL, nearbyLandmarks TEXT NULL, ownershipType ENUM('COOPERATIVE', 'FREEHOLD', 'LEASEHOLD') NOT NULL, parkingSpaces INT NOT NULL, pincode VARCHAR(10) NOT NULL, price DECIMAL(15, 2) NOT NULL, property_type ENUM('FLAT', 'HOUSE', 'LAND', 'PLOT') NOT NULL, roadWidth DECIMAL(38, 2) NULL, securityFeatures VARCHAR(255) NULL, state VARCHAR(100) NOT NULL, status ENUM('AVAILABLE', 'DELETED', 'PENDING', 'RENTED', 'SOLD') NULL, title VARCHAR(255) NOT NULL, totalFloors INT NULL, updated_at datetime NULL, verificationStatus ENUM('PENDING', 'REJECTED', 'VERIFIED') NULL, videoUrl VARCHAR(255) NULL, waterAvailability BIT(1) NOT NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_PROPERTIES PRIMARY KEY (id));

-- changeset shobh:1742204445843-5
CREATE TABLE properties_documentlinks (properties_id BIGINT NOT NULL, documentLinks JSON NULL);

-- changeset shobh:1742204445843-6
CREATE TABLE properties_images (properties_id BIGINT NOT NULL, images JSON NULL);

-- changeset shobh:1742204445843-7
CREATE TABLE role_permissions (id BIGINT AUTO_INCREMENT NOT NULL, permission_id BIGINT NOT NULL, role_id BIGINT NOT NULL, route_id BIGINT NOT NULL, CONSTRAINT PK_ROLE_PERMISSIONS PRIMARY KEY (id));

-- changeset shobh:1742204445843-8
CREATE TABLE routes (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, `path` VARCHAR(255) NOT NULL, CONSTRAINT PK_ROUTES PRIMARY KEY (id), UNIQUE (name), UNIQUE (`path`));

-- changeset shobh:1742204445843-9
CREATE TABLE routes_action (id BIGINT AUTO_INCREMENT NOT NULL, `description` VARCHAR(255) NULL, name VARCHAR(255) NOT NULL, routes_id BIGINT NOT NULL, CONSTRAINT PK_ROUTES_ACTION PRIMARY KEY (id));

-- changeset shobh:1742204445843-10
CREATE TABLE transactions (id BIGINT AUTO_INCREMENT NOT NULL, amount DECIMAL(15, 2) NOT NULL, status ENUM('COMPLETED', 'FAILED', 'PENDING') NOT NULL, transaction_date datetime NOT NULL, transaction_type ENUM('COMMISSION', 'DEPOSIT', 'RENT', 'SALE') NOT NULL, property_id BIGINT NOT NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_TRANSACTIONS PRIMARY KEY (id));

-- changeset shobh:1742204445843-11
CREATE TABLE user (id BIGINT AUTO_INCREMENT NOT NULL, address VARCHAR(255) NULL, city VARCHAR(100) NULL, country VARCHAR(100) NULL, created_at datetime NOT NULL, email VARCHAR(255) NOT NULL, fullname VARCHAR(100) NULL, last_login datetime NULL, password VARCHAR(255) NOT NULL, phone_number VARCHAR(15) NULL, pincode VARCHAR(10) NULL, preferences JSON NULL, profile_picture VARCHAR(255) NULL, refer_code VARCHAR(255) NULL, registration_date datetime NOT NULL, state VARCHAR(100) NULL, status ENUM('Active', 'Deleted', 'Inactive') NULL, updated_at datetime NOT NULL, username VARCHAR(255) NULL, verification_method ENUM('Documents', 'Email', 'Phone') NULL, verification_status ENUM('Pending', 'Rejected', 'Unverified', 'Verified') NULL, role_id BIGINT NOT NULL, CONSTRAINT PK_USER PRIMARY KEY (id), UNIQUE (email), UNIQUE (phone_number), UNIQUE (username));

-- changeset shobh:1742204445843-12
CREATE TABLE user_activity_logs (id BIGINT AUTO_INCREMENT NOT NULL, activity_type VARCHAR(255) NOT NULL, created_at datetime NOT NULL, `description` TEXT NOT NULL, location VARCHAR(255) NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_USER_ACTIVITY_LOGS PRIMARY KEY (id));

-- changeset shobh:1742204445843-13
CREATE TABLE user_notifications (id BIGINT AUTO_INCREMENT NOT NULL, created_at datetime NOT NULL, message TEXT NOT NULL, notification_type ENUM('ALERT', 'PROPERTY_INQUIRY', 'REMINDER', 'TRANSACTION_UPDATE') NOT NULL, status ENUM('READ', 'SENT', 'UNSENT') NOT NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_USER_NOTIFICATIONS PRIMARY KEY (id));

-- changeset shobh:1742204445843-14
CREATE TABLE user_property_visits (id BIGINT AUTO_INCREMENT NOT NULL, device_info VARCHAR(255) NULL, location_coords VARCHAR(255) NULL, property_id BIGINT NOT NULL, user_id BIGINT NOT NULL, visit_count INT NOT NULL, visit_time datetime NOT NULL, CONSTRAINT PK_USER_PROPERTY_VISITS PRIMARY KEY (id));

-- changeset shobh:1742204445843-15
CREATE TABLE user_roles (id BIGINT AUTO_INCREMENT NOT NULL, `description` VARCHAR(255) NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_USER_ROLES PRIMARY KEY (id), UNIQUE (name));

-- changeset shobh:1742204445843-16
CREATE INDEX FK9e5ssu5c6n40gw5bgt5dg4mph ON transactions(user_id);

-- changeset shobh:1742204445843-17
CREATE INDEX FKa47a15x939nvahj26n91vk2ch ON lead_notes(user_id);

-- changeset shobh:1742204445843-18
CREATE INDEX FKanu660oaj5xkl109w5aobo8nd ON leads(admin_id);

-- changeset shobh:1742204445843-19
CREATE INDEX FKccxen340nac1r7do7w7g04y5s ON transactions(property_id);

-- changeset shobh:1742204445843-20
CREATE INDEX FKfywggbdu1wylrjgoom09owqqq ON leads(property_id);

-- changeset shobh:1742204445843-21
CREATE INDEX FKg7ooruppg84k9uhbs2yfts1tu ON user_notifications(user_id);

-- changeset shobh:1742204445843-22
CREATE INDEX FKginjv310abni51nhehfcf4she ON leads(agent_id);

-- changeset shobh:1742204445843-23
CREATE INDEX FKgpm24np9gq38h4welfqn8gmo6 ON role_permissions(route_id);

-- changeset shobh:1742204445843-24
CREATE INDEX FKh3crblnphejmcmtqupp0vojng ON user_activity_logs(user_id);

-- changeset shobh:1742204445843-25
CREATE INDEX FKhgc8c8og9nyu47lqmbgmg84jq ON role_permissions(role_id);

-- changeset shobh:1742204445843-26
CREATE INDEX FKjeru65hfcodapyg233wr3bvs2 ON properties_documentlinks(properties_id);

-- changeset shobh:1742204445843-27
CREATE INDEX FKkjjsgr8252kf35q0eu1vf5uqp ON role_permissions(permission_id);

-- changeset shobh:1742204445843-28
CREATE INDEX FKlxlprwpfpjbgxnay7bf5f23qy ON routes_action(routes_id);

-- changeset shobh:1742204445843-29
CREATE INDEX FKnepsb6ptlka31r03cw83hni43 ON lead_notes(leadId);

-- changeset shobh:1742204445843-30
CREATE INDEX FKquwk7bgwa55ofxqufldn2vb2x ON properties(user_id);

-- changeset shobh:1742204445843-31
CREATE INDEX FKs1sax5p5s4rlwt6auhlnjnce8 ON properties_images(properties_id);

-- changeset shobh:1742204445843-32
CREATE INDEX FKs360q2exkn1c63qhbgxi4jnsf ON user(role_id);

-- changeset shobh:1742204445843-33
ALTER TABLE transactions ADD CONSTRAINT FK9e5ssu5c6n40gw5bgt5dg4mph FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-34
ALTER TABLE lead_notes ADD CONSTRAINT FKa47a15x939nvahj26n91vk2ch FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-35
ALTER TABLE leads ADD CONSTRAINT FKanu660oaj5xkl109w5aobo8nd FOREIGN KEY (admin_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-36
ALTER TABLE transactions ADD CONSTRAINT FKccxen340nac1r7do7w7g04y5s FOREIGN KEY (property_id) REFERENCES properties (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-37
ALTER TABLE leads ADD CONSTRAINT FKfywggbdu1wylrjgoom09owqqq FOREIGN KEY (property_id) REFERENCES properties (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-38
ALTER TABLE user_notifications ADD CONSTRAINT FKg7ooruppg84k9uhbs2yfts1tu FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-39
ALTER TABLE leads ADD CONSTRAINT FKginjv310abni51nhehfcf4she FOREIGN KEY (agent_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-40
ALTER TABLE role_permissions ADD CONSTRAINT FKgpm24np9gq38h4welfqn8gmo6 FOREIGN KEY (route_id) REFERENCES routes (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-41
ALTER TABLE user_activity_logs ADD CONSTRAINT FKh3crblnphejmcmtqupp0vojng FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-42
ALTER TABLE role_permissions ADD CONSTRAINT FKhgc8c8og9nyu47lqmbgmg84jq FOREIGN KEY (role_id) REFERENCES user_roles (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-43
ALTER TABLE properties_documentlinks ADD CONSTRAINT FKjeru65hfcodapyg233wr3bvs2 FOREIGN KEY (properties_id) REFERENCES properties (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-44
ALTER TABLE role_permissions ADD CONSTRAINT FKkjjsgr8252kf35q0eu1vf5uqp FOREIGN KEY (permission_id) REFERENCES routes_action (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-45
ALTER TABLE routes_action ADD CONSTRAINT FKlxlprwpfpjbgxnay7bf5f23qy FOREIGN KEY (routes_id) REFERENCES routes (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-46
ALTER TABLE lead_notes ADD CONSTRAINT FKnepsb6ptlka31r03cw83hni43 FOREIGN KEY (leadId) REFERENCES leads (Id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-47
ALTER TABLE properties ADD CONSTRAINT FKquwk7bgwa55ofxqufldn2vb2x FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-48
ALTER TABLE properties_images ADD CONSTRAINT FKs1sax5p5s4rlwt6auhlnjnce8 FOREIGN KEY (properties_id) REFERENCES properties (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset shobh:1742204445843-49
ALTER TABLE user ADD CONSTRAINT FKs360q2exkn1c63qhbgxi4jnsf FOREIGN KEY (role_id) REFERENCES user_roles (id) ON UPDATE RESTRICT ON DELETE RESTRICT;


-- changeset shobh:1742119648370-11
--INSERT INTO user_roles (id, `description`, name) VALUES (1, 'Role for Agents', 'ROLE_AGENT');
--INSERT INTO user_roles (id, `description`, name) VALUES (2, 'Role for Users', 'ROLE_USER');
--INSERT INTO user_roles (id, `description`, name) VALUES (3, 'Role for Admins', 'ROLE_ADMIN');
--INSERT INTO user_roles (id, `description`, name) VALUES (4, 'ACESS MANGAGER RESOURCES', 'ROLE_MANAGER');

-- changeset shobh:1742119648370-7
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (1, NULL, NULL, NULL, '2025-03-08T01:08:22', 'h6889yghg@gmail.com', 'Harshit Srivastava', '2025-03-15T22:45:53.323285', '$2a$12$09LcThJZF3YBFdMAqDJu/.yWHRxZWAH6uRBWwDyEr3FN.GydZR4RK', '9838009999', NULL, NULL, NULL, NULL, '2025-03-08T01:08:22', NULL, 'Active', '2025-03-15T22:45:53.466485', 'Harshita@baca24', 'Email', 'Verified', 3);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (2, '123, Main Street', 'New Delhi', 'India', '2025-03-10T19:52:55', 'johndoe@example.com', 'Harshit Srivastava', '2025-03-11T00:06:50', '$2a$12$FWNGihsy9f40CszoWgMGjOPx5yb9ydorDIsLZIH1JRvCV6TFf/yAC', '9838080030', '110001', NULL, NULL, NULL, '2025-03-10T19:52:55', 'Delhi', 'Active', '2025-03-16T10:12:58.694251', 'Shobhit@4ce1a4', 'Email', 'Verified', 3);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (3, NULL, NULL, NULL, '2025-03-10T22:39:34', 'h68yghg@gmail.com', 'Yash Srivastava', NULL, '$2a$12$tU2gAyLm4vFzfEMIqWKKB.P2XFaudU1prBqh.i1adva9SSUaPPjba', '6899089008', NULL, NULL, NULL, NULL, '2025-03-10T22:39:34', NULL, 'Active', '2025-03-16T10:13:13.950107', 'Yash Srivastava@974761', 'Email', 'Verified', 1);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (4, NULL, NULL, NULL, '2025-03-10T23:17:29', 'h6yghg4@gmail.com', 'Mayank', NULL, '$2a$12$018UHNmLvztLnlOKU7T.MOt9cof60VkCtlFQICrxUnVvP4x9ubcWm', '6899089002', NULL, NULL, NULL, NULL, '2025-03-10T23:17:29', NULL, 'Active', '2025-03-16T10:13:22.937666', 'Mayank_e947bc', 'Email', 'Verified', 1);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (5, NULL, NULL, NULL, '2025-03-10T23:18', 'h6yghg@gmail.com', 'Aman Srivastava', '2025-03-10T23:51:18', '$2a$12$dred/FIY5n0SGCru4IG.HuxTjsVebpLliPSSbmb7Aojx3bzTtjmGi', '6899089007', NULL, NULL, NULL, NULL, '2025-03-10T23:18', NULL, 'Active', '2025-03-10T23:51:18', 'AmanSrivas_fa45a4', 'Email', 'Unverified', 3);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (6, NULL, NULL, NULL, '2025-03-11T13:38:41', 'shobhitsrivastava2004@gmail.com', 'Shobhit Srivastava', NULL, '$2a$12$kja.uXdkq6gioenybCdtfucdUkxMfdpXdakqEXfxO1fT2MLJHyZXO', '6899089003', NULL, NULL, NULL, NULL, '2025-03-11T13:38:41', NULL, 'Active', '2025-03-11T13:42:51', 'ShobhitSri_146863', 'Email', 'Verified', 1);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (7, NULL, NULL, NULL, '2025-03-11T13:49:04', 'bit22aiml12@bit.ac.in', 'Harsh Srivastava', '2025-03-12T15:54:14', '$2a$12$z6l0uwAvhhNwNsHs94a0Feke5MyJYUtON1AAqQEznKvmLKw5I/4Yq', '6899089006', NULL, NULL, NULL, NULL, '2025-03-11T13:49:04', NULL, 'Active', '2025-03-12T15:54:14', 'HarshSriva_42f28e', 'Email', 'Unverified', 1);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (8, NULL, NULL, NULL, '2025-03-11T13:54:27', 'shobhitsrivastava07032004@gmail.com', 'Ashish Srivastava', NULL, '$2a$12$/3Phc1OoCUiCFSi7A1KV2O11Ze5NW/9LBrymHS6tm0uSt46n5JjwK', '6307838884', NULL, NULL, NULL, NULL, '2025-03-11T13:54:27', NULL, 'Active', '2025-03-11T13:54:27', 'AshishSriv_90c509', 'Email', 'Unverified', 1);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (9, NULL, NULL, NULL, '2025-03-11T14:03:22', 'adarshpaswan1106@gmail.com', 'Adarsh Paswan', NULL, '$2a$12$ih5xFbWeQcaiVqbzZ9lZGuNaXw9N96gSYBKgHEq9HR1upyzk1.cfm', '6307830085', NULL, NULL, NULL, NULL, '2025-03-11T14:03:22', NULL, 'Active', '2025-03-11T14:03:33', 'AdarshPasw_9b6b6e', 'Email', 'Verified', 2);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (10, NULL, NULL, NULL, '2025-03-11T14:06:40', 'paswanadarsh363@gmail.com', 'Adarsh Paswan', '2025-03-16T10:28:51.783551', '$2a$12$TtEV2Peisj..FTDPKUyzq.AqVHA/Id7l.UTfx5CnSSnjyGvvtThb2', '8382815116', NULL, NULL, NULL, NULL, '2025-03-11T14:06:40', NULL, 'Active', '2025-03-16T10:28:51.910750', 'AdarshPasw_6223e9', 'Email', 'Verified', 2);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (11, NULL, NULL, NULL, '2025-03-11T21:16:44', 'shobhitguider@gmail.com', 'golu Srivastava', NULL, '$2a$12$ktCzoiuSPqLqTPz6El8UhO/ljVjN9Y5s7vKJaCWhqsn70hu0Wu74i', '9838080034', NULL, NULL, NULL, NULL, '2025-03-11T21:16:44', NULL, 'Active', '2025-03-11T21:17:36', 'goluSrivas_c0c808', 'Email', 'Verified', 2);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (12, NULL, NULL, NULL, '2025-03-11T21:19:22', 'Harsh@gmail.com', 'Harsh Srivastava', NULL, '$2a$12$aYaZkqkRAJnJRUqCgVk81.a9fe3zUxYJxRvr67kkWRDPED1jTjCAy', '8874092773', NULL, NULL, NULL, NULL, '2025-03-11T21:19:22', NULL, 'Active', '2025-03-11T21:20:03', 'HarshSriva_caf503', 'Phone', 'Verified', 2);
INSERT INTO user (id, address, city, country, created_at, email, fullname, last_login, password, phone_number, pincode, preferences, profile_picture, refer_code, registration_date, state, status, updated_at, username, verification_method, verification_status, role_id) VALUES (13, NULL, NULL, NULL, '2025-03-12T19:59:18', 'sggsjs@gmail.com', 'karan Srivastava', NULL, '$2a$12$pO7SqE9RNffn3tLx5qbTS.m4Cjntaivec2TxJzWVePVj0m90HfmSG', '9995326358', NULL, NULL, NULL, NULL, '2025-03-12T19:59:18', NULL, 'Active', '2025-03-12T20:01:52', 'karanSriva_f2f902', 'Phone', 'Verified', 2);


-- changeset shobh:1742119648370-5
--
--INSERT INTO routes (id, name, `path`) VALUES (1, 'Create new roles', '/v1/admin/create_roles');
--INSERT INTO routes (id, name, `path`) VALUES (2, 'Get all roles', '/v1/admin/get_roles');
--INSERT INTO routes (id, name, `path`) VALUES (3, 'Create new permissions', '/v1/admin/create_permits');
--INSERT INTO routes (id, name, `path`) VALUES (4, 'Get all permissions', '/v1/admin/get_permits');
--INSERT INTO routes (id, name, `path`) VALUES (5, 'Create new routes', '/v1/admin/create_routes');
--INSERT INTO routes (id, name, `path`) VALUES (6, 'Assign routes to users', '/v1/admin/assign_route_to_user');
--INSERT INTO routes (id, name, `path`) VALUES (7, 'Get all users', '/v1/users');
--INSERT INTO routes (id, name, `path`) VALUES (8, 'Get user by ID', '/v1/users/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (9, 'Update user details', '/v1/users/update_user/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (10, 'Soft delete user', '/v1/users/delete_user/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (11, 'Manage notes by Lead ID', '/v1/lead-notes/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (12, 'Add new lead note', '/v1/lead-notes/addnotes');
--INSERT INTO routes (id, name, `path`) VALUES (13, 'Get lead note by ID', '/v1/lead-notes/note/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (14, 'Mange LeadsNotes Controller', '/v1/lead-notes');
--INSERT INTO routes (id, name, `path`) VALUES (15, 'Create a lead', '/v1/leads/create');
--INSERT INTO routes (id, name, `path`) VALUES (16, 'Assign agent to lead', '/v1/leads/{id}/assign');
--INSERT INTO routes (id, name, `path`) VALUES (17, 'Get lead by ID', '/v1/leads/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (18, 'Get all leads', '/v1/leads/all');
--INSERT INTO routes (id, name, `path`) VALUES (19, 'Update lead', '/v1/leads/update/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (20, 'Delete lead', '/v1/leads/delete/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (21, 'update_user_role', '/v1/admin/update_user_role');
--INSERT INTO routes (id, name, `path`) VALUES (22, 'Creating Property routes', '/v1/props');
--INSERT INTO routes (id, name, `path`) VALUES (27, 'Property routes', '/v1/props/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (28, 'new props routes', '/v1/props/view_all');
--INSERT INTO routes (id, name, `path`) VALUES (29, 'Leads Controller', '/v1/leads/allLeadsAssignByAdmin');
--INSERT INTO routes (id, name, `path`) VALUES (31, 'Leads Controller second', '/v1/leads/allLeadsAssignedByAgent');
--INSERT INTO routes (id, name, `path`) VALUES (32, 'Leads Controller In wich You Can Getall Leads', '/v1/leads');
--INSERT INTO routes (id, name, `path`) VALUES (33, 'Leads Controller New', '/v1/leads/assign/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (35, 'Leads Controller Update', '/v1/leads/getLeadsByPropertyId/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (36, 'Get Current User details ', '/v1/users/currentUser');
--INSERT INTO routes (id, name, `path`) VALUES (37, 'Get User Activity Logs', '/v1/user_activity/logs');
--INSERT INTO routes (id, name, `path`) VALUES (38, 'Get User Activity Logs By Type', '/v1/user_activity');
--INSERT INTO routes (id, name, `path`) VALUES (39, 'Get Recent User Activity Logs', '/v1/user_activity/logs/recent');
--INSERT INTO routes (id, name, `path`) VALUES (40, 'Admin Get User Activity Logs', '/v1/user_activity/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (41, 'Get Current User Properties', '/v1/props/currentUserProperty');
--INSERT INTO routes (id, name, `path`) VALUES (42, 'Get Property Visit Details', '/v1/props/visits/{id}');
--INSERT INTO routes (id, name, `path`) VALUES (43, 'Get Property Visit COUNt ', '/v1/props/visits_count/{id}');
--
--
---- changeset shobh:1742119648370-6
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (1, 'Can create and manage roles', 'MANAGE_ROLES', 1);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (2, 'Can view roles', 'VIEW_ROLES', 2);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (3, 'Can create and manage permissions', 'MANAGE_PERMISSIONS', 3);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (4, 'Can view permissions', 'VIEW_PERMISSIONS', 4);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (5, 'Can create and manage routes', 'MANAGE_ROUTES', 5);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (6, 'Can assign routes to users', 'ASSIGN_ROUTES', 6);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (7, 'Can view all users', 'VIEW_ALL_USERS', 7);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (8, 'Can view user by ID', 'VIEW_USER_BY_ID', 8);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (9, 'Can update user details', 'UPDATE_USER', 9);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (10, 'Can soft delete users', 'DELETE_USER', 10);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (11, 'Can view notes by Lead ID', 'VIEW_LEAD_NOTES', 11);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (12, 'Can add new lead notes', 'ADD_LEAD_NOTE', 12);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (13, 'Can view lead note by ID', 'VIEW_LEAD_NOTE_BY_ID', 13);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (14, 'Can delete lead note by ID', 'DELETE_LEAD_NOTE', 14);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (15, 'Can create a lead', 'CREATE_LEAD', 15);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (16, 'Can assign agent to lead', 'ASSIGN_LEAD_AGENT', 16);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (17, 'Can view lead by ID', 'VIEW_LEAD_BY_ID', 17);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (18, 'Can view all leads', 'VIEW_ALL_LEADS', 18);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (19, 'Can update lead details', 'UPDATE_LEAD', 19);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (20, 'Can delete lead', 'DELETE_LEAD', 20);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (21, 'Can update user details', 'UPDATE_USER_ROLE', 21);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (22, 'Manage All Property Controller', 'MANGAGE_ALL_PROPS', 22);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (24, 'Manage VIEW ALL Property Controller', 'VIEW_ALL_AVAILABLE_PROPS', 27);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (26, 'VIEW ALL PROPS', 'VIEW_ALL_PROPS', 28);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (27, ' All leads', 'VIEW_ALL_LEADS', 29);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (28, ' All of the leads', 'VIEW_ALL_LEADS_FROM_DATABSE', 32);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (29, ' All of the leads assginBY agent', 'VIEW_ALL_LEADS_OF_AGENT_FROM_DATABSE', 31);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (30, 'Assing leads controller', 'Assign a  leads ', 33);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (31, 'GET leads By props id', 'get leads By props id', 35);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (32, 'GET Current User', 'get current user', 36);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (33, 'GET User Activity Logs', 'get user activity logs', 37);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (34, 'GET User Activity Logs By Type', 'get user activity logs by type', 38);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (35, 'GET Recent User Activity Logs', 'get recent user activity logs', 39);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (36, 'GET Admin User Activity Logs', 'admin get user activity logs', 40);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (37, 'GET Current User Properties', 'get current user properties', 41);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (38, 'Get Property View Activity', 'View_UserActivityON_PROPS', 42);
--INSERT INTO routes_action (id, `description`, name, routes_id) VALUES (39, 'Get Property View Count of User_property_Vist_Activity', 'GET_COUNT_UserActivityON_PROPS', 43);
--
---- changeset shobh:1742119648370-4
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (1, 1, 3, 1);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (2, 2, 3, 2);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (3, 3, 3, 3);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (4, 4, 3, 4);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (5, 5, 3, 5);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (6, 6, 3, 6);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (7, 7, 3, 7);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (8, 8, 3, 8);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (9, 9, 3, 9);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (10, 10, 3, 10);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (11, 11, 3, 11);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (12, 12, 3, 12);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (13, 13, 3, 13);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (14, 14, 3, 14);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (15, 15, 3, 15);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (16, 16, 3, 16);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (17, 17, 3, 17);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (18, 18, 3, 18);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (19, 19, 3, 19);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (20, 20, 3, 20);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (21, 21, 3, 21);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (22, 22, 3, 22);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (24, 24, 3, 27);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (26, 26, 3, 28);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (27, 27, 3, 29);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (28, 28, 3, 32);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (29, 29, 3, 31);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (30, 30, 3, 33);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (31, 30, 3, 33);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (32, 31, 3, 35);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (33, 32, 3, 36);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (34, 33, 3, 37);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (35, 34, 3, 38);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (36, 35, 3, 39);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (37, 36, 1, 40);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (38, 37, 3, 41);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (39, 36, 3, 40);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (40, 38, 3, 42);
--INSERT INTO role_permissions (id, permission_id, role_id, route_id) VALUES (41, 39, 3, 43);

-- changeset shobh:1742119648370-3
INSERT INTO properties (id, ageOfProperty, amenities, area_size, area_unit, balconies, bathrooms, bedrooms, city, constructionStatus, country, created_at, currency, `description`, electricityAvailability, facingDirection, floorNumber, furnishedStatus, latitude, listing_type, locationAddress, longitude, nearbyLandmarks, ownershipType, parkingSpaces, pincode, price, property_type, roadWidth, securityFeatures, state, status, title, totalFloors, updated_at, verificationStatus, videoUrl, waterAvailability, user_id) VALUES (1, '5 years', 'Swimming Pool, Gym', 1200.50, 'SQ_FT', 2, 3, 2, 'Mumbai', 'READY_TO_MOVE', 'India', '2025-03-15T22:14:54', 'INR', 'A beautiful 2BHK flat near city center', 1, 'EAST', 5, 'FURNISHED', 19.08, 'FOR_SALE', 'Bandra West, Mumbai', 72.88, 'Near Bandra Station', 'FREEHOLD', 1, '400050', 15000000.00, 'FLAT', 10.50, 'CCTV, Security Guard', 'Maharashtra', 'AVAILABLE', 'Luxury 2BHK Flat', 10, '2025-03-15T22:14:54', 'VERIFIED', 'https://example.com/video1.mp4', 1, 1);
INSERT INTO properties (id, ageOfProperty, amenities, area_size, area_unit, balconies, bathrooms, bedrooms, city, constructionStatus, country, created_at, currency, `description`, electricityAvailability, facingDirection, floorNumber, furnishedStatus, latitude, listing_type, locationAddress, longitude, nearbyLandmarks, ownershipType, parkingSpaces, pincode, price, property_type, roadWidth, securityFeatures, state, status, title, totalFloors, updated_at, verificationStatus, videoUrl, waterAvailability, user_id) VALUES (2, 'Under Construction', 'Garden, Parking', 500.00, 'SQ_YARDS', 1, 2, 1, 'Delhi', 'UNDER_CONSTRUCTION', 'India', '2025-03-15T22:14:54', 'INR', 'Upcoming premium villa in South Delhi', 1, 'NORTH', NULL, 'SEMI_FURNISHED', 28.70, 'FOR_SALE', 'South Extension, Delhi', 77.10, 'Near AIIMS', 'LEASEHOLD', 2, '110001', 25000000.00, 'HOUSE', 12.00, '24x7 Security', 'Delhi', 'PENDING', '3BHK Luxury Villa', NULL, '2025-03-15T22:14:54', 'PENDING', NULL, 1, 1);
INSERT INTO properties (id, ageOfProperty, amenities, area_size, area_unit, balconies, bathrooms, bedrooms, city, constructionStatus, country, created_at, currency, `description`, electricityAvailability, facingDirection, floorNumber, furnishedStatus, latitude, listing_type, locationAddress, longitude, nearbyLandmarks, ownershipType, parkingSpaces, pincode, price, property_type, roadWidth, securityFeatures, state, status, title, totalFloors, updated_at, verificationStatus, videoUrl, waterAvailability, user_id) VALUES (3, '10 years', 'Park, Gym, Clubhouse', 2.50, 'ACRES', 3, 4, 4, 'Bangalore', 'READY_TO_MOVE', 'India', '2025-03-15T22:14:54', 'INR', 'Spacious independent house with garden', 1, 'SOUTH_WEST', 1, 'FURNISHED', 12.97, 'FOR_RENT', 'Koramangala, Bangalore', 77.59, 'Near Forum Mall', 'COOPERATIVE', 2, '560034', 75000.00, 'HOUSE', 8.00, 'Intercom, Security Cameras', 'Karnataka', 'RENTED', 'Independent 4BHK House', 2, '2025-03-15T22:14:54', 'VERIFIED', 'https://example.com/video3.mp4', 1, 1);
INSERT INTO properties (id, ageOfProperty, amenities, area_size, area_unit, balconies, bathrooms, bedrooms, city, constructionStatus, country, created_at, currency, `description`, electricityAvailability, facingDirection, floorNumber, furnishedStatus, latitude, listing_type, locationAddress, longitude, nearbyLandmarks, ownershipType, parkingSpaces, pincode, price, property_type, roadWidth, securityFeatures, state, status, title, totalFloors, updated_at, verificationStatus, videoUrl, waterAvailability, user_id) VALUES (4, 'New', 'Close to Market, Schools', 1500.00, 'SQ_FT', 2, 3, 3, 'Hyderabad', 'READY_TO_MOVE', 'India', '2025-03-15T22:14:54', 'INR', 'Brand new 3BHK apartment', 1, 'NORTH_EAST', 7, 'UNFURNISHED', 17.39, 'LEASE', 'Gachibowli, Hyderabad', 78.49, 'Near Microsoft Office', 'FREEHOLD', 1, '500032', 32000.00, 'FLAT', 9.50, 'Gated Community', 'Telangana', 'AVAILABLE', '3BHK Apartment for Lease', 10, '2025-03-15T22:14:54', 'VERIFIED', 'https://example.com/video4.mp4', 1, 1);
INSERT INTO properties (id, ageOfProperty, amenities, area_size, area_unit, balconies, bathrooms, bedrooms, city, constructionStatus, country, created_at, currency, `description`, electricityAvailability, facingDirection, floorNumber, furnishedStatus, latitude, listing_type, locationAddress, longitude, nearbyLandmarks, ownershipType, parkingSpaces, pincode, price, property_type, roadWidth, securityFeatures, state, status, title, totalFloors, updated_at, verificationStatus, videoUrl, waterAvailability, user_id) VALUES (5, 'New', 'Near Metro Station', 600.00, 'SQ_YARDS', 0, 0, 0, 'Chennai', 'READY_TO_MOVE', 'India', '2025-03-15T22:14:54', 'INR', 'Commercial plot near IT hub', 1, 'WEST', NULL, 'UNFURNISHED', 13.08, 'FOR_SALE', 'OMR Road, Chennai', 80.27, 'Near Infosys Campus', 'FREEHOLD', 0, '600096', 5500000.00, 'PLOT', 15.00, 'Boundary Wall, Security', 'Tamil Nadu', 'AVAILABLE', 'Commercial Plot for Sale', NULL, '2025-03-15T22:14:54', 'VERIFIED', 'https://example.com/video5.mp4', 1, 1);

-- changeset shobh:1742119648370-2
INSERT INTO leads (Id, assignedBy, assignedTo, createdAt, email, isArchived, leadDetails, message, name, phone, source, status, updatedAt, admin_id, agent_id, property_id) VALUES (1, NULL, NULL, '2025-03-12T18:45:18', 'john@example.com', 1, 'Looking for a 2BHK apartment', 'Need an urgent response', 'John Doe', '9876543210', 'Website', 'NEW', '2025-03-14T01:26:37', 1, 6, 1);
INSERT INTO leads (Id, assignedBy, assignedTo, createdAt, email, isArchived, leadDetails, message, name, phone, source, status, updatedAt, admin_id, agent_id, property_id) VALUES (2, NULL, NULL, '2025-03-12T19:28:14', 'john.doe@example.com', 1, 'Looking for a 3BHK apartment.', 'Please share more details about the property.', 'John Doe', '+1234567890', 'Website', 'NEW', '2025-03-14T01:30', 1, 3, 1);
INSERT INTO leads (Id, assignedBy, assignedTo, createdAt, email, isArchived, leadDetails, message, name, phone, source, status, updatedAt, admin_id, agent_id, property_id) VALUES (5, NULL, NULL, '2025-03-12T19:50:38', 'jane.smith@example.com', 0, 'Looking for a villa in a prime location.', 'Interested in the pricing and payment options.', 'Jane Smith', '+9876543210', 'Social Media', 'CONTACTED', '2025-03-12T19:50:38', 2, 4, 1);
INSERT INTO leads (Id, assignedBy, assignedTo, createdAt, email, isArchived, leadDetails, message, name, phone, source, status, updatedAt, admin_id, agent_id, property_id) VALUES (7, 'Yash Srivastava', NULL, '2025-03-15T16:20:09', 'john.do1e@example.com', 0, 'Searching for a spacious apartment with modern amenities.', 'Would like to schedule a visit this weekend.', 'John Doe', '+1234567890', 'Real Estate Website', 'NEW', '2025-03-15T16:20:09', 3, 7, 5);
INSERT INTO leads (Id, assignedBy, assignedTo, createdAt, email, isArchived, leadDetails, message, name, phone, source, status, updatedAt, admin_id, agent_id, property_id) VALUES (9, 'Yash Srivastava', 'Ashish Srivastava', '2025-03-15T16:23:54', 'john.do1e8@example.com', 0, 'Searching for a spacious apartment with modern amenities.', 'Would like to schedule a visit this weekend.', 'John Doe', '8234567890', 'Real Estate Website', 'NEW', '2025-03-15T16:23:54', 3, 8, 5);
INSERT INTO leads (Id, assignedBy, assignedTo, createdAt, email, isArchived, leadDetails, message, name, phone, source, status, updatedAt, admin_id, agent_id, property_id) VALUES (10, 'Yash Srivastava', 'Yash Srivastava', '2025-03-15T16:58:22', 'john.55o1e8@example.com', 0, 'Searching for a spacious apartment with modern amenities.', 'Would like to schedule a visit this weekend.', 'John Doe', '8234567890', 'Real Estate Website', 'NEW', '2025-03-15T16:58:22', 3, 3, 5);


-- changeset shobh:1742119648370-1
INSERT INTO lead_notes (noteId, createdAt, note, updatedAt,leadId, user_id) VALUES (2, '2025-03-15T16:20:09', 'Requested a virtual tour before an in-person visit.','2025-03-16T09:52:37.823874', 7, 1);
INSERT INTO lead_notes (noteId, createdAt, note, updatedAt,leadId, user_id) VALUES (3, '2025-03-15T16:23:54', 'Requested a virtual tour before an in-person visit.','2025-03-16T09:52:37.823874', 9, 1);
INSERT INTO lead_notes (noteId, createdAt, note, updatedAt,leadId, user_id) VALUES (4, '2025-03-15T16:58:23', 'Requested a virtual tour before an in-person visit.','2025-03-16T09:52:37.823874', 10, 1);
INSERT INTO lead_notes (noteId, createdAt, note, updatedAt,leadId, user_id) VALUES (5, '2025-03-16T04:06:33.406772', 'Following up with the client regarding project requirements.', '2025-03-16T09:56:13.137093', 1, 1);


-- changeset shobh:1742119648370-8
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (1, 'LOGIN', '2025-03-08T19:51:32', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (2, 'LOGIN', '2025-03-08T19:53:35', 'PATCH /v1/admin/update_user_role - Status: 403 - Duration: 291 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (3, 'LOGIN', '2025-03-08T20:07:48', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (4, 'LOGIN', '2025-03-08T20:08:05', 'PATCH /v1/admin/update_user_role - Status: 403 - Duration: 297 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (5, 'LOGIN', '2025-03-08T20:15:45', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (6, 'LOGIN', '2025-03-08T20:15:59', 'PATCH /v1/admin/update_user_role - Status: 403 - Duration: 452 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (7, 'LOGIN', '2025-03-08T20:19:44', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (8, 'LOGIN', '2025-03-08T20:20:01', 'PATCH /v1/admin/update_user_role - Status: 403 - Duration: 407 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (9, 'LOGIN', '2025-03-09T12:24:43', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (10, 'LOGIN', '2025-03-09T12:25:09', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 150 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (11, 'LOGIN', '2025-03-09T12:25:34', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 80 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (12, 'LOGIN', '2025-03-09T12:25:40', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 95 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (13, 'LOGIN', '2025-03-09T12:27:56', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 88 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (14, 'LOGIN', '2025-03-09T12:28:03', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (15, 'LOGIN', '2025-03-09T12:28:21', 'PATCH /v1/admin/update_user_role - Status: 403 - Duration: 88 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (16, 'LOGIN', '2025-03-09T12:52:25', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (17, 'LOGIN', '2025-03-09T12:55:40', 'POST /v1/admin/create_roles - Status: 200 - Duration: 182 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (18, 'LOGIN', '2025-03-09T12:55:59', 'GET /v1/admin/get_roles - Status: 200 - Duration: 81 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (19, 'LOGIN', '2025-03-09T12:57:32', 'POST /v1/admin/create_routes - Status: 201 - Duration: 102 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (20, 'LOGIN', '2025-03-09T13:02:47', 'POST /v1/admin/create_permits - Status: 201 - Duration: 91 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (21, 'LOGIN', '2025-03-09T13:04:23', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 139 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (22, 'LOGIN', '2025-03-09T13:07:04', 'GET /v1/props - Status: 200 - Duration: 82 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (23, 'LOGIN', '2025-03-09T13:11:40', 'POST /v1/props - Status: 201 - Duration: 110 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (24, 'LOGIN', '2025-03-09T13:12:13', 'GET /v1/props - Status: 500 - Duration: 158 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (25, 'LOGIN', '2025-03-09T13:13:04', 'GET /v1/props - Status: 500 - Duration: 121 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (26, 'LOGIN', '2025-03-09T13:42:38', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (27, 'LOGIN', '2025-03-09T13:44:55', 'POST /v1/admin/create_routes - Status: 201 - Duration: 168 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (28, 'LOGIN', '2025-03-09T13:48:29', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 405 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (29, 'LOGIN', '2025-03-09T13:56:36', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (30, 'LOGIN', '2025-03-09T13:58:40', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 330 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (31, 'LOGIN', '2025-03-09T13:59:48', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 193 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (32, 'LOGIN', '2025-03-09T14:02:34', 'GET /v1/props/my-properties - Status: 403 - Duration: 91 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (33, 'LOGIN', '2025-03-09T14:22:34', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (34, 'LOGIN', '2025-03-09T14:22:59', 'GET /v1/props - Status: 500 - Duration: 524 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (35, 'LOGIN', '2025-03-09T14:27:13', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (36, 'LOGIN', '2025-03-09T14:27:33', 'GET /v1/props - Status: 500 - Duration: 476 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (37, 'LOGIN', '2025-03-09T15:02:59', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (38, 'LOGIN', '2025-03-09T15:03:18', 'GET /v1/props - Status: 200 - Duration: 295 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (39, 'LOGIN', '2025-03-09T18:11:52', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (40, 'LOGIN', '2025-03-10T15:27:48', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (41, 'LOGIN', '2025-03-10T15:28:23', 'POST /v1/admin/create_permits - Status: 404 - Duration: 302 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (42, 'LOGIN', '2025-03-10T15:29:57', 'POST /v1/admin/create_routes - Status: 200 - Duration: 1365 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (43, 'LOGIN', '2025-03-10T15:30:24', 'POST /v1/admin/create_routes - Status: 201 - Duration: 186 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (44, 'LOGIN', '2025-03-10T15:31:29', 'POST /v1/admin/create_permits - Status: 201 - Duration: 223 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (45, 'LOGIN', '2025-03-10T15:32:01', 'POST /v1/admin/assign_route_to_user - Status: 400 - Duration: 188 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (46, 'LOGIN', '2025-03-10T15:37:30', 'POST /v1/admin/create_routes - Status: 200 - Duration: 88 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (47, 'LOGIN', '2025-03-10T15:39:16', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (48, 'LOGIN', '2025-03-10T16:11:48', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (49, 'LOGIN', '2025-03-10T16:12:33', 'POST /v1/admin/create_routes - Status: 201 - Duration: 258 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (50, 'LOGIN', '2025-03-10T16:13:08', 'POST /v1/admin/create_permits - Status: 201 - Duration: 134 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (51, 'LOGIN', '2025-03-10T16:13:40', 'POST /v1/admin/assign_route_to_user - Status: 400 - Duration: 73 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (52, 'LOGIN', '2025-03-10T16:14:03', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 126 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (53, 'LOGIN', '2025-03-10T16:15:20', 'GET /v1/props/1 - Status: 403 - Duration: 37 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (54, 'LOGIN', '2025-03-10T16:16:46', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (55, 'LOGIN', '2025-03-10T16:17:13', 'GET /v1/props/1 - Status: 403 - Duration: 254 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (56, 'LOGIN', '2025-03-10T16:34:03', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (57, 'LOGIN', '2025-03-10T16:34:22', 'GET /v1/props/1 - Status: 200 - Duration: 332 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (58, 'LOGIN', '2025-03-10T16:34:30', 'GET /v1/props/3 - Status: 400 - Duration: 171 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (59, 'LOGIN', '2025-03-10T16:57:20', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (60, 'LOGIN', '2025-03-10T16:58:44', 'POST /v1/admin/create_routes - Status: 201 - Duration: 406 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (61, 'LOGIN', '2025-03-10T17:00:17', 'POST /v1/admin/create_permits - Status: 200 - Duration: 179 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (62, 'LOGIN', '2025-03-10T17:01:29', 'POST /v1/admin/create_permits - Status: 200 - Duration: 120 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (63, 'LOGIN', '2025-03-10T17:02:55', 'POST /v1/admin/create_permits - Status: 500 - Duration: 269 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (64, 'LOGIN', '2025-03-10T17:11:33', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (65, 'LOGIN', '2025-03-10T17:12:41', 'POST /v1/admin/create_permits - Status: 200 - Duration: 357 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (66, 'LOGIN', '2025-03-10T17:21:58', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (67, 'LOGIN', '2025-03-10T17:22:29', 'POST /v1/admin/create_permits - Status: 201 - Duration: 397 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (68, 'LOGIN', '2025-03-10T17:24:15', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 190 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (69, 'LOGIN', '2025-03-10T17:25:27', 'GET /v1/props/view_all - Status: 403 - Duration: 68 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (70, 'LOGIN', '2025-03-10T17:26:19', 'GET /v1/props/view_all - Status: 403 - Duration: 62 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (71, 'LOGIN', '2025-03-10T17:28:21', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 199 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (72, 'LOGIN', '2025-03-10T17:28:32', 'GET /v1/props/view_all - Status: 200 - Duration: 156 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (73, 'LOGIN', '2025-03-10T17:56:25', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (74, 'LOGIN', '2025-03-10T17:59:45', 'PUT /v1/props - Status: 200 - Duration: 283 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (75, 'LOGIN', '2025-03-10T18:04:29', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (76, 'LOGIN', '2025-03-10T18:04:43', 'PUT /v1/props - Status: 200 - Duration: 185 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (77, 'LOGIN', '2025-03-10T19:16:19', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (78, 'LOGIN', '2025-03-10T19:55:52', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (79, 'LOGIN', '2025-03-10T19:56:08', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 420 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (80, 'LOGIN', '2025-03-10T20:11:20', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (81, 'LOGIN', '2025-03-10T20:11:44', 'GET /v1/leads/create - Status: 400 - Duration: 339 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (82, 'LOGIN', '2025-03-10T20:18:18', 'POST /v1/leads/create - Status: 400 - Duration: 389 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (83, 'LOGIN', '2025-03-10T20:50:20', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (84, 'LOGIN', '2025-03-10T20:50:46', 'POST /v1/leads/create - Status: 400 - Duration: 496 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (85, 'LOGIN', '2025-03-10T21:43:35', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (86, 'LOGIN', '2025-03-10T21:43:54', 'POST /v1/props - Status: 201 - Duration: 176 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (87, 'LOGIN', '2025-03-10T21:44:31', 'POST /v1/props - Status: 201 - Duration: 80 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (88, 'LOGIN', '2025-03-10T21:45:11', 'POST /v1/props - Status: 201 - Duration: 82 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (89, 'LOGIN', '2025-03-10T21:45:24', 'POST /v1/props - Status: 201 - Duration: 69 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (90, 'LOGIN', '2025-03-10T21:45:44', 'POST /v1/props - Status: 201 - Duration: 62 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (91, 'LOGIN', '2025-03-10T23:32:13', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (92, 'LOGIN', '2025-03-10T23:33:16', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (93, 'LOGIN', '2025-03-10T23:35:51', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (94, 'LOGIN', '2025-03-10T23:36:13', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 417 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (95, 'LOGIN', '2025-03-10T23:36:39', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (96, 'LOGIN', '2025-03-10T23:37:04', 'GET /v1/props - Status: 404 - Duration: 130 ms', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (97, 'LOGIN', '2025-03-10T23:43:16', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (98, 'LOGIN', '2025-03-10T23:43:41', 'GET /v1/props - Status: 404 - Duration: 340 ms', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (99, 'LOGIN', '2025-03-10T23:49:06', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (100, 'LOGIN', '2025-03-10T23:49:20', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (101, 'LOGIN', '2025-03-10T23:50:10', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (102, 'LOGIN', '2025-03-10T23:50:18', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (103, 'LOGIN', '2025-03-10T23:51:18', 'User logged in successfully', 'Local Development', 5);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (104, 'LOGIN', '2025-03-10T23:52:15', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (105, 'LOGIN', '2025-03-10T23:53:26', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (106, 'LOGIN', '2025-03-10T23:59:20', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (107, 'LOGIN', '2025-03-11T00:00:27', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (108, 'LOGIN', '2025-03-11T00:00:49', 'PATCH /v1/admin/update_user_role - Status: 200 - Duration: 195 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (109, 'LOGIN', '2025-03-11T00:00:57', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (110, 'LOGIN', '2025-03-11T00:01:10', 'GET /v1/admin/get_roles - Status: 200 - Duration: 87 ms', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (111, 'LOGIN', '2025-03-11T00:01:26', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (112, 'LOGIN', '2025-03-11T00:01:49', 'GET /v1/admin/get_roles - Status: 200 - Duration: 89 ms', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (113, 'LOGIN', '2025-03-11T00:04:11', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (114, 'LOGIN', '2025-03-11T00:04:26', 'GET /v1/admin/get_roles - Status: 401 - Duration: 89 ms', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (115, 'LOGIN', '2025-03-11T00:06:50', 'User logged in successfully', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (116, 'LOGIN', '2025-03-11T00:07:37', 'GET /v1/admin/get_roles - Status: 401 - Duration: 151 ms', 'Local Development', 2);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (117, 'LOGIN', '2025-03-12T15:51:52', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (118, 'LOGIN', '2025-03-12T15:54:14', 'User logged in successfully', 'Local Development', 7);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (119, 'LOGIN', '2025-03-12T16:23:47', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (120, 'LOGIN', '2025-03-12T16:29:23', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (121, 'LOGIN', '2025-03-12T16:30:22', 'POST /v1/leads/create - Status: 400 - Duration: 546 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (122, 'LOGIN', '2025-03-12T17:00:11', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (123, 'LOGIN', '2025-03-12T17:02:16', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (124, 'LOGIN', '2025-03-12T18:17:19', 'POST /v1/leads/create - Status: 400 - Duration: 1649 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (125, 'LOGIN', '2025-03-12T18:45:18', 'POST /v1/leads/create - Status: 201 - Duration: 559 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (126, 'LOGIN', '2025-03-12T19:24:39', 'POST /v1/leads/create - Status: 400 - Duration: 466 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (127, 'LOGIN', '2025-03-12T19:28:14', 'POST /v1/leads/create - Status: 201 - Duration: 337 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (128, 'LOGIN', '2025-03-12T19:39:27', 'POST /v1/leads/create - Status: 400 - Duration: 1874 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (129, 'LOGIN', '2025-03-12T19:40:09', 'POST /v1/leads/create - Status: 400 - Duration: 181 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (130, 'LOGIN', '2025-03-12T19:50:38', 'POST /v1/leads/create - Status: 201 - Duration: 636 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (131, 'LOGIN', '2025-03-12T19:53:59', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (132, 'LOGIN', '2025-03-12T20:10:41', 'POST /v1/leads/1/assign - Status: 400 - Duration: 952 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (133, 'LOGIN', '2025-03-12T20:12:58', 'POST /v1/leads/1/assign - Status: 200 - Duration: 235 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (134, 'LOGIN', '2025-03-12T20:14:51', 'GET /v1/leads/1 - Status: 500 - Duration: 672 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (135, 'LOGIN', '2025-03-12T20:15:54', 'GET /v1/leads/all - Status: 500 - Duration: 235 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (136, 'LOGIN', '2025-03-12T20:59:29', 'GET /v1/leads/all - Status: 400 - Duration: 345 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (137, 'LOGIN', '2025-03-12T21:00:41', 'GET /v1/leads/all - Status: 200 - Duration: 326 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (138, 'LOGIN', '2025-03-12T21:41:59', 'GET /v1/leads/1 - Status: 403 - Duration: 254 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (139, 'LOGIN', '2025-03-12T21:50:32', 'GET /v1/leads/1 - Status: 404 - Duration: 178 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (140, 'LOGIN', '2025-03-12T21:51', 'GET /v1/leads/3 - Status: 500 - Duration: 50 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (141, 'LOGIN', '2025-03-12T21:51:19', 'GET /v1/leads/5 - Status: 200 - Duration: 74 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (142, 'LOGIN', '2025-03-13T19:35:33', 'POST /v1/admin/create_routes - Status: 201 - Duration: 719 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (143, 'LOGIN', '2025-03-13T19:36:58', 'POST /v1/admin/create_permits - Status: 201 - Duration: 193 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (144, 'LOGIN', '2025-03-13T19:38:02', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 408 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (145, 'LOGIN', '2025-03-13T19:42:22', 'GET /v1/leads/allLeadsAssignByAdmin - Status: 200 - Duration: 334 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (146, 'LOGIN', '2025-03-13T20:46:32', 'GET /v1/leads/all - Status: 400 - Duration: 375 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (147, 'LOGIN', '2025-03-13T20:47:10', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 207 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (148, 'LOGIN', '2025-03-13T20:52:13', 'GET /v1/leads/all-leads - Status: 200 - Duration: 196 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (149, 'LOGIN', '2025-03-13T21:09:58', 'GET /v1/leads/all-leads - Status: 200 - Duration: 84 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (150, 'LOGIN', '2025-03-13T21:10:06', 'GET /v1/leads - Status: 403 - Duration: 29 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (151, 'LOGIN', '2025-03-13T21:10:24', 'GET /v1/leads/jjj - Status: 400 - Duration: 45 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (152, 'LOGIN', '2025-03-13T21:10:47', 'GET /v1/leads/all-leads - Status: 200 - Duration: 64 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (153, 'LOGIN', '2025-03-13T21:11:11', 'GET /v1/leads/all - Status: 400 - Duration: 73 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (154, 'LOGIN', '2025-03-13T21:17:34', 'GET /v1/leads/all - Status: 400 - Duration: 390 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (155, 'LOGIN', '2025-03-13T21:17:41', 'GET /v1/leads/all-leads - Status: 200 - Duration: 268 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (156, 'LOGIN', '2025-03-13T21:20:57', 'GET /v1/leads/all-leads - Status: 200 - Duration: 488 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (157, 'LOGIN', '2025-03-13T22:14:54', 'GET /v1/leads/all-leads - Status: 403 - Duration: 252 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (158, 'LOGIN', '2025-03-13T22:15:08', 'GET /v1/leads/all - Status: 400 - Duration: 144 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (159, 'LOGIN', '2025-03-13T22:16:11', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 403 - Duration: 75 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (160, 'LOGIN', '2025-03-13T22:17:30', 'POST /v1/admin/create_routes - Status: 200 - Duration: 1405 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (161, 'LOGIN', '2025-03-13T22:17:49', 'POST /v1/admin/create_routes - Status: 201 - Duration: 128 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (162, 'LOGIN', '2025-03-13T22:19:08', 'POST /v1/admin/create_routes - Status: 201 - Duration: 94 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (163, 'LOGIN', '2025-03-13T22:22:11', 'POST /v1/admin/create_permits - Status: 201 - Duration: 176 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (164, 'LOGIN', '2025-03-13T22:23:29', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 223 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (165, 'LOGIN', '2025-03-13T22:25:46', 'POST /v1/admin/create_permits - Status: 500 - Duration: 350 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (166, 'LOGIN', '2025-03-13T22:42:56', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 177 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (167, 'LOGIN', '2025-03-13T22:44:35', 'GET /v1/leads/all-leads - Status: 200 - Duration: 229 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (168, 'LOGIN', '2025-03-13T22:45:14', 'GET /v1/leads/5 - Status: 403 - Duration: 95 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (169, 'LOGIN', '2025-03-13T22:46:11', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 72 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (170, 'LOGIN', '2025-03-13T22:49:27', 'GET /v1/leads/allLeadsAssignByAdmin - Status: 200 - Duration: 178 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (171, 'LOGIN', '2025-03-13T22:50:19', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 61 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (172, 'LOGIN', '2025-03-13T22:50:56', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 57 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (173, 'LOGIN', '2025-03-13T23:02', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 55 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (174, 'LOGIN', '2025-03-13T23:05:31', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (175, 'LOGIN', '2025-03-13T23:07:41', 'GET /v1/leads/all-leads - Status: 200 - Duration: 154 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (176, 'LOGIN', '2025-03-13T23:07:57', 'GET /v1/leads/1 - Status: 403 - Duration: 74 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (177, 'LOGIN', '2025-03-13T23:08:03', 'GET /v1/leads/allLeadsAssignByAdmin - Status: 200 - Duration: 121 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (178, 'LOGIN', '2025-03-13T23:08:14', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 54 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (179, 'LOGIN', '2025-03-13T23:08:25', 'GET /v1/leads/1 - Status: 403 - Duration: 32 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (180, 'LOGIN', '2025-03-13T23:08:35', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 27 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (181, 'LOGIN', '2025-03-13T23:10:11', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 62 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (182, 'LOGIN', '2025-03-13T23:26:40', 'GET /v1/leads/all-leads - Status: 200 - Duration: 194 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (183, 'LOGIN', '2025-03-13T23:26:50', 'GET /v1/leads/1 - Status: 200 - Duration: 47 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (184, 'LOGIN', '2025-03-13T23:27:33', 'GET /v1/leads/allLeadsAssignByAdmin - Status: 200 - Duration: 101 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (185, 'LOGIN', '2025-03-13T23:27:45', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 46 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (186, 'LOGIN', '2025-03-13T23:31:08', 'GET /v1/leads/1 - Status: 403 - Duration: 122 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (187, 'LOGIN', '2025-03-13T23:35:27', 'POST /v1/leads/1/assign - Status: 403 - Duration: 58 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (188, 'LOGIN', '2025-03-13T23:37:37', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 58 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (189, 'LOGIN', '2025-03-13T23:58:39', 'GET /v1/leads/allLeadsAssignByAgent - Status: 403 - Duration: 82 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (190, 'LOGIN', '2025-03-13T23:59:25', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 78 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (191, 'LOGIN', '2025-03-14T00:00:54', 'GET /v1/leads/1 - Status: 403 - Duration: 103 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (192, 'LOGIN', '2025-03-14T00:02:24', 'GET /v1/leads/1 - Status: 200 - Duration: 253 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (193, 'LOGIN', '2025-03-14T00:04:52', 'GET /v1/leads/1 - Status: 403 - Duration: 299 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (194, 'LOGIN', '2025-03-14T00:05:09', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 118 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (195, 'LOGIN', '2025-03-14T00:07:50', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 42 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (196, 'LOGIN', '2025-03-14T00:18:17', 'GET /v1/leads/allLeadsAssignByAdmin - Status: 200 - Duration: 367 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (197, 'LOGIN', '2025-03-14T00:18:29', 'GET /v1/leads/all-leads - Status: 200 - Duration: 192 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (198, 'LOGIN', '2025-03-14T00:18:40', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 72 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (199, 'LOGIN', '2025-03-14T00:29:44', 'GET /v1/leads/all-leads - Status: 200 - Duration: 446 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (200, 'LOGIN', '2025-03-14T00:30:11', 'GET /v1/leads/1 - Status: 403 - Duration: 131 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (201, 'LOGIN', '2025-03-14T00:30:37', 'GET /v1/leads/allLeadsAssignByAdmin - Status: 200 - Duration: 142 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (202, 'LOGIN', '2025-03-14T00:31:09', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 78 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (203, 'LOGIN', '2025-03-14T00:37:27', 'GET /v1/leads/allLeadsAssignedByAgent - Status: 200 - Duration: 184 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (204, 'LOGIN', '2025-03-14T00:38:25', 'GET /v1/leads/1 - Status: 404 - Duration: 202 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (205, 'LOGIN', '2025-03-14T00:38:33', 'GET /v1/leads/2 - Status: 404 - Duration: 105 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (206, 'LOGIN', '2025-03-14T00:38:38', 'GET /v1/leads/5 - Status: 200 - Duration: 119 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (207, 'LOGIN', '2025-03-14T00:47:37', 'GET /v1/leads/1 - Status: 404 - Duration: 255 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (208, 'LOGIN', '2025-03-14T00:47:46', 'GET /v1/leads/2 - Status: 404 - Duration: 153 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (209, 'LOGIN', '2025-03-14T00:51:55', 'GET /v1/leads/2 - Status: 500 - Duration: 479 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (210, 'LOGIN', '2025-03-14T00:56:41', 'GET /v1/leads/2 - Status: 404 - Duration: 360 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (211, 'LOGIN', '2025-03-14T00:56:47', 'GET /v1/leads/5 - Status: 200 - Duration: 168 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (212, 'LOGIN', '2025-03-14T01:04:33', 'DELETE /v1/leads/delete/1 - Status: 403 - Duration: 33 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (213, 'LOGIN', '2025-03-14T01:26:37', 'DELETE /v1/leads/delete/1 - Status: 200 - Duration: 468 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (214, 'LOGIN', '2025-03-14T01:30', 'DELETE /v1/leads/delete/2 - Status: 200 - Duration: 430 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (215, 'LOGIN', '2025-03-14T01:42:49', 'POST /v1/admin/create_routes - Status: 201 - Duration: 412 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (216, 'LOGIN', '2025-03-14T01:44:52', 'POST /v1/admin/create_permits - Status: 201 - Duration: 264 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (217, 'LOGIN', '2025-03-14T01:45:38', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 248 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (218, 'LOGIN', '2025-03-14T01:52:35', 'DELETE /v1/leads/delete/2 - Status: 200 - Duration: 345 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (219, 'LOGIN', '2025-03-14T01:55:54', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 267 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (220, 'LOGIN', '2025-03-15T13:03:30', 'POST /v1/admin/create_routes - Status: 200 - Duration: 1477 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (221, 'LOGIN', '2025-03-15T13:03:42', 'POST /v1/admin/create_routes - Status: 201 - Duration: 75 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (222, 'LOGIN', '2025-03-15T13:05:06', 'POST /v1/admin/create_permits - Status: 201 - Duration: 108 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (223, 'LOGIN', '2025-03-15T13:06:05', 'POST /v1/admin/assign_route_to_user - Status: 200 - Duration: 174 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (224, 'LOGIN', '2025-03-15T13:07:55', 'GET /v1/leads/getLeadsByPropertyId/1 - Status: 200 - Duration: 135 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (225, 'LOGIN', '2025-03-15T13:53:34', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (226, 'LOGIN', '2025-03-15T13:54:03', 'GET /v1/auth/getUser - Status: 500 - Duration: 213 ms', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (227, 'LOGIN', '2025-03-15T14:11:33', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (228, 'LOGIN', '2025-03-15T14:11:44', 'GET /v1/auth/getUser - Status: 200 - Duration: 36 ms', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (229, 'LOGIN', '2025-03-15T14:32:23', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (230, 'LOGIN', '2025-03-15T14:32:37', 'GET /v1/auth/getUser - Status: 200 - Duration: 115 ms', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (231, 'LOGIN', '2025-03-15T14:42:54', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (232, 'LOGIN', '2025-03-15T14:43:05', 'GET /v1/auth/getUser - Status: 200 - Duration: 106 ms', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (233, 'LOGIN', '2025-03-15T15:07:09', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (234, 'LOGIN', '2025-03-15T15:07:24', 'GET /v1/auth/getUser - Status: 404 - Duration: 7 ms', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (235, 'LOGIN', '2025-03-15T15:08:19', 'GET /v1/Users/get_all_user - Status: 403 - Duration: 208 ms', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (236, 'LOGIN', '2025-03-15T15:13:27', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (237, 'LOGIN', '2025-03-15T15:17:02', 'GET /v1/Users/get_all_user - Status: 200 - Duration: 449 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (238, 'LOGIN', '2025-03-15T15:21', 'GET /v1/Users/get_all_user - Status: 200 - Duration: 373 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (239, 'LOGIN', '2025-03-15T15:21:26', 'GET /v1/Users/get_user/5 - Status: 200 - Duration: 150 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (240, 'LOGIN', '2025-03-15T15:33:07', 'GET /v1/users - Status: 403 - Duration: 265 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (241, 'LOGIN', '2025-03-15T15:39:38', 'GET /v1/users - Status: 200 - Duration: 122 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (242, 'LOGIN', '2025-03-15T15:40:15', 'GET /v1/users/1 - Status: 200 - Duration: 64 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (243, 'LOGIN', '2025-03-15T15:49:20', 'GET /v1/users/1 - Status: 200 - Duration: 392 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (244, 'LOGIN', '2025-03-15T15:49:30', 'GET /v1/users - Status: 200 - Duration: 114 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (245, 'LOGIN', '2025-03-15T15:50', 'GET /v1/Users/5 - Status: 403 - Duration: 76 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (246, 'LOGIN', '2025-03-15T15:50:07', 'GET /v1/users/5 - Status: 200 - Duration: 148 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (247, 'LOGIN', '2025-03-15T16:17:56', 'POST /v1/leads/create - Status: 400 - Duration: 504 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (248, 'LOGIN', '2025-03-15T16:19:46', 'POST /v1/leads/create - Status: 400 - Duration: 436 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (249, 'LOGIN', '2025-03-15T16:20:09', 'POST /v1/leads/create - Status: 201 - Duration: 294 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (250, 'LOGIN', '2025-03-15T16:23:30', 'POST /v1/leads/create - Status: 400 - Duration: 517 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (251, 'LOGIN', '2025-03-15T16:23:54', 'POST /v1/leads/create - Status: 201 - Duration: 252 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (252, 'LOGIN', '2025-03-15T16:58:23', 'POST /v1/leads/create - Status: 201 - Duration: 491 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (253, 'LOGIN', '2025-03-15T22:20:16.064643', 'GET /v1/users - Status: 200 - Duration: 372 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (254, 'LOGIN', '2025-03-15T22:23:24.245110', 'GET /v1/users/5 - Status: 200 - Duration: 220 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (255, 'LOGIN', '2025-03-15T22:23:45.392734', 'DELETE /v1/users/2 - Status: 200 - Duration: 265 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (256, 'LOGIN', '2025-03-15T22:28:16.927195', 'PUT /v1/users/2 - Status: 200 - Duration: 856 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (257, 'LOGIN', '2025-03-15T22:33:13.649976', 'GET /v1/users/currentuser - Status: 403 - Duration: 99 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (258, 'LOGIN', '2025-03-15T22:33:55.538530', 'GET /v1/users/currentUser - Status: 403 - Duration: 70 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (259, 'LOGIN', '2025-03-15T22:35:57.555464', 'GET /v1/users/currentUser - Status: 200 - Duration: 391 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (260, 'LOGIN', '2025-03-15T22:40:59.682309', 'User logged in successfully', 'Local Development', 10);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (261, 'LOGIN', '2025-03-15T22:44:42.398425', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (262, 'LOGIN', '2025-03-15T22:45:53.310185', 'User logged in successfully', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (263, 'LOGIN', '2025-03-15T22:52:14.931905', 'GET /v1/users/currentUser - Status: 200 - Duration: 601 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (264, 'LOGIN', '2025-03-15T23:03:32.778951', 'GET /v1/users/currentUser - Status: 200 - Duration: 740 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (265, 'LOGIN', '2025-03-15T23:22:44.647466', 'GET /v1/props - Status: 200 - Duration: 522 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (266, 'LOGIN', '2025-03-15T23:23:22.596730', 'GET /v1/props/currentUserProperty - Status: 404 - Duration: 101 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (267, 'CHECK_USER_ACTIVITY', '2025-03-16T00:34:55.102252', 'GET /v1/user_activity/1 - Status: 404 - Duration: 132 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (268, 'CHECK_USER_ACTIVITY', '2025-03-16T00:42:35.128422', 'GET /v1/user_activity/1 - Status: 500 - Duration: 357 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (269, 'CHECK_USER_ACTIVITY', '2025-03-16T00:44:22.946700', 'GET /v1/user_activity/1 - Status: 200 - Duration: 419 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (270, 'USER_ACTIVITIES_CHECK', '2025-03-16T00:46:52.619179', 'GET /v1/user_activity/1 - Status: 200 - Duration: 217 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (271, 'LOGIN', '2025-03-16T00:54:14.766733', 'GET /v1/user_activity - Status: 404 - Duration: 309 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (272, 'LOGIN', '2025-03-16T01:00:30.369421', 'GET /v1/user_activity/ - Status: 404 - Duration: 251 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (273, 'LOGIN', '2025-03-16T01:01:09.246682', 'GET /v1/user_activity - Status: 200 - Duration: 307 ms', 'Local Development', 1);
INSERT INTO user_activity_logs (id, activity_type, created_at, `description`, location, user_id) VALUES (274, 'LOGIN', '2025-03-16T10:28:51.737587', 'User logged in successfully', 'Local Development', 10);

-- changeset shobh:1742119648370-9
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (1, '2025-03-08T19:53:35', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (2, '2025-03-08T20:08:05', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (3, '2025-03-08T20:15:59', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (4, '2025-03-08T20:20:01', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (5, '2025-03-09T12:25:09', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (6, '2025-03-09T12:25:34', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (7, '2025-03-09T12:25:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (8, '2025-03-09T12:27:56', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (9, '2025-03-09T12:28:21', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (10, '2025-03-09T12:55:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (11, '2025-03-09T12:55:59', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (12, '2025-03-09T12:57:32', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (13, '2025-03-09T13:02:47', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (14, '2025-03-09T13:04:23', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (15, '2025-03-09T13:07:04', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (16, '2025-03-09T13:11:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (17, '2025-03-09T13:12:13', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (18, '2025-03-09T13:13:04', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (19, '2025-03-09T13:44:55', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (20, '2025-03-09T13:48:29', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (21, '2025-03-09T13:58:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (22, '2025-03-09T13:59:48', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (23, '2025-03-09T14:02:34', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (24, '2025-03-09T14:22:59', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (25, '2025-03-09T14:27:33', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (26, '2025-03-09T15:03:18', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (27, '2025-03-10T15:28:23', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (28, '2025-03-10T15:29:57', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (29, '2025-03-10T15:30:24', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (30, '2025-03-10T15:31:29', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (31, '2025-03-10T15:32:01', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (32, '2025-03-10T15:37:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (33, '2025-03-10T16:12:33', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (34, '2025-03-10T16:13:08', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (35, '2025-03-10T16:13:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (36, '2025-03-10T16:14:03', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (37, '2025-03-10T16:15:20', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (38, '2025-03-10T16:17:13', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (39, '2025-03-10T16:34:22', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (40, '2025-03-10T16:34:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (41, '2025-03-10T16:58:44', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (42, '2025-03-10T17:00:17', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (43, '2025-03-10T17:01:29', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (44, '2025-03-10T17:02:55', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (45, '2025-03-10T17:12:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (46, '2025-03-10T17:22:29', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (47, '2025-03-10T17:24:15', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (48, '2025-03-10T17:25:27', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (49, '2025-03-10T17:26:19', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (50, '2025-03-10T17:28:21', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (51, '2025-03-10T17:28:32', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (52, '2025-03-10T17:59:45', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (53, '2025-03-10T18:04:43', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (54, '2025-03-10T19:56:08', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (55, '2025-03-10T20:11:44', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (56, '2025-03-10T20:18:18', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (57, '2025-03-10T20:50:46', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (58, '2025-03-10T21:43:54', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (59, '2025-03-10T21:44:31', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (60, '2025-03-10T21:45:11', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (61, '2025-03-10T21:45:24', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (62, '2025-03-10T21:45:44', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (63, '2025-03-10T23:36:13', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (64, '2025-03-10T23:37:04', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 5);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (65, '2025-03-10T23:43:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 5);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (66, '2025-03-11T00:00:49', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (67, '2025-03-11T00:01:10', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 2);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (68, '2025-03-11T00:01:49', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 2);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (69, '2025-03-11T00:04:26', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 2);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (70, '2025-03-11T00:07:37', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 2);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (71, '2025-03-12T16:30:22', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (72, '2025-03-12T18:17:19', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (73, '2025-03-12T18:45:18', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (74, '2025-03-12T19:24:39', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (75, '2025-03-12T19:28:14', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (76, '2025-03-12T19:39:27', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (77, '2025-03-12T19:40:09', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (78, '2025-03-12T19:50:38', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (79, '2025-03-12T20:10:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (80, '2025-03-12T20:12:58', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (81, '2025-03-12T20:14:51', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (82, '2025-03-12T20:15:54', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (83, '2025-03-12T20:59:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (84, '2025-03-12T21:00:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (85, '2025-03-12T21:41:59', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (86, '2025-03-12T21:50:32', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (87, '2025-03-12T21:51', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (88, '2025-03-12T21:51:19', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (89, '2025-03-13T19:35:33', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (90, '2025-03-13T19:36:58', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (91, '2025-03-13T19:38:02', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (92, '2025-03-13T19:42:22', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (93, '2025-03-13T20:46:32', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (94, '2025-03-13T20:47:10', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (95, '2025-03-13T20:52:13', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (96, '2025-03-13T21:09:58', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (97, '2025-03-13T21:10:06', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (98, '2025-03-13T21:10:24', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (99, '2025-03-13T21:10:47', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (100, '2025-03-13T21:11:11', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (101, '2025-03-13T21:17:34', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (102, '2025-03-13T21:17:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (103, '2025-03-13T21:20:57', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (104, '2025-03-13T22:14:54', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (105, '2025-03-13T22:15:08', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (106, '2025-03-13T22:16:11', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (107, '2025-03-13T22:17:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (108, '2025-03-13T22:17:49', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (109, '2025-03-13T22:19:08', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (110, '2025-03-13T22:22:11', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (111, '2025-03-13T22:23:29', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (112, '2025-03-13T22:25:46', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (113, '2025-03-13T22:42:56', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (114, '2025-03-13T22:44:35', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (115, '2025-03-13T22:45:14', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (116, '2025-03-13T22:46:11', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (117, '2025-03-13T22:49:27', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (118, '2025-03-13T22:50:19', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (119, '2025-03-13T22:50:56', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (120, '2025-03-13T23:02', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (121, '2025-03-13T23:07:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (122, '2025-03-13T23:07:57', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (123, '2025-03-13T23:08:03', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (124, '2025-03-13T23:08:14', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (125, '2025-03-13T23:08:25', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (126, '2025-03-13T23:08:35', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (127, '2025-03-13T23:10:11', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (128, '2025-03-13T23:26:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (129, '2025-03-13T23:26:50', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (130, '2025-03-13T23:27:33', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (131, '2025-03-13T23:27:45', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (132, '2025-03-13T23:31:08', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (133, '2025-03-13T23:35:27', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (134, '2025-03-13T23:37:37', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (135, '2025-03-13T23:58:39', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (136, '2025-03-13T23:59:25', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (137, '2025-03-14T00:00:54', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (138, '2025-03-14T00:02:24', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (139, '2025-03-14T00:04:52', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (140, '2025-03-14T00:05:09', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (141, '2025-03-14T00:07:50', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (142, '2025-03-14T00:18:17', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (143, '2025-03-14T00:18:29', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (144, '2025-03-14T00:18:40', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (145, '2025-03-14T00:29:44', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (146, '2025-03-14T00:30:12', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (147, '2025-03-14T00:30:37', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (148, '2025-03-14T00:31:09', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (149, '2025-03-14T00:37:27', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (150, '2025-03-14T00:38:25', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (151, '2025-03-14T00:38:33', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (152, '2025-03-14T00:38:38', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (153, '2025-03-14T00:47:37', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (154, '2025-03-14T00:47:46', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (155, '2025-03-14T00:51:55', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (156, '2025-03-14T00:56:41', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (157, '2025-03-14T00:56:47', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (158, '2025-03-14T01:04:33', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (159, '2025-03-14T01:26:37', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (160, '2025-03-14T01:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (161, '2025-03-14T01:42:49', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (162, '2025-03-14T01:44:52', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (163, '2025-03-14T01:45:38', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (164, '2025-03-14T01:52:35', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (165, '2025-03-14T01:55:54', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (166, '2025-03-15T13:03:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (167, '2025-03-15T13:03:42', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (168, '2025-03-15T13:05:06', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (169, '2025-03-15T13:06:05', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (170, '2025-03-15T13:07:55', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (171, '2025-03-15T13:54:03', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 10);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (172, '2025-03-15T14:11:44', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 10);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (173, '2025-03-15T14:32:37', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 10);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (174, '2025-03-15T14:43:05', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 10);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (175, '2025-03-15T15:07:24', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 10);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (176, '2025-03-15T15:08:19', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 10);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (177, '2025-03-15T15:17:02', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (178, '2025-03-15T15:21', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (179, '2025-03-15T15:21:26', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (180, '2025-03-15T15:33:07', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (181, '2025-03-15T15:39:38', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (182, '2025-03-15T15:40:15', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (183, '2025-03-15T15:49:21', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (184, '2025-03-15T15:49:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (185, '2025-03-15T15:50', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (186, '2025-03-15T15:50:07', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (187, '2025-03-15T16:17:56', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (188, '2025-03-15T16:19:46', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (189, '2025-03-15T16:20:09', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (190, '2025-03-15T16:23:30', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (191, '2025-03-15T16:23:54', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (192, '2025-03-15T16:58:23', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (193, '2025-03-15T22:20:16.127419', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (194, '2025-03-15T22:23:24.280047', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (195, '2025-03-15T22:23:45.404735', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (196, '2025-03-15T22:28:16.939195', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (197, '2025-03-15T22:33:13.692738', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (198, '2025-03-15T22:33:55.573211', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (199, '2025-03-15T22:35:57.569353', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (200, '2025-03-15T22:52:14.946900', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (201, '2025-03-15T23:03:32.827897', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (202, '2025-03-15T23:22:44.713467', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (203, '2025-03-15T23:23:22.614827', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (204, '2025-03-16T00:34:55.156853', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (205, '2025-03-16T00:42:35.196149', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (206, '2025-03-16T00:44:23.026275', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (207, '2025-03-16T00:46:52.656055', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (208, '2025-03-16T00:54:14.918885', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (209, '2025-03-16T01:00:30.429794', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);
INSERT INTO user_notifications (id, created_at, message, notification_type, status, user_id) VALUES (210, '2025-03-16T01:01:09.301624', 'Thank You For Accessing The Route', 'ALERT', 'UNSENT', 1);

-- changeset shobh:1742119648370-10
INSERT INTO user_property_visits (id, device_info, location_coords, property_id, user_id, visit_count, visit_time) VALUES (1, 'PostmanRuntime/7.43.2', NULL, 1, 1, 1, '2025-03-16T11:42:03.205048');
INSERT INTO user_property_visits (id, device_info, location_coords, property_id, user_id, visit_count, visit_time) VALUES (2, 'PostmanRuntime/7.43.2', NULL, 2, 1, 1, '2025-03-16T11:42:03.251191');
INSERT INTO user_property_visits (id, device_info, location_coords, property_id, user_id, visit_count, visit_time) VALUES (3, 'PostmanRuntime/7.43.2', NULL, 3, 1, 1, '2025-03-16T11:42:03.265558');
INSERT INTO user_property_visits (id, device_info, location_coords, property_id, user_id, visit_count, visit_time) VALUES (4, 'PostmanRuntime/7.43.2', NULL, 4, 1, 1, '2025-03-16T11:42:03.276662');
INSERT INTO user_property_visits (id, device_info, location_coords, property_id, user_id, visit_count, visit_time) VALUES (5, 'PostmanRuntime/7.43.2', NULL, 5, 1, 1, '2025-03-16T11:42:03.287524');
