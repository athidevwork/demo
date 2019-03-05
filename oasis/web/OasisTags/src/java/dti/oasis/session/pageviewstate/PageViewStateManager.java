package dti.oasis.session.pageviewstate;

import dti.oasis.app.ApplicationContext;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSession;
import dti.oasis.session.pageviewstate.impl.PageViewStateManagerImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 13, 2011
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 9/19/2015        Parker      Issue#162759 Generate new pageViewStateId at the beginning of the executeOnPageLoad.
 *
 * ---------------------------------------------------
 */
public abstract class PageViewStateManager {

  /**
   * The bean name of a PageViewStateManager extension.
   */
  public static final String BEAN_NAME = "PageViewStateManager";

  public static final String TERMINATE_PAGE_VIEW_STATE = "terminatePageViewState";


  /**
   * Returns an instance of page view state manager.
   *
   * @return PageViewStateManager, an instance of page view state manager with implementation information.
   */
  public synchronized static final PageViewStateManager getInstance() {
      if (c_instance == null) {
          if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
              c_instance = (PageViewStateManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
          }
          else{
              c_instance = new PageViewStateManagerImpl();
          }
      }
      return c_instance;
  }

  /**
  *  Performs the scheduling task for cleanup activity.
  */
  public abstract void scheduleCleanupTask(PageViewStateAdmin pageViewStateAdmin, String Id);

  /**
   * Method that returns a boolean value indicating whether the view state has been already marked for cleanup.
   *
   * @param pageViewStateId
   * @return
   */
  public abstract boolean isPageViewStateAlreadyScheduledForCleanup (String pageViewStateId);

  /**
  *  Performs the cleaning task for all scheduled cleanup items.
  *  This is triggered by java timer via Spring Configuration.
  */
  public abstract void cleanupScheduledTasks();

  /**
   * Method that generate new page view state id for the request.
   */
  public abstract void replaceWithNewPageView(HttpServletRequest request) ;

  /**
   * Method that sets up new page view state map for the request.
   */
  public abstract void setupPageViewState(HttpServletRequest request) ;

  /**
   * Method that logs page view state cache content to log file.
   */
  public abstract void logPageViewState(PageViewStateAdmin PageViewStateAdmin, String pageViewStateId) ;

  private static PageViewStateManager c_instance;
}
