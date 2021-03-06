CREATE DATABASE IF NOT EXISTS DOCS2;

CREATE TABLE `FILELIST` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `FILENAME` text,
  `OWNER` text,
  `PRIVLAGE` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

CREATE TABLE `LOG` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `QUERY` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `LOGIN` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` text,
  `PASSWORD` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;

