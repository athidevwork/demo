<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="26cm" margin-top="50pt"
                                       margin-bottom="72pt" margin-left="72pt" margin-right="72pt">
                    <fo:region-body margin-top="10pt" margin-bottom="30pt"/>
                    <fo:region-after extent="20pt"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4">
                <fo:static-content flow-name="xsl-region-after">

                                     <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="77.5pt"/>
            <fo:table-column column-width="77.5pt"/>
            <fo:table-column column-width="27.5pt"/>
            <fo:table-column column-width="77.5pt"/>
            <fo:table-column column-width="37.5pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="37.5pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="77pt"/>
            <fo:table-column column-width="30pt"/>
            <fo:table-column column-width="40pt"/>

            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="left" number-columns-spanned="3">
                        <fo:block font-weight="bold">
                            Total Active Members: <xsl:value-of select="SECTION2/ROWS/ROW/TOTALACTIVEMEMBERS"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" number-columns-spanned="3">
                        <fo:block font-weight="bold">
                            No. Of Company Insured: <xsl:value-of select="SECTION2/ROWS/ROW/NOOFCOMPANYINSURED"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right" number-columns-spanned="3">
                        <fo:block font-weight="bold">
                            % Of Company Insured: <xsl:value-of select="SECTION2/ROWS/ROW/PERCENTAGEOFCOMPANYINSURED"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

            </fo:table-body>
        </fo:table>

                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:apply-templates select="SECTION1/ROWS"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1/ROWS">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-column column-width="75pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Full Name
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Relation
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Policy No
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Limit
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Status
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Eff. From Date
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Eff. To Date
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="center" border-style="solid" border-width=".5pt">
                        <fo:block font-weight="bold">
                            Reason
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <xsl:for-each select="ROW">
                    <fo:table-row>
                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="FULLNAME"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="RELATIONTEXT"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="EXTERNALID"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="LIMIT"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="STATUS"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="EFFECTIVEFROMDATE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="EFFECTIVETODATE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-style="solid" border-width=".5pt">
                            <fo:block>
                                <xsl:value-of select="REASON"/>
                            </fo:block>
                        </fo:table-cell>

                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
    </xsl:template>

</xsl:stylesheet>
