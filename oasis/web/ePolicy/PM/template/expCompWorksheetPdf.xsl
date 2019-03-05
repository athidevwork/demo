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
            <fo:region-body margin-top="3.9cm" />
		    <fo:region-before extent="8cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4" initial-page-number="1" format="1">
	        <fo:static-content flow-name="xsl-region-before">
              <fo:block>
		        <xsl:apply-templates select="SECTION1/ROWS"/>
              </fo:block>
              </fo:static-content>
              <fo:flow flow-name="xsl-region-body">
                 <fo:block>
                    <xsl:apply-templates select="SECTION2"/>
                 </fo:block>
		         <fo:block>
                    <xsl:apply-templates select="SECTION3"/>
                 </fo:block>
              </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="SECTION1/ROWS">
	<fo:block>&#160;</fo:block>
	<fo:table font-size="10pt" table-layout="fixed" width="100%">
      <fo:table-column column-width="100pt"/>
      <fo:table-column column-width="100pt"/>
      <fo:table-column column-width="120pt"/>
      <fo:table-column column-width="60pt"/>
      <fo:table-column column-width="100pt"/>
      <fo:table-column column-width="40pt"/>
	  <fo:table-column column-width="80pt"/>
	  <fo:table-column column-width="40pt"/>
		
	  <fo:table-body>
	    <fo:table-row>
	        <fo:table-cell text-align="center" number-columns-spanned="8">
		    <fo:block font-weight="bold">
		      Experience Component Worksheet
		    </fo:block>
		    <fo:block>&#160;</fo:block>
	        </fo:table-cell>
	    </fo:table-row>
		
	    <fo:table-row text-align="center" border="solid" border-width=".5pt" height="20px">
		  <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Years Insured
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid">
			<fo:block>
			    <xsl:value-of select="ROW/INSURED_YEARS"/>
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			   Evaluation Date:
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid" number-columns-spanned="5">
			<fo:block>
			   <xsl:value-of select="ROW/PROCESS_DATE"/>
			</fo:block>
		  </fo:table-cell>
	    </fo:table-row>
		  
	    <fo:table-row text-align="center" border="solid" border-width=".5pt" height="20px">
		  <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			    Total Gross EP
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid">
			<fo:block>
			   <xsl:value-of select="format-number(ROW/EARNED_PREMIUM,'$###,###,##0.00')"/>
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			   Total Gross Com Inc
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid">
			<fo:block>
			    <xsl:value-of select="format-number(ROW/TOTAL_COMBINED_INCURRED,'$###,###,##0.00')"/>
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid">
			<fo:block font-weight="bold">
			   Total Gross LR%
			</fo:block>
		  </fo:table-cell>
		  <fo:table-cell border="solid" number-columns-spanned="3">
			<fo:block>
			   <xsl:value-of select="format-number(ROW/TOTAL_LOSS_RATIO,'###,###,##0.00')"/>
			</fo:block>
		  </fo:table-cell>
	    </fo:table-row>
	  </fo:table-body>
	</fo:table>
	<fo:block>&#160;</fo:block>
    </xsl:template>

    <xsl:template match="SECTION2">
    <fo:table font-size="10pt" table-layout="fixed" width="100%">
      <fo:table-column column-width="100pt"/>
      <fo:table-column column-width="100pt"/>
      <fo:table-column column-width="120pt"/>
      <fo:table-column column-width="100pt"/>
      <fo:table-column column-width="220pt"/>

      <fo:table-header>
        <fo:table-row text-align="center" border="solid" border-width=".5pt" height="20px">
          <fo:table-cell border="solid">
            <fo:block font-weight="bold"> Policy# </fo:block>
          </fo:table-cell>
          <fo:table-cell border="solid">
            <fo:block font-weight="bold"> Eff Date </fo:block>
          </fo:table-cell>
          <fo:table-cell border="solid">
            <fo:block font-weight="bold"> Risk Type </fo:block>
          </fo:table-cell>
          <fo:table-cell border="solid">
            <fo:block font-weight="bold"> Risk Group EP </fo:block>
          </fo:table-cell>
          <fo:table-cell border="solid">
            <fo:block font-weight="bold"> Risk Spec </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-header>

      <fo:table-body>
        <xsl:for-each select="ROWS/ROW">
	    <fo:table-row text-align="left">
		  <fo:table-cell border-left="solid">
		    <fo:block>
		      <xsl:value-of select="POLICY_NO"/>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell>
		    <fo:block>
		      <xsl:value-of select="RISK_EFFECTIVE_DATE"/>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell>
		    <fo:block>
		      <xsl:value-of select="SHORT_DESCRIPTION"/>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell text-align="right">
		    <fo:block>
		      <xsl:value-of select="format-number(EARNED_PREMIUM,'$###,###,##0.00')"/>&#160;
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell border-right="solid">
		    <fo:block>
		      <xsl:value-of select="RISK_SPEC"/>
		    </fo:block>
		  </fo:table-cell>
		 </fo:table-row>
	       </xsl:for-each>
	       <fo:table-row>
		 <fo:table-cell border-top="solid" number-columns-spanned="5">
		   <fo:block>&#160;</fo:block>
		 </fo:table-cell>
	    </fo:table-row>
      </fo:table-body>
    </fo:table>
    </xsl:template>

    <xsl:template match="SECTION3">
	<fo:table font-size="10pt" table-layout="fixed" width="100%">
	  <fo:table-column column-width="100pt"/>
	  <fo:table-column column-width="90pt"/>
	  <fo:table-column column-width="90pt"/>
	  <fo:table-column column-width="70pt"/>
	  <fo:table-column column-width="70pt"/>
	  <fo:table-column column-width="70pt"/>
	  <fo:table-column column-width="70pt"/>
	  <fo:table-column column-width="80pt"/>

      <fo:table-header>
        <fo:table-row text-align="center" border="solid" border-width=".5pt" height="20px">
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Claim# </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Accident Date </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Notification Date </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Gross Inc Indem </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Gross Inc Exp </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Ded Amt/Type </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Comb Net Inc </fo:block>
		</fo:table-cell>
		<fo:table-cell border="solid">
		  <fo:block font-weight="bold"> Claims Status </fo:block>
		</fo:table-cell>
   	    </fo:table-row>
      </fo:table-header>
		  
      <fo:table-body>
	    <xsl:for-each select="ROWS/ROW">
	    <fo:table-row text-align="left">
		  <fo:table-cell border-left="solid">
		    <fo:block>
		      <xsl:value-of select="CLAIM_NO"/>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell>
		    <fo:block>
		      <xsl:value-of select="LOSS_DATE"/>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell>
		    <fo:block>
		      <xsl:value-of select="REPORT_DATE"/>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell text-align="right">
		    <fo:block>
		      <xsl:if test="GROSS_IND >= 0">
		        <xsl:value-of select="format-number(GROSS_IND,'$###,###,##0.00')"/>
		       </xsl:if>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell text-align="right">
		    <fo:block>
		     <xsl:if test="GROSS_EXP >= 0">
		      <xsl:value-of select="format-number(GROSS_EXP,'$###,###,##0.00')"/>
		     </xsl:if>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell text-align="right">
		    <fo:block>
		      <xsl:if test="DEDUCT > 0">
			<xsl:value-of select="format-number(DEDUCT,'$###,###,##0.00')"/>
		      </xsl:if>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell text-align="right">
		    <fo:block>
		     <xsl:if test="COMB_NET_INC >= 0">
		      <xsl:value-of select="format-number(COMB_NET_INC,'$###,###,##0.00')"/>&#160;
		     </xsl:if>
		    </fo:block>
		  </fo:table-cell>
		  <fo:table-cell border-right="solid">
		    <fo:block>
		      <xsl:value-of select="STATUS"/>
		    </fo:block>
		  </fo:table-cell>
		</fo:table-row>
	    </xsl:for-each>
		
	    <fo:table-row>
		 <fo:table-cell border-top="solid" number-columns-spanned="8">
		   <fo:block>&#160;</fo:block>
		 </fo:table-cell>
	    </fo:table-row>
      </fo:table-body>
    </fo:table>
    </xsl:template>

</xsl:stylesheet>