<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>

    <xsl:param name="duplication.limit"/>

    <xsl:template match="check">
        <xsl:if test="(sum(//set/@lineCount) div sum(//check/summary/@totalSignificantLineCount) * 100) > $duplication.limit">
            <xsl:message terminate="yes">
                <xsl:text>
                    DUPLICATION EXCEEDS LIMIT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                </xsl:text>
            </xsl:message>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>


