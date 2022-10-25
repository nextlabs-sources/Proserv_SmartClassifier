package com.nextlabs.tomcat.hibernate;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.type.StringType;

public class SQLServerDialectOverrider 
		extends SQLServer2012Dialect {
	
	public SQLServerDialectOverrider() {
		super();
		registerHibernateType(Types.NVARCHAR, StringType.INSTANCE.getName());
		registerHibernateType(Types.NCHAR, StringType.INSTANCE.getName());
	}
}
