package com.nextlabs.tomcat.secured;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.XADataSource;

import com.nextlabs.smartclassifier.util.NxlCryptoUtil;

public class EncryptedDataSourceFactory extends DataSourceFactory {

  @Override
  public DataSource createDataSource(Properties properties, Context context, boolean XA)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SQLException,
          NoSuchAlgorithmException, NoSuchPaddingException {

    PoolConfiguration poolProperties = EncryptedDataSourceFactory.parsePoolProperties(properties);
    poolProperties.setPassword(NxlCryptoUtil.decrypt(poolProperties.getPassword()));

    if (poolProperties.getDataSourceJNDI() != null && poolProperties.getDataSource() == null) {
      performJNDILookup(context, poolProperties);
    }

    org.apache.tomcat.jdbc.pool.DataSource dataSource =
        XA
            ? new XADataSource(poolProperties)
            : new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
    dataSource.createPool();

    return dataSource;
  }
}
