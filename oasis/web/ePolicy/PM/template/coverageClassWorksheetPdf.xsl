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
		    <fo:region-body margin-top="1cm" margin-left="3cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:apply-templates select="SECTION1/ROWS"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1/ROWS">
	<fo:block>&#160;</fo:block>
	<fo:table font-size="10pt" table-layout="fixed" width="100%">
	    <fo:table-column column-width="300pt"/>
	    <fo:table-column column-width="80pt"/>
	    <fo:table-column column-width="80pt"/>
	    <fo:table-column column-width="80pt"/>
	    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell text-align="center" number-columns-spanned="4" height="30px">
		<fo:block font-weight="bold">
		    Coverage Class Worksheet
		</fo:block>
		<fo:block>&#160;</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row text-align="center" border="solid" border-width=".5pt" height="20px">
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Out-Patient Activity
			</fo:block>
		    </fo:table-cell>
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Number of Visits
			</fo:block>
		    </fo:table-cell>
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    EQV Factor(Divisor)
			</fo:block>
		    </fo:table-cell>
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			   Equivalent Beds
			</fo:block>
		    </fo:table-cell>
	      </fo:table-row>
	      <xsl:for-each select="ROW[SECTION_TYPE='A']">
                <fo:table-row border="solid" border-width=".5pt" text-align="right">
		    <xsl:choose>
			<xsl:when test="COVERAGE_CLASS_CODE = 'TOPA'">
			    <fo:table-cell text-align="left" border-left="solid" border-bottom="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-top="solid" border-bottom="solid">
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			   <fo:table-cell border-bottom="solid">
				<fo:block>
				<xsl:if test="FACTOR > 0">
				   <xsl:value-of select="format-number(FACTOR, '###,###,###')"/>
				</xsl:if>
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-top="solid" border-right="solid" border-bottom="solid">
				<fo:block>
				  <xsl:if test="EQUIVALENT_BEDS > 0">
				   <xsl:value-of select="format-number(EQUIVALENT_BEDS,'###,###,##0.00')"/>
				  </xsl:if>
				</fo:block>
			    </fo:table-cell>
			</xsl:when>
			<xsl:otherwise>
			   <fo:table-cell text-align="left" border-left="solid" >
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			      <fo:block>
				<xsl:if test="FACTOR > 0">
				  <xsl:value-of select="format-number(FACTOR, '###,###,###')"/>
				</xsl:if>
			      </fo:block>
			    </fo:table-cell>
			     <fo:table-cell border-right="solid">
				<fo:block>
				  <xsl:if test="EQUIVALENT_BEDS > 0">
				    <xsl:value-of select="format-number(EQUIVALENT_BEDS, '###,###,###,##0.00')"/>
				  </xsl:if>
				</fo:block>
			    </fo:table-cell>
			</xsl:otherwise>
		    </xsl:choose>
	        </fo:table-row>
	      </xsl:for-each>

	     <fo:table-row height="20px">
		    <fo:table-cell>
			<fo:block>&#160;</fo:block>
		    </fo:table-cell>
	      </fo:table-row>

	       <fo:table-row text-align="center" border="solid" border-width=".5pt" height="20px">
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Bed Category
			</fo:block>
		    </fo:table-cell>
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Number of Beds
			</fo:block>
		    </fo:table-cell>
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Patient Days of Care
			</fo:block>
		    </fo:table-cell>
		    <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			   %Occupancy
			</fo:block>
		    </fo:table-cell>
	      </fo:table-row>

	      <xsl:for-each select="ROW[SECTION_TYPE='B']">
                <fo:table-row text-align="right">
		     <xsl:choose>
			  <xsl:when test="COVERAGE_CLASS_CODE = 'TOA'">
			     <fo:table-cell text-align="left" border-left="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block border-top="solid">
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block border-top="solid">
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_PATIENT_DAY > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_PATIENT_DAY, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-right="solid">
			       <fo:block border-top="solid">
			         <xsl:choose>
				   <xsl:when test="OCCUPANCY > 0">
				     <xsl:value-of select="format-number(OCCUPANCY, '###,###,###,##0.00%')"/>
				   </xsl:when>
				   <xsl:otherwise> 0.00% </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			  </xsl:when>
			  <xsl:otherwise>
			     <fo:table-cell text-align="left" border-left="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_PATIENT_DAY > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_PATIENT_DAY, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-right="solid">
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="OCCUPANCY > 0">
				     <xsl:value-of select="format-number(OCCUPANCY, '###,###,###,##0.00%')"/>
				   </xsl:when>
				   <xsl:otherwise> 0.00%</xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			  </xsl:otherwise>
		      </xsl:choose>
	        </fo:table-row>
	      </xsl:for-each>

	     <fo:table-row>
		    <fo:table-cell border-left="solid" border-right="solid" number-columns-spanned="4">
			<fo:block>&#160;</fo:block>
		    </fo:table-cell>
	      </fo:table-row>

	      <xsl:for-each select="ROW[SECTION_TYPE='C']">
                <fo:table-row text-align="right">
		     <xsl:choose>
			  <xsl:when test="COVERAGE_CLASS_CODE = 'TB'">
			    <fo:table-cell text-align="left" border-left="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block border-top="solid">
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block border-top="solid">
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_PATIENT_DAY > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_PATIENT_DAY, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-right="solid">
			       <fo:block border-top="solid">
			         <xsl:choose>
				   <xsl:when test="OCCUPANCY > 0">
				     <xsl:value-of select="format-number(OCCUPANCY, '###,###,###,##0.00%')"/>
				   </xsl:when>
				   <xsl:otherwise> 0.00% </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			  </xsl:when>
			  <xsl:otherwise>
			     <fo:table-cell text-align="left" border-left="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_PATIENT_DAY > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_PATIENT_DAY, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-right="solid">
			       <fo:block>
			         <xsl:choose>
				   <xsl:when test="OCCUPANCY > 0">
				     <xsl:value-of select="format-number(OCCUPANCY, '###,###,###,##0.00%')"/>
				   </xsl:when>
				   <xsl:otherwise> 0.00% </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			  </xsl:otherwise>
			</xsl:choose>
	        </fo:table-row>
	      </xsl:for-each>
	      <fo:table-row>
		    <fo:table-cell border-left="solid" border-right="solid" number-columns-spanned="4">
			<fo:block>&#160;</fo:block>
		    </fo:table-cell>
	      </fo:table-row>
	       <xsl:for-each select="ROW[SECTION_TYPE='D']">
                <fo:table-row text-align="right">
		     <xsl:choose>
			  <xsl:when test="COVERAGE_CLASS_CODE = 'TEB'">
			    <fo:table-cell text-align="left" border-left="solid" border-bottom="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-top="solid" border-bottom="solid">
				<fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-bottom="solid">
				<fo:block></fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-right="solid" border-bottom="solid">
				<fo:block></fo:block>
			    </fo:table-cell>
			   </xsl:when>
			  <xsl:otherwise>
			    <fo:table-cell text-align="left" border-left="solid">
				<fo:block>
				   <xsl:value-of select="COVERAGE_CLASS" disable-output-escaping="yes" />
				</fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
				<fo:block>
			         <xsl:choose>
				   <xsl:when test="NUMBER_OF_VISITS > 0">
				     <xsl:value-of select="format-number(NUMBER_OF_VISITS, '###,###,###')"/>
				   </xsl:when>
				   <xsl:otherwise> 0 </xsl:otherwise>
				 </xsl:choose>
			       </fo:block>
			    </fo:table-cell>
			    <fo:table-cell>
				<fo:block></fo:block>
			    </fo:table-cell>
			    <fo:table-cell border-right="solid">
				<fo:block></fo:block>
			    </fo:table-cell>
			  </xsl:otherwise>
			</xsl:choose>
	        </fo:table-row>
	      </xsl:for-each>
	      </fo:table-body>
	</fo:table>
    </xsl:template>
</xsl:stylesheet>