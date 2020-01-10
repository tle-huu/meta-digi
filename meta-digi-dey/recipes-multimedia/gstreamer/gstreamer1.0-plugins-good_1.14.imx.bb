require gstreamer1.0-plugins-good.inc
FILESEXTRAPATHS_prepend := "${COREBASE}/meta/recipes-multimedia/gstreamer/files:"

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=a4e1830fce078028c8f0974161272607 \
                    file://gst/replaygain/rganalysis.c;beginline=1;endline=23;md5=b60ebefd5b2f5a8e0cab6bfee391a5fe"

DEPENDS += "libdrm"

GST1.0-PLUGINS-GOOD_SRC ?= "gitsm://source.codeaurora.org/external/imx/gst-plugins-good.git;protocol=https"
SRCBRANCH = "MM_04.05.00_1908_L4.14.98"

SRC_URI = " \
    ${GST1.0-PLUGINS-GOOD_SRC};branch=${SRCBRANCH} \
"
SRCREV = "6aa0b5632a610ea09278624826fb312db9f7b1b9"

EXTRA_AUTORECONF = ""
PACKAGECONFIG_append = " vpx"

# Fix: unrecognised options: --disable-sunaudio [unknown-configure-option]
EXTRA_OECONF_remove = " --disable-sunaudio"

PV = "1.14.4.imx"

S = "${WORKDIR}/git"

