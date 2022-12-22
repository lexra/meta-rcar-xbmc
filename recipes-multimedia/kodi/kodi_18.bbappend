FILESEXTRAPATHS_prepend := "${THISDIR}/kodi-18:"

SRC_URI += " \
	file://DllLibCurl.patch \
	file://EGLContext.patch \
	file://cassert.patch \
	file://wayland-scanner++.pc \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += " \
	flatbuffers \
	rapidjson \
	fstrcmp \
	ffmpeg \
	virtual/libgles2 \
	virtual/egl \
	waylandpp \
	waylandpp-native \
	wayland \
	wayland-native \
	wayland-protocols \
"

RDEPENDS_${PN} += " ca-certificates "

WINDOWSYSTEM = "wayland"
PACKAGECONFIG = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	pulseaudio \
	lcms \
"

OECMAKE_GENERATOR = "Unix Makefiles"
PARALLEL_MAKE = "-j 1"

CCACHE_DISABLE = "1"
ASNEEDED = ""

FULL_OPTIMIZATION_armv7a = "-fexpensive-optimizations -fomit-frame-pointer -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"

# for python modules
export HOST_SYS
export BUILD_SYS
export STAGING_LIBDIR
export STAGING_INCDIR
export PYTHON_DIR
export TARGET_PREFIX

EXTRA_OECMAKE = " \
	-DVERBOSE=ON \
	-DNATIVEPREFIX=${STAGING_DIR_NATIVE}/usr \
	-DJava_JAVA_EXECUTABLE=/usr/bin/java \
	-DWITH_TEXTUREPACKER=${STAGING_BINDIR_NATIVE}/TexturePacker \
	-DWITH_JSONSCHEMABUILDER=${STAGING_BINDIR_NATIVE}/JsonSchemaBuilder \
	\
	-DENABLE_STATIC_LIBS=FALSE \
	-DCMAKE_NM='${NM}' \
	\
	-DFFMPEG_PATH=${STAGING_DIR_TARGET} \
	-DENABLE_INTERNAL_FFMPEG=OFF \
	-DENABLE_INTERNAL_RapidJSON=OFF \
	-DENABLE_INTERNAL_CROSSGUID=OFF \
	-DENABLE_INTERNAL_FLATBUFFERS=OFF \
	\
	-DENABLE_DVDCSS=OFF \
	-DENABLE_OPTICAL=OFF \
	-DCMAKE_BUILD_TYPE=RelWithDebInfo \
"

#EXTRA_OECMAKE_append = " -DENABLE_LDGOLD=OFF "

EXTRA_OECMAKE_append = " \
	-DPACKAGE_ZIP=1 \
	-DPACKAGE_TGZ=1 \
"

EXTRA_OECMAKE_append = " \
	-DWAYLANDPP_PROTOCOLS_DIR=${STAGING_DIR_TARGET}/usr/share/waylandpp/protocols \
	-DWAYLAND_PROTOCOLS_DIR=${STAGING_DIR_TARGET}/usr/share/wayland-protocols \
"

do_configure_prepend () {
	mkdir -p ${STAGING_DIR_TARGET}/${libdir}/pkgconfig
	install ${WORKDIR}/wayland-scanner++.pc ${STAGING_DIR_TARGET}${libdir}/pkgconfig
}

SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP_${PN}_append = " already-stripped"
INSANE_SKIP_${PN}-dev_append = " already-stripped"
