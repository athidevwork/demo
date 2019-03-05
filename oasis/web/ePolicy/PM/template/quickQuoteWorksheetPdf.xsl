<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="21cm" page-width="29.7cm" margin-top="72pt"
                                       margin-bottom="72pt" margin-left="72pt" margin-right="72pt">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:apply-templates select="SECTION1"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="180pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="60pt"/>

            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="center" number-columns-spanned="8">
                        <fo:block font-weight="bold">
                            Duplicate Entity Report
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <fo:table-cell number-columns-spanned="8">
                        <fo:block>&#160;</fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <fo:table-cell number-columns-spanned="4">
                        <fo:block>
                            Run Date:
                            <xsl:for-each select="ROWS/ROW">
                                <xsl:if test="position() = 1">
                                    <xsl:value-of select="TODAY"/>
                                </xsl:if>
                            </xsl:for-each>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="right" number-columns-spanned="4">
                        <fo:block>
                            Page
                            <fo:page-number/>
                            of
                            <fo:page-number-citation ref-id="totalPage"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <fo:table-cell number-columns-spanned="8">
                        <fo:block>&#160;</fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row text-align="center" border="solid" border-width="0.5pt" height="30px">
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">Roster Id</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">Last Name</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">First Name</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">Middle Name</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">Address Line1</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">City</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">State Code</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border="solid">
                        <fo:block font-weight="bold">Zipcode</fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row text-align="center" height="20px">
                        <fo:table-cell border-left="solid" border-width="0.5pt">
                            <fo:block>
                                <xsl:value-of select="ENTITY_UNIQUE_ID"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="LAST_NAME"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="FIRST_NAME"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="MIDDLE_NAME"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="ADDRESS_LINE1"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="CITY"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="STATE_CODE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right="solid" border-width="0.5pt">
                            <fo:block>
                                <xsl:value-of select="ZIPCODE"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>

                <fo:table-row height="20px">
                    <fo:table-cell border="solid" border-width="0.5pt" text-align="left" number-columns-spanned="8">
                        <fo:block>&#160;&#160;Count:&#160;
                            <xsl:value-of disable-output-escaping="yes" select="count(//ENTITY_UNIQUE_ID)"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        <fo:block id="totalPage"></fo:block>
    </xsl:template>

</xsl:stylesheet>