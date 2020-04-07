--    uniCenta oPOS - Touch Friendly Point Of Sale
--    Copyright (c) 2009-2017 uniCenta
--    https://unicenta.com
--
--    This file is part of uniCenta oPOS.
--
--    uniCenta oPOS is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    uniCenta oPOS is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for MySQL
-- v4.1.2 - v4.3.0 25JAN2017

--
-- CLEAR THE DECKS
--
DELETE FROM sharedtickets;

--
-- CREATE PRODUCT_BUNDLE
-- 
/* Header line. Object: products_bundle. Script date: 26/07/2016 15:25:00. */
CREATE TABLE IF NOT EXISTS `products_bundle` (
    `id` varchar(255) NOT NULL,
    `product` VARCHAR(255) NOT NULL,
    `product_bundle` VARCHAR(255) NOT NULL,
    `quantity` DOUBLE NOT NULL,
    PRIMARY KEY ( `id` ),
    UNIQUE INDEX `pbundle_inx_prod` ( `product` , `product_bundle` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;


CREATE TABLE IF NOT EXISTS `orders` (
    `id` MEDIUMINT NOT NULL AUTO_INCREMENT,
    `orderid` varchar(50) DEFAULT NULL,
    `qty` int(11) DEFAULT '1',
    `details` varchar(255) DEFAULT NULL,
    `attributes` varchar(255) DEFAULT NULL,
    `notes` varchar(255) DEFAULT NULL,
    `ticketid` varchar(50) DEFAULT NULL,
    `ordertime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `displayid` int(11) DEFAULT '1',
    `auxiliary` int(11) DEFAULT NULL,
    `completetime` timestamp DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

-- 
-- UPDATE ROLES
--
UPDATE `roles` SET `permissions` = $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} WHERE `id` = '0';
UPDATE `roles` SET `permissions` = $FILE{/com/openbravo/pos/templates/Role.Manager.xml} WHERE `id` = '1';

-- UPDATE resources --
-- MENU
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Menu.Root.txt} WHERE `id` = '0';

-- 
-- UPDATE PRODUCTS
--
ALTER TABLE `products` DROP INDEX `products_name_inx`;
ALTER TABLE `products` ADD INDEX `products_name_inx` (name);
ALTER TABLE `products` ADD COLUMN `memodate` datetime default '1900-01-01 00:00:01' after `uom` ;

-- 
-- UPDATE csvimport
--
ALTER TABLE `csvimport` 
	ADD COLUMN `tax` varchar(255)  COLLATE utf8_general_ci NULL after `category`,
	ADD COLUMN `searchkey` varchar(255)  COLLATE utf8_general_ci NULL after `tax` ;

-- 
-- UPDATE customers
--
ALTER TABLE `customers`
        ADD COLUMN `memodate` datetime default '1900-01-01 00:00:01' after `discount` ;

--
-- posApps
--
DELETE FROM resources WHERE  name = 'Printer.Ticket';
DELETE FROM resources WHERE  name = 'Ticket.Close';
INSERT INTO resources(id, name, restype, content) VALUES('44', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('69', 'Ticket.Close', 0, $FILE{/com/openbravo/pos/templates/Ticket.Close.xml});

INSERT INTO resources(id, name, restype, content) VALUES('77', 'script.posapps', 0, $FILE{/com/openbravo/pos/templates/script.posapps.txt});
INSERT INTO resources(id, name, restype, content) VALUES('78', 'img.posapps', 1, $FILE{/com/openbravo/pos/templates/img.posapps.png});

-- ADD TAXCATEGORIES + TAXES
/* 002 added 31/01/2017 00:00:00. */
INSERT INTO taxcategories(id, name) VALUES ('002', 'Tax Other');
INSERT INTO taxes(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('002', 'Tax Other', '002', NULL, NULL, 0, FALSE, NULL);

-- UPDATE App' version
UPDATE applications SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};