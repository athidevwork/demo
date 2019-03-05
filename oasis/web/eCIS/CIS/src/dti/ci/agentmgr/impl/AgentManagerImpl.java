package dti.ci.agentmgr.impl;

import dti.ci.agentmgr.AgentFields;
import dti.ci.agentmgr.AgentManager;
import dti.ci.agentmgr.dao.AgentDAO;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.validationmgr.impl.DateRecordValidator;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of AgentManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 26, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Mar 21, 2008     James       Issue#75265 CreatedAdd Agent Tab to eCIS.
 *                              Same functionality, look and feel
 * 04/09/2008       fcb         validateAllPolicyAgent: isValidSubproducerOnSave added.
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * Apr 18, 2008     James       Issue#81846 Client -> Agent page: Set the Appointment
 *                              End with a valid date and left the Appointment Start
 *                              date empty. Save. No message returned and the Appointment
 *                              End date is saved. It is incorrect
 * Apr 18, 2008     James       Issue#81847 CIS -> Agent Page ->Agent Contract Commission
 *                              part: Set the NB (RN, ERE) Comm Basis to be persent, set
 *                              the NB (RN, ERE) to be less than 0 and save. No message
 *                              returned and changes will be saved
 * Apr 21, 2008     James       Issue#81843 In CIS -> Agent page: Set Agent Start Date
 *                              to "00/00/0000" and save. No message returned  and the
 *                              change will be saved
 * Apr 28, 2008     James       Issue#81844 CIS -. Agent Page: System return the incorrect
 *                              message when set Agent Pay Commision Start date to be
 *                              before the Agent start date
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 03/04/2010       kshen       Added method loadAllSubProducer.
 * 03/05/2010       kshen       Fix the bug that save
 * 10/20/2011       clm         issue 122671, Change validateAgentOutputOptionForSave method
 * 07/19/2016       iwang       Issue 177546 - 1) Added loadAllAgentStaff, loadAllAgentStaffOverride.
 *                                             2) Modified validateAllAgent, saveAllAgent.
 * 08/26/2016       iwang       Issue 167601 - Modified validateAllAgent to add date validation.
 * 09/27/2016       htwang      Issue 178227 - 1) Modified validateAllAgent to check if the agent name is valid.
 *                                             2) Fixed goto error on Agent Staff grid.
 * 10/24/2017       htwang      Issue 188776 - add message field name that will be used in
 *                              MessageManager.getInstance().addErrorMessage() later.
 * ---------------------------------------------------
 */
public class AgentManagerImpl implements AgentManager {

    /**
     * load agent information
     *
     * @param inputRecord
     * @return
     */
    public Record loadAllAgent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgent", new Object[]{inputRecord});
        }
        Record outRecord = getAgentDAO().loadAllAgent(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgent", outRecord);
        }
        return outRecord;
    }

    /**
     * load agent pay commission list
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentPayCommission(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentPayCommission", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getAgentDAO().loadAllAgentPayCommission(inputRecord, new AgentGridRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentPayCommission", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load agent contract list
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentContract(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentContract", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getAgentDAO().loadAllAgentContract(inputRecord, new ContractGridRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentContract", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load agent staff list
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentStaff(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentStaff", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getAgentDAO().loadAllAgentStaff(inputRecord, new AgentGridRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentStaff", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load Agent Staff Override List
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentStaffOverride(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentStaffOverride", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getAgentDAO().loadAllAgentStaffOverride(inputRecord, new AgentGridRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentStaffOverride", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load agent contract commission
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentContractCommission(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentContractCommission", new Object[]{inputRecord});
        }
        RecordSet outRecordSet = getAgentDAO().loadAllAgentContractCommission(inputRecord, new AgentGridRecordLoadProcessor());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentContractCommission", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * method to validation all records within the recordset. althought per UC requirements
     * only one row is inserted, with potentially many updated rows
     * (to specialConditionCode and agentNote fields)
     *
     * @param inputRecord
     * @param payCommissionRecords
     * @param contractRecords
     * @param contractCommissionRecords
     */
    protected void validateAllAgent(Record inputRecord, RecordSet payCommissionRecords,
                                    RecordSet contractRecords, RecordSet contractCommissionRecords,
                                    RecordSet agentStaffRecords, RecordSet agentOverrideRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validateAllAgent",
                new Object[]{inputRecord, payCommissionRecords, contractRecords, contractCommissionRecords});

        MessageManager messageManager = MessageManager.getInstance();

        Date effectiveStartDate = null;
        Date effectiveEndDate = null;

        if (!inputRecord.hasStringValue(AgentFields.SITUATION_CODE)) {
            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.situationCode",
                    new String[0], AgentFields.SITUATION_CODE);
        }

        //validate date value
        DateRecordValidator dateRecordValidator = new DateRecordValidator(new String[]{
                AgentFields.EFFECTIVE_START_DATE, AgentFields.EFFECTIVE_END_DATE},
                "ci.agentmgr.maintainAgent.invalidDate");
        dateRecordValidator.validate(inputRecord);
        //validate date value in pay commission list
        dateRecordValidator = new DateRecordValidator(new String[]{
                AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE, AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE},
                "ci.agentmgr.maintainAgent.paycommission.invalidDate",
                AgentFields.PAY_COMMISSION_ID);
        Iterator iterator = payCommissionRecords.getRecords();
        while (iterator.hasNext()) {
            dateRecordValidator.validate((Record) iterator.next());
        }
        //validate date value in contract list
        dateRecordValidator = new DateRecordValidator(new String[]{
                AgentFields.CONTRACT_EFFECTIVE_START_DATE, AgentFields.CONTRACT_EFFECTIVE_END_DATE,
                AgentFields.CONTRACT_APPOINTMENT_START_DATE, AgentFields.CONTRACT_APPOINTMENT_END_DATE,
                AgentFields.CONTRACT_ADDL_LICENSE_START_DATE, AgentFields.CONTRACT_ADDL_LICENSE_END_DATE,
                AgentFields.CONTRACT_NIPN_START_DATE, AgentFields.CONTRACT_NIPN_END_DATE},
                "ci.agentmgr.maintainAgent.agentcontract.invalidDate",
                AgentFields.CONTRACT_ID);
        iterator = contractRecords.getRecords();
        while (iterator.hasNext()) {
            dateRecordValidator.validate((Record) iterator.next());
        }
        //validate date value in contract commission
        ContractCommissionDateRecordSetValidator validator = new ContractCommissionDateRecordSetValidator(new String[]{
                AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE, AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE},
                "ci.agentmgr.maintainAgent.contractcommission.invalidDate",
                AgentFields.CONTRACT_COMMISSION_ID);
        validator.setParentResultSet(contractRecords);
        iterator = contractCommissionRecords.getRecords();
        while (iterator.hasNext()) {
            validator.validate((Record) iterator.next());
        }

        if (agentStaffRecords != null && agentStaffRecords.getSize() > 0) {
            //validate date value in agent staff
            dateRecordValidator = new DateRecordValidator(new String[]{
                    AgentFields.STAFF_EFF_START_DATE, AgentFields.STAFF_EFF_END_DATE},
                    "ci.agentmgr.maintainAgent.agentStaff.invalidDate");
            iterator = agentStaffRecords.getRecords();
            while (iterator.hasNext()) {
                dateRecordValidator.validate((Record) iterator.next());
            }

            //validate date value in agent staff override
            AgentStaffOverrideDateRecordSetValidator overrideValidator = new AgentStaffOverrideDateRecordSetValidator(
                    new String[]{AgentFields.OVERRIDE_EFF_START_DATE, AgentFields.OVERRIDE_EFF_END_DATE},
                    "ci.agentmgr.maintainAgent.agentStaffOverride.invalidDate",
                    AgentFields.AGENT_STAFF_OVERRIDE_ID);
            overrideValidator.setParentResultSet(agentStaffRecords);
            iterator = agentOverrideRecords.getRecords();
            while (iterator.hasNext()) {
                overrideValidator.validate((Record) iterator.next());
            }
        }

        if (!messageManager.hasErrorMessages()) {
            if (!inputRecord.hasStringValue(AgentFields.EFFECTIVE_START_DATE)) {
                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.emptyStartDate",
                        new String[0], AgentFields.EFFECTIVE_START_DATE);
            } else {
                effectiveStartDate = AgentFields.getEffectiveStartDate(inputRecord);
                effectiveEndDate = AgentFields.getEffectiveEndDate(inputRecord);
                if (effectiveStartDate.after(effectiveEndDate)) {
                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.startDateAfterEndDate",
                            new String[0], AgentFields.EFFECTIVE_START_DATE);
                } else {
                    //validate pay commission grid
                    iterator = payCommissionRecords.getRecords();
                    while (iterator.hasNext()) {
                        Date payCommissionEffectiveStartDate = null;
                        Date payCommissionEffectiveEndDate = null;
                        Record record = (Record) iterator.next();
                        String rowId = AgentFields.getAgentPayCommissionId(record);
                        if (!record.hasStringValue(AgentFields.PAY_COMMISSION_PAY_CODE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.paycommission.commissionPayCode",
                                    new String[0], AgentFields.PAY_COMMISSION_PAY_CODE, rowId);
                        }
                        if (!record.hasStringValue(AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.paycommission.emptyStartData",
                                    new String[0], AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE, rowId);
                        } else {
                            payCommissionEffectiveStartDate = AgentFields.getPayCommissionEffectiveStartDate(record);
                            payCommissionEffectiveEndDate = AgentFields.getPayCommissionEffectiveEndDate(record);
                            if (payCommissionEffectiveStartDate.after(payCommissionEffectiveEndDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.paycommission.startDateAfterEndDate",
                                        new String[0], AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE, rowId);
                            }
                            if (payCommissionEffectiveStartDate.before(effectiveStartDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.paycommission.startDateBeforeAgentStartDate",
                                        new String[0], AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE, rowId);
                            }
                        }
                    }
                    //validate contract grid
                    iterator = contractRecords.getRecords();
                    while (iterator.hasNext()) {
                        Date contractEffectiveStartDate = null;
                        Date contractEffectiveEndDate = null;
                        Record record = (Record) iterator.next();
                        String rowId = AgentFields.getAgentLicenseId(record);
                        if (!record.hasStringValue(AgentFields.CONTRACT_CLASS_CODE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.licenseClassCode",
                                    new String[0], AgentFields.CONTRACT_CLASS_CODE, rowId);
                        }
                        if (!record.hasStringValue(AgentFields.CONTRACT_EFFECTIVE_START_DATE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.emptyStartData",
                                    new String[0], AgentFields.CONTRACT_EFFECTIVE_START_DATE, rowId);
                        } else {
                            contractEffectiveStartDate = AgentFields.getContractEffectiveStartDate(record);
                            contractEffectiveEndDate = AgentFields.getContractEffectiveEndDate(record);
                            if (contractEffectiveStartDate.after(contractEffectiveEndDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.startDateAfterEndDate",
                                        new String[0], AgentFields.CONTRACT_EFFECTIVE_START_DATE, rowId);
                            }
                            if (contractEffectiveStartDate.before(effectiveStartDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.startDateBeforeAgentStartDate",
                                        new String[0], AgentFields.CONTRACT_EFFECTIVE_START_DATE, rowId);
                            }
                            if (record.hasStringValue(AgentFields.CONTRACT_APPOINTMENT_START_DATE)) {
                                Date appointmentStartDate = AgentFields.getAppointmentStartDate(record);
                                Date appointmentEndDate = AgentFields.getAppointmentEndDate(record);
                                if (appointmentStartDate.after(appointmentEndDate)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.ApptStartDateAfterApptEndDate",
                                            new String[0], AgentFields.CONTRACT_APPOINTMENT_START_DATE, rowId);
                                }
                                if (appointmentStartDate.before(effectiveStartDate)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.ApptStartDateBeforeAgentStartDate",
                                            new String[0], AgentFields.CONTRACT_APPOINTMENT_START_DATE, rowId);
                                }
                            } else {
                                if (record.hasStringValue(AgentFields.CONTRACT_APPOINTMENT_END_DATE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.hasApptStartNoApptEnd",
                                            new String[0], AgentFields.CONTRACT_APPOINTMENT_START_DATE, rowId);
                                }
                            }
                            if (record.hasStringValue(AgentFields.CONTRACT_CLASS_CODE)) {
                                String licenseClassCode = AgentFields.getLicenseClassCode(record);
                                if (AgentManagerImpl.LICENSE_CLASS_CODE_PRODUCER.equals(licenseClassCode)
                                        && !record.hasStringValue(AgentFields.CONTRACT_NUMBER)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.producerAndEmptyNumber",
                                            new String[0], AgentFields.CONTRACT_NUMBER, rowId);
                                }
                            }
                            if (!record.hasStringValue(AgentFields.CONTRACT_TYPE)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.emptyType",
                                        new String[0], AgentFields.CONTRACT_TYPE, rowId);
                            }
                            if (!record.hasStringValue(AgentFields.CONTRACT_STATE_CODE)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.emptyState",
                                        new String[0], AgentFields.CONTRACT_STATE_CODE, rowId);
                            }
                            if (record.hasStringValue(AgentFields.CONTRACT_ADDL_LICENSE_START_DATE)
                                    && record.hasStringValue(AgentFields.CONTRACT_ADDL_LICENSE_END_DATE)) {
                                Date addlLicenseStartDate = AgentFields.getContractAddlLicenseStartDate(record);
                                Date addlLicenseEndDate = AgentFields.getContractAddlLicenseEndDate(record);
                                if (addlLicenseStartDate.after(addlLicenseEndDate)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.AddlStartDateAfterAddlEndDate",
                                            new String[0], AgentFields.CONTRACT_ADDL_LICENSE_START_DATE, rowId);
                                }
                            }
                            if (record.hasStringValue(AgentFields.CONTRACT_NIPN_START_DATE)
                                    && record.hasStringValue(AgentFields.CONTRACT_NIPN_END_DATE)) {
                                Date nipnStartDate = AgentFields.getContractNipnStartDate(record);
                                Date nipnEndDate = AgentFields.getContractNipnEndDate(record);
                                if (nipnStartDate.after(nipnEndDate)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentcontract.NIPNStartDateAfterNIPNEndDate",
                                            new String[0], AgentFields.CONTRACT_NIPN_START_DATE, rowId);
                                }
                            }
                        }
                    }

                    //validate contract commission grid
                    SysParmProvider sysParm = SysParmProvider.getInstance();
                    double maxNewbusRateDouble = Double.parseDouble(sysParm.getSysParm(NEW_BUS_RATE_MAX,
                            NEW_BUS_RATE_MAX_DEFAULT));
                    double maxRenwalRateDouble = Double.parseDouble(sysParm.getSysParm(RENEWAL_RATE_MAX,
                            RENEWAL_RATE_MAX_DEFAULT));
                    double maxEreRateDouble = Double.parseDouble(sysParm.getSysParm(ERE_RATE_MAX,
                            ERE_RATE_MAX_DEFAULT));

                    iterator = contractCommissionRecords.getRecords();
                    while (iterator.hasNext()) {
                        Date contractCommissionEffectiveStartDate = null;
                        Date contractCommissionEffectiveEndDate = null;
                        Record record = (Record) iterator.next();
                        String rowId = AgentFields.getAgentLicenseCommissionId(record);
                        String agentLicenseId = AgentFields.getAgentLicenseId(record);
                        //get contract rowid
                        for (int i = 0; i < contractRecords.getSize(); i++) {
                            Record contractRecord = contractRecords.getRecord(i);
                            String contractRowId = AgentFields.getAgentLicenseId(contractRecord);
                            if (contractRowId.equals(agentLicenseId)) {
                                rowId = contractRowId + "," + rowId;
                                break;
                            }
                        }

                        if (!record.hasStringValue(AgentFields.CONTRACT_COMMISSION_POLICY_TYPE_CODE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.policyTypeCode",
                                    new String[0], AgentFields.CONTRACT_COMMISSION_POLICY_TYPE_CODE, rowId);
                        }

                        if ((inputRecord.getFieldValue(AgentFields.IS_PRIMARY_RISK_TYPE_CODE_VISIBLE).equals("Y")) &&
                            (!record.hasStringValue(AgentFields.CONTRACT_COMMISSION_PRIMARY_RISK_TYPE_CODE))) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.riskTypeCode",
                                    new String[0], AgentFields.CONTRACT_COMMISSION_PRIMARY_RISK_TYPE_CODE, rowId);

                        }
                        
                        if (!record.hasStringValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.emptyStartDate",
                                    new String[0], AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE, rowId);
                        } else {
                            contractCommissionEffectiveStartDate = AgentFields.getContractCommissionStartDate(record);
                            contractCommissionEffectiveEndDate = AgentFields.getContractCommissionEndDate(record);
                            if (contractCommissionEffectiveStartDate.after(contractCommissionEffectiveEndDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.startDateAfterEndDate",
                                        new String[0], AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE, rowId);
                            }
                            if (contractCommissionEffectiveStartDate.before(effectiveStartDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.startDateBeforeAgentEndDate",
                                        new String[0], AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE, rowId);
                            }
                        }

                        //new business
                        String newbusCommBasis = null;
                        if (record.hasStringValue(AgentFields.NB_COMM_BASIS)) {
                            newbusCommBasis = AgentFields.getNewbusCommBasis(record);
                            if (AgentManagerImpl.COMM_BASIS_PERCENT.equals(newbusCommBasis)) {
                                double newbusCommRateDouble = -1;
                                if (record.hasStringValue(AgentFields.NB_COMM_RATE)) {
                                    String newbusCommRate = AgentFields.getNewbusRate(record);
                                    newbusCommRateDouble = Double.parseDouble(newbusCommRate);
                                    if (newbusCommRateDouble < 0 || newbusCommRateDouble > maxNewbusRateDouble) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.invalidCommRate",
                                                new String[]{AgentFields.NEW_BUSINESS, Double.toString(maxNewbusRateDouble)},
                                                AgentFields.NB_COMM_RATE, rowId);
                                    }
                                } else {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.emptyCommRate",
                                            new String[]{AgentFields.NEW_BUSINESS},
                                            AgentFields.NB_COMM_RATE, rowId);
                                }
                                if (record.hasStringValue(AgentFields.NB_COMM_LIMIT)) {
                                    double newbusCommLimitDouble = -1;
                                    String newbusCommLimit = AgentFields.getNewbusCommLimit(record);
                                    newbusCommLimitDouble = Double.parseDouble(newbusCommLimit);
                                    if (newbusCommLimitDouble < 0) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.invalidCommLimit",
                                                new String[]{AgentFields.NEW_BUSINESS},
                                                AgentFields.NB_COMM_LIMIT, rowId);
                                    }
                                }
                            } else if (AgentManagerImpl.COMM_BASIS_FLAT.equals(newbusCommBasis)) {
                                if (!record.hasStringValue(AgentFields.NB_COMM_FLAT_AMOUNT)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.flatCommBasis.emptyFlatAmount",
                                            new String[]{AgentFields.NEW_BUSINESS},
                                            AgentFields.NB_COMM_FLAT_AMOUNT, rowId);
                                } else {
                                    double newbusCommFlatAmountDouble = -1;
                                    String newbusCommFlatAmount = AgentFields.getNewbusFlatAmt(record);
                                    newbusCommFlatAmountDouble = Double.parseDouble(newbusCommFlatAmount);
                                    if (newbusCommFlatAmountDouble < 0) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.flatCommBasis.invalidFlatAmount",
                                                new String[]{AgentFields.NEW_BUSINESS},
                                                AgentFields.NB_COMM_FLAT_AMOUNT, rowId);
                                    }
                                }
                            } else if (AgentManagerImpl.COMM_BASIS_SCHEDULE.equals(newbusCommBasis)) {
                                if (!record.hasStringValue(AgentFields.NB_COMM_RATE_SCHEDULE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.schedCommBasis.emptySchedule",
                                            new String[]{AgentFields.NEW_BUSINESS},
                                            AgentFields.NB_COMM_RATE_SCHEDULE, rowId);
                                }
                            }
                        } else {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.commBasis.required",
                                    new String[]{AgentFields.NEW_BUSINESS}, AgentFields.NB_COMM_BASIS, rowId);
                        }

                        //renewal
                        String renewalCommBasis = null;
                        if (record.hasStringValue(AgentFields.RN_COMM_BASIS)) {
                            renewalCommBasis = AgentFields.getRenewalCommBasis(record);
                            if (AgentManagerImpl.COMM_BASIS_PERCENT.equals(renewalCommBasis)) {
                                double renewalCommRateDouble = -1;
                                if (record.hasStringValue(AgentFields.RN_COMM_RATE)) {
                                    String renewalCommRate = AgentFields.getRenewalRate(record);
                                    renewalCommRateDouble = Double.parseDouble(renewalCommRate);
                                    if (renewalCommRateDouble < 0 || renewalCommRateDouble > maxRenwalRateDouble) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.invalidCommRate",
                                                new String[]{AgentFields.RENEWWAL, Double.toString(maxRenwalRateDouble)},
                                                AgentFields.RN_COMM_RATE, rowId);
                                    }
                                } else {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.emptyCommRate",
                                            new String[]{AgentFields.RENEWWAL},
                                            AgentFields.RN_COMM_RATE, rowId);
                                }
                                if (record.hasStringValue(AgentFields.RN_COMM_LIMIT)) {
                                    double renewalCommLimitDouble = -1;
                                    String renewalCommLimit = AgentFields.getRenewalCommLimit(record);
                                    renewalCommLimitDouble = Double.parseDouble(renewalCommLimit);
                                    if (renewalCommLimitDouble < 0) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.invalidCommLimit",
                                                new String[]{AgentFields.RENEWWAL},
                                                AgentFields.RN_COMM_LIMIT, rowId);
                                    }
                                }
                            } else if (AgentManagerImpl.COMM_BASIS_FLAT.equals(renewalCommBasis)) {
                                if (!record.hasStringValue(AgentFields.RN_COMM_FLAT_AMOUNT)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.flatCommBasis.emptyFlatAmount",
                                            new String[]{AgentFields.RENEWWAL},
                                            AgentFields.RN_COMM_FLAT_AMOUNT, rowId);
                                } else {
                                    double renewalCommFlatAmountDouble = -1;
                                    String renewalCommFlatAmount = AgentFields.getRenewalFlatAmt(record);
                                    renewalCommFlatAmountDouble = Double.parseDouble(renewalCommFlatAmount);
                                    if (renewalCommFlatAmountDouble < 0) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.flatCommBasis.invalidFlatAmount",
                                                new String[]{AgentFields.RENEWWAL},
                                                AgentFields.RN_COMM_FLAT_AMOUNT, rowId);
                                    }
                                }
                            } else if (AgentManagerImpl.COMM_BASIS_SCHEDULE.equals(renewalCommBasis)) {
                                if (!record.hasStringValue(AgentFields.RN_COMM_RATE_SCHEDULE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.schedCommBasis.emptySchedule",
                                            new String[]{AgentFields.RENEWWAL},
                                            AgentFields.RN_COMM_RATE_SCHEDULE, rowId);
                                }
                            }
                        } else {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.commBasis.required",
                                    new String[]{AgentFields.RN_COMM_RATE_SCHEDULE}, AgentFields.RN_COMM_BASIS, rowId);
                        }

                        //ERE
                        String ereCommBasis = null;
                        if (record.hasStringValue(AgentFields.ERE_COMM_BASIS)) {
                            ereCommBasis = AgentFields.getEreCommBasis(record);
                            if (AgentManagerImpl.COMM_BASIS_PERCENT.equals(ereCommBasis)) {
                                double ereCommRateDouble = -1;
                                if (record.hasStringValue(AgentFields.ERE_COMM_RATE)) {
                                    String ereCommRate = AgentFields.getEreRate(record);
                                    ereCommRateDouble = Double.parseDouble(ereCommRate);
                                    if (ereCommRateDouble < 0 || ereCommRateDouble > maxEreRateDouble) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.invalidCommRate",
                                                new String[]{AgentFields.ERE, Double.toString(maxEreRateDouble)},
                                                AgentFields.ERE_COMM_RATE, rowId);
                                    }
                                } else {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.emptyCommRate",
                                            new String[]{AgentFields.ERE},
                                            AgentFields.ERE_COMM_RATE, rowId);
                                }
                                if (record.hasStringValue(AgentFields.ERE_COMM_LIMIT)) {
                                    double ereCommLimitDouble = -1;
                                    String ereCommLimit = AgentFields.getEreCommLimit(record);
                                    ereCommLimitDouble = Double.parseDouble(ereCommLimit);
                                    if (ereCommLimitDouble < 0) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.percentCommBasis.invalidCommLimit",
                                                new String[]{AgentFields.ERE},
                                                AgentFields.ERE_COMM_LIMIT, rowId);
                                    }
                                }
                            } else if (AgentManagerImpl.COMM_BASIS_FLAT.equals(ereCommBasis)) {
                                if (!record.hasStringValue(AgentFields.ERE_COMM_FLAT_AMOUNT)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.flatCommBasis.emptyFlatAmount",
                                            new String[]{AgentFields.ERE},
                                            AgentFields.ERE_COMM_FLAT_AMOUNT, rowId);
                                } else {
                                    double ereCommFlatAmountDouble = -1;
                                    String ereCommFlatAmount = AgentFields.getEreFlatAmt(record);
                                    ereCommFlatAmountDouble = Double.parseDouble(ereCommFlatAmount);
                                    if (ereCommFlatAmountDouble < 0) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.flatCommBasis.invalidFlatAmount",
                                                new String[]{AgentFields.ERE},
                                                AgentFields.ERE_COMM_FLAT_AMOUNT, rowId);
                                    }
                                }
                            } else if (AgentManagerImpl.COMM_BASIS_SCHEDULE.equals(ereCommBasis)) {
                                if (!record.hasStringValue(AgentFields.ERE_COMM_RATE_SCHEDULE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.schedCommBasis.emptySchedule",
                                            new String[]{AgentFields.ERE},
                                            AgentFields.ERE_COMM_RATE_SCHEDULE, rowId);
                                }
                            }
                        } else {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.contractcommission.commBasis.required",
                                    new String[]{AgentFields.ERE}, AgentFields.ERE_COMM_BASIS, rowId);
                        }
                    }

                    //validate Agent
                    iterator = agentStaffRecords.getRecords();
                    while (iterator.hasNext()) {
                        Date staffEffStartDate = null;
                        Date staffEffEndDate = null;
                        Record record = (Record) iterator.next();
                        String agentStaffId = AgentFields.getAgentStaffId(record);
                        if (!record.hasStringValue(AgentFields.AGENT_STAFF_ENTITY_ID)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaff.invalidAgentName",
                                    new String[0], AgentFields.AGENT_STAFF_ENTITY_NAME, agentStaffId, AgentFields.AGENT_STAFF_GRID_ID);
                        }
                        else if (!record.hasStringValue(AgentFields.STAFF_EFF_START_DATE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaff.emptyStartDate",
                                    new String[0], AgentFields.STAFF_EFF_START_DATE, agentStaffId, AgentFields.AGENT_STAFF_GRID_ID);
                        } else if (!record.hasStringValue(AgentFields.STAFF_EFF_END_DATE)) {
                            messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaff.emptyEndDate",
                                    new String[0], AgentFields.STAFF_EFF_END_DATE, agentStaffId, AgentFields.AGENT_STAFF_GRID_ID);
                        } else {
                            staffEffStartDate = AgentFields.getStaffEffStartDate(record);
                            staffEffEndDate = AgentFields.getStaffEffEndDate(record);
                            if (staffEffStartDate.after(staffEffEndDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaff.startDateAfterEndDate",
                                        new String[0], AgentFields.STAFF_EFF_START_DATE, agentStaffId, AgentFields.AGENT_STAFF_GRID_ID);
                            }
                            if (staffEffStartDate.before(effectiveStartDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaff.startDateBeforeAgencyStartDate",
                                        new String[0], AgentFields.STAFF_EFF_START_DATE, agentStaffId, AgentFields.AGENT_STAFF_GRID_ID);
                            }
                            if (staffEffEndDate.after(effectiveEndDate)) {
                                messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaff.endDateAfterAgencyEndDate",
                                        new String[0], AgentFields.STAFF_EFF_START_DATE, agentStaffId, AgentFields.AGENT_STAFF_GRID_ID);
                            }
                            //validate Child Agent Overrides
                            RecordSet subOverrideRecords = agentOverrideRecords.getSubSet(new RecordFilter(AgentFields.AGENT_STAFF_ID, agentStaffId));
                            Iterator agentOverrideIterator = subOverrideRecords.getRecords();
                            while (agentOverrideIterator.hasNext()) {
                                Record overrideRecord = (Record) agentOverrideIterator.next();
                                Date overrideEffStartDate = null;
                                Date overrideEffEndDate = null;
                                String agentStaffOverrideId = AgentFields.getAgentStaffOverrideId(overrideRecord);
                                String rowId = agentStaffId + "," + agentStaffOverrideId;
                                if (!overrideRecord.hasStringValue(AgentFields.OVERRIDE_NB_COMM_RATE)
                                        && !overrideRecord.hasStringValue(AgentFields.OVERRIDE_RN_COMM_RATE)
                                        && !overrideRecord.hasStringValue(AgentFields.OVERRIDE_ERE_COMM_RATE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.percentCommBasis.emptyCommRate",
                                            new String[]{AgentFields.NEW_BUSINESS},
                                            AgentFields.OVERRIDE_NB_COMM_RATE, rowId);
                                }
                                if (!overrideRecord.hasStringValue(AgentFields.OVERRIDE_EFF_START_DATE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.emptyStartDate",
                                            new String[0], AgentFields.OVERRIDE_EFF_START_DATE, rowId);
                                } else if (!overrideRecord.hasStringValue(AgentFields.OVERRIDE_EFF_END_DATE)) {
                                    messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.emptyEndDate",
                                            new String[0], AgentFields.STAFF_EFF_END_DATE, rowId);
                                } else {
                                    overrideEffStartDate = AgentFields.getOverrideEffStartDate(overrideRecord);
                                    overrideEffEndDate = AgentFields.getOverrideEffEndDate(overrideRecord);
                                    if (overrideEffStartDate.after(overrideEffEndDate)) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.startDateAfterEndDate",
                                                new String[0], AgentFields.OVERRIDE_EFF_START_DATE, rowId);
                                    }
                                    if (overrideEffStartDate.before(staffEffStartDate)) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.startDateBeforeAgentEndDate",
                                                new String[0], AgentFields.OVERRIDE_EFF_START_DATE, rowId);
                                    }
                                    if (overrideEffEndDate.after(staffEffEndDate)) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.endDateAfterAgentEndDate",
                                                new String[0], AgentFields.OVERRIDE_EFF_END_DATE, rowId);
                                    }
                                }

                                //new business
                                boolean isRateOverride = false;
                                if (overrideRecord.hasStringValue(AgentFields.OVERRIDE_NB_COMM_RATE)) {
                                    String newbusRate = AgentFields.getOverrideNBRate(overrideRecord);
                                    double newbusRateDouble = Double.parseDouble(newbusRate);
                                    if (newbusRateDouble < 0 || newbusRateDouble > maxNewbusRateDouble) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.percentCommBasis.newBusiness.invalidCommRate",
                                                new String[]{Double.toString(maxNewbusRateDouble)},
                                                AgentFields.OVERRIDE_NB_COMM_RATE, rowId);
                                    }
                                }

                                //renewal
                                if (overrideRecord.hasStringValue(AgentFields.OVERRIDE_RN_COMM_RATE)) {
                                    String renewalRate = AgentFields.getOverrideRNRate(overrideRecord);
                                    double renewalRateDouble = Double.parseDouble(renewalRate);
                                    if (renewalRateDouble < 0 || renewalRateDouble > maxRenwalRateDouble) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.percentCommBasis.renewal.invalidCommRate",
                                                new String[]{Double.toString(maxRenwalRateDouble)},
                                                AgentFields.OVERRIDE_RN_COMM_RATE, rowId);
                                    }
                                }

                                //ERE
                                if (overrideRecord.hasStringValue(AgentFields.OVERRIDE_ERE_COMM_RATE)) {
                                    String ereRate = AgentFields.getOverrideERERate(overrideRecord);
                                    double ereRateDouble = Double.parseDouble(ereRate);
                                    if (ereRateDouble < 0 || ereRateDouble > maxEreRateDouble) {
                                        messageManager.addErrorMessage("ci.agentmgr.maintainAgent.agentStaffOverride.percentCommBasis.ere.invalidCommRate",
                                                new String[]{Double.toString(maxEreRateDouble)},
                                                AgentFields.OVERRIDE_ERE_COMM_RATE, rowId);
                                    }
                                }
                            }
                            // End validating Child Agent Overrides
                        }
                    }
                    // End validating Agent
                }
            }
        }

        // Validation :  Validate continuity
        if (!messageManager.hasErrorMessages()) {
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                    AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE, AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE,
                    AgentFields.PAY_COMMISSION_ID,
                    "ci.agentmgr.maintainAgent.paycommission.overlappingTime", AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE);
            continuityValidator.setMessageFieldName(AgentFields.PAY_COMMISSION_ID);
            continuityValidator.validate(payCommissionRecords);

            //Generate contract recordset by splitting the issue company list
            RecordSet inputRecords = new RecordSet();
            Record record = null;

            iterator = contractRecords.getRecords();
            while (iterator.hasNext()) {
               Record contractRecord = (Record) iterator.next();

               String [] issueCompanyEntityFkArray;

               //String issueCompanyEntityFkList = AgentFields.getIssueCompanyEntityFkList(contractRecord);
               if (contractRecord.hasStringValue(AgentFields.CONTRACT_ISSUE_COMPANY_ENTITY_FK_LIST)) {
                 issueCompanyEntityFkArray = AgentFields.getIssueCompanyEntityFkList(contractRecord).split(",");
                 for (int i = 0; i < issueCompanyEntityFkArray.length; i++) {
                   record = new Record();
                   AgentFields.setContractEffectiveStartDate(record, AgentFields.getContractEffectiveStartDateString(contractRecord));
                   AgentFields.setContractEffectiveEndDate(record, AgentFields.getContractEffectiveEndDateString(contractRecord));
                   AgentFields.setAgentLicenseId(record, AgentFields.getAgentLicenseId(contractRecord));
                   AgentFields.setContractStateCode(record, AgentFields.getContractStateCode(contractRecord));
                   AgentFields.setContractNumber(record, AgentFields.getContractNumber(contractRecord));
                   AgentFields.setContractType(record, AgentFields.getContractType(contractRecord));
                   AgentFields.setIssueCompanyEntityFkList(record, issueCompanyEntityFkArray[i]);
                   inputRecords.addRecord(record);
                 }
               }
               else {
                   record = new Record();
                   AgentFields.setContractEffectiveStartDate(record, AgentFields.getContractEffectiveStartDateString(contractRecord));
                   AgentFields.setContractEffectiveEndDate(record, AgentFields.getContractEffectiveEndDateString(contractRecord));
                   AgentFields.setAgentLicenseId(record, AgentFields.getAgentLicenseId(contractRecord));
                   AgentFields.setContractStateCode(record, AgentFields.getContractStateCode(contractRecord));
                   AgentFields.setContractNumber(record, AgentFields.getContractNumber(contractRecord));
                   AgentFields.setContractType(record, AgentFields.getContractType(contractRecord));
                   AgentFields.setIssueCompanyEntityFkList(record, AgentFields.getIssueCompanyEntityFkList(contractRecord)); 
                   inputRecords.addRecord(record);
               }
            }

            continuityValidator = new ContinuityRecordSetValidator(
                    AgentFields.CONTRACT_EFFECTIVE_START_DATE, AgentFields.CONTRACT_EFFECTIVE_END_DATE,
                    AgentFields.CONTRACT_ID,
                    "ci.agentmgr.maintainAgent.agentcontract.overlappingTime",
                    new String[]{AgentFields.CONTRACT_STATE_CODE, AgentFields.CONTRACT_NUMBER, AgentFields.CONTRACT_TYPE,
                                 AgentFields.CONTRACT_ISSUE_COMPANY_ENTITY_FK_LIST},
                                 new String[0], false, AgentFields.CONTRACT_EFFECTIVE_START_DATE);
            continuityValidator.validate(inputRecords);

            ContractCommissionRecordSetValidator contractCommissionRecordSetValidator = new ContractCommissionRecordSetValidator(
                    AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE, AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE,
                    AgentFields.CONTRACT_COMMISSION_ID,
                    "ci.agentmgr.maintainAgent.contractcommission.overlappingTime",
                    new String[]{AgentFields.CONTRACT_ID, AgentFields.CONTRACT_COMMISSION_POLICY_TYPE_CODE, AgentFields.CONTRACT_COMMISSION_PRIMARY_RISK_TYPE_CODE},
                    new String[0], false, AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE);
            contractCommissionRecordSetValidator.setParentResultSet(contractRecords);
            contractCommissionRecordSetValidator.validate(contractCommissionRecords);


            // Validate continuity on Agent Overrides
            if (agentOverrideRecords != null && agentOverrideRecords.getSize() > 0) {
                AgentOverrideRecordSetValidator agentOverrideRecordSetValidator = new AgentOverrideRecordSetValidator(
                        AgentFields.OVERRIDE_EFF_START_DATE, AgentFields.OVERRIDE_EFF_END_DATE,
                        AgentFields.AGENT_STAFF_OVERRIDE_ID,
                        "ci.agentmgr.maintainAgent.agentStaffOverride.overlappingTime",
                        new String[]{AgentFields.AGENT_STAFF_ID, AgentFields.OVERRIDE_POLICY_TYPE,
                                     AgentFields.OVERRIDE_STATE_CODE, AgentFields.OVERRIDE_ISSUE_COMPANY},
                        new String[0], false, AgentFields.OVERRIDE_EFF_START_DATE);
                agentOverrideRecordSetValidator.setParentResultSet(agentStaffRecords);
                agentOverrideRecordSetValidator.setMessageFieldName(AgentFields.OVERRIDE_POLICY_TYPE);
                agentOverrideRecordSetValidator.validate(agentOverrideRecords);
            }
        }

        // if any error Messages stored, we throw validation exception here
        if (messageManager.hasErrorMessages()) {
            ValidationException ve = new ValidationException("Record did not pass validations.");
            l.throwing(getClass().getName(), "Record did not pass validations.", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateAllAgent");
    }


    /**
     * set End date on each level
     *
     * @param inputRecord
     * @param payCommissionRecords
     * @param contractRecords
     * @param contractCommissionRecords
     */
    protected void setEndDate(Record inputRecord, RecordSet payCommissionRecords,
                              RecordSet contractRecords, RecordSet contractCommissionRecords) {
        Logger l = LogUtils.enterLog(getClass(), "setEndDate",
                new Object[]{inputRecord, payCommissionRecords, contractRecords, contractCommissionRecords});

        String effectiveEndDate = null;
        if (!inputRecord.hasStringValue(AgentFields.EFFECTIVE_END_DATE)) {
            AgentFields.setEffectiveEndDate(inputRecord, AgentManagerImpl.DEFAULT_END_DATE);
            effectiveEndDate = AgentManagerImpl.DEFAULT_END_DATE;
        } else {
            effectiveEndDate = AgentFields.getEffectiveEndDateString(inputRecord);
        }

        //set end date on pay commission grid
        Iterator insertIterator = payCommissionRecords.getRecords();
        while (insertIterator.hasNext()) {
            Record record = (Record) insertIterator.next();
            if (!record.hasStringValue(AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE)) {
                AgentFields.setPayCommissionEffectiveEndDate(record, effectiveEndDate);
                if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                    record.setUpdateIndicator(UpdateIndicator.UPDATED);
                }
            } else {
                String payCommissionEffectiveEndDate = AgentFields.getPayCommissionEffectiveEndDateString(record);
                if (AgentManagerImpl.DEFAULT_END_DATE.equals(payCommissionEffectiveEndDate)) {
                    AgentFields.setPayCommissionEffectiveEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
            }
        }
        //set end date on contract grid
        insertIterator = contractRecords.getRecords();
        while (insertIterator.hasNext()) {
            Record record = (Record) insertIterator.next();
            if (!record.hasStringValue(AgentFields.CONTRACT_EFFECTIVE_END_DATE)) {
                AgentFields.setContractEffectiveEndDate(record, effectiveEndDate);
                if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                    record.setUpdateIndicator(UpdateIndicator.UPDATED);
                }
            } else {
                String contractEffectiveEndDate = AgentFields.getContractEffectiveEndDateString(record);
                if (AgentManagerImpl.DEFAULT_END_DATE.equals(contractEffectiveEndDate)) {
                    AgentFields.setContractEffectiveEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
            }
            if (record.hasStringValue(AgentFields.CONTRACT_APPOINTMENT_START_DATE)) {
                if (!record.hasStringValue(AgentFields.CONTRACT_APPOINTMENT_END_DATE)) {
                    AgentFields.setAppointmentEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                } else {
                    String appointmentEndDate = AgentFields.getAppointmentEndDateString(record);
                    if (AgentManagerImpl.DEFAULT_END_DATE.equals(appointmentEndDate)) {
                        AgentFields.setAppointmentEndDate(record, effectiveEndDate);
                        if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                            record.setUpdateIndicator(UpdateIndicator.UPDATED);
                        }
                    }
                }
            }
        }
        //set end date on contract commission grid
        insertIterator = contractCommissionRecords.getRecords();
        while (insertIterator.hasNext()) {
            Record record = (Record) insertIterator.next();
            if (!record.hasStringValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE)) {
                AgentFields.setContractCommissionEndDate(record, effectiveEndDate);
                if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                    record.setUpdateIndicator(UpdateIndicator.UPDATED);
                }
            } else {
                String contractCommissionEffectiveEndDate = AgentFields.getContractCommissionEndDateString(record);
                if (AgentManagerImpl.DEFAULT_END_DATE.equals(contractCommissionEffectiveEndDate)) {
                    AgentFields.setContractCommissionEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
            }
        }
        l.exiting(getClass().getName(), "setEndDate");
    }

    /**
     * load available agent contract list
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAvailableAgentContract(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableAgentContract", new Object[]{inputRecord});
        }
        if (inputRecord.hasFieldValue("passLicenseNumber")) {
            AgentFields.setSearchBy(inputRecord, "NUM");
            AgentFields.setSearchString(inputRecord, AgentFields.getPassLicenseNumber(inputRecord));
        }
        RecordSet outRecordSet = getAgentDAO().loadAllAvailableAgentContract(inputRecord);

        outRecordSet.getSummaryRecord().setFields(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableAgentContract", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * method to get the initial value when adding agent
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddAgent(Record inputRecord) {

        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddAgent", new Object[]{inputRecord});
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(AgentManagerImpl.MAINTAIN_AGENT_ACTION_CLASS_NAME);

        //start with the default record
        outRecord.setFields(defaultValuesRecord);

        //generate new agent license id
        Long newAgentLicenseId = getDbUtilityManager().getNextSequenceNo();
        AgentFields.setId(outRecord, newAgentLicenseId.toString());

        //overlay it with inputRecord
        outRecord.setFields(inputRecord);

        // get the initial pageEntitlement for a agent
        outRecord.setFields(AgentGridRecordLoadProcessor.getInitialEntitlementValuesForAgent());

        l.exiting(getClass().toString(), "getInitialValuesForAddAgent", outRecord);
        return outRecord;
    }


    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param inputRecord          agent information
     * @param payCommissionRecords agent pay commission list
     * @param contractRecords      agent contract list
     * @param contractCommission   agent contract commission list
     * @return the number of rows updated.
     */
    public int saveAllAgent(Record inputRecord, RecordSet payCommissionRecords,
                            RecordSet contractRecords, RecordSet contractCommission,
                            RecordSet agentStaffRecords, RecordSet agentOverrideRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAgent",
                new Object[]{inputRecord, payCommissionRecords, contractRecords, contractCommission});

        int updateCount = 0;

        setEndDate(inputRecord, payCommissionRecords, contractRecords, contractCommission);

        // Validate the input agent records prior to saving them.
        validateAllAgent(inputRecord, payCommissionRecords, contractRecords, contractCommission, agentStaffRecords, agentOverrideRecords);

        //save agent information
        getAgentDAO().saveAllAgent(inputRecord);

        //pay commission grid
        // insert into db in batch mode:.
        RecordSet insertedRecords = payCommissionRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedRecords.setFieldValueOnAll(AgentFields.ENTITY_ID, AgentFields.getEntityId(inputRecord));
        updateCount = getAgentDAO().addAllAgentPayCommission(insertedRecords);
        // update the db in batch mode:
        RecordSet updatedRecords = payCommissionRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updateCount += getAgentDAO().updateAllAgentPayCommission(updatedRecords);

        //contract grid
        // insert into db in batch mode:.
        insertedRecords = contractRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedRecords.setFieldValueOnAll(AgentFields.ENTITY_ID, AgentFields.getEntityId(inputRecord));
        updateCount = getAgentDAO().addAllAgentContract(insertedRecords);
        // update the db in batch mode:
        updatedRecords = contractRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updateCount += getAgentDAO().updateAllAgentContract(updatedRecords);

        //contract commission grid
        // insert into db in batch mode:.
        insertedRecords = contractCommission.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedRecords.setFieldValueOnAll(AgentFields.ENTITY_ID, AgentFields.getEntityId(inputRecord));
        updateCount = getAgentDAO().addAllAgentContractCommission(insertedRecords);
        // update the db in batch mode:
        updatedRecords = contractCommission.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updateCount += getAgentDAO().updateAllAgentContractCommission(updatedRecords);

        if (agentStaffRecords != null && agentStaffRecords.getSize() > 0) {
            //agent grid
            //insert into db in batch mode:.
            insertedRecords = agentStaffRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            insertedRecords.setFieldValueOnAll(AgentFields.ENTITY_ID, AgentFields.getEntityId(inputRecord));
            updateCount = getAgentDAO().addAllAgentStaff(insertedRecords);
            // update the db in batch mode:
            updatedRecords = agentStaffRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
            updateCount += getAgentDAO().updateAllAgentStaff(updatedRecords);

            //agent override grid
            // insert into db in batch mode:.
            insertedRecords = agentOverrideRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            updateCount = getAgentDAO().addAllAgentStaffOverride(insertedRecords);
            // update the db in batch mode:
            updatedRecords = agentOverrideRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
            updateCount += getAgentDAO().updateAllAgentStaffOverride(updatedRecords);
        }

        l.exiting(getClass().getName(), "saveAllAgent", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Load all sub producers of an producer.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllSubProducer(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSubProducer", new Object[]{inputRecord});
        }

        RecordSet rs = getAgentDAO().loadAllSubProducer(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSubProducer", rs);
        }

        return rs;
    }

    /**
     * To load all agent output options for a given agent and policy
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentOutputOption(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentOutputOption", new Object[]{inputRecord});
        }

        RecordSet rs = getAgentDAO().loadAllAgentOutputOption(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentOutputOption", rs);
        }
        return rs;
    }

    /**
     * To get initial values for Add Output Option
     *
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForAddOutputOption(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddOutputOption", new Object[]{inputRecord});
        }

        Record record = new Record();
        AgentFields.setFormBucketCode(record, null);
        AgentFields.setCopyTypeCode(record, null);
        AgentFields.setIssueCompanyEntityId(record, null);
        AgentFields.setPolicyTypeCode(record, null);
        AgentFields.setEffectiveFromDate(record, null);
        AgentFields.setEffectiveToDate(record, null);
        AgentFields.setAddressId(record, null);
        AgentFields.setAddressOptionCode(record, AgentFields.ADDRESS_OPTION_CODE_NA);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentOutputOption", record);
        }
        return record;
    }

    /**
     * To save all agent output options
     *
     * @param inputRecord
     * @param outputRs
     */
    public void saveAllAgentOutputOption(Record inputRecord, RecordSet outputRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAgentOutputOption", new Object[]{inputRecord, outputRs});
        }

        RecordSet chgRs = validateAgentOutputOptionForSave(outputRs, inputRecord);
        chgRs.setFieldValueOnAll(AgentFields.AGENT_ID, AgentFields.getAgentId(inputRecord));
        if (inputRecord.hasStringValue(AgentFields.POLICY_ID)) {
            chgRs.setFieldValueOnAll(AgentFields.POLICY_ID, AgentFields.getPolicyId(inputRecord));
        }

        getAgentDAO().saveAllAgentOutputOption(chgRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAgentOutputOption");
        }
    }

    /**
     * To validate agent output options before saving
     *
     * @param outputRs
     * @param inputRecord
     * @return
     */
    protected RecordSet validateAgentOutputOptionForSave(RecordSet outputRs, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAgentOutputOptionForSave", new Object[]{outputRs});
        }

        RecordSet chgRs = new RecordSet();
        for (int i = 0, rowNo = 0; i != outputRs.getSize(); i++) {
            Record current = outputRs.getRecord(i);
            String indicator = current.getUpdateIndicator();
            if(!UpdateIndicator.NOT_CHANGED.equals(indicator)){
                chgRs.addRecord(current);
                setActionValue(current, indicator);
            }

            if (UpdateIndicator.DELETED.equals(indicator)) {
                continue;
            }
            rowNo++;
            // Check required fields
            if (!current.hasStringValue(AgentFields.FORM_BUCKET_CODE)) {
                MessageManager.getInstance().addErrorMessage("ci.agentmgr.maintainAgentOutputOptions.formBucket.isRequired.error",
                        new String[]{String.valueOf(rowNo)});
                throw new ValidationException("invalid output options");
            }
            if (!current.hasStringValue(AgentFields.COPY_TYPE_CODE)) {
                MessageManager.getInstance().addErrorMessage("ci.agentmgr.maintainAgentOutputOptions.copyType.isRequired.error",
                        new String[]{String.valueOf(rowNo)});
                throw new ValidationException("invalid output options");
            }
            if (!current.hasStringValue(AgentFields.EFFECTIVE_FROM_DATE)) {
                MessageManager.getInstance().addErrorMessage("ci.agentmgr.maintainAgentOutputOptions.effFromDate.isRequired.error",
                        new String[]{String.valueOf(rowNo)});
                throw new ValidationException("invalid output options");
            }
            if (!current.hasStringValue(AgentFields.EFFECTIVE_TO_DATE)) {
                MessageManager.getInstance().addErrorMessage("ci.agentmgr.maintainAgentOutputOptions.effToDate.isRequired.error",
                        new String[]{String.valueOf(rowNo)});
                throw new ValidationException("invalid output options");
            }

            Date effFromDateForCurrent = AgentFields.getEffectiveFromDate(current);
            Date effToDateForCurrent = AgentFields.getEffectiveToDate(current);
            // Check if Effective From Date is after Effective To Date
            if (effFromDateForCurrent.after(effToDateForCurrent)) {
                MessageManager.getInstance().addErrorMessage("ci.agentmgr.maintainAgentOutputOptions.toDateBeforeFromDate.error",
                        new String[]{String.valueOf(rowNo)});
            }

            // Check time period overlap
            checkTimePeriodOverlap(outputRs, rowNo, current, effFromDateForCurrent, effToDateForCurrent);
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("invalid output options");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAgentOutputOptionForSave");
        }
        return chgRs;
    }

    /**
     * Set action value by update indicator
     * @param current
     * @param indicator
     */
    private void setActionValue(Record current, String indicator) {
        if(UpdateIndicator.UPDATED.equals(indicator)){
           AgentFields.setAction(current, UPDATE_ACTION);
        }
        else{
           AgentFields.setAction(current, indicator);
        }
    }

    /**
     * Check if time period overlaps others for the same bucket/copy type/issue company/policy type
     * @param outputRs
     * @param rowNo
     * @param current
     * @param effFromDateForCurrent
     * @param effToDateForCurrent
     */
    private void checkTimePeriodOverlap(RecordSet outputRs, int rowNo, Record current, Date effFromDateForCurrent, Date effToDateForCurrent) {
        for (int j = 0; j != outputRs.getSize(); j++) {
            Record next = outputRs.getRecord(j);
            if (next.getUpdateIndicator() == UpdateIndicator.DELETED ||
                    AgentFields.getAgentOutputOptionId(current).equals(AgentFields.getAgentOutputOptionId(next))) {
                continue;
            }
            if (checkValues(current, next)) {
                Date effFromDateForNext = AgentFields.getEffectiveFromDate(next);
                Date effToDateForNext = AgentFields.getEffectiveToDate(next);
                if (effFromDateForCurrent.before(effToDateForNext) && effToDateForCurrent.after(effFromDateForNext) ||
                        effFromDateForCurrent.equals(effFromDateForNext) && effToDateForCurrent.equals(effToDateForNext)) {
                    MessageManager.getInstance().addErrorMessage("ci.agentmgr.maintainAgentOutputOptions.dateOverlap.error",
                            new String[]{String.valueOf(rowNo)});
                }
            }
        }
    }

    /**
     * To check if record r1 and record r2 have the same values for bucket/copy type/issue company/policy type
     *
     * @param r1
     * @param r2
     * @return
     */
    private boolean checkValues(Record r1, Record r2) {
        return StringUtils.isSame(AgentFields.getFormBucketCode(r1), AgentFields.getFormBucketCode(r2)) &&
                StringUtils.isSame(AgentFields.getCopyTypeCode(r1), AgentFields.getCopyTypeCode(r2)) &&
                StringUtils.isSame(AgentFields.getIssueCompanyEntityId(r1), AgentFields.getIssueCompanyEntityId(r2)) &&
                StringUtils.isSame(AgentFields.getPolicyTypeCode(r1), AgentFields.getPolicyTypeCode(r2));
    }

    public void verifyConfig() {
        if (getAgentDAO() == null)
            throw new ConfigurationException("The required property 'agentDAO' is missing.");

        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");

        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public AgentDAO getAgentDAO() {
        return m_agentDAO;
    }

    public void setAgentDAO(AgentDAO agentDAO) {
        m_agentDAO = agentDAO;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;
    private AgentDAO m_agentDAO;
    private DBUtilityManager m_dbUtilityManager;
    
    private static final String MAINTAIN_AGENT_ACTION_CLASS_NAME = "dti.ci.agentmgr.struts.MaintainAgentAction";
    private static final String LICENSE_CLASS_CODE_PRODUCER = "PRODUCER";

    private static final String COMM_BASIS_PERCENT = "PERCENT";
    private static final String COMM_BASIS_SCHEDULE = "SCHED";
    private static final String COMM_BASIS_FLAT = "FLAT";

    private static final String DEFAULT_END_DATE = "01/01/3000";
    private static final String UPDATE_ACTION = "U";

    public static final String NEW_BUS_RATE_MAX = "NEW BUS. RATE MAX";
    public static final String NEW_BUS_RATE_MAX_DEFAULT = "20";
    public static final String RENEWAL_RATE_MAX = "RENEWAL RATE MAX";
    public static final String RENEWAL_RATE_MAX_DEFAULT = "20";
    public static final String ERE_RATE_MAX = "ERE RATE MAX";
    public static final String ERE_RATE_MAX_DEFAULT = "20";
    public static final String PM_VAL_AFFI_DATES = "PM_VAL_AFFI_DATES";

}
