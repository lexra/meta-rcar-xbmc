SUMMARY = "Kodi Media Center"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=930e2a5f63425d8dd72dbd7391c43c46"

FILESPATH =. "${FILE_DIRNAME}/kodi-18:"

inherit cmake gettext python-dir pythonnative systemd

DEPENDS += " \
            libfmt \
            rapidjson \
            crossguid \
            texturepacker-native \
            libdvdnav libdvdcss libdvdread \
            git-native \
            curl-native \
            gperf-native \
            jsonschemabuilder-native \
            nasm-native \
            swig-native \
            unzip-native \
            yasm-native \
            zip-native \
            \
            avahi \
            boost \
            bzip2 \
            curl \
            dcadec \
            enca \
            expat \
            faad2 \
            ffmpeg \
            fontconfig \
            fribidi \
            giflib \
            libass \
            libcdio \
            libcec \
            libdvdcss \
            libdvdread \
            libinput \
            libmad \
            libmicrohttpd \
            libmms \
            libmodplug \
            libnfs \
            libpcre \
            libplist \
            libsamplerate0 \
            libsquish \
            libssh \
            libtinyxml \
            libusb1 \
            libxslt \
            lzo \
            mpeg2dec \
            python \
            samba \
            sqlite3 \
            taglib \
            virtual/egl \
            wavpack \
            yajl \
            zlib \
          "

SRCREV = "2ab16beeeaf67e3b8dca703d96615bd80aca7c35"

# 'patch' doesn't support binary diffs
PATCHTOOL = "git"

PV = "18.0+gitr${SRCPV}"
SRC_URI = "git://github.com/xbmc/xbmc.git;protocol=https \
           file://0001-estuary-move-recently-added-entries-to-the-top-in-ho.patch \
           file://0001-EGLutils-don-t-request-16-bit-depth.patch \
           file://0001-gbm-select-valid-connector.patch \
           file://0002-kodi.sh-set-mesa-debug.patch \
           file://baba4e1d8517a9ab2c473eb4dbea1ba7ffc3aa74.patch \
           file://kodi.service \
           file://kodi-x11.service \
          "

S = "${WORKDIR}/git"

# breaks compilation
CCACHE = ""
ASNEEDED = ""

ACCEL ?= ""
ACCEL_x86 = "vaapi vdpau"
ACCEL_x86-64 = "vaapi vdpau"

# Default to GBM everywhere, sucks to be nvidia
WINDOWSYSTEM ?= "gbm"

PACKAGECONFIG ??= "${ACCEL} ${WINDOWSYSTEM} pulseaudio lcms"

# Core windowing system choices

PACKAGECONFIG[x11] = "-DCORE_PLATFORM_NAME=x11,,libxinerama libxmu libxrandr libxtst glew"
PACKAGECONFIG[gbm] = "-DCORE_PLATFORM_NAME=gbm,,"
PACKAGECONFIG[raspberrypi] = "-DCORE_PLATFORM_NAME=rbpi,,userland"
PACKAGECONFIG[amlogic] = "-DCORE_PLATFORM_NAME=aml,,"
PACKAGECONFIG[wayland] = "-DCORE_PLATFORM_NAME=wayland -DWAYLAND_RENDER_SYSTEM=gles,,wayland waylandpp"

PACKAGECONFIG[vaapi] = "-DENABLE_VAAPI=ON,-DENABLE_VAAPI=OFF,libva"
PACKAGECONFIG[vdpau] = "-DENABLE_VDPAU=ON,-DENABLE_VDPAU=OFF,libvdpau"
PACKAGECONFIG[mysql] = "-DENABLE_MYSQLCLIENT=ON,-DENABLE_MYSQLCLIENT=OFF,mysql5"
PACKAGECONFIG[pulseaudio] = "-DENABLE_PULSEAUDIO=ON,-DENABLE_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[lcms] = ",,lcms"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

EXTRA_OECMAKE = " \
    -DNATIVEPREFIX=${STAGING_DIR_NATIVE}${prefix} \
    -DJava_JAVA_EXECUTABLE=/usr/bin/java \
    -DWITH_TEXTUREPACKER=${STAGING_BINDIR_NATIVE}/TexturePacker \
    \
    -DENABLE_LDGOLD=ON \
    -DENABLE_STATIC_LIBS=FALSE \
    -DUSE_LTO=${@oe.utils.cpu_count()} \
    \
    -DFFMPEG_PATH=${STAGING_DIR_TARGET} \
    -DENABLE_INTERNAL_FFMPEG=OFF \
    -DENABLE_INTERNAL_CROSSGUID=OFF \
    -DLIBDVD_INCLUDE_DIRS=${STAGING_INCDIR} \
    -DNFS_INCLUDE_DIR=${STAGING_INCDIR} \
    -DSHAIRPLAY_INCLUDE_DIR=${STAGING_INCDIR} \
    \
    -DENABLE_AIRTUNES=ON \
    -DENABLE_OPTICAL=OFF \
    -DENABLE_DVDCSS=OFF \
    -DENABLE_DEBUGFISSION=OFF \
    -DCMAKE_BUILD_TYPE=Release \
"

# OECMAKE_GENERATOR="Unix Makefiles"
# PARALLEL_MAKE = ""

FULL_OPTIMIZATION_armv7a = "-fexpensive-optimizations -fomit-frame-pointer -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"

# for python modules
export HOST_SYS
export BUILD_SYS
export STAGING_LIBDIR
export STAGING_INCDIR
export PYTHON_DIR

do_install_append() {
	install -d ${D}/lib/systemd/system

	if [ -e ${D}${libdir}/kodi/kodi-gbm ] ; then
		install -m 0644 ${WORKDIR}/kodi.service ${D}/lib/systemd/system/kodi.service
	else
		install -m 0644 ${WORKDIR}/kodi-x11.service ${D}/lib/systemd/system/kodi.service

	fi
}


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "kodi.service"
INSANE_SKIP_${PN} = "rpaths"

FILES_${PN} += "${datadir}/xsessions ${datadir}/icons ${libdir}/xbmc ${datadir}/xbmc ${libdir}/firewalld"
FILES_${PN}-dbg += "${libdir}/kodi/.debug ${libdir}/kodi/*/.debug ${libdir}/kodi/*/*/.debug ${libdir}/kodi/*/*/*/.debug"

# kodi uses some kind of dlopen() method for libcec so we need to add it manually
# OpenGL builds need glxinfo, that's in mesa-demos
RRECOMMENDS_${PN}_append = " libcec \
                             libcurl \
                             libnfs \
                             ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'xdyinfo xrandr xinit mesa-demos', '', d)} \
                             python \
                             python-ctypes \
                             python-lang \
                             python-re \
                             python-netclient \
                             python-html \
                             python-difflib \
                             python-json \
                             python-zlib \
                             python-shell \
                             python-sqlite3 \
                             python-compression \
                             python-xmlrpc \
                             tzdata-africa \
                             tzdata-americas \
                             tzdata-antarctica \
                             tzdata-arctic \
                             tzdata-asia \
                             tzdata-atlantic \
                             tzdata-australia \
                             tzdata-europe \
                             tzdata-pacific \
                           "
RRECOMMENDS_${PN}_append_libc-glibc = " glibc-charmap-ibm850 \
                                        glibc-gconv-ibm850 \
					glibc-gconv-unicode \
                                        glibc-gconv-utf-32 \
					glibc-charmap-utf-8 \
					glibc-localedata-en-us \
                                      "
