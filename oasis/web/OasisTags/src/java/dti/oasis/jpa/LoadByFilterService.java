package dti.oasis.jpa;

import java.util.HashMap;
import java.util.List;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/29/13
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/24/2015       kshen       168409. Enhance Services to restrict usage to a single Entity type.
 * ---------------------------------------------------
 */
public interface LoadByFilterService<T> {

    /**
     * Count the number of entities based on filter.
     *
     * @param filterEntity
     * @return
     */
    public long countAllByFilter(T filterEntity);

    /**
     * Count the number of entities based on filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param loadByFilterProcessor
     * @return
     */
    public long countAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor);

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity);

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, List<HashMap> orderList);

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, LoadByFilterProcessor loadByFilterProcessor);

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List loadOptionsByFilter(String[] resultColumns, T filterEntity, List<HashMap> orderList, LoadByFilterProcessor loadByFilterProcessor);

    /**
     * Load all entities based on a filter.
     *
     * @param filterEntity
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity);

    /**
     * Load all entities based on a filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param orderList
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity, List<HashMap> orderList);

    /**
     * Load all entities based on a filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param loadByFilterProcessor
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor);

    /**
     * Load all entities based on a filter, applying the loadByFilterProcessor during the process.
     *
     * @param filterEntity
     * @param loadByFilterProcessor
     * @return
     */
    public List<T> loadAllByFilter(T filterEntity, LoadByFilterProcessor loadByFilterProcessor, List<HashMap> orderList);

}
