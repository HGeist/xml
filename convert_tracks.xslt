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
		<xsl:if test="username">
			<xsl:attribute name="author">
				<xsl:value-of select="username/."/>
			</xsl:attribute>
		</xsl:if>
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
		<xsl:if test="altitudeMaxHeightM">
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
		<trackTypes>
			<xsl:for-each select="trackTypes/trackType">
				<trackType>
					<xsl:value-of select="." />
				</trackType>
			</xsl:for-each>
		</trackTypes>
		<trackAttributes>
			<xsl:for-each select="trackAttributes/trackAttribute">
				<trackAttribute>
					<xsl:value-of select="." />
				</trackAttribute>
			</xsl:for-each>
		</trackAttributes>
		<trackCharacters>
			<xsl:for-each select="trackCharacters/trackCharacter">
				<trackCharacter>
					<xsl:value-of select="." />
				</trackCharacter>
			</xsl:for-each>
		</trackCharacters>
		<trackRoadbeds>
			<xsl:for-each select="trackRoadbeds/trackRoadbed">
				<trackRoadbed>
					<xsl:value-of select="." />
				</trackRoadbed>
			</xsl:for-each>
		</trackRoadbeds>
		<trackRoads>
			<xsl:for-each select="trackRoads/trackRoad">
				<trackRoad>
					<xsl:value-of select="." />
				</trackRoad>
			</xsl:for-each>
		</trackRoads>
		<trackTypes>
			<xsl:for-each select="trackTypes/trackType">
				<trackType>
					<xsl:value-of select="." />
				</trackType>
			</xsl:for-each>
		</trackTypes>
		<points>
			<xsl:value-of select="kml/Document/Placemark/coordinates/." />
			<!--<xsl:analyze-string select="kml/Document/Placemark/coordinates" regex="[|](\d\.\d){1}[,](\d\.\d){1}[,](\d\.\d){1}[|]" flags="x">
				<xsl:matching-substring>
					<xsl:element name="point">
						<xsl:attribute name="lon">
							<xsl:value-of select="regex-group(1)"/>
						</xsl:attribute>
						<xsl:attribute name="lat">
							<xsl:value-of select="regex-group(2)"/>
						</xsl:attribute>
						<xsl:attribute name="ele">
							<xsl:value-of select="regex-group(3)"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:matching-substring>
			</xsl:analyze-string>-->
		</points>
	</track>
</xsl:template>

<!--prevent meta output with empty template-->
<xsl:template match="meta" />

</xsl:stylesheet>