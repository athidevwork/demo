package dti.oasis.session.pageviewstate.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.pageviewstate.PageViewStateManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 7, 2011
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 9/19/2015        Parker      Issue#162759 Generate new pageViewStateId at the beginning of the executeOnPageLoad.
 * 11/13/2018       wreeder     196147 - Propagate the queued page view data to the new page view data in case it's a load request and the page view id gets regenerated
 * ---------------------------------------------------
 */
public class PageViewStateManagerImpl extends PageViewStateManager {

    /**
    *  Performs the scheduling task for cleaning page view state cache.
    */
    public void scheduleCleanupTask(PageViewStateAdmin pageViewStateAdmin, String Id) {
        Logger l = LogUtils.enterLog(getClass(), "scheduleCleanupTask", new Object[]{pageViewStateAdmin, Id});
        if (!isPageViewStateAlreadyScheduledForCleanup(Id)) {
          Converter longConverter = ConverterFactory.getInstance().getConverter(Long.class);
          Long currentTime = new Date().getTime();
          Long scheduledTimeout = (Long) longConverter.convert(Long.class, ApplicationContext.getInstance().getProperty(PAGE_VIEW_STATE_CACHE_TIMEOUT, "10000"));
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "scheduleCleanupTask", "Scheduling clean up activity for page state view id [" + Id + "] after [" + (scheduledTimeout / 1000) + "] seconds.");
                l.logp(Level.FINE, getClass().getName(), "scheduleCleanupTask", "scheduledTimeoutPeriod:" + scheduledTimeout + "/currentTimeTicks:" + currentTime + "/scheduledTimeTicks:" + (currentTime + scheduledTimeout));
            }

          m_pageViewStateToCleanup.put(Id, pageViewStateAdmin);
          m_cleanupTimer.put(currentTime + scheduledTimeout, Id);
          if (l.isLoggable(Level.FINE))
            l.logp(Level.FINE, getClass().getName(), "scheduleCleanupTask", "Cleanup activity for page state view id [" + Id + "] scheduled successfully. Total Scheduled Timer Instances:" + m_cleanupTimer.keySet().size());
        }
        l.exiting(getClass().getName(), "scheduleCleanupTask");
    }


    /**
    *  Performs the cleaning task for expired page view state cached.
    */
    public void cleanupScheduledTasks() {
      Logger l = LogUtils.enterLog(getClass(), "cleanupScheduledTasks");

      Long currentTimeTicks = new Date().getTime();
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "**** [" + ApplicationContext.getInstance().getProperty(KEY_FOR_APPLICATION_ID) + "] cleanupScheduledTasks triggered. currentTimeTicks:" + currentTimeTicks + ". Total page view state map scheduled for Cleanup:" + m_cleanupTimer.keySet().size());
        }
      List expiredPageViewStateIdList = new ArrayList();
      List expiredTimerList = new ArrayList();
      synchronized (m_cleanupTimer) {
        Iterator it = m_cleanupTimer.keySet().iterator();
        while (it.hasNext()) {
          long timer = (Long) it.next();
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "scheduled timer tick:" + timer + "/Current timer tick:" + currentTimeTicks);
            }
          if (timer < currentTimeTicks) {

            String pageViewStateId = (String) m_cleanupTimer.get(timer);
            PageViewStateAdmin PageViewStateAdmin = (PageViewStateAdmin) m_pageViewStateToCleanup.get(pageViewStateId);
              if (l.isLoggable(Level.FINE)) {
                  l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "pageViewStateId:" + pageViewStateId);
                  l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "PageViewStateAdmin:" + PageViewStateAdmin);
              }
            // If the user session is still valid, then proceed with page view state cleanup task. Otherwise, assume the
            // user session time out would have cleaned up the page view state as well.
            if (PageViewStateAdmin != null) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "PageViewStateAdmin.getPageViewData(pageViewStateId):" + PageViewStateAdmin.getPageViewData(pageViewStateId));
                    l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "Number of items cached in page view state [" + pageViewStateId + "] is :" + PageViewStateAdmin.getPageViewData(pageViewStateId).keySet().toArray().length);
                    l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "About to clear Page view state information for ID:" + pageViewStateId);
                }

                try {
                    logPageViewState(PageViewStateAdmin, pageViewStateId);
                } catch (Exception e) {
                    if (l.isLoggable(Level.WARNING)) {
                        l.logp(Level.WARNING, getClass().getName(), "cleanupScheduledTasks", "Error logging page view state data for ID:" + pageViewStateId, e);
                    }
                }
                PageViewStateAdmin.clearPageViewData(pageViewStateId);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "cleanupScheduledTasks", "Successfully cleared Page view state information for ID:" + pageViewStateId);
                }
            }

            expiredTimerList.add(timer);
            expiredPageViewStateIdList.add(pageViewStateId);

          } else {
            break;
          }
        }
      }
      m_pageViewStateToCleanup.keySet().removeAll(expiredPageViewStateIdList);
      m_cleanupTimer.keySet().removeAll(expiredTimerList);

      l.exiting(getClass().getName(), "cleanupScheduledTasks");
    }

    @Override
    public void  replaceWithNewPageView(HttpServletRequest request){
        Logger l = LogUtils.enterLog(getClass(), "replaceWithNewPageView", new Object[]{request});

        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
        String pageViewStateId = pageViewStateAdmin.getNewPageViewId();
        RequestStorageManager.getInstance().set(RequestIds.CACHE_ID_FOR_PAGE_VIEW, pageViewStateId);
        request.setAttribute(RequestIds.CACHE_ID_FOR_PAGE_VIEW, pageViewStateId);

        // Propagate the queued data to the new page view data
        Map pageViewData = pageViewStateAdmin.getPageViewData();
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        if (rsm.has(RequestStorageIds.QUEUED_DATA_FOR_PAGE_VIEW_CACHE)) {
            Map queue = (Map) rsm.get(RequestStorageIds.QUEUED_DATA_FOR_PAGE_VIEW_CACHE);
            pageViewData.putAll(queue);
        }

        l.exiting(getClass().getName(), "replaceWithNewPageView", pageViewStateId);
    }

    public void setupPageViewState(HttpServletRequest request) {
      Logger l = LogUtils.enterLog(getClass(), "setupPageViewState", new Object[]{request});

      String pageViewStateId = "";
      if (request.getAttribute(RequestIds.CACHE_ID_FOR_PAGE_VIEW) != null) {
        pageViewStateId = (String) request.getAttribute(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
      } else {
          if (request.getParameter(RequestIds.CACHE_ID_FOR_PAGE_VIEW) != null) {
              pageViewStateId = (String) request.getParameter(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
          } else {
              if (RequestStorageManager.getInstance().has(RequestIds.CACHE_ID_FOR_PAGE_VIEW)) {
                  pageViewStateId = (String) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
              }
          }
      }
      if (StringUtils.isBlank(pageViewStateId)) {
          pageViewStateId = ((PageViewStateAdmin) UserSessionManager.getInstance().getUserSession()).getNewPageViewId();
          if (l.isLoggable(Level.FINE)) {
              l.logp(Level.FINE, getClass().getName(), "setupPageViewState", "New page view state id [" + pageViewStateId + "] generated successfully for url - " + request.getRequestURI() + "&" + request.getPathInfo());
          }
      } else {
          if (l.isLoggable(Level.FINE)) {
              l.logp(Level.FINE, getClass().getName(), "setupPageViewState", "Page view state id [" + pageViewStateId + "] obtained from request for url - " + request.getRequestURI() + "&" + request.getPathInfo());
          }
      }

      RequestStorageManager.getInstance().set(RequestIds.CACHE_ID_FOR_PAGE_VIEW, pageViewStateId);
      request.setAttribute(RequestIds.CACHE_ID_FOR_PAGE_VIEW, pageViewStateId);

      l.exiting(getClass().getName(), "setupPageViewState", pageViewStateId);
      return;
  }

  public void logPageViewState(PageViewStateAdmin PageViewStateAdmin, String pageViewStateId) {
      Logger l = LogUtils.enterLog(getClass(), "logPageViewState", new Object[]{PageViewStateAdmin, pageViewStateId});

      if (l.isLoggable(Level.FINEST)) {
          try {
              Map pageViewCachedData = PageViewStateAdmin.getPageViewData(pageViewStateId);
              l.logp(Level.FINEST, getClass().getName(), "logPageViewState", "*** [START] From Cache for Page View Id:" + pageViewStateId);
              for (Object cachedDataKey : pageViewCachedData.keySet()) {
                  if (((String) cachedDataKey).equalsIgnoreCase(dti.oasis.http.RequestIds.NON_GRID_FIELDS_RECORD)) {
                      Record FieldRecord = (Record) pageViewCachedData.get(dti.oasis.http.RequestIds.NON_GRID_FIELDS_RECORD) ;
                      l.logp(Level.FINEST, getClass().getName(), "logPageViewState", "*** Cached Non Grid Fields for Page View Id:" + pageViewStateId);
                      l.logp(Level.FINEST, getClass().getName(), "logPageViewState", FieldRecord.toString());
                  } else if (((String) cachedDataKey).equalsIgnoreCase(dti.oasis.http.RequestIds.HEADER_FIELDS_RECORD)) {
                      Record FieldRecord = (Record) pageViewCachedData.get(dti.oasis.http.RequestIds.HEADER_FIELDS_RECORD) ;
                      l.logp(Level.FINEST, getClass().getName(), "logPageViewState", "*** Cached Header Fields for Page View Id:" + pageViewStateId);
                      l.logp(Level.FINEST, getClass().getName(), "logPageViewState", FieldRecord.toString());
                  } else if (((String) cachedDataKey).equalsIgnoreCase(dti.oasis.http.RequestIds.RECORDSET_MAP)) {
                      l.logp(Level.FINEST, getClass().getName(), "logPageViewState", "*** Cached Grid Fields for Page View Id:" + pageViewStateId);
                      HashMap<String, RecordSet> recordSetMap = (HashMap) pageViewCachedData.get(dti.oasis.http.RequestIds.RECORDSET_MAP) ;
                      for (String gridId : recordSetMap.keySet()) {
                          l.logp(Level.FINEST, getClass().getName(), "logPageViewState", "*** Cached Grid Fact Data List (original field related data only) for Grid Id:" + gridId);
                          RecordSet rs = recordSetMap.get(gridId);
                          for (Record r : rs.getRecordList()) {
                              l.logp(Level.FINEST, getClass().getName(), "logPageViewState", r.toString());
                          }
                      }
                  } else {
                      l.logp(Level.FINEST, getClass().getName(), "logPageViewState", pageViewCachedData.get((String) cachedDataKey).toString());
                  }
              }
          } catch (Exception e) {
              l.logp(Level.WARNING, getClass().getName(), "logPageViewState", "Unable to log page view state content for Page View Id:" + pageViewStateId, e);
          }
          l.logp(Level.FINEST, getClass().getName(), "logPageViewState", "*** [END] From Cache for Page View Id:" + pageViewStateId);
      }
      l.exiting(getClass().getName(), "logPageViewState");
      return;
  }

  public boolean isPageViewStateAlreadyScheduledForCleanup (String pageViewStateId) {
      Logger l = LogUtils.enterLog(getClass(), "isPageViewStateAlreadyScheduledForCleanup", new Object[]{pageViewStateId});
      boolean isPageViewStateAlreadyScheduled = false; 
      isPageViewStateAlreadyScheduled = m_pageViewStateToCleanup.containsKey(pageViewStateId);
      l.exiting(getClass().getName(), "isPageViewStateAlreadyScheduledForCleanup", Boolean.valueOf(isPageViewStateAlreadyScheduled));
      return isPageViewStateAlreadyScheduled; 
  }

  //-------------------------------------------------
  // Configuration constructor and accessor methods
  //-------------------------------------------------
  public PageViewStateManagerImpl() {

  }

  private static final String KEY_FOR_APPLICATION_ID = "applicationId";
  private SortedMap m_pageViewStateToCleanup = Collections.synchronizedSortedMap(new TreeMap());
  private SortedMap m_cleanupTimer = Collections.synchronizedSortedMap(new TreeMap());
  private static final String PAGE_VIEW_STATE_CACHE_TIMEOUT = "pageViewState.scheduled.timeout";
}
