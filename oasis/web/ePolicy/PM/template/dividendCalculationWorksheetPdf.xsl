<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="22cm" page-width="29.7cm" margin-top="30pt"
                                       margin-bottom="60pt" margin-left="30pt" margin-right="30pt">
                    <fo:region-body margin-top="50pt" margin-bottom="0pt"/>
                    <fo:region-before extent="50pt"/>
                    <fo:region-after extent="80pt"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4">
                <fo:static-content flow-name="xsl-region-before">
                    <xsl:apply-templates select="SECTION1/ROWS/ROW"/>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <xsl:apply-templates select="SECTION3/ROWS/ROW"/>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block text-align="center">
                        <xsl:apply-templates select="SECTION2/ROWS"/>
                    </fo:block>
                    <fo:block id="last-page"/>
                </fo:flow>                        
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1/ROWS/ROW">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="160pt"/>
            <fo:table-column column-width="460pt"/>
            <fo:table-column column-width="160pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block>
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            <xsl:value-of select="TITLE1"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Run Date: <xsl:value-of select="RUNDATE"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block>
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            Dividend Calculation Report
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Run Time: <xsl:value-of select="RUNTIME"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block>
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Page
                            <fo:page-number/>
                            of
                            <fo:page-number-citation ref-id="last-page"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION2/ROWS">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Name
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Policy No
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Status
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Effective From
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Effective To
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Written Premium
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Dividend Base
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Dividend
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="left" border-width=".5pt" number-columns-spanned="8">
                        <fo:block font-weight="bold">
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <xsl:for-each select="ROW">
                    <fo:table-row>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="RISKNAME"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="POLICYNO"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="POLICYSTATE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="EFFFROM"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="EFFTO"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                $<xsl:value-of select="format-number(WRITTEN, '###,###,###,###')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                $<xsl:value-of select="format-number(DIVBASE, '###,###,###,###')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell text-align="left" border-width=".5pt">
                            <fo:block>
                                $<xsl:value-of select="format-number(DIV, '###,###,###,###')"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
                <fo:table-row>
                    <fo:table-cell text-align="left" border-width=".5pt" number-columns-spanned="8">
                        <fo:block font-weight="bold">
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Total # Calculated: <xsl:value-of select="count(ROW)"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Total Amounts:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            $<xsl:value-of select="format-number(sum(ROW/WRITTEN), '###,###,###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            $<xsl:value-of select="format-number(sum(ROW/DIVBASE), '###,###,###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" border-width=".5pt">
                        <fo:block font-weight="bold">
                            $<xsl:value-of select="format-number(sum(ROW/DIV), '###,###,###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION3/ROWS/ROW">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="780pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            Report Input Parameters
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            Declaration Date: <xsl:value-of select="DIVIDENDDATE"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            Transaction Acct. Date: <xsl:value-of select="TRANSACCOUNTINGDATE"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            Dividend Percentage: <xsl:value-of select="DIVIDENDPERCENT"/>%
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block font-weight="bold">
                            Policy Type: <xsl:value-of select="POLICYTYPE"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>
</xsl:stylesheet>