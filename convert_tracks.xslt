<xsl:stylesheet version = '2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="xml"/> 

<xsl:template match="/">
	<tracks>
		<xsl:apply-templates />
	</tracks>
</xsl:template>

<xsl:template match="track">
     <track>
		<xsl:if test="trackProperty">
			<trackProperty>
				<xsl:value-of select="trackProperty" />
			</trackProperty>
		</xsl:if>
		<xsl:if test="description">
			<description>
				<xsl:value-of select="description" />
			</description>
		</xsl:if>
		<xsl:if test="trackProperty">
			<trackProperty>
				<xsl:value-of select="trackProperty" />
			</trackProperty>
		</xsl:if>
		<!--<xsl:template match="trackTypes">
			<xsl:template match="trackType">
		</xsl:template>-->
		<xsl:if test="title">
			<title>
				<xsl:value-of select="title" />
			</title>
		</xsl:if>
     </track>
</xsl:template>

<!--<xsl:template match="track">
	<xsl:element name="{name()}">
		<xsl:value-of select="." />
	</xsl:element>
</xsl:template>-->

<!--<xsl:template match="track/trackProperty">
	<trackProperty>
		<xsl:value-of select="." />
	</trackProperty>
</xsl:template>-->

</xsl:stylesheet>