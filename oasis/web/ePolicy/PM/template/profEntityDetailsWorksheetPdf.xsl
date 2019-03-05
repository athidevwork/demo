<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="22cm" page-width="29.7cm" margin-top="20pt"
                                       margin-bottom="60pt" margin-left="72pt" margin-right="72pt">
                    <fo:region-body margin-top="20pt" margin-bottom="20pt"/>
                    <fo:region-before extent="20pt" margin-bottom="10pt"/>
                    <fo:region-after extent="20pt" margin-bottom="10pt"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
                <fo:static-content flow-name="xsl-region-after">
                    <fo:table font-size="10pt" table-layout="fixed" width="100%" padding="2pt">
                        <fo:table-column column-width="700pt"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="right">
                                    <fo:block>
                                        Page
                                        <fo:page-number/>
                                        of
                                        <fo:page-number-citation ref-id="totalPage"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table font-size="10pt" table-layout="fixed" width="100%" padding="2pt">
                        <fo:table-column column-width="80pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="110pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="60pt"/>
                        <fo:table-column column-width="60pt"/>
                        <fo:table-column column-width="60pt"/>
                        <fo:table-column column-width="60pt"/>
                        <fo:table-body>
                            <fo:table-row text-align="center" font-weight="bold" height="20pt">
                                <fo:table-cell>
                                    <fo:block>Tran Pol#/</fo:block>
                                    <fo:block>Policy#</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Eff Date/</fo:block>
                                    <fo:block>Rel Type</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Acct Date/</fo:block>
                                    <fo:block>Risk Name</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Trans Type/</fo:block>
                                    <fo:block>Eff Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Trans Code/</fo:block>
                                    <fo:block>Exp Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Member</fo:block>
                                    <fo:block>Prem</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Member</fo:block>
                                    <fo:block>Delta</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Entity</fo:block>
                                    <fo:block>Adj Prem</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Entity</fo:block>
                                    <fo:block>Adj Delta</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:apply-templates select="SECTION1"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1">
        <fo:table font-size="10pt" table-layout="fixed" border="solid" border-width="0.5pt" width="100%" padding="2pt">
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-body>
                <xsl:for-each select="ROWS/ROW">
                    <xsl:if test="ORD != 9999">
                        <!-- Transactions -->
                        <fo:table-row height="16pt">
                            <fo:table-cell padding-top="2pt" padding-left="2pt">
                                <fo:block>
                                    <xsl:value-of select="POLICY_NO"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt">
                                <fo:block>
                                    <xsl:value-of select="TRANS_EFFECTIVE_FROM_DATE"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt">
                                <fo:block>
                                    <xsl:value-of select="TRANS_ACCOUNTING_DATE"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt">
                                <fo:block>
                                    <xsl:value-of select="TRANSACTION_TYPE"/>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt">
                                <fo:block>
                                <xsl:choose>
                                    <xsl:when test="ORD=3 or ORD=4">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="TRANSACTION_CODE"/>
                                        </fo:block>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:block>
                                            <xsl:value-of select="TRANSACTION_CODE"/>
                                        </fo:block>
                                    </xsl:otherwise>
                                </xsl:choose>
                                </fo:block>
                            </fo:table-cell>
                            <xsl:decimal-format NaN=""/>
                            <fo:table-cell padding-top="2pt" padding-left="2pt" text-align="right">
                                <fo:block>
                                <xsl:if test="normalize-space(MEMBER_PREMIUM) !=''">
                                <xsl:choose>
                                    <xsl:when test="normalize-space(MEMBER_PREMIUM) !='' and MEMBER_PREMIUM >= 0">
                                        <fo:block>
                                            <xsl:value-of select="format-number(MEMBER_PREMIUM, '$###,###,###,##0.00')"/>
                                        </fo:block>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:block color="red">
                                            (<xsl:value-of select="format-number(0-MEMBER_PREMIUM, '$###,###,###,##0.00')"/>)
                                        </fo:block>
                                    </xsl:otherwise>
                                </xsl:choose>
                                </xsl:if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt" text-align="right">
                                <fo:block>
                                <xsl:if test="normalize-space(MEMBER_DELTA) !=''">
                                <xsl:choose>
                                    <xsl:when test="MEMBER_DELTA >= 0">
                                        <fo:block>
                                            <xsl:value-of select="format-number(MEMBER_DELTA, '$###,###,###,##0.00')"/>
                                        </fo:block>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:block color="red">
                                            (<xsl:value-of select="format-number(0-MEMBER_DELTA, '$###,###,###,##0.00')"/>)
                                        </fo:block>
                                    </xsl:otherwise>
                                </xsl:choose>
                                </xsl:if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt" text-align="right">
                                <fo:block>
                                <xsl:if test="normalize-space(ENTITY_ADJ_PREMIUM) !=''">
                                <xsl:choose>
                                    <xsl:when test="ENTITY_ADJ_PREMIUM >= 0">
                                        <fo:block>
                                            <xsl:value-of select="format-number(ENTITY_ADJ_PREMIUM, '$###,###,###,##0.00')"/>
                                        </fo:block>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:block color="red">
                                            (<xsl:value-of select="format-number(0-ENTITY_ADJ_PREMIUM, '$###,###,###,##0.00')"/>)
                                        </fo:block>
                                    </xsl:otherwise>
                                </xsl:choose>
                                </xsl:if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding-top="2pt" padding-left="2pt" text-align="right">
                                <fo:block>
                                <xsl:if test="normalize-space(ENTITY_ADJ_DELTA) !=''">
                                <xsl:choose>
                                    <xsl:when test="ENTITY_ADJ_DELTA >= 0">
                                        <fo:block>
                                            <xsl:value-of select="format-number(ENTITY_ADJ_DELTA, '$###,###,###,##0.00')"/>
                                        </fo:block>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:block color="red">
                                            (<xsl:value-of select="format-number(0-ENTITY_ADJ_DELTA, '$###,###,###,##0.00')"/>)
                                        </fo:block>
                                    </xsl:otherwise>
                                </xsl:choose>
                                </xsl:if>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </xsl:if>
                    <xsl:if test="ORD=2 and ORIGINAL_TRANSACTION_LOG_PK > 0">
                        <!-- Call tmp_detail to display Details -->
                        <xsl:call-template name="tmp_detail">
                            <xsl:with-param name="transactionLogId" select="ORIGINAL_TRANSACTION_LOG_PK"/>
                        </xsl:call-template>
                    </xsl:if>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
        <fo:block id="totalPage"></fo:block>
    </xsl:template>
    <xsl:template name="tmp_detail">
        <xsl:param name="transactionLogId"/>
        <xsl:variable name="detailCount"
                      select="count(//SECTION1/ROWS/ROW[TRANSACTION_LOG_PK=$transactionLogId and normalize-space(POLICY_NO) = 'DETAIL'])"/>
        <xsl:if test="$detailCount > 0">
            <fo:table-row>
                <fo:table-cell number-columns-spanned="9">
                    <fo:table font-size="9pt" table-layout="fixed" width="100%">
                        <fo:table-column column-width="200pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="90pt"/>
                        <fo:table-column column-width="65pt"/>
                        <fo:table-column column-width="65pt"/>
                        <fo:table-column column-width="10pt"/>
                        <fo:table-body>
                            <!-- Detail title -->
                            <fo:table-row text-align="center" border-width="0.5pt" font-weight="bold" height="14pt">
                                <fo:table-cell>
                                    <fo:block>&#160;</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" padding-top="2pt">
                                    <fo:block>Change Desc</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" padding-top="2pt">
                                    <fo:block>Risk</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" padding-top="2pt">
                                    <fo:block>Coverage</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" padding-top="2pt">
                                    <fo:block>Component</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" padding-top="2pt">
                                    <fo:block>From</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" padding-top="2pt">
                                    <fo:block>To</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>&#160;</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:for-each select="//SECTION1/ROWS/ROW">
                                <xsl:if test="$transactionLogId = TRANSACTION_LOG_PK and normalize-space(POLICY_NO) = 'DETAIL'">
                                    <fo:table-row height="14pt" border-width="0.5pt">
                                        <fo:table-cell>
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                            <fo:block>
                                                <xsl:value-of select="TRANS_EFFECTIVE_FROM_DATE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                            <fo:block>
                                                <xsl:value-of select="TRANS_ACCOUNTING_DATE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                            <fo:block>
                                                <xsl:value-of select="TRANSACTION_TYPE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                            <fo:block>
                                                <xsl:value-of select="TRANSACTION_CODE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                            <fo:block>
                                                <xsl:value-of select="TERM_EFFECTIVE_DATE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                            <fo:block>
                                                <xsl:value-of select="TERM_EXPIRATION_DATE"/>
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell>
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:if>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                </fo:table-cell>
            </fo:table-row>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>