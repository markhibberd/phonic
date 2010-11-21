<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>

    <xsl:param name="significant.lines.limit"/>

    <xsl:template match="check">
        <xsl:if test="sum(//check/summary/@totalSignificantLineCount) > $significant.lines.limit">
            <xsl:message terminate="yes">
                <xsl:text>
                    TOO MANY LINES OF CODE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                </xsl:text>
            </xsl:message>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>


