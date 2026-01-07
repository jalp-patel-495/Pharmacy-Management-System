-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 21, 2025 at 07:06 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `experiments`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkStock` ()   BEGIN
SELECT * from products where prod_quantity<=10;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllSales` ()   BEGIN
SELECT 
        s.sale_code,
        s.cust_name,
        s.cust_mobile,
        si.prod_code,
        si.prod_name,
        si.prod_quantity,
        si.prod_price,
        s.sale_date,
        s.total,
        s.pay_type
    FROM 
        sales s
    INNER JOIN 
    saleitems si ON s.sale_code=si.sale_code;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getExpiredProducts` ()   BEGIN
SELECT * FROM products WHERE prod_expiry<CURRENT_DATE();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getExpiringProducts` ()   BEGIN
SELECT * FROM products WHERE prod_expiry BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getSalesByDate` (IN `sdate` DATE, IN `edate` DATE)   BEGIN
    SELECT 
        s.sale_code,
        s.cust_name,
        s.cust_mobile,
        si.prod_code,
        si.prod_name,
        si.prod_quantity,
        si.prod_price,
        s.sale_date,
        s.total,
        s.pay_type
        
    FROM sales s
    INNER JOIN saleitems si ON s.sale_code = si.sale_code
    WHERE s.sale_date BETWEEN sdate AND edate;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getSalesByPayment` (IN `paytype` VARCHAR(20))   BEGIN
    SELECT 
        s.sale_code,
        s.cust_name,
        s.cust_mobile,
        si.prod_code,
        si.prod_name,
        si.prod_quantity,
        si.prod_price,
        s.sale_date,
        s.total,
        s.pay_type
    FROM 
        sales s
    INNER JOIN 
        saleitems si ON s.sale_code = si.sale_code
    WHERE 
        s.pay_type = paytype;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `viewAllCustomers` ()   BEGIN
SELECT * from customer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `viewAllProducts` ()   BEGIN
SELECT * FROM products;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `viewProductByCode` (IN `pid` INT)   BEGIN
SELECT * from products WHERE prod_code=pid;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `viewProductByMfg` (IN `pmfg` VARCHAR(20))   BEGIN
SELECT * from products WHERE prod_manufacturer=pmfg;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `viewProductByType` (IN `ptype` VARCHAR(20))   BEGIN
SELECT * FROM products WHERE prod_type=ptype;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `sumOfAmtGain` () RETURNS DOUBLE  BEGIN
    DECLARE totalSum double;
    SELECT SUM(total) INTO totalSum FROM sales;
    RETURN IFNULL(totalSum, 0); 
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `sumOfAmtGainByRp` () RETURNS DOUBLE  BEGIN
    DECLARE totalSum double;
    SELECT SUM(total) INTO totalSum FROM sales 
    WHERE pay_type="Reward Points";
    RETURN IFNULL(totalSum, 0); 
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `sumOfAmtGainByUPI` () RETURNS DOUBLE  BEGIN
    DECLARE totalSum double;
    SELECT SUM(total) INTO totalSum FROM sales WHERE pay_type="UPI";
    RETURN IFNULL(totalSum, 0); 
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `admin_passwd` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`admin_passwd`) VALUES
('Admin@123');

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_code` int(11) NOT NULL,
  `prod_code` int(11) NOT NULL,
  `prod_name` varchar(50) NOT NULL,
  `prod_price` double NOT NULL,
  `cust_mobile` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`cart_code`, `prod_code`, `prod_name`, `prod_price`, `cust_mobile`) VALUES
(18, 3, 'sdc', 75, 9584710236),
(22, 20, 'bonvita', 70, 9584710236),
(30, 102, 'injection', 200, 8574921036),
(31, 15, 'dove', 150, 8574921036),
(33, 6, 'paracetamol', 9, 8574921036),
(34, 10, 'phenyl', 40, 8574921036);

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `cust_code` int(11) NOT NULL,
  `cust_name` varchar(50) NOT NULL,
  `cust_mobile` bigint(10) NOT NULL,
  `cust_passwd` varchar(20) NOT NULL,
  `cust_rewardpoint` double NOT NULL DEFAULT 0,
  `cust_order` int(11) NOT NULL DEFAULT 0,
  `cust_bcity` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`cust_code`, `cust_name`, `cust_mobile`, `cust_passwd`, `cust_rewardpoint`, `cust_order`, `cust_bcity`) VALUES
(1, 'Ramesh', 9584710236, 'R@mu_101', 59.584, 4, 'surat'),
(2, 'Suresh', 8574921036, 'abcd@000', 37.69600000000001, 11, 'ahmedabad'),
(3, 'sujal', 1234567890, 'Sujal@3107', 0, 0, 'kalol'),
(4, 'abc', 9898989898, 'Abc@12345', 15.680000000000001, 2, 'rajkot'),
(5, 'vedant', 7211172096, 'vedant@1', 0, 0, 'ahmedabad'),
(6, 'meahes', 9633690000, 'abcd@111', 0, 0, 'palanpur');

-- --------------------------------------------------------

--
-- Table structure for table `log`
--

CREATE TABLE `log` (
  `log_code` int(11) NOT NULL,
  `product_code` int(11) NOT NULL,
  `operation` varchar(20) NOT NULL,
  `log_timestamp` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `log`
--

INSERT INTO `log` (`log_code`, `product_code`, `operation`, `log_timestamp`) VALUES
(1, 27, 'Insertion', '2025-08-19 17:36:04'),
(2, 27, 'Updation', '2025-08-19 17:40:29'),
(3, 21, 'Deletion', '2025-08-19 17:44:18'),
(4, 27, 'Updation', '2025-08-19 17:47:29'),
(5, 1, 'Deletion', '2025-08-19 21:26:19'),
(6, 11, 'Deletion', '2025-08-19 21:28:12'),
(7, 2, 'Updation', '2025-08-19 23:40:27'),
(8, 15, 'Updation', '2025-08-19 23:40:27'),
(9, 102, 'Insertion', '2025-08-20 01:32:54'),
(10, 2, 'Deletion', '2025-08-20 01:51:32'),
(11, 101, 'Updation', '2025-08-20 15:41:22'),
(12, 101, 'Updation', '2025-08-20 17:41:16'),
(13, 101, 'Updation', '2025-08-20 17:41:45'),
(14, 101, 'Updation', '2025-08-20 17:42:18'),
(15, 101, 'Updation', '2025-08-20 17:43:03'),
(16, 101, 'Updation', '2025-08-20 17:43:15'),
(17, 102, 'Updation', '2025-08-20 17:46:52'),
(18, 102, 'Updation', '2025-08-20 17:47:15'),
(19, 101, 'Updation', '2025-08-20 18:26:19'),
(20, 103, 'Insertion', '2025-08-20 18:35:47'),
(21, 103, 'Updation', '2025-08-20 19:04:07'),
(22, 1, 'Insertion', '2025-08-20 22:22:36'),
(23, 2, 'Insertion', '2025-08-20 22:34:51'),
(24, 3, 'Deletion', '2025-08-20 22:35:19'),
(25, 4, 'Deletion', '2025-08-20 22:35:22'),
(26, 5, 'Deletion', '2025-08-20 22:35:25'),
(27, 10, 'Deletion', '2025-08-20 22:35:31'),
(28, 4, 'Updation', '2025-08-20 22:36:05'),
(29, 3, 'Updation', '2025-08-20 22:37:37'),
(30, 5, 'Updation', '2025-08-20 22:38:03'),
(31, 5, 'Updation', '2025-08-20 22:38:24'),
(32, 5, 'Updation', '2025-08-20 22:38:35'),
(33, 6, 'Insertion', '2025-08-20 22:39:29'),
(34, 7, 'Insertion', '2025-08-20 22:40:12'),
(35, 8, 'Updation', '2025-08-20 22:40:42'),
(36, 9, 'Updation', '2025-08-20 22:41:14'),
(37, 10, 'Updation', '2025-08-20 22:45:48'),
(38, 11, 'Insertion', '2025-08-20 22:47:08'),
(39, 12, 'Insertion', '2025-08-20 22:49:23'),
(40, 13, 'Insertion', '2025-08-20 22:50:59'),
(41, 14, 'Insertion', '2025-08-20 22:53:11'),
(42, 9, 'Updation', '2025-08-20 22:55:06'),
(43, 15, 'Insertion', '2025-08-20 22:58:09');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `prod_code` int(11) NOT NULL,
  `prod_name` varchar(50) NOT NULL,
  `prod_price` double NOT NULL,
  `prod_quantity` int(11) NOT NULL,
  `prod_usage` varchar(50) NOT NULL,
  `prod_expiry` date NOT NULL,
  `prod_manufacturer` varchar(50) NOT NULL,
  `prod_type` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`prod_code`, `prod_name`, `prod_price`, `prod_quantity`, `prod_usage`, `prod_expiry`, `prod_manufacturer`, `prod_type`) VALUES
(1, 'Cotton Baby Wipes', 70, 20, 'baby care', '2025-09-15', 'reddy pharma', 'generic'),
(2, 'Pigeon Baby Bottle', 150, 20, 'baby care', '2025-09-30', 'pigeon corporation', 'non generic'),
(3, 'bournvita', 70, 7, 'nutrition', '2025-04-12', 'cadbury', 'non generic'),
(4, 'juice', 45, 15, 'nutrition', '2025-12-15', 'real', 'non generic'),
(5, 'dove soap', 150, 10, 'cosmetic', '2025-08-17', 'dove limited', 'non generic'),
(6, 'paracetamol', 9, 25, 'medical', '2025-10-15', 'cipla', 'generic'),
(7, 'dolo', 10, 20, 'medical', '2025-09-30', 'zydus', 'non generic'),
(8, 'cough syrup', 140, 48, 'medical', '2025-12-31', 'zydus', 'non generic'),
(9, 'paracetamol', 15, 12, 'medical', '2025-08-24', 'Glenmark Pharmaceuticals', 'non generic'),
(10, 'phenyl', 40, 23, 'household', '2025-09-25', 'nandan', 'generic'),
(11, 'Nutrilite Daily', 499, 7, 'nutrition', '2025-08-24', 'Amway', 'non generic'),
(12, 'Folding Cane', 899, 15, 'assistive devices', '2027-03-14', 'mahajan&sons', 'generic'),
(13, 'Herbals Nourishing Skin Cream', 60, 17, 'cosmetic', '2025-11-15', 'Himalaya Wellness Company', 'non generic'),
(14, 'disinfecting wipes', 299, 10, 'household', '2025-09-16', 'clorox', 'non generic'),
(15, 'White Vinegar', 99, 4, 'household', '2025-08-15', 'chimanlal&sons', 'generic');

--
-- Triggers `products`
--
DELIMITER $$
CREATE TRIGGER `deletionOfProduct` AFTER DELETE ON `products` FOR EACH ROW BEGIN
INSERT INTO log VALUES(null,old.prod_code,"Deletion",now());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `insertionOfProduct` AFTER INSERT ON `products` FOR EACH ROW BEGIN
INSERT into log VALUES(null,new.prod_code,"Insertion",NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `updationOfProduct` AFTER UPDATE ON `products` FOR EACH ROW BEGIN
INSERT into log VALUES(null,new.prod_code,"Updation",now());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `saleitems`
--

CREATE TABLE `saleitems` (
  `sale_item_code` int(11) NOT NULL,
  `sale_code` int(11) NOT NULL,
  `prod_code` int(11) NOT NULL,
  `prod_name` varchar(50) NOT NULL,
  `prod_quantity` int(11) NOT NULL,
  `prod_price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `saleitems`
--

INSERT INTO `saleitems` (`sale_item_code`, `sale_code`, `prod_code`, `prod_name`, `prod_quantity`, `prod_price`) VALUES
(17, 12, 2, 'complain', 2, 90),
(18, 12, 5, 'uyt', 2, 45),
(19, 13, 20, 'bonvita', 3, 70),
(20, 14, 15, 'dove', 7, 150),
(21, 15, 2, 'complain', 3, 90),
(22, 16, 20, 'bonvita', 1, 70),
(24, 16, 3, 'sdc', 1, 75),
(25, 17, 27, 'cough syrup', 2, 140),
(26, 18, 2, 'complain', 7, 90),
(27, 18, 15, 'dove', 2, 150),
(28, 19, 101, 'juice', 2, 22),
(29, 20, 103, 'phenyl', 2, 40);

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `sale_code` int(11) NOT NULL,
  `cust_name` varchar(50) NOT NULL,
  `cust_mobile` bigint(20) NOT NULL,
  `sale_date` date NOT NULL,
  `total` double NOT NULL,
  `pay_type` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`sale_code`, `cust_name`, `cust_mobile`, `sale_date`, `total`, `pay_type`) VALUES
(10, 'Ramesh', 9584710236, '2025-08-17', 212.8, 'UPI'),
(11, 'Suresh', 8574921036, '2025-08-17', 6.72, 'Reward Points'),
(12, 'Suresh', 8574921036, '2025-08-17', 302.4, 'UPI'),
(13, 'Suresh', 8574921036, '2025-08-17', 235.2, 'UPI'),
(14, 'Suresh', 8574921036, '2025-08-17', 1176, 'UPI'),
(15, 'Suresh', 8574921036, '2025-08-17', 302.4, 'UPI'),
(16, 'abc', 9898989898, '2025-08-18', 164.64, 'UPI'),
(17, 'abc', 9898989898, '2025-08-19', 313.6, 'UPI'),
(18, 'Suresh', 8574921036, '2025-08-19', 1041.6, 'UPI'),
(19, 'Suresh', 8574921036, '2025-08-20', 51.92, 'Reward Points'),
(20, 'Suresh', 8574921036, '2025-08-20', 87.792, 'Reward Points');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_code`),
  ADD KEY `prod_code` (`prod_code`),
  ADD KEY `cust_mobile` (`cust_mobile`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`cust_code`),
  ADD UNIQUE KEY `cust_mobile` (`cust_mobile`),
  ADD UNIQUE KEY `cust_passwd` (`cust_passwd`);

--
-- Indexes for table `log`
--
ALTER TABLE `log`
  ADD PRIMARY KEY (`log_code`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`prod_code`);

--
-- Indexes for table `saleitems`
--
ALTER TABLE `saleitems`
  ADD PRIMARY KEY (`sale_item_code`),
  ADD KEY `sale_code` (`sale_code`),
  ADD KEY `saleitems_ibfk_2` (`prod_code`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`sale_code`),
  ADD KEY `cust_mobile` (`cust_mobile`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `cart_code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `cust_code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `log`
--
ALTER TABLE `log`
  MODIFY `log_code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `prod_code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;

--
-- AUTO_INCREMENT for table `saleitems`
--
ALTER TABLE `saleitems`
  MODIFY `sale_item_code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `sale_code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`cust_mobile`) REFERENCES `customer` (`cust_mobile`);

--
-- Constraints for table `saleitems`
--
ALTER TABLE `saleitems`
  ADD CONSTRAINT `saleitems_ibfk_1` FOREIGN KEY (`sale_code`) REFERENCES `sales` (`sale_code`);

--
-- Constraints for table `sales`
--
ALTER TABLE `sales`
  ADD CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`cust_mobile`) REFERENCES `customer` (`cust_mobile`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
