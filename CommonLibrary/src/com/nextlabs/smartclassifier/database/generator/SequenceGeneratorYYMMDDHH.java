package com.nextlabs.smartclassifier.database.generator;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.SequenceGenerator;

public final class SequenceGeneratorYYMMDDHH extends SequenceGenerator {

  private static final String PREFIX_FORMAT = "yyMMddHH";
  private static final char PADDING_CHAR = '0';
  private static final byte LENGTH = 0x8;

  @Override
  public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
    String result = toDateString(new Date(), PREFIX_FORMAT);
    Session querySession = session.getFactory().openSession();

    try {
      Query query = querySession.createSQLQuery("SELECT NEXT VALUE FOR " + getSequenceName());
      Long key = ((BigInteger) query.uniqueResult()).longValue();

      result += leftPad(String.valueOf(key), PADDING_CHAR, LENGTH);
    } catch (Exception e) {
      throw new HibernateException(e.getMessage(), e.getCause());
    } finally {
      querySession.close();
    }

    return new Long(result);
  }

  private static String toDateString(Date date, String format) {
    try {
      return (new SimpleDateFormat(format).format(date));
    } catch (Exception err) {
      return null;
    }
  }

  private static String leftPad(String value, char paddingChar, byte length) {
    StringBuilder result = new StringBuilder();
    int padLength = length - value.length();

    while (padLength > 0) {
      result.append(paddingChar);
      padLength--;
    }

    return result.append(value).toString();
  }
}
