/*
SQLyog Ultimate v8.32 
MySQL - 5.7.16 : Database - zhiwen
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`zhiwen` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `zhiwen`;

/*Table structure for table `fansid` */

DROP TABLE IF EXISTS `fansid`;

CREATE TABLE `fansid` (
  `id` int(50) unsigned NOT NULL AUTO_INCREMENT,
  `uid` int(50) DEFAULT NULL,
  `fansids` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `fansid` */

LOCK TABLES `fansid` WRITE;

insert  into `fansid`(`id`,`uid`,`fansids`) values (1,38,'[]'),(2,37,'[38]');

UNLOCK TABLES;

/*Table structure for table `ques` */

DROP TABLE IF EXISTS `ques`;

CREATE TABLE `ques` (
  `q_id` int(200) unsigned NOT NULL AUTO_INCREMENT,
  `q_c_time` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '问题创建时间',
  `q_title` varchar(1000) CHARACTER SET utf8 DEFAULT NULL COMMENT '标题',
  `q_content` text CHARACTER SET utf8 COMMENT '内容',
  `q_belt_u_id` int(200) DEFAULT NULL COMMENT '问题的发布者id',
  `q_pic_urls` varchar(1000) CHARACTER SET utf8 DEFAULT NULL COMMENT '问题的图片url json数组',
  `q_file_urls` varchar(1000) CHARACTER SET utf8 DEFAULT NULL COMMENT '问题的附件url json数组',
  `q_comments_ids` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '问题的回复id json数组',
  `q_score` int(200) DEFAULT NULL COMMENT '问题的积分',
  `q_comm_num` int(200) DEFAULT NULL COMMENT '回复的个数',
  `q_digest` varchar(400) CHARACTER SET utf8 DEFAULT NULL COMMENT '问题摘要',
  `digest_pic_url` varchar(400) CHARACTER SET utf8 DEFAULT NULL COMMENT '摘要图片的url',
  `q_belt_u_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '问题的发布者名称',
  `q_belt_u_head_url` varchar(400) CHARACTER SET utf8 NOT NULL COMMENT '问题的发布者头像url',
  `q_belt_subject` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '所属学科',
  `q_label` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '标签',
  `is_of_adopted_reply` int(50) DEFAULT NULL,
  PRIMARY KEY (`q_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4;

/*Data for the table `ques` */

LOCK TABLES `ques` WRITE;

insert  into `ques`(`q_id`,`q_c_time`,`q_title`,`q_content`,`q_belt_u_id`,`q_pic_urls`,`q_file_urls`,`q_comments_ids`,`q_score`,`q_comm_num`,`q_digest`,`digest_pic_url`,`q_belt_u_name`,`q_belt_u_head_url`,`q_belt_subject`,`q_label`,`is_of_adopted_reply`) values (21,'2018-10-25 23:12','8086CPU','8086CPU的地址线为20根，寻址空间为1MB。最少可划分为多少个逻辑段？最多呢？',37,'[\"1540480366948.jpg\"]','{}','[23,51]',7,2,'8086CPU的地址线为20根','1540480366948.jpg','张凯','1540475635245.jpg','计算机组成原理','[\"计算机组成原理\"]',-1),(22,'2018-10-25 23:14','mov指令','请指出下列指令的错误    ：MOV COUNT,[SI]',37,'[]','{}','[24]',2,1,'请指出下列指令的错误    ：','klbertj.jpg','张凯','1540475635245.jpg','汇编语言','[\"汇编语言\"]',-1),(23,'2018-10-25 23:16','寻址方式','指令MOV AX,ES:COUNT[DI]，源操作数的寻址方式是         。 \r\nA．基址变址寻址    B. 立即寻址     C. 寄存器相对寻址    D. 相对基址变址寻址',37,'[]','{}','[25,26,35,40,42,66,72]',5,7,'指令MOV AX,ES:COU','klbertj.jpg','张凯','1540475635245.jpg','汇编语言','[\"汇编语言\"]',-1),(24,'2018-10-25 23:18','ASCII码转换','编写一子程序 asc2bin ，将 ASCII 转换为二进制数 \r\n要求： \r\n输入参数：AL 中存放需要转换的 ASCII \r\n输出参数：AL 中存放转换后的二进制数并返回',37,'[]','{}','[28]',8,1,'编写一子程序 asc2bin ','klbertj.jpg','张凯','1540475635245.jpg','汇编语言','[\"汇编语言\"]',-1),(25,'2018-10-25 23:20','内存编址','对32位字长的IA-32处理器来说，其主存只能采用32位编址，即每个存储器地址保存一个32位数据。',37,'[]','{}','[29,37,38,41]',8,5,'对32位字长的IA-32处理器','klbertj.jpg','张凯','1540475635245.jpg','汇编语言','[\"汇编语言\"]',36),(26,'2018-10-26 10:48','数据结构','数据结构中，与所使用的计算机无关的结构是？\nA.存储结构\nB.物理结构\nC.逻辑结构\nD.物理和存储结构',38,'[\"1540522091035.jpg\"]','{}','[67]',8,1,'数据结构中，与所使用的计算机无','1540522091035.jpg','吴壮','1540604643990.jpg','数据结构','[\"数据结构\"]',-1),(35,'2018-10-27 08:36','排序','请问时间复杂度为O(nlogn)的排序有哪些？',38,'[]','{}','[43,44,45,56,57,58,59,65,68,71]',1,11,'请问时间复杂度为O(nlogn','klbertj.jpg','吴壮','1540604643990.jpg','数据结构','[\"数据结构\"]',53),(36,'2018-10-27 09:00','SPI总线','如何理解SPI总线的环形结构？',37,'[]','{}','[46]',6,1,'如何理解SPI总线的环形结构','klbertj.jpg','Klbertj','1540475635245.jpg','数字逻辑','[\"数字逻辑\"]',-1),(37,'2018-10-27 09:01','为什么结构体占用的空间不等于成员变量的总空间的和？','占用的空间和成员变量的总空间不想等',38,'[]','{}','[]',10,1,'占用的空间和成员变量的总空间不','klbertj.jpg','吴壮','1540604643990.jpg','数据结构','[\"数据结构\"]',61),(38,'2018-10-27 09:03','spi总线的原理？','如何理解spi总线的环形结构',38,'[]','{}','[50]',10,2,'如何理解spi总线的环形结','klbertj.jpg','吴壮','1540604643990.jpg','数字逻辑','[\"数字逻辑\"]',47),(39,'2018-10-27 09:03','PV操作怎么实现？','看了操作系统书，这块不太明白',37,'[]','{}','[62,63]',3,2,'看了操作系统书，这块不太明','klbertj.jpg','Klbertj','1540475635245.jpg','操作系统','[\"操作系统\"]',-1),(40,'2018-10-27 09:11','问题test','正文。。。。。。',37,'[\"1540602704549.jpg\",\"1540602704551.jpg\"]','{\"server_name\":[\"1540602704553\"],\"server_real_name\":[{\"1540602704553\":\"家庭经济困难学生认定工作相关附件.zip\"}]}','[49,52,64]',4,4,'正文。。。。。','1540602704549.jpg','Klbertj','1540475635245.jpg','操作系统','[\"算法分析与设计\"]',48),(41,'2018-10-27 09:39','谁最帅','wwb',37,'[]','{}','[54,55,60]',3,3,'ww','klbertj.jpg','Klbertj','1540475635245.jpg','操作系统','[\"操作系统\"]',-1),(42,'2018-10-27 12:11','哦哦哦','噢噢噢哦哦，我想用一下',37,'[]','{}','[69,70]',3,2,'噢噢噢哦哦，我想用一','klbertj.jpg','Klbertj','1540475635245.jpg','算法分析与设计','[\"C++程序设计\"]',-1),(43,'2018-10-28 18:24','哈哈','哈哈',37,'[\"1540722270916.jpg\"]','{}','[]',2,0,'哈','1540722270916.jpg','Klbertj','1540475635245.jpg','计算机网络','[\"计算机网络\"]',-1);

UNLOCK TABLES;

/*Table structure for table `reply` */

DROP TABLE IF EXISTS `reply`;

CREATE TABLE `reply` (
  `r_id` int(200) unsigned NOT NULL AUTO_INCREMENT,
  `belt_user_id` int(200) DEFAULT NULL,
  `belt_user_uname` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `belt_user_head_url` varchar(400) CHARACTER SET utf8 DEFAULT NULL,
  `is_adopted` int(2) DEFAULT NULL,
  `content` varchar(4000) CHARACTER SET utf8 DEFAULT NULL,
  `create_time` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `pic_urls` varchar(600) CHARACTER SET utf8 DEFAULT NULL,
  `belt_ques_id` int(50) DEFAULT NULL,
  PRIMARY KEY (`r_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4;

/*Data for the table `reply` */

LOCK TABLES `reply` WRITE;

insert  into `reply`(`r_id`,`belt_user_id`,`belt_user_uname`,`belt_user_head_url`,`is_adopted`,`content`,`create_time`,`pic_urls`,`belt_ques_id`) values (23,37,'张凯','1540475635245.jpg',0,'最少划分为16个逻辑段，最多划分为65536个逻辑段（每段16个字节','2018-10-25 23:13','[]',21),(24,37,'张凯','1540475635245.jpg',0,'1.MOV COUNT,[SI]  错。两个操作数不能都是存储单元 ','2018-10-25 23:15','[]',22),(25,37,'张凯','1540475635245.jpg',0,'选C','2018-10-25 23:16','[]',23),(26,37,'张凯','1540475635245.jpg',0,'能解释一下为什么嘛','2018-10-25 23:17','[]',23),(28,37,'张凯','1540475635245.jpg',0,'对于ascii码，先都当做数字来处理，即都减30h，在与9比大小，大于9的说明是10~15，再减7即可。\r\n\r\n代码如下：\r\n\r\nasc2bin     proc\r\n    sub     al, 30h         ;先都当做表示数字的acsii\r\n    cmp     al, 9           ;与9比较，小于等于9的，直接跳转，大\r\n                            ;于9的，仍然需要处理\r\n    jbe     asc2bin_sub     ;跳转子程序\r\n    sub     al, 7           ;继续减7\r\nasc2bin_sub:\r\n    ret                     ;返回主程序\r\nasc2bin     endp','2018-10-25 23:19','[\"1540480782090.jpg\"]',24),(29,37,'张凯','1540475635245.jpg',0,'错误 可以保存8位以及16位数据','2018-10-25 23:21','[]',25),(35,38,'吴壮','1540604643990.jpg',0,'李金铭摸摸摸','2018-10-26 19:00','[]',23),(36,38,'吴壮','1540604643990.jpg',1,'回复我也是醉了。','2018-10-26 19:03','[]',25),(37,38,'吴壮','1540604643990.jpg',0,'ing敏敏弄','2018-10-26 21:00','[]',25),(38,38,'吴壮','1540604643990.jpg',0,'回老家了','2018-10-26 21:01','[]',25),(40,38,'吴壮','1540604643990.jpg',0,'个咯啦咯啦咯哈哈哈','2018-10-26 22:16','[]',23),(41,38,'吴壮','1540604643990.jpg',0,'监控','2018-10-26 22:20','[]',25),(42,38,'吴壮','1540604643990.jpg',0,'时候','2018-10-26 22:32','[]',23),(43,37,'Klbertj','1540475635245.jpg',0,'快速，堆排序，二路归并\r\n时间复杂度为 O(n²)排序？\r\n直接插入，折半插入，希尔，冒泡，简单选择','2018-10-27 08:36','[]',35),(44,37,'Klbertj','1540475635245.jpg',0,'下图','2018-10-27 08:38','[\"1540600682274.jpg\"]',35),(45,37,'Klbertj','1540475635245.jpg',0,'再给你来个总结','2018-10-27 08:43','[\"1540601013640.jpg\"]',35),(46,37,'Klbertj','1540475635245.jpg',0,'回答：SPI总线的物理层是一对移位寄存器，寄存器两端互相连接，使数据从一个寄存器出来，进入另一个寄存器。寄存器在时钟总线的驱动下一位一位地移到另一个寄存器，所以对于主机和从机来说，自己发送的同时也在接收，这也是SPI总线全双工的原因。','2018-10-27 09:01','[]',36),(47,37,'Klbertj','1540475635245.jpg',1,'回答：SPI总线的物理层是一对移位寄存器，寄存器两端互相连接，使数据从一个寄存器出来，进入另一个寄存器。寄存器在时钟总线的驱动下一位一位地移到另一个寄存器，所以对于主机和从机来说，自己发送的同时也在接收，这也是SPI总线全双工的原因。','2018-10-27 09:04','[]',38),(48,38,'吴壮','1540604643990.jpg',1,'这个问题。。。。','2018-10-27 09:12','[\"1540602759150.jpg\"]',40),(49,38,'吴壮','1540604643990.jpg',0,'回复回复','2018-10-27 09:35','[]',40),(50,37,'Klbertj','1540475635245.jpg',0,'回复你。。。。','2018-10-27 09:37','[]',38),(51,38,'吴壮','1540604643990.jpg',0,'hello,world','2018-10-27 09:37','[]',21),(52,38,'吴壮','1540604643990.jpg',0,'vvvvhjjj','2018-10-27 09:41','[]',40),(53,37,'Klbertj','1540475635245.jpg',1,'电话费你放假纯牛奶急急急','2018-10-27 09:41','[]',35),(54,38,'吴壮','1540604643990.jpg',0,'我。。。','2018-10-27 09:51','[]',41),(55,38,'吴壮','1540604643990.jpg',0,'还是我','2018-10-27 09:51','[]',41),(56,37,'Klbertj','1540475635245.jpg',0,'恢复恢复klbertj','2018-10-27 09:58','[]',35),(57,37,'Klbertj','1540475635245.jpg',0,'时间复杂度啥意思','2018-10-27 10:41','[]',35),(58,37,'Klbertj','1540475635245.jpg',0,'时间复杂度是。。。。。','2018-10-27 10:42','[]',35),(59,37,'Klbertj','1540475635245.jpg',0,'还是不知道啊','2018-10-27 10:42','[]',35),(60,37,'Klbertj','1540475635245.jpg',0,'积极ACL','2018-10-27 10:50','[]',41),(61,37,'Klbertj','1540475635245.jpg',1,'为什么结构体占用的空间不等于成员变量的空间之和？\n回答：编译器为了优化CPU的存取效率，对结构体才用了地址对齐策略。比如，struct{char a;int b;};编译器会把char a单独占用4个字节的空间，int b独占4个字节的空间，虽然造成了空间浪费，但提高了寻址效率。','2018-10-27 10:55','[]',37),(62,38,'吴壮','1540604643990.jpg',0,'如何pv呢？','2018-10-27 11:05','[]',39),(63,38,'吴壮','1540604643990.jpg',0,'详情请@王文博','2018-10-27 11:05','[]',39),(64,38,'吴壮','1540604643990.jpg',0,'这是个啥问题','2018-10-27 11:10','[]',40),(65,37,'Klbertj','1540475635245.jpg',0,'通知。。。','2018-10-27 11:18','[]',35),(66,38,'吴壮','1540604643990.jpg',0,'通知你','2018-10-27 11:19','[]',23),(67,38,'吴壮','1540604643990.jpg',0,'没人回答我哦','2018-10-27 12:04','[]',26),(68,37,'Klbertj','1540475635245.jpg',0,'我想用一下','2018-10-27 12:10','[]',35),(69,37,'Klbertj','1540475635245.jpg',0,'空军建军节','2018-10-27 12:11','[]',42),(70,38,'吴壮','1540604643990.jpg',0,'考虑考虑考虑考虑','2018-10-27 12:11','[]',42),(71,37,'Klbertj','1540475635245.jpg',0,'再来一次','2018-10-27 12:15','[]',35),(72,37,'Klbertj','1540475635245.jpg',0,'dbdndbxb','2018-10-27 17:55','[]',23);

UNLOCK TABLES;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `u_id` int(200) unsigned NOT NULL AUTO_INCREMENT,
  `u_name` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `u_pass` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `u_email` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `u_code` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `u_colle_que_ids` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '用户收藏的所有问题id数组',
  `u_avator_path` varchar(400) CHARACTER SET utf8 DEFAULT NULL,
  `u_bg_path` varchar(400) CHARACTER SET utf8 DEFAULT NULL,
  `u_attention_people_ids` varchar(400) CHARACTER SET utf8 DEFAULT NULL,
  `u_attention_people_count` int(20) DEFAULT NULL,
  `u_colle_que_num` int(20) DEFAULT NULL,
  `u_ques_asked_ids` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `u_ques_asked_num` int(20) DEFAULT NULL,
  `u_discuss_num` int(50) DEFAULT NULL,
  `fans_num` int(50) DEFAULT NULL,
  `college` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `major` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `score` int(20) DEFAULT NULL,
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4;

/*Data for the table `user` */

LOCK TABLES `user` WRITE;

insert  into `user`(`u_id`,`u_name`,`u_pass`,`u_email`,`u_code`,`u_colle_que_ids`,`u_avator_path`,`u_bg_path`,`u_attention_people_ids`,`u_attention_people_count`,`u_colle_que_num`,`u_ques_asked_ids`,`u_ques_asked_num`,`u_discuss_num`,`fans_num`,`college`,`major`,`score`) values (37,'Klbertj','25f9e794323b453885f5181f1b624d0b','1810826525@qq.com','','[34,38]','1540475635245.jpg','1540531954676.jpg','[]',0,2,'[21,22,23,24,25,36,39,40,41,42,43]',11,59,1,'信息工程学院','计算机科学与技术',6000),(38,'吴壮','e10adc3949ba59abbe56e057f20f883e','wuzhuangmail@icloud.com','','[25,23,41,40]','1540604643990.jpg','1540604661084.jpg','[37]',1,4,'[26,35,37,38]',4,20,0,'信息工程学院','计算机科学与技术',5983),(39,'yaosean','e10adc3949ba59abbe56e057f20f883e','120636520@qq.com','1540600491764','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(40,'zzzz','670b14728ad9902aecba32e22fa4f6bd','852673181@qq.com','1540600759613','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(41,'20152430133','25f9e794323b453885f5181f1b624d0b','20152430133@qq.com','','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60000),(42,'20152430205','25f9e794323b453885f5181f1b624d0b','20152430205@@qq.com','','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60000),(43,'20152430206','e10adc3949ba59abbe56e057f20f883e','1197130914@qq.com','1540601507813','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(44,'ymw','e10adc3949ba59abbe56e057f20f883e','209414167@qq.com','1540601762180','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(45,'whq','e10adc3949ba59abbe56e057f20f883e','2107633162@qq.com','1540601950049','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(46,'yaosean1','e10adc3949ba59abbe56e057f20f883e','3216252324@qq.com','1540607595264','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(47,'zzu','e10adc3949ba59abbe56e057f20f883e','15638539475@163.com','1540607789958','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60),(48,'张凯1','e10adc3949ba59abbe56e057f20f883e','woaizuolijuan1314@gmail.com','1540607905479','[]','klbertj.jpg','','[]',0,0,'[]',0,0,0,'','',60);

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
