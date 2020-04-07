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

/*
 * Script created by Jack, uniCenta 27/08/2015 08:42:37.
 *
 * Creating for version unicentaopos43. Feb 2017
*/


/* Header line. Object: applications. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `applications` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`version` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attribute. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `attribute` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributeinstance. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `attributeinstance` (
	`id` varchar(255) NOT NULL,
	`attributesetinstance_id` varchar(255) NOT NULL,
	`attribute_id` varchar(255) NOT NULL,
	`value` varchar(255) default NULL,
	KEY `attinst_att` ( `attribute_id` ),
	KEY `attinst_set` ( `attributesetinstance_id` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributeset. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `attributeset` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributesetinstance. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `attributesetinstance` (
	`id` varchar(255) NOT NULL,
	`attributeset_id` varchar(255) NOT NULL,
	`description` varchar(255) default NULL,
	KEY `attsetinst_set` ( `attributeset_id` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributeuse. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `attributeuse` (
	`id` varchar(255) NOT NULL,
	`attributeset_id` varchar(255) NOT NULL,
	`attribute_id` varchar(255) NOT NULL,
	`lineno` int(11) default NULL,
	KEY `attuse_att` ( `attribute_id` ),
	UNIQUE INDEX `attuse_line` ( `attributeset_id`, `lineno` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributevalue. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `attributevalue` (
	`id` varchar(255) NOT NULL,
	`attribute_id` varchar(255) NOT NULL,
	`value` varchar(255) default NULL,
	KEY `attval_att` ( `attribute_id` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: breaks. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `breaks` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`visible` tinyint(1) NOT NULL default '1',
	`notes` varchar(255) default NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: categories. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `categories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`parentid` varchar(255) default NULL,
	`image` mediumblob default NULL,
	`texttip` varchar(255) default NULL,
	`catshowname` smallint(6) NOT NULL default '1',
	`catorder` varchar(255) default NULL,
	KEY `categories_fk_1` ( `parentid` ),
	UNIQUE INDEX `categories_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: closedcash. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `closedcash` (
	`money` varchar(255) NOT NULL,
	`host` varchar(255) NOT NULL,
	`hostsequence` int(11) NOT NULL,
	`datestart` datetime NOT NULL,
	`dateend` datetime default NULL,
	`nosales` int(11) NOT NULL default '0',
	KEY `closedcash_inx_1` ( `datestart` ),
	UNIQUE INDEX `closedcash_inx_seq` ( `host`, `hostsequence` ),
	PRIMARY KEY  ( `money` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: csvimport. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `csvimport` (
	`id` varchar(255) NOT NULL,
	`rownumber` varchar(255) default NULL,
	`csverror` varchar(255) default NULL,
	`reference` varchar(255) default NULL,
	`code` varchar(255) default NULL,
	`name` varchar(255) default NULL,
	`pricebuy` double default NULL,
	`pricesell` double default NULL,
	`previousbuy` double default NULL,
	`previoussell` double default NULL,
	`category` varchar(255) default NULL,
	`tax` varchar(255) default NULL,
	`searchkey` varchar(255) default NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: customers. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `customers` (
	`id` varchar(255) NOT NULL,
	`searchkey` varchar(255) NOT NULL,
	`taxid` varchar(255) default NULL,
	`name` varchar(255) NOT NULL,
	`taxcategory` varchar(255) default NULL,
	`card` varchar(255) default NULL,
	`maxdebt` double NOT NULL default '0',
	`address` varchar(255) default NULL,
	`address2` varchar(255) default NULL,
	`postal` varchar(255) default NULL,
	`city` varchar(255) default NULL,
	`region` varchar(255) default NULL,
	`country` varchar(255) default NULL,
	`firstname` varchar(255) default NULL,
	`lastname` varchar(255) default NULL,
	`email` varchar(255) default NULL,
	`phone` varchar(255) default NULL,
	`phone2` varchar(255) default NULL,
	`fax` varchar(255) default NULL,
	`notes` varchar(255) default NULL,
	`visible` bit(1) NOT NULL default b'1',
	`curdate` datetime default NULL,
	`curdebt` double default '0',
	`image` mediumblob default NULL,
	`isvip` bit(1) NOT NULL default b'0',
	`discount` double default '0',
	`memodate` datetime default '1900-01-01 00:00:01',
	KEY `customers_card_inx` ( `card` ),
	KEY `customers_name_inx` ( `name` ),
	UNIQUE INDEX `customers_skey_inx` ( `searchkey` ),
	KEY `customers_taxcat` ( `taxcategory` ),
	KEY `customers_taxid_inx` ( `taxid` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: draweropened. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `draweropened` (
	`opendate` timestamp NOT NULL,
	`name` varchar(255) default NULL,
	`ticketid` varchar(255) default NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: floors. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `floors` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`image` mediumblob default NULL,
	UNIQUE INDEX `floors_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: leaves. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `leaves` (
	`id` varchar(255) NOT NULL,
	`pplid` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`startdate` datetime NOT NULL,
	`enddate` datetime NOT NULL,
	`notes` varchar(255) default NULL,
	KEY `leaves_pplid` ( `pplid` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: lineremoved. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `lineremoved` (
	`removeddate` timestamp NOT NULL,
	`name` varchar(255) default NULL,
	`ticketid` varchar(255) default NULL,
	`productid` varchar(255) default NULL,
	`productname` varchar(255) default NULL,
	`units` double NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: locations. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `locations` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`address` varchar(255) default NULL,
	UNIQUE INDEX `locations_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: moorers. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `moorers` (
	`vesselname` varchar(255) default NULL,
	`size` int(11) default NULL,
	`days` int(11) default NULL,
	`power` bit(1) NOT NULL default b'0'
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

/* Header line. Object: payments. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `payments` (
	`id` varchar(255) NOT NULL,
	`receipt` varchar(255) NOT NULL,
	`payment` varchar(255) NOT NULL,
	`total` double NOT NULL default '0',
	`tip` double default '0',
	`transid` varchar(255) default NULL,
	`isprocessed` bit(1) default b'0',
	`returnmsg` mediumblob default NULL,
	`notes` varchar(255) default NULL,
	`tendered` double default NULL,
	`cardname` varchar(255) default NULL,
        `voucher` varchar(255) default NULL,
	KEY `payments_fk_receipt` ( `receipt` ),
	KEY `payments_inx_1` ( `payment` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: people. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `people` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`apppassword` varchar(255) default NULL,
	`card` varchar(255) default NULL,
	`role` varchar(255) NOT NULL,
	`visible` bit(1) NOT NULL,
	`image` mediumblob default NULL,
	KEY `people_card_inx` ( `card` ),
	KEY `people_fk_1` ( `role` ),
	UNIQUE INDEX `people_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: pickup_number. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `pickup_number` (
	`id` int(11) NOT NULL default '0'
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: places. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `places` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`x` int(11) NOT NULL,
	`y` int(11) NOT NULL,
	`floor` varchar(255) NOT NULL,
	`customer` varchar(255) default NULL,
	`waiter` varchar(255) default NULL,
	`ticketid` varchar(255) default NULL,
	`tablemoved` smallint(6) NOT NULL default '0',
	UNIQUE INDEX `places_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products. Script date: 02/04/2016 10:53:00. */
CREATE TABLE `products` (
	`id` varchar(255) NOT NULL,
	`reference` varchar(255) NOT NULL,
	`code` varchar(255) NOT NULL,
	`codetype` varchar(255) default NULL,
	`name` varchar(255) NOT NULL,
	`pricebuy` double NOT NULL default '0',
	`pricesell` double NOT NULL default '0',
	`category` varchar(255) NOT NULL,
	`taxcat` varchar(255) NOT NULL,
	`attributeset_id` varchar(255) default NULL,
	`stockcost` double NOT NULL default '0',
	`stockvolume` double NOT NULL default '0',
	`image` mediumblob default NULL,
	`iscom` bit(1) NOT NULL default b'0',
	`isscale` bit(1) NOT NULL default b'0',
	`isconstant` bit(1) NOT NULL default b'0',
	`printkb` bit(1) NOT NULL default b'0',
	`sendstatus` bit(1) NOT NULL default b'0',
	`isservice` bit(1) NOT NULL default b'0',
	`attributes` mediumblob default NULL,
	`display` varchar(255) default NULL,
	`isvprice` smallint(6) NOT NULL default '0',
	`isverpatrib` smallint(6) NOT NULL default '0',
	`texttip` varchar(255) default NULL,
	`warranty` smallint(6) NOT NULL default '0',
	`stockunits` double NOT NULL default '0',
	`printto` varchar(255) default '1',
	`supplier` varchar(255) default NULL,
        `uom` varchar(255) default '0',
	`memodate` datetime default '1900-01-01 00:00:01',

	PRIMARY KEY  ( `id` ),
	KEY `products_attrset_fx` ( `attributeset_id` ),
	KEY `products_fk_1` ( `category` ),
	UNIQUE INDEX `products_inx_0` ( `reference` ),
	UNIQUE INDEX `products_inx_1` ( `code` ),
	INDEX `products_name_inx` ( `name` ),
	KEY `products_taxcat_fk` ( `taxcat` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products_cat. Script date: 11/05/2016 05:25:00. */
CREATE TABLE `products_bundle` (
    `id` varchar(255) NOT NULL,
    `product` VARCHAR(255) NOT NULL,
    `product_bundle` VARCHAR(255) NOT NULL,
    `quantity` DOUBLE NOT NULL,
    PRIMARY KEY ( `id` ),
    UNIQUE INDEX `pbundle_inx_prod` ( `product` , `product_bundle` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products_cat. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `products_cat` (
	`product` varchar(255) NOT NULL,
	`catorder` int(11) default NULL,
	PRIMARY KEY  ( `product` ),
	KEY `products_cat_inx_1` ( `catorder` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products_com. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `products_com` (
	`id` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`product2` varchar(255) NOT NULL,
	UNIQUE INDEX `pcom_inx_prod` ( `product`, `product2` ),
	PRIMARY KEY  ( `id` ),
	KEY `products_com_fk_2` ( `product2` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: receipts. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `receipts` (
	`id` varchar(255) NOT NULL,
	`money` varchar(255) NOT NULL,
	`datenew` datetime NOT NULL,
	`attributes` mediumblob default NULL,
	`person` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `receipts_fk_money` ( `money` ),
	KEY `receipts_inx_1` ( `datenew` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: reservation_customers. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `reservation_customers` (
	`id` varchar(255) NOT NULL,
	`customer` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` ),
	KEY `res_cust_fk_2` ( `customer` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: reservations. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `reservations` (
	`id` varchar(255) NOT NULL,
	`created` datetime NOT NULL,
	`datenew` datetime NOT NULL default '2017-01-01 00:00:00',
	`title` varchar(255) NOT NULL,
	`chairs` int(11) NOT NULL,
	`isdone` bit(1) NOT NULL,
	`description` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `reservations_inx_1` ( `datenew` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: resources. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `resources` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`restype` int(11) NOT NULL,
	`content` mediumblob default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `resources_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: roles. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `roles` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`permissions` mediumblob default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `roles_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: sharedtickets. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `sharedtickets` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`content` mediumblob default NULL,
	`appuser` varchar(255) default NULL,
	`pickupid` smallint(6) NOT NULL default '0',
	`locked` varchar(20) default NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: shift_breaks. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `shift_breaks` (
	`id` varchar(255) NOT NULL,
	`shiftid` varchar(255) NOT NULL,
	`breakid` varchar(255) NOT NULL,
	`starttime` timestamp NOT NULL,
	`endtime` timestamp NOT NULL,
	PRIMARY KEY  ( `id` ),
	KEY `shift_breaks_breakid` ( `breakid` ),
	KEY `shift_breaks_shiftid` ( `shiftid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: shifts. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `shifts` (
	`id` varchar(255) NOT NULL,
	`startshift` datetime NOT NULL,
	`endshift` datetime default NULL,
	`pplid` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: stockcurrent. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `stockcurrent` (
	`location` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`attributesetinstance_id` varchar(255) default NULL,
	`units` double NOT NULL,
	KEY `stockcurrent_attsetinst` ( `attributesetinstance_id` ),
	KEY `stockcurrent_fk_1` ( `product` ),
	UNIQUE INDEX `stockcurrent_inx` ( `location`, `product`, `attributesetinstance_id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: stockdiary. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `stockdiary` (
	`id` varchar(255) NOT NULL,
	`datenew` datetime NOT NULL,
	`reason` int(11) NOT NULL,
	`location` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`attributesetinstance_id` varchar(255) default NULL,
	`units` double NOT NULL,
	`price` double NOT NULL,
	`appuser` varchar(255) default NULL,
	`supplier` varchar(255) default NULL,
	`supplierdoc` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `stockdiary_attsetinst` ( `attributesetinstance_id` ),
	KEY `stockdiary_fk_1` ( `product` ),
	KEY `stockdiary_fk_2` ( `location` ),
	KEY `stockdiary_inx_1` ( `datenew` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: stocklevel. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `stocklevel` (
	`id` varchar(255) NOT NULL,
	`location` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`stocksecurity` double default NULL,
	`stockmaximum` double default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `stocklevel_location` ( `location` ),
	KEY `stocklevel_product` ( `product` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: suppliers. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `suppliers` (
	`id` varchar(255) NOT NULL,
	`searchkey` varchar(255) NOT NULL,
	`taxid` varchar(255) default NULL,
	`name` varchar(255) NOT NULL,
	`maxdebt` double NOT NULL default '0',
	`address` varchar(255) default NULL,
	`address2` varchar(255) default NULL,
	`postal` varchar(255) default NULL,
	`city` varchar(255) default NULL,
	`region` varchar(255) default NULL,
	`country` varchar(255) default NULL,
	`firstname` varchar(255) default NULL,
	`lastname` varchar(255) default NULL,
	`email` varchar(255) default NULL,
	`phone` varchar(255) default NULL,
	`phone2` varchar(255) default NULL,
	`fax` varchar(255) default NULL,
	`notes` varchar(255) default NULL,
	`visible` bit(1) NOT NULL default b'1',
	`curdate` datetime default NULL,
	`curdebt` double default '0',
	`vatid` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `suppliers_name_inx` ( `name` ),
	UNIQUE INDEX `suppliers_skey_inx` ( `searchkey` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxcategories. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `taxcategories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `taxcat_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxcustcategories. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `taxcustcategories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `taxcustcat_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxes. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `taxes` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`category` varchar(255) NOT NULL,
	`custcategory` varchar(255) default NULL,
	`parentid` varchar(255) default NULL,
	`rate` double NOT NULL default '0',
	`ratecascade` bit(1) NOT NULL default b'0',
	`rateorder` int(11) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `taxes_cat_fk` ( `category` ),
	KEY `taxes_custcat_fk` ( `custcategory` ),
	UNIQUE INDEX `taxes_name_inx` ( `name` ),
	KEY `taxes_taxes_fk` ( `parentid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxlines. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `taxlines` (
	`id` varchar(255) NOT NULL,
	`receipt` varchar(255) NOT NULL,
	`taxid` varchar(255) NOT NULL,
	`base` double NOT NULL default '0',
	`amount` double NOT NULL default '0',
	PRIMARY KEY  ( `id` ),
	KEY `taxlines_receipt` ( `receipt` ),
	KEY `taxlines_tax` ( `taxid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxsuppcategories. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `taxsuppcategories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: thirdparties. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `thirdparties` (
	`id` varchar(255) NOT NULL,
	`cif` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`address` varchar(255) default NULL,
	`contactcomm` varchar(255) default NULL,
	`contactfact` varchar(255) default NULL,
	`payrule` varchar(255) default NULL,
	`faxnumber` varchar(255) default NULL,
	`phonenumber` varchar(255) default NULL,
	`mobilenumber` varchar(255) default NULL,
	`email` varchar(255) default NULL,
	`webpage` varchar(255) default NULL,
	`notes` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `thirdparties_cif_inx` ( `cif` ),
	UNIQUE INDEX `thirdparties_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketlines. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `ticketlines` (
	`ticket` varchar(255) NOT NULL,
	`line` int(11) NOT NULL,
	`product` varchar(255) default NULL,
	`attributesetinstance_id` varchar(255) default NULL,
	`units` double NOT NULL,
	`price` double NOT NULL,
	`taxid` varchar(255) NOT NULL,
	`attributes` mediumblob default NULL,
	PRIMARY KEY  ( `ticket`, `line` ),
	KEY `ticketlines_attsetinst` ( `attributesetinstance_id` ),
	KEY `ticketlines_fk_2` ( `product` ),
	KEY `ticketlines_fk_3` ( `taxid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: tickets. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `tickets` (
	`id` varchar(255) NOT NULL,
	`tickettype` int(11) NOT NULL default '0',
	`ticketid` int(11) NOT NULL,
	`person` varchar(255) NOT NULL,
	`customer` varchar(255) default NULL,
	`status` int(11) NOT NULL default '0',
	PRIMARY KEY  ( `id` ),
	KEY `tickets_customers_fk` ( `customer` ),
	KEY `tickets_fk_2` ( `person` ),
	KEY `tickets_ticketid` ( `tickettype`, `ticketid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketsnum. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `ticketsnum` (
	`id` int(11) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketsnum_payment. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `ticketsnum_payment` (
--	`id` int(11) NOT NULL auto_increment,
	`id` int(11) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketsnum_refund. Script date: 27/08/2015 08:42:37. */
CREATE TABLE `ticketsnum_refund` (
	`id` int(11) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: uom. Script date: 30/09/2015 13:07:00. */
CREATE TABLE `uom` (
    `id` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: vouchers. Script date: 30/09/2015 09:33:33. */
CREATE TABLE `vouchers` (
   `id` VARCHAR(100) NOT NULL,
   `voucher_number` VARCHAR(100) DEFAULT NULL,
   `customer` VARCHAR(100) DEFAULT NULL,
   `amount` DOUBLE DEFAULT NULL,
   `status` CHAR(1) DEFAULT 'A',
  PRIMARY KEY ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

-- Update foreign keys of attributeinstance
ALTER TABLE `attributeinstance` ADD CONSTRAINT `attinst_att`
	FOREIGN KEY ( `attribute_id` ) REFERENCES `attribute` ( `id` );

ALTER TABLE `attributeinstance` ADD CONSTRAINT `attinst_set`
	FOREIGN KEY ( `attributesetinstance_id` ) REFERENCES `attributesetinstance` ( `id` ) ON DELETE CASCADE;

-- Update foreign keys of attributesetinstance
ALTER TABLE `attributesetinstance` ADD CONSTRAINT `attsetinst_set`
	FOREIGN KEY ( `attributeset_id` ) REFERENCES `attributeset` ( `id` ) ON DELETE CASCADE;

-- Update foreign keys of attributeuse
ALTER TABLE `attributeuse` ADD CONSTRAINT `attuse_att`
	FOREIGN KEY ( `attribute_id` ) REFERENCES `attribute` ( `id` );

ALTER TABLE `attributeuse` ADD CONSTRAINT `attuse_set`
	FOREIGN KEY ( `attributeset_id` ) REFERENCES `attributeset` ( `id` ) ON DELETE CASCADE;

-- Update foreign keys of attributevalue
ALTER TABLE `attributevalue` ADD CONSTRAINT `attval_att`
	FOREIGN KEY ( `attribute_id` ) REFERENCES `attribute` ( `id` ) ON DELETE CASCADE;

-- Update foreign keys of categories
ALTER TABLE `categories` ADD CONSTRAINT `categories_fk_1`
	FOREIGN KEY ( `parentid` ) REFERENCES `categories` ( `id` );

-- Update foreign keys of customers
ALTER TABLE `customers` ADD CONSTRAINT `customers_taxcat`
	FOREIGN KEY ( `taxcategory` ) REFERENCES `taxcustcategories` ( `id` );

-- Update foreign keys of leaves
ALTER TABLE `leaves` ADD CONSTRAINT `leaves_pplid`
	FOREIGN KEY ( `pplid` ) REFERENCES `people` ( `id` );

-- Update foreign keys of payments
ALTER TABLE `payments` ADD CONSTRAINT `payments_fk_receipt`
	FOREIGN KEY ( `receipt` ) REFERENCES `receipts` ( `id` );

-- Update foreign keys of people
ALTER TABLE `people` ADD CONSTRAINT `people_fk_1`
	FOREIGN KEY ( `role` ) REFERENCES `roles` ( `id` );

-- Update foreign keys of products
ALTER TABLE `products` ADD CONSTRAINT `products_attrset_fk`
	FOREIGN KEY ( `attributeset_id` ) REFERENCES `attributeset` ( `id` );

ALTER TABLE `products` ADD CONSTRAINT `products_fk_1`
	FOREIGN KEY ( `category` ) REFERENCES `categories` ( `id` );

ALTER TABLE `products` ADD CONSTRAINT `products_taxcat_fk`
	FOREIGN KEY ( `taxcat` ) REFERENCES `taxcategories` ( `id` );

-- Update foreign keys of product_bundle
ALTER TABLE `products_bundle` ADD CONSTRAINT `products_bundle_fk_1` 
        FOREIGN KEY ( `product` ) REFERENCES `products`( `id` );

ALTER TABLE `products_bundle` ADD CONSTRAINT `products_bundle_fk_2`     
        FOREIGN KEY ( `product_bundle` ) REFERENCES `products`( `id` );

-- Update foreign keys of products_cat
ALTER TABLE `products_cat` ADD CONSTRAINT `products_cat_fk_1`
	FOREIGN KEY ( `product` ) REFERENCES `products` ( `id` );

-- Update foreign keys of products_com
ALTER TABLE `products_com` ADD CONSTRAINT `products_com_fk_1`
	FOREIGN KEY ( `product` ) REFERENCES `products` ( `id` );

ALTER TABLE `products_com` ADD CONSTRAINT `products_com_fk_2`
	FOREIGN KEY ( `product2` ) REFERENCES `products` ( `id` );

-- Update foreign keys of receipts
ALTER TABLE `receipts` ADD CONSTRAINT `receipts_fk_money`
	FOREIGN KEY ( `money` ) REFERENCES `closedcash` ( `money` );

-- Update foreign keys of reservation_customers
ALTER TABLE `reservation_customers` ADD CONSTRAINT `res_cust_fk_1`
	FOREIGN KEY ( `id` ) REFERENCES `reservations` ( `id` );

ALTER TABLE `reservation_customers` ADD CONSTRAINT `res_cust_fk_2`
	FOREIGN KEY ( `customer` ) REFERENCES `customers` ( `id` );

-- Update foreign keys of shift_breaks
ALTER TABLE `shift_breaks` ADD CONSTRAINT `shift_breaks_breakid`
	FOREIGN KEY ( `breakid` ) REFERENCES `breaks` ( `id` );

ALTER TABLE `shift_breaks` ADD CONSTRAINT `shift_breaks_shiftid`
	FOREIGN KEY ( `shiftid` ) REFERENCES `shifts` ( `id` );

-- Update foreign keys of stockcurrent
ALTER TABLE `stockcurrent` ADD CONSTRAINT `stockcurrent_attsetinst`
	FOREIGN KEY ( `attributesetinstance_id` ) REFERENCES `attributesetinstance` ( `id` );

ALTER TABLE `stockcurrent` ADD CONSTRAINT `stockcurrent_fk_1`
	FOREIGN KEY ( `product` ) REFERENCES `products` ( `id` );

ALTER TABLE `stockcurrent` ADD CONSTRAINT `stockcurrent_fk_2`
	FOREIGN KEY ( `location` ) REFERENCES `locations` ( `id` );

-- Update foreign keys of stockdiary
ALTER TABLE `stockdiary` ADD CONSTRAINT `stockdiary_attsetinst`
	FOREIGN KEY ( `attributesetinstance_id` ) REFERENCES `attributesetinstance` ( `id` );

ALTER TABLE `stockdiary` ADD CONSTRAINT `stockdiary_fk_1`
	FOREIGN KEY ( `product` ) REFERENCES `products` ( `id` );

ALTER TABLE `stockdiary` ADD CONSTRAINT `stockdiary_fk_2`
	FOREIGN KEY ( `location` ) REFERENCES `locations` ( `id` );

-- Update foreign keys of stocklevel
ALTER TABLE `stocklevel` ADD CONSTRAINT `stocklevel_location`
	FOREIGN KEY ( `location` ) REFERENCES `locations` ( `id` );

ALTER TABLE `stocklevel` ADD CONSTRAINT `stocklevel_product`
	FOREIGN KEY ( `product` ) REFERENCES `products` ( `id` );

-- Update foreign keys of taxes
ALTER TABLE `taxes` ADD CONSTRAINT `taxes_cat_fk`
	FOREIGN KEY ( `category` ) REFERENCES `taxcategories` ( `id` );

ALTER TABLE `taxes` ADD CONSTRAINT `taxes_custcat_fk`
	FOREIGN KEY ( `custcategory` ) REFERENCES `taxcustcategories` ( `id` );

ALTER TABLE `taxes` ADD CONSTRAINT `taxes_taxes_fk`
	FOREIGN KEY ( `parentid` ) REFERENCES `taxes` ( `id` );

-- Update foreign keys of taxlines
ALTER TABLE `taxlines` ADD CONSTRAINT `taxlines_receipt`
	FOREIGN KEY ( `receipt` ) REFERENCES `receipts` ( `id` );

ALTER TABLE `taxlines` ADD CONSTRAINT `taxlines_tax`
	FOREIGN KEY ( `taxid` ) REFERENCES `taxes` ( `id` );

-- Update foreign keys of ticketlines
ALTER TABLE `ticketlines` ADD CONSTRAINT `ticketlines_attsetinst`
	FOREIGN KEY ( `attributesetinstance_id` ) REFERENCES `attributesetinstance` ( `id` );

ALTER TABLE `ticketlines` ADD CONSTRAINT `ticketlines_fk_2`
	FOREIGN KEY ( `product` ) REFERENCES `products` ( `id` );

ALTER TABLE `ticketlines` ADD CONSTRAINT `ticketlines_fk_3`
	FOREIGN KEY ( `taxid` ) REFERENCES `taxes` ( `id` );

ALTER TABLE `ticketlines` ADD CONSTRAINT `ticketlines_fk_ticket`
	FOREIGN KEY ( `ticket` ) REFERENCES `tickets` ( `id` );

-- Update foreign keys of tickets
ALTER TABLE `tickets` ADD CONSTRAINT `tickets_customers_fk`
	FOREIGN KEY ( `customer` ) REFERENCES `customers` ( `id` );

ALTER TABLE `tickets` ADD CONSTRAINT `tickets_fk_2`
	FOREIGN KEY ( `person` ) REFERENCES `people` ( `id` );

ALTER TABLE `tickets` ADD CONSTRAINT `tickets_fk_id`
	FOREIGN KEY ( `id` ) REFERENCES `receipts` ( `id` );

-- *****************************************************************************

 -- ADD roles
INSERT INTO roles(id, name, permissions) VALUES('0', 'Administrator role', $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} );
INSERT INTO roles(id, name, permissions) VALUES('1', 'Manager role', $FILE{/com/openbravo/pos/templates/Role.Manager.xml} );
INSERT INTO roles(id, name, permissions) VALUES('2', 'Employee role', $FILE{/com/openbravo/pos/templates/Role.Employee.xml} );
INSERT INTO roles(id, name, permissions) VALUES('3', 'Guest role', $FILE{/com/openbravo/pos/templates/Role.Guest.xml} );

-- ADD people
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('0', 'Administrator', NULL, '0', TRUE, NULL);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('1', 'Manager', NULL, '1', TRUE, NULL);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('2', 'Employee', NULL, '2', TRUE, NULL);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('3', 'Guest', NULL, '3', TRUE, NULL);

-- ADD resources --
-- MENU

INSERT INTO resources(id, name, restype, content) VALUES('0', 'Menu.Root', 0, $FILE{/com/openbravo/pos/templates/Menu.Root.txt});

-- IMAGES
INSERT INTO resources(id, name, restype, content) VALUES('1', 'coin.01', 1, $FILE{/com/openbravo/pos/templates/coin.01.png});
INSERT INTO resources(id, name, restype, content) VALUES('2', 'coin.02', 1, $FILE{/com/openbravo/pos/templates/coin.02.png});
INSERT INTO resources(id, name, restype, content) VALUES('3', 'coin.05', 1, $FILE{/com/openbravo/pos/templates/coin.05.png});
INSERT INTO resources(id, name, restype, content) VALUES('4', 'coin.1', 1, $FILE{/com/openbravo/pos/templates/coin.1.png});
INSERT INTO resources(id, name, restype, content) VALUES('5', 'coin.10', 1, $FILE{/com/openbravo/pos/templates/coin.10.png});
INSERT INTO resources(id, name, restype, content) VALUES('6', 'coin.2', 1, $FILE{/com/openbravo/pos/templates/coin.2.png});
INSERT INTO resources(id, name, restype, content) VALUES('7', 'coin.20', 1, $FILE{/com/openbravo/pos/templates/coin.20.png});
INSERT INTO resources(id, name, restype, content) VALUES('8', 'coin.50', 1, $FILE{/com/openbravo/pos/templates/coin.50.png});
INSERT INTO resources(id, name, restype, content) VALUES('9', 'img.cash', 1, $FILE{/com/openbravo/pos/templates/img.cash.png});
INSERT INTO resources(id, name, restype, content) VALUES('10', 'img.cashdrawer', 1, $FILE{/com/openbravo/pos/templates/img.cashdrawer.png});
INSERT INTO resources(id, name, restype, content) VALUES('11', 'img.discount', 1, $FILE{/com/openbravo/pos/templates/img.discount.png});
INSERT INTO resources(id, name, restype, content) VALUES('12', 'img.discount_b', 1, $FILE{/com/openbravo/pos/templates/img.discount_b.png});
INSERT INTO resources(id, name, restype, content) VALUES('13', 'img.empty', 1, $FILE{/com/openbravo/pos/templates/img.empty.png});
INSERT INTO resources(id, name, restype, content) VALUES('14', 'img.heart', 1, $FILE{/com/openbravo/pos/templates/img.heart.png});
INSERT INTO resources(id, name, restype, content) VALUES('15', 'img.keyboard_32', 1, $FILE{/com/openbravo/pos/templates/img.keyboard_32.png});
INSERT INTO resources(id, name, restype, content) VALUES('16', 'img.kit_print', 1, $FILE{/com/openbravo/pos/templates/img.kit_print.png});
INSERT INTO resources(id, name, restype, content) VALUES('17', 'img.no_photo', 1, $FILE{/com/openbravo/pos/templates/img.no_photo.png});
INSERT INTO resources(id, name, restype, content) VALUES('18', 'img.refundit', 1, $FILE{/com/openbravo/pos/templates/img.refundit.png});
INSERT INTO resources(id, name, restype, content) VALUES('19', 'img.run_script', 1, $FILE{/com/openbravo/pos/templates/img.run_script.png});
INSERT INTO resources(id, name, restype, content) VALUES('20', 'img.ticket_print', 1, $FILE{/com/openbravo/pos/templates/img.ticket_print.png});
INSERT INTO resources(id, name, restype, content) VALUES('21', 'img.user', 1, $FILE{/com/openbravo/pos/templates/img.user.png});
INSERT INTO resources(id, name, restype, content) VALUES('22', 'note.50', 1, $FILE{/com/openbravo/pos/templates/note.50.png});
INSERT INTO resources(id, name, restype, content) VALUES('23', 'note.20', 1, $FILE{/com/openbravo/pos/templates/note.20.png});
INSERT INTO resources(id, name, restype, content) VALUES('24', 'note.10', 1, $FILE{/com/openbravo/pos/templates/note.10.png});
INSERT INTO resources(id, name, restype, content) VALUES('25', 'note.5', 1, $FILE{/com/openbravo/pos/templates/note.5.png});

-- PRINTER
INSERT INTO resources(id, name, restype, content) VALUES('26', 'Printer.CloseCash.Preview', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.Preview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('27', 'Printer.CloseCash', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.xml});
INSERT INTO resources(id, name, restype, content) VALUES('28', 'Printer.CustomerPaid', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid.xml});
INSERT INTO resources(id, name, restype, content) VALUES('29', 'Printer.CustomerPaid2', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('30', 'Printer.FiscalTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.FiscalTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('31', 'Printer.Inventory', 0, $FILE{/com/openbravo/pos/templates/Printer.Inventory.xml});
INSERT INTO resources(id, name, restype, content) VALUES('32', 'Printer.OpenDrawer', 0, $FILE{/com/openbravo/pos/templates/Printer.OpenDrawer.xml});
INSERT INTO resources(id, name, restype, content) VALUES('33', 'Printer.PartialCash', 0, $FILE{/com/openbravo/pos/templates/Printer.PartialCash.xml});
INSERT INTO resources(id, name, restype, content) VALUES('34', 'Printer.PrintLastTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.PrintLastTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('35', 'Printer.Product', 0, $FILE{/com/openbravo/pos/templates/Printer.Product.xml});
INSERT INTO resources(id, name, restype, content) VALUES('36', 'Printer.ReprintTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.ReprintTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('37', 'Printer.Start', 0, $FILE{/com/openbravo/pos/templates/Printer.Start.xml});
INSERT INTO resources(id, name, restype, content) VALUES('38', 'Printer.Ticket.P1', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P1.xml});
INSERT INTO resources(id, name, restype, content) VALUES('39', 'Printer.Ticket.P2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('40', 'Printer.Ticket.P3', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P3.xml});
INSERT INTO resources(id, name, restype, content) VALUES('41', 'Printer.Ticket.P4', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P4.xml});
INSERT INTO resources(id, name, restype, content) VALUES('42', 'Printer.Ticket.P5', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P5.xml});
INSERT INTO resources(id, name, restype, content) VALUES('43', 'Printer.Ticket.P6', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P6.xml});
INSERT INTO resources(id, name, restype, content) VALUES('44', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('45', 'Printer.Ticket2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('46', 'Printer.TicketClose', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketClose.xml});
INSERT INTO resources(id, name, restype, content) VALUES('47', 'Printer.TicketRemote', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketRemote.xml});
INSERT INTO resources(id, name, restype, content) VALUES('48', 'Printer.TicketLine', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO resources(id, name, restype, content) VALUES('49', 'Printer.TicketNew', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO resources(id, name, restype, content) VALUES('50', 'Printer.TicketPreview', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketPreview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('51', 'Printer.TicketTotal', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketTotal.xml});
INSERT INTO resources(id, name, restype, content) VALUES('52', 'Printer.Ticket.Logo', 1, $FILE{/com/openbravo/pos/templates/printer.ticket.logo.png});

-- SCRIPTS
INSERT INTO resources(id, name, restype, content) VALUES('53', 'script.AddLineNote', 0, $FILE{/com/openbravo/pos/templates/script.AddLineNote.txt});
INSERT INTO resources(id, name, restype, content) VALUES('54', 'script.Event.Total', 0, $FILE{/com/openbravo/pos/templates/script.Event.Total.txt});
INSERT INTO resources(id, name, restype, content) VALUES('55', 'script.Keyboard', 0, $FILE{/com/openbravo/pos/templates/script.Keyboard.txt});
INSERT INTO resources(id, name, restype, content) VALUES('56', 'script.linediscount', 0, $FILE{/com/openbravo/pos/templates/script.linediscount.txt});
INSERT INTO resources(id, name, restype, content) VALUES('57', 'script.ReceiptConsolidate', 0, $FILE{/com/openbravo/pos/templates/script.ReceiptConsolidate.txt});
INSERT INTO resources(id, name, restype, content) VALUES('58', 'script.Refundit', 0, $FILE{/com/openbravo/pos/templates/script.Refundit.txt});
INSERT INTO resources(id, name, restype, content) VALUES('59', 'script.SendOrder', 0, $FILE{/com/openbravo/pos/templates/script.SendOrder.txt});
INSERT INTO resources(id, name, restype, content) VALUES('60', 'script.ServiceCharge', 0, $FILE{/com/openbravo/pos/templates/script.script.ServiceCharge.txt});
INSERT INTO resources(id, name, restype, content) VALUES('61', 'script.SetPerson', 0, $FILE{/com/openbravo/pos/templates/script.SetPerson.txt});
INSERT INTO resources(id, name, restype, content) VALUES('62', 'script.StockCurrentAdd', 0, $FILE{/com/openbravo/pos/templates/script.StockCurrentAdd.txt});
INSERT INTO resources(id, name, restype, content) VALUES('63', 'script.StockCurrentSet', 0, $FILE{/com/openbravo/pos/templates/script.StockCurrentSet.txt});
INSERT INTO resources(id, name, restype, content) VALUES('64', 'script.totaldiscount', 0, $FILE{/com/openbravo/pos/templates/script.totaldiscount.txt});

-- SYSTEM
INSERT INTO resources(id, name, restype, content) VALUES('65', 'payment.cash', 0, $FILE{/com/openbravo/pos/templates/payment.cash.txt});
INSERT INTO resources(id, name, restype, content) VALUES('66', 'ticket.addline', 0, $FILE{/com/openbravo/pos/templates/ticket.addline.txt});
INSERT INTO resources(id, name, restype, content) VALUES('67', 'ticket.change', 0, $FILE{/com/openbravo/pos/templates/ticket.change.txt});
INSERT INTO resources(id, name, restype, content) VALUES('68', 'Ticket.Buttons', 0, $FILE{/com/openbravo/pos/templates/Ticket.Buttons.xml});

INSERT INTO resources(id, name, restype, content) VALUES('69', 'Ticket.Close', 0, $FILE{/com/openbravo/pos/templates/Ticket.Close.xml});

INSERT INTO resources(id, name, restype, content) VALUES('70', 'Ticket.Discount', 0, $FILE{/com/openbravo/pos/templates/Ticket.Discount.xml});
INSERT INTO resources(id, name, restype, content) VALUES('71', 'Ticket.Line', 0, $FILE{/com/openbravo/pos/templates/Ticket.Line.xml});
INSERT INTO resources(id, name, restype, content) VALUES('72', 'ticket.removeline', 0, $FILE{/com/openbravo/pos/templates/ticket.removeline.txt});
INSERT INTO resources(id, name, restype, content) VALUES('73', 'ticket.setline', 0, $FILE{/com/openbravo/pos/templates/ticket.setline.txt});
INSERT INTO resources(id, name, restype, content) VALUES('74', 'Ticket.TicketLineTaxesIncluded', 0, $FILE{/com/openbravo/pos/templates/Ticket.TicketLineTaxesIncluded.xml});
INSERT INTO resources(id, name, restype, content) VALUES('75', 'Window.Logo', 1, $FILE{/com/openbravo/pos/templates/window.logo.png});
INSERT INTO resources(id, name, restype, content) VALUES('76', 'Window.Title', 0, $FILE{/com/openbravo/pos/templates/Window.Title.txt});
INSERT INTO resources(id, name, restype, content) VALUES('77', 'script.posapps', 0, $FILE{/com/openbravo/pos/templates/script.posapps.txt});
INSERT INTO resources(id, name, restype, content) VALUES('78', 'img.posapps', 1, $FILE{/com/openbravo/pos/templates/img.posapps.png});

-- ADD CATEGORIES
INSERT INTO categories(id, name) VALUES ('000', 'Category Standard');

-- ADD TAXCATEGORIES
/* 002 added 31/01/2017 00:00:00. */
INSERT INTO taxcategories(id, name) VALUES ('000', 'Tax Exempt');
INSERT INTO taxcategories(id, name) VALUES ('001', 'Tax Standard');
INSERT INTO taxcategories(id, name) VALUES ('002', 'Tax Other');

-- ADD TAXES
/* 002 added 31/01/2017 00:00:00. */
INSERT INTO taxes(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('000', 'Tax Exempt', '000', NULL, NULL, 0, FALSE, NULL);
INSERT INTO taxes(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('001', 'Tax Standard', '001', NULL, NULL, 0.20, FALSE, NULL);
INSERT INTO taxes(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('002', 'Tax Other', '002', NULL, NULL, 0, FALSE, NULL);

-- ADD PRODUCTS
INSERT INTO products(id, reference, code, name, category, taxcat, isservice, display, printto) 
VALUES ('xxx999_999xxx_x9x9x9', 'xxx999', 'xxx999', '***', '000', '001', 1, '<html><center>***', '1');
INSERT INTO products(id, reference, code, name, category, taxcat, isservice, display, printto) 
VALUES ('xxx998_998xxx_x8x8x8', 'xxx998', 'xxx998', '****', '000', '001', 1, '<html><center>****', '1');

-- ADD PRODUCTS_CAT
INSERT INTO products_cat(product) VALUES ('xxx999_999xxx_x9x9x9');
INSERT INTO products_cat(product) VALUES ('xxx998_998xxx_x8x8x8');

-- ADD LOCATION
INSERT INTO locations(id, name, address) VALUES ('0','Location 1','Local');

-- ADD SUPPLIERS
INSERT INTO suppliers(id, searchkey, name) VALUES ('0','uniCenta','uniCenta');

-- ADD UOM
INSERT INTO uom(id, name) VALUES ('0','Each');

-- ADD FLOORS
INSERT INTO floors(id, name, image) VALUES ('0', 'Restaurant floor', $FILE{/com/openbravo/pos/templates/restaurant_floor.png});

-- ADD PLACES
INSERT INTO places(id, name, x, y, floor) VALUES ('1', 'Table 1', 100, 50, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('2', 'Table 2', 250, 50, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('3', 'Table 3', 400, 50, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('4', 'Table 4', 550, 50, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('5', 'Table 5', 700, 50, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('6', 'Table 6', 850, 50, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('7', 'Table 7', 100, 150, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('8', 'Table 8', 250, 150, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('9', 'Table 9', 400, 150, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('10', 'Table 10', 550, 150, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('11', 'Table 11', 700, 150, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('12', 'Table 12', 850, 150, '0');

-- ADD SHIFTS
INSERT INTO shifts(id, startshift, endshift, pplid) VALUES ('0', '2016-01-01 00:00:00.001', '2016-01-01 00:00:00.002','0');

-- ADD BREAKS
INSERT INTO breaks(id, name, visible, notes) VALUES ('0', 'Lunch Break', TRUE, NULL);
INSERT INTO breaks(id, name, visible, notes) VALUES ('1', 'Tea Break', TRUE, NULL);
INSERT INTO breaks(id, name, visible, notes) VALUES ('2', 'Mid Break', TRUE, NULL);

-- ADD SHIFT_BREAKS
INSERT INTO shift_breaks(id, shiftid, breakid, starttime, endtime) VALUES ('0', '0', '0', '2016-01-01 00:00:00.003', '2016-01-01 00:00:00.004');

-- ADD SEQUENCES
INSERT INTO pickup_number VALUES(1);
INSERT INTO ticketsnum VALUES(1);
INSERT INTO ticketsnum_refund VALUES(1);
INSERT INTO ticketsnum_payment VALUES(1);

-- ADD APPLICATION VERSION
INSERT INTO applications(id, name, version) VALUES($APP_ID{}, $APP_NAME{}, $APP_VERSION{});