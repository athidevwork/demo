package dti.oasis.util;

/**
 * The LoadProcessor Interface does not have any defined methods.
 * It is merely a Marker Interface, used to designate Interfaces and Classes that adhere to this pattern.
 * It describes the pattern of exposing two methods that will be invoked during the process of loading objects.
 * The first method typically follows the pattern:
 * postProcess${Object}( obj:${Object} ) : boolean
 * This method is invoked while loading the particular objects.
 * It is called once per object, after the object has been fully initialized.
 * This is the point when a concrete implementation can analyze the given object, modify its contents, etc.
 * The method returns a boolean result, indicating if the object should be included in the resulting collection.
 * <p/>
 * The second method typically follows the pattern:
 * postProcess${Object.Collection}( coll:${Object.Collection} ) : void
 * This method is invoked once, after the entire collection of objects has been loaded.
 * This is the point when a concrete implementation can mark completion of the load process, modify the collection,
 * report debugging information, etc.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface LoadProcessor {
}