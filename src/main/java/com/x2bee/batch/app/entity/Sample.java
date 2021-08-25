package com.x2bee.batch.app.entity;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("sample")
@Getter
@Setter
@ToString
public class Sample extends BaseCommonEntity {
	private Long id;
	private String name;
	private String description;
}
