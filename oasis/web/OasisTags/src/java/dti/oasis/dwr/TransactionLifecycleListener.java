package dti.oasis.dwr;

import dti.oasis.data.DBTransactionInterceptor;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.TransactionStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: mproekt
 * Date: Apr 7, 2009
 * Time: 1:55:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionLifecycleListener extends org.springframework.transaction.interceptor.TransactionInterceptor {

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
         Logger l = LogUtils.enterLog(getClass(),"invoke");

        Object result =null;

        String method=methodInvocation.getMethod().getName()+" class:"+methodInvocation.getMethod().getDeclaringClass().getName();
        l.fine("Method intercepted:"+method);

        DwrProcessor processor=DwrProcessor.getInstance();
        //Check if method name matches business event
        //Method name should begin with business event name that is similar to Spring config for interceptor
        //The reason not to use Spring config is to have declarative method interceptor as oppose to static config.
        //This is debatable issue and should be revised in the future implementation.
            //Activate after legacy code re-factoring
            // Currently not all the "managers" are managed as spring beans and therefore not all the
            // methods will be intercepted. Commented off for now
/*         if (!processor.isMethodMatches(method)){

            result = methodInvocation.proceed();
            return result;
        }*/

//        TransactionInfo tnxInfo = createTransactionIfNecessary(
//        methodInvocation.getMethod(), methodInvocation.getClass() );
        TransactionInfo tnxInfo = null;
         Connection conn=null;
        try{
            ValidationException busMethodException = null;
            ValidationException dwrException = null;
            //Proceed to the next interceptor.
            try{
            result = methodInvocation.proceed();
            }catch (ValidationException vx){
                busMethodException = vx;
                //do nothing for now
            }
            //Create connection to be used for transaction lifetime
            //StoredProcedure DAO will create connection
            //Spring will wrap Datasource by TransactionAwareDataSource
            conn= StoredProcedureDAO.getInstance("WB_CS_RULE.get_rules").getAppConnection();
           // Process rule as after advice
            processor.setConnection(conn);
            //Process Data Window Rules if required
            //processor will throw exception if there is an validation error.
            //In this case the process will block exection of the business method
            l.fine("TransactionLifecycleListener.DWR start process");
            try {
                processor.process();
            } catch (ValidationException ex) {
                dwrException = ex;
            }
            l.fine("TransactionLifecycleListener.DWR Complete. Commiting changes ");

            //reconsile exception. Leave business exception if exisits
             if(busMethodException != null){
                 dwrException = null;
                 throw busMethodException;
             }
             if(dwrException != null){
                throw dwrException; 
             }

            if(tnxInfo != null && tnxInfo.hasTransaction() ){
                l.fine("TransactionLifecycleListener.DWR Trying to commit");
                commitTransactionAfterReturning(tnxInfo);
                l.fine("TransactionLifecycleListener.DWR Transaction committed");
            }

        }catch(ValidationException ve){
            l.fine("TransactionLifecycleListener. GotValidation exception. Rolling back.");
             completeTransactionAfterThrowing(tnxInfo, ve);
            System.out.println("tr list "+ve.getMessage());
            if(ve.getMessage().indexOf(DwrConstants.DWR_VALIDATION_EXCEPTION_MSG)<0){
               throw ve;
            } else {
                throw new AppException(ve.getMessage(), ve);
            }

        } catch (Exception e) {
            //Transaction must be completed even an exception encountered
            l.throwing(getClass().getName(), "invoke", e);
            completeTransactionAfterThrowing(tnxInfo, e);
            throw e;
        }
        finally{
            cleanupTransactionInfo(tnxInfo);
               l.fine("TransactionLifecycleListener. Finally Closing connection for DWR");
                DatabaseUtils.close(conn);
        }

        return result;
     }

}
