DROP DATABASE IF EXISTS dev;
CREATE DATABASE dev;
USE dev;
CREATE TABLE IF NOT EXISTS services(
  `service_id` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL ,
  `url` VARCHAR(500) ,
  `creation_date` DATE ,
  `status` VARCHAR(255) ,
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB;