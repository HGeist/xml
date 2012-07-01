<xsl:stylesheet version = '2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="xml" indent="yes" /> 

<!--base template-->
<xsl:template match="/">
	<tracks>
		<xsl:apply-templates />
	</tracks>
</xsl:template>

<!--track template-->
<xsl:template match="track">
	<track>
	
		<!--attributes-->
		<xsl:if test="title">
			<xsl:attribute name="trackName">
				<xsl:value-of select="title/."/>
			</xsl:attribute>
		</xsl:if>
		<!--<xsl:if test="username">
			<xsl:attribute name="author">
				<xsl:value-of select="username/."/>
			</xsl:attribute>
		</xsl:if>-->
		<xsl:attribute name="createTimestamp">
			<xsl:value-of select="createdDate/."/>
		</xsl:attribute>
		<xsl:if test="trackLengthM">
			<xsl:attribute name="totalLength">
				<xsl:value-of select="trackLengthM/."/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="totalAscendM">
			<xsl:attribute name="totalAscend">
				<xsl:value-of select="totalAscendM/."/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="totalAscendM">
			<xsl:attribute name="totalDescend">
				<xsl:value-of select="totalDescendM/."/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="altitudeMaxHeightM">
			<xsl:attribute name="altitudeMax">
				<xsl:value-of select="altitudeMaxHeightM/."/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="altitudeMinHeightM">
			<xsl:attribute name="altitudeMin">
				<xsl:value-of select="altitudeMinHeightM/."/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="altitudeDifferenceM">
			<xsl:attribute name="altitudeDifference">
				<xsl:value-of select="altitudeDifferenceM/."/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="totalDescendM">
			<xsl:attribute name="quality">
				<xsl:value-of select="totalDescendM/."/>
			</xsl:attribute>
		</xsl:if>
		
		<!--elements-->
		<xsl:if test="fileId">
			<fileId>
				<xsl:value-of select="fileId/."/>
			</fileId>
		</xsl:if>
		<xsl:if test="downloadLink">
			<kmlLink>
				<xsl:value-of select="downloadLink/."/>
			</kmlLink>
		</xsl:if>
		<xsl:if test="trackProperty">
			<trackProperty>
				<xsl:value-of select="trackProperty" />
			</trackProperty>
		</xsl:if>
		<xsl:if test="description">
			<description>
				<xsl:value-of select="*[local-name()='kml']/*[local-name()='Document']/*[local-name()='Placemark']/*[local-name()='description']" />
			</description>
		</xsl:if>
		<xsl:if test="trackAttributes">
		<trackAttributes>
			<xsl:for-each select="trackAttributes/trackAttribute">
				<trackAttribute>
					<xsl:value-of select="." />
				</trackAttribute>
			</xsl:for-each>
		</trackAttributes>
		</xsl:if>
		<xsl:if test="trackCharacters">
		<trackCharacters>
			<xsl:for-each select="trackCharacters/trackCharacter">
				<trackCharacter>
					<xsl:value-of select="." />
				</trackCharacter>
			</xsl:for-each>
		</trackCharacters>
		</xsl:if>
		<xsl:if test="trackRoadbeds">
		<trackRoadbeds>
			<xsl:for-each select="trackRoadbeds/trackRoadbed">
				<trackRoadbed>
					<xsl:value-of select="." />
				</trackRoadbed>
			</xsl:for-each>
		</trackRoadbeds>
		</xsl:if>
		<xsl:if test="trackRoads">
		<trackRoads>
			<xsl:for-each select="trackRoads/trackRoad">
				<trackRoad>
					<xsl:value-of select="." />
				</trackRoad>
			</xsl:for-each>
		</trackRoads>
		</xsl:if>
		<xsl:if test="trackTypes">
		<trackTypes>
			<xsl:for-each select="trackTypes/trackType">
				<trackType>
					<xsl:value-of select="." />
				</trackType>
			</xsl:for-each>
		</trackTypes>
		</xsl:if>
		<points>
			<xsl:for-each select="tokenize(*[local-name()='kml']/*[local-name()='Document']/*[local-name()='Placemark']/*[local-name()='coordinates'],
			'\|')">
				<xsl:variable name="selectThis" select="." />
				
				<point>
					<xsl:for-each select="tokenize($selectThis, ',')">
						<xsl:if test="position() = 1">
							<xsl:attribute name="lon">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="position() = 2">
							<xsl:attribute name="lat">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="position() = 3">
							<xsl:attribute name="ele">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:if>
					</xsl:for-each>
				</point>
			</xsl:for-each>
		</points>
	</track>
</xsl:template>

<!--prevent meta output with empty template-->
<xsl:template match="meta" />

</xsl:stylesheet>