package dti.oasis.session;

import java.util.Map;

/**
 * The PageViewStateAdmin interface describes the methods used to create and maintain data associated with each page
 * view.
 *  
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 23, 2011
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PageViewStateAdmin {

  /**
   * Returns a new unique id for page document.
   */
  public String getNewPageViewId();

  /**
   * Get cached data for the cached page document id, if exists.
   * Otherwise a new page view state instance is created, cached and returned.
   */
  public Map getPageViewData();

 /**
  * Get cached data for the page document.
  * @return a map representing cached data for the page document.
  */
  public Map getPageViewData(String pageViewId);

 /**
  * Removes cached data for the page document.
  */
  public void clearPageViewData(String pageViewId);
}
