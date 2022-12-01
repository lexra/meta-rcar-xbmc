FILESEXTRAPATHS_prepend := "${THISDIR}/kodi-18:"

SRC_URI += " \
	file://DllLibCurl.patch \
	file://EGLContext.patch \
	file://wayland-scanner++.pc \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += " \
	ffmpeg \
	omx-user-module \
	gles-user-module \
	waylandpp \
	waylandpp-native \
	wayland \
	wayland-native \
	wayland-protocols \
"

WINDOWSYSTEM = "wayland"
PACKAGECONFIG = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	pulseaudio \
	lcms \
"

OECMAKE_GENERATOR = "Unix Makefiles"
PARALLEL_MAKE = "-j 1"

CFLAGS += " \
	-Wno-maybe-uninitialized \
	-Wno-implicit-fallthr \
	-Wno-implicit-function-declaration \
	-Wno-attributes \
	-Wno-deprecated-declarations \
"

#-DKODI_DEPENDSBUILD=1 


EXTRA_OECMAKE = " \
	-DNATIVEPREFIX=${STAGING_DIR_NATIVE}/usr \
	-DJava_JAVA_EXECUTABLE=/usr/bin/java \
	-DWITH_TEXTUREPACKER=${STAGING_BINDIR_NATIVE}/TexturePacker \
	\
	-DENABLE_LDGOLD=ON \
	-DENABLE_STATIC_LIBS=FALSE \
	\
	-DFFMPEG_PATH=${STAGING_DIR_TARGET} \
	-DENABLE_INTERNAL_FFMPEG=OFF \
	-DENABLE_INTERNAL_CROSSGUID=OFF \
	\
	-DENABLE_DVDCSS=OFF \
	-DENABLE_OPTICAL=OFF \
	-DENABLE_DEBUGFISSION=OFF \
	-DCMAKE_BUILD_TYPE=Release \
"

EXTRA_OECMAKE_append = " \
	-DWAYLANDPP_PROTOCOLS_DIR=${STAGING_DIR_TARGET}/usr/share/waylandpp/protocols \
	-DWAYLAND_PROTOCOLS_DIR=${STAGING_DIR_TARGET}/usr/share/wayland-protocols \
"

#do_compile () {
#	make -C ${S}/../build
#}

do_patches () {
	mkdir -p ${STAGING_DIR_TARGET}/${libdir}/pkgconfig
	install ${WORKDIR}/wayland-scanner++.pc ${STAGING_DIR_TARGET}${libdir}/pkgconfig
	#cd ${STAGING_DIR_NATIVE}/bin
	#ln -sf ../usr/bin/JsonSchemaBuilder .
	#cd -
}

addtask patches before do_configure after do_patch


SYSTEMD_AUTO_ENABLE = "disable"

#INSANE_SKIP_${PN}_append = " already-stripped "
#INSANE_SKIP_${PN}-dev_append = " already-stripped "