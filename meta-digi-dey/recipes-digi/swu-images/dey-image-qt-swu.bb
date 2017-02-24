# Copyright (C) 2016, 2017 Digi International Inc.
SUMMARY = "Generate update package for SWUpdate"
SECTION = "base"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://sw-description"
SRC_URI_append_ccimx6 = " ${@oe.utils.ifelse(d.getVar('TRUSTFENCE_INITRAMFS_IMAGE', True), 'file://preinstall_swu.sh', '')}"

inherit swupdate

IMAGE_DEPENDS = "dey-image-qt"

SWUPDATE_IMAGES = "dey-image-qt-${GRAPHICAL_BACKEND}"

SOFTWARE_VERSION ?= "0.0.1"

BOOTFS_EXT ?= ".boot.vfat"
BOOTFS_EXT_ccimx6ul ?= ".boot.ubifs"
ROOTFS_EXT ?= ".ext4"
ROOTFS_EXT_ccimx6ul ?= ".ubifs"

BOOT_DEV_NAME ?= "/dev/mmcblk0p1"
BOOT_DEV_NAME_ccimx6ul ?= "linux"
ROOTFS_DEV_NAME ?= "/dev/mmcblk0p3"
ROOTFS_DEV_NAME_ccimx6ul ?= "rootfs"
ROOTFS_ENC_DEV = "/dev/mapper/cryptroot"
ROOTFS_ENC_DEV_ccimx6ul = "${ROOTFS_DEV_NAME}"
ROOTFS_DEV_NAME_FINAL = "${@oe.utils.ifelse(d.getVar('TRUSTFENCE_INITRAMFS_IMAGE', True), '${ROOTFS_ENC_DEV}', '${ROOTFS_DEV_NAME}')}"
PREINST_SCRIPT_TEMPLATE = "scripts: ( { filename = \\"preinstall_swu.sh\\"; type = \\"preinstall\\"; sha256 = \\"@preinstall_swu.sh\\"; \\x7D );"
PREINST_SCRIPT_DESC = ""
PREINST_SCRIPT_DESC_ccimx6sbc = "${@oe.utils.ifelse(d.getVar('TRUSTFENCE_INITRAMFS_IMAGE', True), '${PREINST_SCRIPT_TEMPLATE}', '')}"

python () {
    img_fstypes = d.getVar('BOOTFS_EXT', True) + " " + d.getVar('ROOTFS_EXT', True)
    d.setVarFlag("SWUPDATE_IMAGES_FSTYPES", "dey-image-qt-" + d.getVar('GRAPHICAL_BACKEND', True), img_fstypes)
}

do_unpack[postfuncs] += "fill_description"

fill_description() {
	sed -i -e "s,##BOOTIMG_NAME##,dey-image-qt-${GRAPHICAL_BACKEND}-${MACHINE}${BOOTFS_EXT},g" "${WORKDIR}/sw-description"
	sed -i -e "s,##BOOT_DEV##,${BOOT_DEV_NAME},g" "${WORKDIR}/sw-description"
	sed -i -e "s,##ROOTIMG_NAME##,dey-image-qt-${GRAPHICAL_BACKEND}-${MACHINE}${ROOTFS_EXT},g" "${WORKDIR}/sw-description"
	sed -i -e "s,##ROOTFS_DEV##,${ROOTFS_DEV_NAME_FINAL},g" "${WORKDIR}/sw-description"
	sed -i -e "s,##SW_VERSION##,${SOFTWARE_VERSION},g" "${WORKDIR}/sw-description"
	sed -i -e "s/##PREINSTALL_SCRIPT##/${PREINST_SCRIPT_DESC}/g" "${WORKDIR}/sw-description"
}
