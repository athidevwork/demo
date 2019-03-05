package dti.pm.busobjs;

import dti.oasis.busobjs.EnumType;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumerated type that represents POLICY, COVERAGE or TAIL type of the owner of Component.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 10, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ComponentOwner extends EnumType {

    private static int c_nextIntValue = 1;
    private static int getNextIntValue() { return c_nextIntValue++; }
    private static Map c_validTypes = new HashMap();

    public final static int POLICY_VALUE = getNextIntValue();
    public final static ComponentOwner POLICY = new ComponentOwner(POLICY_VALUE, "POLICY", "POLICY");

    public final static int COVERAGE_VALUE = getNextIntValue();
    public final static ComponentOwner COVERAGE = new ComponentOwner(COVERAGE_VALUE, "COVERAGE", "COVERAGE");

    public final static int TAIL_VALUE = getNextIntValue();
    public final static ComponentOwner TAIL = new ComponentOwner(TAIL_VALUE, "TAIL", "COVERAGE");

    public final static int PRIOR_ACT_VALUE = getNextIntValue();
    public final static ComponentOwner PRIOR_ACT = new ComponentOwner(PRIOR_ACT_VALUE, "PRIOR_ACT", "COVERAGE");

    public static ComponentOwner getInstance(String ownerName) {
        ComponentOwner result = (ComponentOwner) c_validTypes.get(ownerName.toUpperCase());
        if (result == null) {
            throw new IllegalArgumentException("The ownerName '" + ownerName + "' is not a valid ComponentOwner.");
        }
        return result;
    }

    public boolean isPolicyOwner() {
        return intValue() == POLICY_VALUE;
    }

    public boolean isCoverageOwner() {
        return intValue() == COVERAGE_VALUE;
    }

    public boolean isTailOwner() {
        return intValue() == TAIL_VALUE;
    }

    public boolean isPriorActOwner() {
        return intValue() == PRIOR_ACT_VALUE;
    }

    public String getOwnerName() {
        return getName();
    }

    public String getOwnerType() {
        return m_ownerType;
    }

    private ComponentOwner(int value, String ownerName, String ownerType) {
        super(value, ownerName);
        m_ownerType = ownerType;
        c_validTypes.put(ownerName, this);
    }

    public ComponentOwner() {
    }

    private String m_ownerType;
}
