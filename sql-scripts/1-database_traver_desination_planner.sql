CREATE DATABASE  IF NOT EXISTS `travel_planner`;
USE `travel_planner`;

SET foreign_key_checks = 0;
DROP TABLE IF EXISTS `client`,`role`,`clients_roles`,`destination`,`wishlist`;

--
-- Table structure for table `user`
--
CREATE TABLE `client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(64) NOT NULL unique,
  `password` char(68) NOT NULL,
  `enabled` tinyint NOT NULL,  
  `first_name` varchar(64) NOT NULL,
  `last_name` varchar(64) NOT NULL,
  `address` varchar(100) Default Null,
  `phone_number` char(11) Default Null,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
-- Default passwords here are: fun123
--
INSERT INTO `client` (`email`,`password`,`enabled`, `first_name`, `last_name`)
VALUES 
('john@luv2code.com','$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K',1,'John', 'Doe'),
('mary@luv2code.com','$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K',1,'Mary', 'Smith');


--
-- Table structure for table `role`
--
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `roles`
--
INSERT INTO `role` (name)
VALUES 
('ROLE_USER'),('ROLE_ADMIN');

--
-- Table structure for table `users_roles`
--
CREATE TABLE `clients_roles` (
  `client_id` int NOT NULL,
  `role_id` int NOT NULL,
  
  PRIMARY KEY (`client_id`,`role_id`), -- this will automatically make an index on client_id and covers role_id 
  
  KEY `FK_ROLE_idx` (`role_id`), -- make an index on role id -- 
  
  CONSTRAINT `FK_CLIENT_05` FOREIGN KEY (`client_id`) 
  REFERENCES `client` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`role_id`) 
  REFERENCES `role` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Dumping data for table `users_roles`
--
INSERT INTO `clients_roles` (client_id,role_id)
VALUES 
(1, 1),
(2, 1),
(2, 2);



CREATE TABLE `destination` (

  `id` int NOT NULL AUTO_INCREMENT,
  `country` varchar(50) NOT NULL unique,
  `capital` varchar(50) NOT NULL,
  `region` varchar(50) NOT NULL,
  `population` int NOT NULL CHECK ( `population` > 0 ),
  `currency` varchar(50) NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
  
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;



CREATE TABLE `wishlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `destination_id` int NOT NULL,
  
  PRIMARY KEY (`id`),
  UNIQUE (`user_id`,`destination_id`), 
  KEY `FK_DESTINATION_ID_idx` (`destination_id`), -- make an index on destination_id  -- 
  
  CONSTRAINT `FK_USER` FOREIGN KEY (`user_id`) 
  REFERENCES `client` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT `FK_DESTINATION` FOREIGN KEY (`destination_id`) 
  REFERENCES `destination` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
  
) ENGINE=InnoDB  AUTO_INCREMENT=1  DEFAULT CHARSET=latin1;




SET FOREIGN_KEY_CHECKS = 1;
