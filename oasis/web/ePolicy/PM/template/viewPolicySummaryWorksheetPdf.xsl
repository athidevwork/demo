<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:param name="versionParam" select="'1.0'"/>

    <xsl:template match="REPORT">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simpleA4" page-height="22cm" page-width="29.7cm" margin-top="35pt"
                                       margin-bottom="35pt" margin-left="5pt" margin-right="5pt">
                    <fo:region-body margin-top="35pt" margin-bottom="32pt"/>
                    <fo:region-before extent="35pt"/>
                    <fo:region-after extent="22pt" margin-top="10pt"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table font-size="10pt" table-layout="fixed" border="solid" border-width="0.5pt" width="100%">
                        <fo:table-column column-width="60pt"/>
                        <fo:table-column column-width="56pt"/>
                        <fo:table-column column-width="50pt"/>
                        <fo:table-column column-width="76pt"/>
                        <fo:table-column column-width="28pt"/>
                        <fo:table-column column-width="48pt"/>
                        <fo:table-column column-width="62pt"/>
                        <fo:table-column column-width="54pt"/>
                        <fo:table-column column-width="54pt"/>
                        <fo:table-column column-width="56pt"/>
                        <fo:table-column column-width="56pt"/>
                        <fo:table-column column-width="67pt"/>
			            <fo:table-column column-width="54pt"/>
			            <fo:table-column column-width="54pt"/>
			            <fo:table-column column-width="54pt"/>
                        <fo:table-body>
                            <fo:table-row text-align="center" font-weight="bold">
                                <fo:table-cell padding-top="10pt">
                                    <fo:block>Risk Type</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Risk Name</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Status</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Specialty</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>State</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>County</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Form/</fo:block>
									<fo:block>Discount</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Retro/</fo:block>
									<fo:block>%</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Limit</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Eff Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Exp Date</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Net Prem</fo:block>
                                </fo:table-cell>
								<fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Net Fund</fo:block>
                                </fo:table-cell>
								<fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Tax</fo:block>
                                </fo:table-cell>
								<fo:table-cell border-left="solid" padding-top="10pt">
                                    <fo:block>Fee</fo:block>
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
			<fo:table-column column-width="60pt"/>
            <fo:table-column column-width="56pt"/>
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="76pt"/>
            <fo:table-column column-width="28pt"/>
            <fo:table-column column-width="48pt"/>
            <fo:table-column column-width="62pt"/>
            <fo:table-column column-width="54pt"/>
            <fo:table-column column-width="54pt"/>
            <fo:table-column column-width="56pt"/>
            <fo:table-column column-width="56pt"/>
            <fo:table-column column-width="67pt"/>
			<fo:table-column column-width="54pt"/>
			<fo:table-column column-width="54pt"/>
			<fo:table-column column-width="54pt"/>
			
            <fo:table-body>
                <xsl:for-each select="ROWS/ROW">
                    <fo:table-row border="solid" border-width="0.5pt" height="16pt">
					    <xsl:choose>
                            <xsl:when test="normalize-space(RISK_TYPE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RISK_TYPE"/>
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
                            <xsl:when test="normalize-space(RISK_NAME)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RISK_NAME"/>
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
                            <xsl:when test="normalize-space(RISK_STATUS)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RISK_STATUS"/>
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
                            <xsl:when test="normalize-space(RISK_CLASS)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RISK_CLASS"/>
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
                            <xsl:when test="normalize-space(PRACTICE_STATE_CODE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="PRACTICE_STATE_CODE"/>
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
                            <xsl:when test="normalize-space(RISK_COUNTY)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="RISK_COUNTY"/>
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
                            <xsl:when test="normalize-space(POLICY_FORM_DESC)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="POLICY_FORM_DESC"/>
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
                            <xsl:when test="normalize-space(RETRO_DATE)!=''">
                                <fo:table-cell border="solid" text-align="right" border-bottom="none" padding-top="2pt" padding-left="2pt">
									<fo:block>
										<xsl:value-of select="RETRO_DATE"/>
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
                            <xsl:when test="normalize-space(COVERAGE_LIMIT_CODE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="COVERAGE_LIMIT_CODE"/>
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
                            <xsl:when test="normalize-space(EFFECTIVE_FROM_DATE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="EFFECTIVE_FROM_DATE"/>
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
                            <xsl:when test="normalize-space(EFFECTIVE_TO_DATE)!=''">
                                <fo:table-cell border="solid" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="EFFECTIVE_TO_DATE"/>
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
                            <xsl:when test="normalize-space(NET_PREMIUM)!=''">
                                <fo:table-cell border="solid" text-align="right" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="format-number(NET_PREMIUM, '$###,###,###,##0.00')"/>
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
                            <xsl:when test="normalize-space(NET_FUND)!=''">
                                <fo:table-cell border="solid" text-align="right" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="format-number(NET_FUND, '$###,###,###,##0.00')"/>
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
                            <xsl:when test="normalize-space(TAX)!=''">
                                <fo:table-cell border="solid" text-align="right" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="format-number(TAX, '$###,###,###,##0.00')"/>
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
                            <xsl:when test="normalize-space(FEE)!=''">
                                <fo:table-cell border="solid" text-align="right" border-bottom="none" padding-top="2pt" padding-left="2pt">
                                    <fo:block>
                                        <xsl:value-of select="format-number(FEE, '$###,###,###,##0.00')"/>
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
                    </fo:table-row>
                </xsl:for-each>
            </fo:table-body>
        </fo:table>
        <fo:block id="totalPage"></fo:block>
    </xsl:template>
    <xsl:template match="SECTION2/ROWS/ROW">
        <fo:table font-size="10pt" table-layout="fixed" width="100%">
            <fo:table-column column-width="55pt"/>
            <fo:table-column column-width="130pt"/>
            <fo:table-column column-width="30pt"/>
            <fo:table-column column-width="155pt"/>
            <fo:table-column column-width="35pt"/>
            <fo:table-column column-width="195pt"/>
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