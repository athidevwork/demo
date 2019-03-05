<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="20.33cm" page-width="27.95cm" margin-top="1cm"
                                       margin-bottom="72pt" margin-left="72pt" margin-right="72pt">
                    <fo:region-body margin-top="9.1cm"/>
                    <fo:region-before extent="8.4cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block>
                        <xsl:apply-templates select="SECTION1"/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:apply-templates select="SECTION2"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1">
        <fo:table font-size="9pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="110pt"/>

            <fo:table-body>

                <xsl:choose>
                    <xsl:when test="normalize-space(ROWS/ROW/POLICY_NO)='' ">
                        <fo:table-row>
                            <fo:table-cell text-align="center" height="5.5cm" number-columns-spanned="6">
                                <fo:block>&#160;</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </xsl:when>
                    <xsl:otherwise>
                        <fo:table-row>
                            <fo:table-cell text-align="center" number-columns-spanned="6">
                                <fo:block>
                                    <xsl:value-of select="ROWS/ROW/ISSUE_COMPANY_NAME"/>
                                </fo:block>
                                <fo:block>
                                    Premium Worksheet
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell text-align="right" number-columns-spanned="3">
                                <fo:block>
                                    Generated On:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" number-columns-spanned="3">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/GENERATED_ON"/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row>
                            <fo:table-cell text-align="right" number-columns-spanned="3">
                                <fo:block>
                                    Accounting:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" number-columns-spanned="3">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/TL_ACCOUNTING"/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row>
                            <fo:table-cell text-align="right" number-columns-spanned="3">
                                <fo:block>
                                    UserID:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left" number-columns-spanned="3">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/USER_ID"/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Policy Holder Name:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block-container height="33pt" overflow="hidden">
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/POL_HOLDER"/>
                                        </xsl:call-template>
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Policy Number:
                                </fo:block>
                                <fo:block>
                                    Account Number:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/POLICY_NO"/>
                                </fo:block>
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/ACCOUNT_NO"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Policy Effective Date:
                                </fo:block>
                                <fo:block>
                                    Policy Expiration Date:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/POLICY_EFF"/>
                                </fo:block>
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/POLICY_EXP"/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Policy Address:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block-container height="44pt" overflow="hidden">
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/ADD1"/>
                                        </xsl:call-template>
                                    </fo:block>
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/ADD2"/>
                                        </xsl:call-template>
                                    </fo:block>
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/ADD3"/>
                                        </xsl:call-template>
                                    </fo:block>
                                    <fo:block text-decoration="underline">
                                        <xsl:value-of select="ROWS/ROW/CITY"/>
                                        <xsl:text>&#160;&#160;</xsl:text>
                                        <xsl:value-of select="ROWS/ROW/STATE"/>
                                        <xsl:text>&#160;&#160;</xsl:text>
                                        <xsl:value-of select="ROWS/ROW/ZIP"/>
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Payment Plan:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/PLAN_DESCRIPTION"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Status:
                                </fo:block>
                                <fo:block>
                                    Transaction:
                                </fo:block>
                                <fo:block>
                                    Comments:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block-container height="44pt" overflow="hidden">
                                    <fo:block text-decoration="underline">
                                        <xsl:value-of select="ROWS/ROW/POL_STATUS"/>
                                    </fo:block>
                                    <fo:block text-decoration="underline">
                                        <xsl:value-of select="ROWS/ROW/TRANSACTION_CODE_DESC"/>
                                    </fo:block>
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/COMMENTS"/>
                                        </xsl:call-template>
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row>
                            <fo:table-cell text-align="right">
                                <fo:block-container height="33pt">
                                    <fo:block>
                                        Producer Name:
                                    </fo:block>
                                </fo:block-container>
                                <fo:block-container height="11pt">
                                    <fo:block>
                                        Issue State:
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block-container height="33pt" overflow="hidden">
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/AGENT"/>
                                        </xsl:call-template>
                                    </fo:block>
                                </fo:block-container>
                                <fo:block-container height="11pt">
                                    <fo:block text-decoration="underline">
                                        <xsl:value-of select="ROWS/ROW/ISSUE_STATE"/>
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Commission Rate:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block text-decoration="underline">
                                    <xsl:value-of select="ROWS/ROW/COMM_RATE"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="right">
                                <fo:block>
                                    Transaction Effective:
                                </fo:block>
                                <fo:block>
                                    Underwriter:
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="left">
                                <fo:block-container height="55pt" overflow="hidden">
                                    <fo:block text-decoration="underline">
                                        <xsl:value-of select="ROWS/ROW/TL_EFFECTIVE"/>
                                    </fo:block>
                                    <fo:block text-decoration="underline">
                                        <xsl:call-template name="zero_width_space">
                                            <xsl:with-param name="data" select="ROWS/ROW/UNDERWRITER"/>
                                        </xsl:call-template>
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell text-align="left" number-columns-spanned="6">
                                <fo:block-container height="11pt" overflow="hidden">
                                    <fo:block>
                                        <xsl:value-of select="ROWS/ROW/RISK_STR"/>
                                    </fo:block>
                                </fo:block-container>
                            </fo:table-cell>
                        </fo:table-row>

                    </xsl:otherwise>
                </xsl:choose>

                <fo:table-row>
                    <fo:table-cell number-columns-spanned="6">
                        <fo:block text-align="right">
                            page
                            <fo:page-number/>
                            of
                            <fo:page-number-citation ref-id="totalPage"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>

        <fo:block>&#160;</fo:block>
        <fo:table font-size="9pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="63pt"/>
            <fo:table-column column-width="105pt"/>
            <fo:table-column column-width="98pt"/>
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="98pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="98pt"/>
            <fo:table-column column-width="62pt"/>
            <fo:table-column column-width="66pt"/>
            <fo:table-body>
                <fo:table-row border="solid" border-width=".5pt">
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            Risk Type Client Id
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Name,Suffix]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Primary Address &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Corresponding phone#]]></xsl:text>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Specialty &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Class &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Eff/Exp Date ]]></xsl:text>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[County &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Practice State ]]></xsl:text>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Coverage &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Retro Date &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Eff/Exp Date]]></xsl:text>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            Limit of Liability
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Disc/Ded/Surcharge &]]></xsl:text>
                        </fo:block>
                        <fo:block font-weight="bold" text-decoration="underline">
                            <xsl:text disable-output-escaping="yes"><![CDATA[Eff/Exp Date]]></xsl:text>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            Written Premium
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border="solid" border-bottom="none">
                        <fo:block font-weight="bold" text-decoration="underline">
                            Change in Premium
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION2">
        <fo:table font-size="9pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="63pt"/>
            <fo:table-column column-width="105pt"/>
            <fo:table-column column-width="98pt"/>
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="98pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="98pt"/>
            <fo:table-column column-width="62pt"/>
            <fo:table-column column-width="66pt"/>
            <fo:table-body>

                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="RISK_TYPE_2"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="NAME_SUF_ADDRESS_CORR"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="SPECIALTY_CLASS_EFF_EXP"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="COUNTY_PRATICE_STATE"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="COVERAGE_RETRO_EFF_EXP"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="LIMIT_OF_LIABILITY"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding-right="2pt">
                            <fo:block linefeed-treatment="preserve">
                                <xsl:call-template name="zero_width_space">
                                    <xsl:with-param name="data" select="DISC_DED_SUR_EFF_EXP"/>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="right">
                            <xsl:choose>
                                <xsl:when test="WRITTEN_PREMIUM &gt;= 0">
                                    <fo:block>
                                        <xsl:value-of select="format-number(WRITTEN_PREMIUM, '$###,###,###,##0.00')"/>
                                    </fo:block>
                                </xsl:when>
                                <xsl:otherwise>
                                    <fo:block color="#ff6666">
                                        <xsl:value-of select="format-number(0-WRITTEN_PREMIUM, '$###,###,###,##0.00')"/>
                                    </fo:block>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:table-cell>
                        <fo:table-cell text-align="right">
                            <xsl:choose>
                                <xsl:when test="DELTA_AMOUNT &gt;= 0">
                                    <fo:block>
                                            <xsl:value-of select="format-number(DELTA_AMOUNT, '$###,###,###,##0.00')"/>
                                    </fo:block>
                                </xsl:when>
                                <xsl:otherwise>
                                    <fo:block color="#ff6666">
                                        (<xsl:value-of select="format-number(0-DELTA_AMOUNT, '$###,###,###,##0.00')"/>)
                                    </fo:block>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>

                <fo:table-row>
                    <fo:table-cell number-columns-spanned="8">
                        <fo:block>&#160;</fo:block>
                        <fo:block text-align="right" font-weight="bold">
                            Total Policy Premium:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>&#160;</fo:block>
                        <fo:block text-align="right" font-weight="bold">
                            <xsl:for-each select="ROWS/ROW">
                                <xsl:if test="contains(DISC_DED_SUR_EFF_EXP,'Transaction Total')">
                                        <xsl:value-of select="format-number(WRITTEN_PREMIUM, '$###,###,###,##0.00')"/>
                                </xsl:if>
                            </xsl:for-each>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        <fo:block id="totalPage">
        </fo:block>
    </xsl:template>
    <!--xsl template for wrap long string text -->
    <xsl:template name="zero_width_space">
        <xsl:param name="data"/>
        <xsl:variable name="spacechars">
            &#x9;&#xA;
            &#x2000;&#x2001;&#x2002;&#x2003;&#x2004;&#x2005;
            &#x2006;&#x2007;&#x2008;&#x2009;&#x200A;&#x200B;
        </xsl:variable>

        <xsl:if test="string-length($data) &gt; 0">
            <xsl:variable name="c1" select="substring($data, 1, 1)"/>
            <xsl:variable name="c2" select="substring($data, 2, 1)"/>

            <xsl:value-of select="$c1"/>
            <xsl:if test="$c2 != '' and
            not(contains($spacechars, $c1) or
            contains($spacechars, $c2))">
                <xsl:text>&#x200B;</xsl:text>
            </xsl:if>

            <xsl:call-template name="zero_width_space">
                <xsl:with-param name="data" select="substring($data, 2)"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>