    CREATE TABLE t_hongbao_info (
      id int(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
      uid varchar(50)  NULL  COMMENT '花椒号',
      update_time  timestamp  NULL   COMMENT '更新时间',
      state varchar(100) DEFAULT NULL COMMENT '账号状态',
      PRIMARY KEY (id)
    );



select * from t_hongbao_info