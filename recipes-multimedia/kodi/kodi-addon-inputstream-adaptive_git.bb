SUMMARY = "kodi inputstream addon for several manifest types"

LICENSE = "GPL"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;beginline=1;md5=930e2a5f63425d8dd72dbd7391c43c46"

inherit kodi-addon

DEPENDS += "expat"

SRCREV = "07921252fce84b38b30f3c95c8bf72270c165c80"


PV = "2.2.28+gitr${SRCPV}"
SRC_URI = "git://github.com/peak3d/inputstream.adaptive.git;branch=Leia;protocol=https \
          "

S = "${WORKDIR}/git"

PARALLEL_MAKE = "-j 1"
OECMAKE_GENERATOR = "Unix Makefiles"

KODIADDONNAME = "inputstream.adaptive"
