MODULE = phonic
PROD = src/prod
TEST = src/test
LIB = lib/run
CP = lib/compile/\*:lib/run/\*:lib/test/\*
CP_PROD = ${CP}:${PROD_CLS}
CP_TEST = ${CP_PROD}:${TEST_CLS}
GEN = gen
ETC = etc
PROD_CLS = gen/prod/classes
TEST_CLS = gen/test/classes
DIST = gen/dist
JAR = ${DIST}/${MODULE}.jar
TAR = ${DIST}/${MODULE}-${VERSION}.tar
VERSION = 0.1
MANIFEST = ${ETC}/MANIFEST.MF
DIST_MANIFEST = ${GEN}/MANIFEST.MF
TAR_IMAGE = ${GEN}/image/${MODULE}-${VERSION}

.PHONY: clean

default: test dist

compile: clean ${PROD_CLS} ${TEST_CLS}
	find ${PROD} -name "*.scala" | xargs -s 30000 fsc -classpath ${CP} -d ${PROD_CLS} && \
	find ${TEST} -name "*.scala" | xargs -s 30000 fsc -classpath ${CP_PROD} -d ${TEST_CLS} 

test: compile
	scala -cp ${CP_TEST} org.scalatest.tools.Runner -p ${TEST_CLS} -oDFW

${JAR}: compile ${DIST_MANIFEST} ${DIST}
	jar cfm ${JAR} ${DIST_MANIFEST} -C ${PROD_CLS} .

${TAR}: ${JAR} ${TAR_IMAGE} ${TAR_IMAGE}/lib/depend 
	cp lib/run/*.jar ${TAR_IMAGE}/lib/depend && \
	cp ${JAR} ${TAR_IMAGE}/lib && \
	cp LICENSE COPYING FEATURES README ${TAR_IMAGE} && \
	cp -r ${ETC}/licenses ${TAR_IMAGE} && \
	tar cfz ${TAR} -C ${GEN}/image .

dist: clean ${TAR}

${DIST_MANIFEST}: ${GEN}
	sed -e 's/VERSION/${VERSION}/' ${MANIFEST} > ${DIST_MANIFEST}

repl: compile
	scala -classpath ${CP}:${PROD_CLS}:${TEST_CLS}

size: 
	find ${PROD} -name "*.scala" | xargs wc | sort -n

simian:
	echo "implement me"

depend:
	cp ../lever/gen/dist/lever.jar lib/run/. && \
	cp ../lever/LICENSE etc/licenses/lever

${GEN} ${GEN}/tmp ${PROD_CLS} ${TEST_CLS} ${DIST} ${LIB} ${TAR_IMAGE} ${TAR_IMAGE}/lib ${TAR_IMAGE}/lib/depend:
	mkdir -p $@

clean:
	rm -rf ${GEN}; find . -name "*~" -o -name "*.core" -print0 | xargs -0 rm
