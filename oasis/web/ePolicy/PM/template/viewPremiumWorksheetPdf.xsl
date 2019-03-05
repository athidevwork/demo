<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="22cm" page-width="29.7cm" margin-top="35pt"
                                       margin-bottom="60pt" margin-left="72pt" margin-right="72pt">
                    <fo:region-body margin-top="35pt" margin-bottom="32pt"/>
                    <fo:region-before extent="35pt"/>
                    <fo:region-after extent="22pt" margin-top="10pt"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table font-size="10pt" table-layout="fixed" border="solid" border-width="0.5pt" width="100%">
                        <fo:table-column column-width="63pt"/>
                        <fo:table-column column-width="63pt"/>
                        <fo:table-column column-width="54pt"/>
                        <fo:table-column column-width="55pt"/>
                        <fo:table-column column-width="72pt"/>
                        <fo:table-column column-width="62pt"/>
                        <fo:table-column column-width="59pt"/>
                        <fo:table-column column-width="54pt"/>
                        <fo:table-column column-width="36pt"/>
                        <fo:table-column column-width="73pt"/>
                        <fo:table-column column-width="56pt"/>
                        <fo:table-column column-width="56pt"/>
                        <fo:table-body>
                            <fo:table-row text-align="center" font-weight="bold">
                                <fo:table-cell padding-top="10pt">
                                    <fo:block>Transaction</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Transaction Code</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="6pt">
                                    <fo:block>Effective Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="6pt">
                                    <fo:block>Accounting Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Risk</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Specialty</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Coverage</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="6pt">
                                    <fo:block>Retro Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" border-bottom="solid" padding-top="6pt">
                                    <fo:block>Cancel Method</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid">
                                    <fo:block>Discount/</fo:block>
                                    <fo:block>Deductible/</fo:block>
                                    <fo:block>Surcharge</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="6pt">
                                    <fo:block>Written Premium</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="6pt">
                                    <fo:block>Change in Premium</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block>
                        <xsl:apply-templates select="SECTION2/ROWS/ROW"/>
                    </fo:block>
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
        <fo:table font-size="10pt" table-layout="fixed" border="solid" border-width="0.5pt" width="100%">
            <fo:table-column column-width="63pt"/>
            <fo:table-column column-width="63pt"/>
            <fo:table-column column-width="54pt"/>
            <fo:table-column column-width="55pt"/>
            <fo:table-column column-width="72pt"/>
            <fo:table-column column-width="62pt"/>
            <fo:table-column column-width="59pt"/>
            <fo:table-column column-width="54pt"/>
            <fo:table-column column-width="36pt"/>
            <fo:table-column column-width="73pt"/>
            <fo:table-column column-width="56pt"/>
            <fo:table-column column-width="56pt"/>
            <fo:table-body>
                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row border="solid" border-width="0.5pt" height="16pt">
                        <xsl:choose>
                            <xsl:when test="normalize-space(TRANSACTION_TYPE_DESC)!='' and position()=1">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="TRANSACTION_TYPE_DESC"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <fo:table-cell border-left="solid">
                                    <fo:block>&#160;</fo:block>
                                </fo:table-cell>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(TRANSACTION_CODE_DESC)!='' and position()=1">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="TRANSACTION_CODE_DESC"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <fo:table-cell border-left="solid">
                                    <fo:block>&#160;</fo:block>
                                </fo:table-cell>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(EFFECTIVE_FROM_DATE)!='' and position()=1">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="EFFECTIVE_FROM_DATE"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <fo:table-cell border-left="solid">
                                    <fo:block>&#160;</fo:block>
                                </fo:table-cell>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(ACCOUNTING_DATE)!='' and position()=1">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="ACCOUNTING_DATE"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <fo:table-cell border-left="solid">
                                    <fo:block>&#160;</fo:block>
                                </fo:table-cell>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(RISK_CODE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RISK_CODE"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="position()=last()">
                                        <fo:table-cell border="solid" border-right="none">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:table-cell border-left="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(SPECIALTY)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="SPECIALTY"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="position()=last()">
                                        <fo:table-cell border="solid" border-right="none">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:table-cell border-left="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(COVERAGE_CODE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="COVERAGE_CODE"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="normalize-space(COMPONENT_CODE)='Risk Total' or position()=last()">
                                        <fo:table-cell border-left="solid" border-top="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:table-cell border-left="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(RETRO_DATE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RETRO_DATE"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="normalize-space(COMPONENT_CODE)='Risk Total' or position()=last()">
                                        <fo:table-cell border-left="solid" border-top="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:table-cell border-left="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                            <xsl:when test="normalize-space(CANCEL_METHOD_CODE)!=''">
                                <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="CANCEL_METHOD_CODE"/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="position()=last()">
                                        <fo:table-cell border-left="solid" border-bottom="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <fo:table-cell border-left="solid">
                                            <fo:block>&#160;</fo:block>
                                        </fo:table-cell>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                        <fo:table-cell border="solid" padding-top="2pt" padding-left="2pt">
                            <fo:block>
                                <xsl:value-of select="COMPONENT_CODE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="solid" text-align="right" padding-top="2pt" padding-left="2pt">
                            <xsl:choose>
                                <xsl:when test="WRITTEN_PREMIUM >= 0">
                                    <fo:block>
                                        <xsl:value-of select="format-number(WRITTEN_PREMIUM, '$###,###,###,##0.00')"/>
                                    </fo:block>
                                </xsl:when>
                                <xsl:otherwise>
                                    <fo:block color="#ff6666" font-weight="bold">
                                        (<xsl:value-of select="format-number(0-WRITTEN_PREMIUM, '$###,###,###,##0.00')"/>)
                                    </fo:block>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:table-cell>
                        <fo:table-cell border="solid" text-align="right" padding-top="2pt" padding-left="2pt">
                            <xsl:choose>
                                <xsl:when test="DELTA_AMOUNT >= 0">
                                    <fo:block>
                                        <xsl:value-of select="format-number(DELTA_AMOUNT, '$###,###,###,##0.00')"/>
                                    </fo:block>
                                </xsl:when>
                                <xsl:otherwise>
                                    <fo:block color="#ff6666" font-weight="bold">
                                        (<xsl:value-of select="format-number(0-DELTA_AMOUNT, '$###,###,###,##0.00')"/>)
                                    </fo:block>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
        <fo:block id="totalPage"></fo:block>
    </xsl:template>
    <xsl:template match="SECTION2/ROWS/ROW">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="55pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="30pt"/>
            <fo:table-column column-width="145pt"/>
            <fo:table-column column-width="35pt"/>
            <fo:table-column column-width="185pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell font-weight="bold">
                        <fo:block>Policy No:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="POLICY_NO"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell font-weight="bold">
                        <fo:block>User:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="USER_NAME"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell font-weight="bold">
                        <fo:block>Term:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="TERM"/>
                        </fo:block>
                    </fo:table-cell>
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
    </xsl:template>
</xsl:stylesheet>