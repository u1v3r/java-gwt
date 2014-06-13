-- phpMyAdmin SQL Dump
-- version 3.3.8.1
-- http://www.phpmyadmin.net
--
-- Hostiteľ: localhost
-- Vygenerované:: 06.Apr, 2011 - 20:58
-- Verzia serveru: 5.1.53
-- Verzia PHP: 5.2.17-pl0-gentoo

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databáza: `kosilka`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `harmonogram`
--

CREATE TABLE IF NOT EXISTS `harmonogram` (
  `hid` int(10) NOT NULL AUTO_INCREMENT,
  `yid` int(10) NOT NULL,
  `harmonogram_name` varchar(255) NOT NULL,
  `sfid` int(10) NOT NULL,
  `date` date NOT NULL,
  `exam_place` varchar(255) NOT NULL,
  PRIMARY KEY (`hid`),
  UNIQUE KEY `harmonogram_name` (`harmonogram_name`),
  KEY `Id_oboru` (`sfid`),
  KEY `yid` (`yid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `harmonogram_professor`
--

CREATE TABLE IF NOT EXISTS `harmonogram_professor` (
  `hid` int(10) NOT NULL,
  `pid` int(10) NOT NULL,
  `role` enum('0','1','2','3','4') NOT NULL,
  PRIMARY KEY (`hid`,`pid`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `harmonogram_student`
--

CREATE TABLE IF NOT EXISTS `harmonogram_student` (
  `hid` int(10) NOT NULL,
  `ID_st` int(10) NOT NULL,
  `time` char(5) DEFAULT NULL,
  `ord` int(10) unsigned NOT NULL,
  `bachelor_thesis` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`hid`,`ID_st`),
  UNIQUE KEY `ID_st` (`ID_st`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `professor`
--

CREATE TABLE IF NOT EXISTS `professor` (
  `pid` int(5) NOT NULL AUTO_INCREMENT,
  `short` varchar(5) DEFAULT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `title_before` varchar(15) NOT NULL,
  `title_behind` varchar(15) NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `name` (`lastname`,`firstname`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=56 ;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `question`
--

CREATE TABLE IF NOT EXISTS `question` (
  `qid` int(10) NOT NULL AUTO_INCREMENT,
  `sid` int(10) NOT NULL,
  `qn` int(10) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`qid`),
  UNIQUE KEY `sid` (`sid`,`qn`),
  KEY `qn` (`qn`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=357 ;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `result`
--

CREATE TABLE IF NOT EXISTS `result` (
  `rid` int(5) NOT NULL AUTO_INCREMENT,
  `ID_st` int(10) NOT NULL,
  `hid` int(10) NOT NULL,
  `oponent_grade` enum('A','B','C','D','E','F') DEFAULT NULL,
  `veduci_grade` enum('A','B','C','D','E','F') DEFAULT NULL,
  `obhajoba_grade` enum('A','B','C','D','E','F') DEFAULT NULL,
  `overall_subjects_grade` enum('A','B','C','D','E','F') DEFAULT NULL,
  `overall_grade` enum('P','PV','N') DEFAULT NULL,
  `question_number` int(3) DEFAULT NULL,
  `note` text,
  PRIMARY KEY (`rid`),
  UNIQUE KEY `ID_st` (`ID_st`),
  KEY `hid` (`hid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=42 ;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `result_subject`
--

CREATE TABLE IF NOT EXISTS `result_subject` (
  `rid` int(10) NOT NULL,
  `sid` int(10) NOT NULL,
  `grade` enum('A','B','C','D','E','F') NOT NULL,
  PRIMARY KEY (`rid`,`sid`),
  KEY `sid` (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `studyfield`
--

CREATE TABLE IF NOT EXISTS `studyfield` (
  `sfid` int(10) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  PRIMARY KEY (`sfid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `studyfield_st_obory`
--

CREATE TABLE IF NOT EXISTS `studyfield_st_obory` (
  `sfid` int(10) NOT NULL,
  `Id_oboru` int(10) NOT NULL,
  KEY `sfid` (`sfid`,`Id_oboru`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `subject`
--

CREATE TABLE IF NOT EXISTS `subject` (
  `sid` int(5) NOT NULL AUTO_INCREMENT,
  `short` varchar(5) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `name_eng` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=18 ;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `subject_studyfield`
--

CREATE TABLE IF NOT EXISTS `subject_studyfield` (
  `sfid` int(10) NOT NULL,
  `sid` int(10) NOT NULL,
  PRIMARY KEY (`sfid`,`sid`),
  KEY `sid` (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `year`
--

CREATE TABLE IF NOT EXISTS `year` (
  `yid` int(10) NOT NULL AUTO_INCREMENT,
  `year_name` char(9) NOT NULL,
  PRIMARY KEY (`yid`),
  UNIQUE KEY `year_name` (`year_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Obmedzenie pre exportované tabuľky
--

--
-- Obmedzenie pre tabuľku `harmonogram`
--
ALTER TABLE `harmonogram`
  ADD CONSTRAINT `harmonogram_ibfk_1` FOREIGN KEY (`sfid`) REFERENCES `studyfield` (`sfid`) ON DELETE CASCADE,
  ADD CONSTRAINT `harmonogram_ibfk_2` FOREIGN KEY (`yid`) REFERENCES `year` (`yid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `harmonogram_professor`
--
ALTER TABLE `harmonogram_professor`
  ADD CONSTRAINT `harmonogram_professor_ibfk_1` FOREIGN KEY (`hid`) REFERENCES `harmonogram` (`hid`) ON DELETE CASCADE,
  ADD CONSTRAINT `harmonogram_professor_ibfk_2` FOREIGN KEY (`pid`) REFERENCES `professor` (`pid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `harmonogram_student`
--
ALTER TABLE `harmonogram_student`
  ADD CONSTRAINT `harmonogram_student_ibfk_1` FOREIGN KEY (`hid`) REFERENCES `harmonogram` (`hid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `question`
--
ALTER TABLE `question`
  ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `subject` (`sid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `result`
--
ALTER TABLE `result`
  ADD CONSTRAINT `result_ibfk_1` FOREIGN KEY (`hid`) REFERENCES `harmonogram` (`hid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `result_subject`
--
ALTER TABLE `result_subject`
  ADD CONSTRAINT `result_subject_ibfk_1` FOREIGN KEY (`rid`) REFERENCES `result` (`rid`) ON DELETE CASCADE,
  ADD CONSTRAINT `result_subject_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `subject` (`sid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `studyfield_st_obory`
--
ALTER TABLE `studyfield_st_obory`
  ADD CONSTRAINT `studyfield_st_obory_ibfk_1` FOREIGN KEY (`sfid`) REFERENCES `studyfield` (`sfid`) ON DELETE CASCADE;

--
-- Obmedzenie pre tabuľku `subject_studyfield`
--
ALTER TABLE `subject_studyfield`
  ADD CONSTRAINT `subject_studyfield_ibfk_1` FOREIGN KEY (`sfid`) REFERENCES `studyfield` (`sfid`) ON DELETE CASCADE,
  ADD CONSTRAINT `subject_studyfield_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `subject` (`sid`) ON DELETE CASCADE;
