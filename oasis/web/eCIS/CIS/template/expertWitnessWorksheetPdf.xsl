<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="22cm" page-width="29.7cm" margin-top="1cm"
                                       margin-bottom="72pt" margin-left="72pt" margin-right="72pt">
                    <fo:region-body margin-top="3.5cm"/>
                    <fo:region-before extent="4cm"/>
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
                    <fo:block>
                        <xsl:apply-templates select="SECTION3"/>
                    </fo:block>
                    <fo:block>
                        <xsl:apply-templates select="SECTION4"/>
                    </fo:block>
                    <fo:block>
                        <xsl:apply-templates select="SECTION5"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="20pt"/>
            <fo:table-column column-width="20pt"/>
            <fo:table-column column-width="30pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="30pt"/>
            <fo:table-column column-width="190pt"/>

            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="center" number-columns-spanned="11">
                        <fo:block>
                            Medical Liability Insurance Company
                        </fo:block>
                        <fo:block>
                            Expert Witness
                        </fo:block>
                        <fo:block>&#160;</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell border-width="1pt" border-top="solid">
                        <fo:block font-weight="bold">
                            Name
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-width="1pt" border-top="solid">
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/LAST_NAME"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-width="1pt" border-top="solid">
                        <fo:block>
                            ,
                            <xsl:value-of select="ROWS/ROW/FIRST_NAME"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-width="1pt" number-columns-spanned="3" border-top="solid">
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/MIDDLE_NAME"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-width="1pt" number-columns-spanned="2" border-top="solid">
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/ACTIVEEW_B"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell number-columns-spanned="3" border-width="1pt" border-top="solid">
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/ENTITY_NAME"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Risk Cty
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell number-columns-spanned="2">
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/RISK_COUNTY"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            County
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-width="1pt">
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/COUNTY"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Sex
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/GENDER"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            DOB
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/DATE_OF_BIRTH"/>
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Email
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block>
                            <xsl:value-of select="ROWS/ROW/EMAIL_ADDRESS1"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <fo:table-cell text-align="right" number-columns-spanned="8" border-width="1pt"
                                   border-bottom="solid">
                        <fo:block font-weight="bold">
                            TIN/SSN
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="3" border-width="1pt" border-bottom="solid">
                        <fo:block>
                            <xsl:choose>
                                <xsl:when test="normalize-space(FEDERAL_TAX_ID)!=''">
                                    <xsl:variable name="taxSize">
                                        <xsl:value-of select="string-length(ROWS/ROW/FEDERAL_TAX_ID)-7"/>
                                    </xsl:variable>
                                    <xsl:value-of select="substring(ROWS/ROW/FEDERAL_TAX_ID,1,2)"/>
                                    -
                                    <xsl:value-of select="substring(ROWS/ROW/FEDERAL_TAX_ID,$taxSize,7)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:variable name="strSize">
                                        <xsl:value-of select="string-length(ROWS/ROW/SOCIAL_SECURITY_NUMBER)-3"/>
                                    </xsl:variable>
                                    <xsl:value-of select="substring(ROWS/ROW/SOCIAL_SECURITY_NUMBER,1,3)"/>
                                    -
                                    <xsl:value-of select="substring(ROWS/ROW/SOCIAL_SECURITY_NUMBER,4,2)"/>
                                    -
                                    <xsl:value-of select="substring(ROWS/ROW/SOCIAL_SECURITY_NUMBER,$strSize,4)"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION2">
        <fo:block>&#160;</fo:block>
        <fo:table font-size="10pt" table-layout="fixed" width="100%" border="solid" border-width="1pt" padding="3px">
            <fo:table-column column-width="20pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-column column-width="110pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell number-columns-spanned="5">
                        <fo:block></fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Eff From
                        </fo:block>
                    </fo:table-cell>

                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Eff To
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row text-align="left">

                        <fo:table-cell>
                            <fo:block></fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="6">
                            <fo:block font-weight="bold">
                                Address
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row text-align="left">
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="PRIMARY_ADDRESS_B"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                            <fo:block>
                                <xsl:value-of select="ADDRESS_TYPE"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                            <fo:block>
                                <xsl:if test="normalize-space(ADDRESS_LINE1)!=''">
                                    <xsl:value-of select="ADDRESS_LINE1"/>
                                </xsl:if>
                                <xsl:if test="normalize-space(ADDRESS_LINE2)!=''">
                                    ,
                                    <xsl:value-of select="ADDRESS_LINE2"/>
                                </xsl:if>
                                <xsl:if test="normalize-space(ADDRESS_LINE3)!=''">
                                    ,
                                    <xsl:value-of select="ADDRESS_LINE3"/>
                                </xsl:if>
                                <xsl:if test="normalize-space(CITY)!=''">
                                    ,
                                    <xsl:value-of select="CITY"/>
                                </xsl:if>

                                <xsl:choose>
                                    <xsl:when test="USA_ADDRESS_B ='Y'">
                                        <xsl:if test="normalize-space(STATE_CODE)!=''">
                                            ,
                                            <xsl:value-of select="STATE_CODE"/>
                                        </xsl:if>
                                        <xsl:if test="normalize-space(ZIPCODE)!=''">
                                            &#160;
                                            <xsl:value-of select="ZIPCODE"/>
                                        </xsl:if>
                                        <xsl:if test="normalize-space(ZIP_PLUS_FOUR)!=''">
                                            -
                                            <xsl:value-of select="ZIP_PLUS_FOUR"/>
                                        </xsl:if>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:if test="normalize-space(PROVINCE)!=''">
                                            ,
                                            <xsl:value-of select="PROVINCE"/>
                                        </xsl:if>
                                        <xsl:if test="normalize-space(foreign_zip)!=''">
                                            ,
                                            <xsl:value-of select="foreign_zip"/>
                                        </xsl:if>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EFFECTIVE_FROM_DATE"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EFFECTIVE_TO_DATE"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row text-align="left">
                        <fo:table-cell>
                            <fo:block></fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="6">
                            <fo:block font-weight="bold">
                                Phone
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row text-align="left">
                        <fo:table-cell>
                            <fo:block></fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="PHONE_NUMBER_TYPE_DESC"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="5">
                            <fo:block>
                                <xsl:if test="normalize-space(AREA_CODE)!=''">
                                    <xsl:value-of select="AREA_CODE"/>
                                    -
                                </xsl:if>
                                <xsl:if test="normalize-space(PHONE_NUMBER)!=''">
                                    <xsl:value-of select="PHONE_NUMBER"/>
                                </xsl:if>
                                <xsl:if test="normalize-space(PHONE_EXTENSION)!=''">
                                    &#160;
                                    <xsl:value-of select="PHONE_EXTENSION"/>
                                </xsl:if>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION3">
        <fo:block>&#160;</fo:block>
        <fo:table font-size="10pt" table-layout="fixed" width="100%" border="solid" border-width="1pt" padding="3px">
            <fo:table-column column-width="300pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="240pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Education
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Year
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Title
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="INSTITUTION_NAME"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="GRADUATION_YEAR"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EDUCATION_DEGREE_CODE"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION4">
        <fo:block>&#160;</fo:block>
        <fo:table font-size="10pt" table-layout="fixed" width="100%" border="solid" border-width="1pt" padding="3px">
            <fo:table-column column-width="200pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-body>
                <fo:table-row border="solid">
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Classification
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            Specialty
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell number-columns-spanned="3">
                        <fo:block font-weight="bold">
                            Sub specialty
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row border="solid">
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="ENTITY_CLASS_CODE_DESC"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="SUB_CLASS_DESC"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="SUB_TYPE_DESC"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EFFECTIVE_FROM_DATE"/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EFFECTIVE_TO_DATE"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:template match="SECTION5">
        <fo:block>&#160;</fo:block>
        <fo:table font-size="10pt" table-layout="fixed" width="100%" border="solid" border-width="1pt" padding="3px">
            <fo:table-column column-width="20pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="170pt"/>
            <fo:table-column column-width="150pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-column column-width="100pt"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block></fo:block>
                    </fo:table-cell>

                    <fo:table-cell number-columns-spanned="5">
                        <fo:block font-weight="bold">
                            Relation
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="PRIMARY_AFFILIATION_B"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="RELATION_TYPE_CODE_DESC"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block font-weight="bold">
                                <xsl:value-of select="NAME"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:if test="normalize-space(REVERSE_RELATION_INDICATOR)='REVERSE RELATION'">
                                    <fo:external-graphic src="url(images/repeat.gif)"/>
                                </xsl:if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EFFECTIVE_FROM_DATE"/>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block>
                                <xsl:value-of select="EFFECTIVE_TO_DATE"/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
    </xsl:template>

</xsl:stylesheet>