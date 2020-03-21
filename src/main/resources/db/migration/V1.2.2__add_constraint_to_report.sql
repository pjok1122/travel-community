ALTER TABLE `report` ADD CONSTRAINT `FK_report_info_TO_report`
FOREIGN KEY (`info_id`) REFERENCES `report_info` (`id`) ON DELETE CASCADE ;
