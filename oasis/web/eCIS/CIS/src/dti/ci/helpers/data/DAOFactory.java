package dti.ci.helpers.data;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * Factory for generating DAO instance.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Hong Yuan
 *         Date:   Apr 18, 2005
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ------------------------------------------------------------------
 *         <p/>
 *         ------------------------------------------------------------------
 */

public class DAOFactory {

  private final static String prefix = "dti.ci.helpers.data.";
  private static DAOFactory daoFactory = null;

  private DAOFactory() {
  }

  /**
   * Public static method, returns system defined dao factory instance.
   *
   * @return daoFactory
   */
  public static DAOFactory getDAOFactory() {
    String methodName = "getDAOFactory";
    Logger lggr = LogUtils.enterLog(DAOFactory.class, methodName);
    if (daoFactory == null) {
      String factoryName = null;
      try {
        factoryName = ApplicationContext.getInstance().getProperty("cidaofactory");
        Class className = Class.forName(factoryName);
        daoFactory = (DAOFactory) className.newInstance();
        lggr.exiting(className.getName(), methodName, daoFactory);
      } catch (AppException ne) {
        lggr.throwing(DAOFactory.class.getName(), methodName, ne);
      } catch (ClassNotFoundException cnfe) {
        lggr.throwing(DAOFactory.class.getName(), methodName, cnfe);
      } catch (InstantiationException ie) {
        lggr.throwing(DAOFactory.class.getName(), methodName, ie);
      } catch (IllegalAccessException iae) {
        lggr.throwing(DAOFactory.class.getName(), methodName, iae);
      }
    }
    return daoFactory;
  }

  /**
   * Generates an instance of DAO.
   *
   * @param daoName
   * @return DAO  data access object
   * @throws dti.ci.helpers.data.DAOInstantiationException
   *
   */
  public CIBaseDAO getDAO(String daoName)
      throws DAOInstantiationException {
    String methodName = "getDAO";
    Logger lggr = LogUtils.enterLog(this.getClass(), methodName);
    CIBaseDAO dao = null;
    try {
      daoName = prefix.concat(daoName);
      Class daoClass = Class.forName(daoName);
      dao = (CIBaseDAO) daoClass.newInstance();
      lggr.exiting(this.getClass().getName(), methodName, dao);
    } catch (Exception e) {
      lggr.throwing(this.getClass().getName(), methodName, e);
      throw new DAOInstantiationException(e.getMessage());
    }
    return dao;
  }
}
