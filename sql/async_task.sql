
CREATE TABLE `async_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务类型',
  `state` int(11) NOT NULL COMMENT '任务状态： 0 待处理； 1 处理成功； 2 处理失败',
  `key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务的key，如：可将orderNo作为key',
  `index` bigint(20) NOT NULL DEFAULT 0 COMMENT '某个key，可以创建多个任务，则用index来区分。默认值为0',
  `data` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务的数据',
  `retry` int(11) NOT NULL COMMENT '任务的执行次数',
  `createTime` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `updateTime` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_key_index`(`type`, `key`, `index`) USING BTREE,
  INDEX `idx_type_state`(`type`, `state`) USING BTREE,
  INDEX `idx_updateTime`(`updateTime`) USING BTREE
) ENGINE = InnoDB COMMENT = '异步任务';
