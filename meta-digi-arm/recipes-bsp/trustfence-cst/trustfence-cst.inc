# Copyright (C) 2017-2019 Digi International
SUMMARY = "NXP Code signing Tool for the High Assurance Boot library"
DESCRIPTION = "Provides software code signing support designed for use with \
i.MX processors that integrate the HAB library in the internal boot ROM."
HOMEPAGE = "https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL"
LICENSE = "CLOSED"

DEPENDS = "openssl byacc flex"

# Explicitly add byacc-native as a dependency when building the package for the
# SDK, otherwise, it won't get installed in the sysroot, causing a compilation
# error.
DEPENDS_append_class-nativesdk = " byacc-native"

SRC_URI = " \
    ${@oe.utils.conditional('TRUSTFENCE_SIGN', '1', 'file://cst-${PV}.tgz', '', d)} \
    file://0001-gen_auth_encrypted_data-reuse-existing-DEK-file.patch \
    file://0002-hab4_pki_tree.sh-automate-script.patch \
    file://0003-openssl_helper-use-dev-urandom-as-seed-source.patch \
    file://0004-hab4_pki_tree.sh-usa-a-random-password-for-the-defau.patch \
    file://0005-ahab_pki_tree.sh-automate-script.patch \
    file://0006-ahab_pki_tree.sh-use-a-random-password-for-the-defau.patch \
"

# Usually local files (with file:// protocol) are not checked for
# premirrors. But in this case we want to be able to download the 'cst'
# package from a premirror in case it's not already in the DL_DIR, so prepend
# a premirror for the 'file://' protocol.
python() {
    source_mirror_url = d.getVar('SOURCE_MIRROR_URL', True)
    if source_mirror_url:
        premirrors = d.getVar('PREMIRRORS', True)
        d.setVar('PREMIRRORS', "file://cst.* %s \\n %s" % (source_mirror_url, premirrors))
}

S = "${WORKDIR}/cst-${PV}/"

do_compile() {
	cd ${S}/code/cst
	oe_runmake OSTYPE=linux64 clean
	oe_runmake OSTYPE=linux64 rel_bin
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 $(find ${S}/code/cst/release/linux64 -type f -name cst) ${D}${bindir}/cst
	install -m 0755 $(find ${S}/code/cst/release/linux64 -type f -name srktool) ${D}${bindir}/srktool
	if [ "${TRUSTFENCE_SIGN_MODE}" = "AHAB" ]; then
		install -m 0755 keys/ahab_pki_tree.sh ${D}${bindir}/trustfence-gen-pki.sh
	elif [ "${TRUSTFENCE_SIGN_MODE}" = "HAB" ]; then
		install -m 0755 keys/hab4_pki_tree.sh ${D}${bindir}/trustfence-gen-pki.sh
	else
		bberror "Unkown TRUSTFENCE_SIGN_MODE value"
		exit 1
	fi
	install -m 0755 ca/openssl.cnf ${D}${bindir}/openssl.cnf
	install -m 0755 ca/v3_ca.cnf ${D}${bindir}/v3_ca.cnf
	install -m 0755 ca/v3_usr.cnf ${D}${bindir}/v3_usr.cnf
}

INSANE_SKIP_${PN} += "already-stripped"

FILES_${PN} = "${bindir}"
BBCLASSEXTEND = "native nativesdk"
