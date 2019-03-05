
package com.delphi_tech.ows.account;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.delphi_tech.ows.account package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PrincipalBillingAccountInformation_QNAME = new QName("http://www.delphi-tech.com/ows/Account", "PrincipalBillingAccountInformation");
    private final static QName _IssueCompany_QNAME = new QName("http://www.delphi-tech.com/ows/Account", "IssueCompany");
    private final static QName _PolicyHolder_QNAME = new QName("http://www.delphi-tech.com/ows/Account", "PolicyHolder");
    private final static QName _ContractPeriod_QNAME = new QName("http://www.delphi-tech.com/ows/Account", "ContractPeriod");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.delphi_tech.ows.account
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContractPeriodType }
     * 
     */
    public ContractPeriodType createContractPeriodType() {
        return new ContractPeriodType();
    }

    /**
     * Create an instance of {@link PolicyHolderType }
     * 
     */
    public PolicyHolderType createPolicyHolderType() {
        return new PolicyHolderType();
    }

    /**
     * Create an instance of {@link PrincipalBillingAccountInformationType }
     * 
     */
    public PrincipalBillingAccountInformationType createPrincipalBillingAccountInformationType() {
        return new PrincipalBillingAccountInformationType();
    }

    /**
     * Create an instance of {@link IssueCompanyType }
     * 
     */
    public IssueCompanyType createIssueCompanyType() {
        return new IssueCompanyType();
    }

    /**
     * Create an instance of {@link InstalmentInformationType }
     * 
     */
    public InstalmentInformationType createInstalmentInformationType() {
        return new InstalmentInformationType();
    }

    /**
     * Create an instance of {@link PaymentOptionType }
     * 
     */
    public PaymentOptionType createPaymentOptionType() {
        return new PaymentOptionType();
    }

    /**
     * Create an instance of {@link AccountHolderType }
     * 
     */
    public AccountHolderType createAccountHolderType() {
        return new AccountHolderType();
    }

    /**
     * Create an instance of {@link BillingAccountBalanceType }
     * 
     */
    public BillingAccountBalanceType createBillingAccountBalanceType() {
        return new BillingAccountBalanceType();
    }

    /**
     * Create an instance of {@link TransactionDetailType }
     * 
     */
    public TransactionDetailType createTransactionDetailType() {
        return new TransactionDetailType();
    }

    /**
     * Create an instance of {@link BillingActivityInformationType }
     * 
     */
    public BillingActivityInformationType createBillingActivityInformationType() {
        return new BillingActivityInformationType();
    }

    /**
     * Create an instance of {@link ReferredPartyType }
     * 
     */
    public ReferredPartyType createReferredPartyType() {
        return new ReferredPartyType();
    }

    /**
     * Create an instance of {@link BillingAccountDetailType }
     * 
     */
    public BillingAccountDetailType createBillingAccountDetailType() {
        return new BillingAccountDetailType();
    }

    /**
     * Create an instance of {@link LinkedPolicyType }
     * 
     */
    public LinkedPolicyType createLinkedPolicyType() {
        return new LinkedPolicyType();
    }

    /**
     * Create an instance of {@link EffectivePeriodType }
     * 
     */
    public EffectivePeriodType createEffectivePeriodType() {
        return new EffectivePeriodType();
    }

    /**
     * Create an instance of {@link PolicyTermType }
     * 
     */
    public PolicyTermType createPolicyTermType() {
        return new PolicyTermType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrincipalBillingAccountInformationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/Account", name = "PrincipalBillingAccountInformation")
    public JAXBElement<PrincipalBillingAccountInformationType> createPrincipalBillingAccountInformation(PrincipalBillingAccountInformationType value) {
        return new JAXBElement<PrincipalBillingAccountInformationType>(_PrincipalBillingAccountInformation_QNAME, PrincipalBillingAccountInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IssueCompanyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/Account", name = "IssueCompany")
    public JAXBElement<IssueCompanyType> createIssueCompany(IssueCompanyType value) {
        return new JAXBElement<IssueCompanyType>(_IssueCompany_QNAME, IssueCompanyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolicyHolderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/Account", name = "PolicyHolder")
    public JAXBElement<PolicyHolderType> createPolicyHolder(PolicyHolderType value) {
        return new JAXBElement<PolicyHolderType>(_PolicyHolder_QNAME, PolicyHolderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContractPeriodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/Account", name = "ContractPeriod")
    public JAXBElement<ContractPeriodType> createContractPeriod(ContractPeriodType value) {
        return new JAXBElement<ContractPeriodType>(_ContractPeriod_QNAME, ContractPeriodType.class, null, value);
    }

}
