<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="30pt"
                                       margin-bottom="30pt"
                                       margin-left="30pt" margin-right="30pt">
                    <fo:region-body margin-top="145pt" margin-bottom="30pt"/>
                    <fo:region-before extent="150pt"/>
                    <fo:region-after extent="20pt"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block font-size="16pt" font-weight="bold" text-align="center">
                        ERP Claim Summary Report
                    </fo:block>
                    <fo:block font-size="16pt" font-weight="bold" text-align="center">
                        &#160;
                    </fo:block>
                    <fo:block text-align="center">
                        <xsl:apply-templates select="SECTION1/ROWS/ROW"/>
                    </fo:block>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <xsl:apply-templates select="SECTION3/ROWS"/>
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
            <fo:table-column column-width="260pt"/>
            <fo:table-column column-width="260pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Renewal Year:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left">
                        <fo:block>
                            <xsl:value-of select="RENEWALYEAR"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Report Date:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left">
                        <fo:block>
                            <xsl:value-of select="PROCESSDATE"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block>
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Insured Name:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" number-columns-spanned="5">
                        <fo:block>
                            <xsl:value-of select="INSDNAME"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            License No:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left">
                        <fo:block>
                            <xsl:value-of select="LICENSENO"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right" number-columns-spanned="2">
                        <fo:block font-weight="bold">
                            Total Individual PMSLIC Points:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block>
                            <xsl:value-of select="TOTALCURRENTPOINTS"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="right">
                        <fo:block font-weight="bold">
                            Policy No:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left">
                        <fo:block>
                            <xsl:value-of select="POLICYNO"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right" number-columns-spanned="2">
                        <fo:block font-weight="bold">
                            Total Individual Prior Points:
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block>
                            <xsl:value-of select="TOTALPRIORPOINTS"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell number-columns-spanned="6">
                        <fo:block>
                            &#160;
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell text-align="center" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Claim No
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-width=".5pt" number-columns-spanned="2">
                        <fo:block font-weight="bold">
                            Patient Name
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Incident Date
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Report Date
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Closed Date
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell number-columns-spanned="6" text-align="center">
                        <fo:block>
                            <fo:leader leader-pattern="rule" leader-length="520pt"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION2/ROWS">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-body>
                <xsl:choose>
                <xsl:when test="ROW">
                <xsl:for-each select="ROW">
                    <xsl:variable name="bgColor">
                        <xsl:choose>
                            <xsl:when test="position() mod 2 = 0">
                                <xsl:text>rgb(242,242,242)</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>white</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <fo:table-row>
                        <xsl:attribute name="background-color">
                            <xsl:value-of select="$bgColor"/>
                        </xsl:attribute>
                        <fo:table-cell border-width=".5pt">
                            <fo:block font-weight="bold">
                                <fo:inline font-style="italic">
                                    <xsl:value-of select="CLAIMNO"/>
                                </fo:inline>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" number-columns-spanned="2">
                            <fo:block>
                                <xsl:value-of select="CLMNTNAME"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="LOSSDATE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="REPORTDATE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="CLOSEDDATE"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <xsl:attribute name="background-color">
                            <xsl:value-of select="$bgColor"/>
                        </xsl:attribute>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Claim Status:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="left">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="CLAIMSTATUS"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Points:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="left">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="CLAIMPOINTS"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <xsl:attribute name="background-color">
                            <xsl:value-of select="$bgColor"/>
                        </xsl:attribute>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Indemnity Reserve:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                <xsl:value-of
                                        select="format-number(INDEMRES, '$###,###,###,##0.00')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Expense Reserve:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                <xsl:value-of select="format-number(EXPRES, '$###,###,###,##0.00')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <xsl:attribute name="background-color">
                            <xsl:value-of select="$bgColor"/>
                        </xsl:attribute>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Indemnity Paid:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block font-weight="bold">
                                <xsl:value-of select="format-number(INDEMPAID, '$###,###,###,##0.00')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Expense Paid:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                <xsl:value-of select="format-number(EXPPAID, '$###,###,###,##0.00')"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <xsl:attribute name="background-color">
                            <xsl:value-of select="$bgColor"/>
                        </xsl:attribute>
                        <fo:table-cell border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-width=".5pt" text-align="right">
                            <fo:block>
                                Allegation:
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell number-columns-spanned="4" border-width=".5pt"
                                       text-align="left">
                            <fo:block linefeed-treatment="preserve"
                                      white-space-collapse="false"
                                      white-space-treatment="preserve">
                                <xsl:value-of select="ALLEGATION"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="6" border-width=".5pt">
                            <fo:block>
                                &#160;
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:otherwise>
                </xsl:choose>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION3/ROWS">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="120pt"/>
            <fo:table-column column-width="120pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="left">
                        <fo:block font-weight="bold">
                            <xsl:value-of select="ROW/TODAY"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right">
                        <fo:block font-size="12pt" text-align="right">
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
</xsl:stylesheet>