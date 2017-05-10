# Copyright (C) 2017 Digi International.

SUMMARY = "SDK for connecting to AWS IoT from a device using embedded C"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-embedded-C"
SECTION = "base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=acc7a1bf87c055789657b148939e4b40"

DEPENDS = "mbedtls"

SRC_URI = " \
    https://github.com/aws/aws-iot-device-sdk-embedded-C/archive/v${PV}.tar.gz \
    file://aws_iot_config.h.template \
    file://awsiotsdk.pc \
    file://Makefile \
    file://Makefile.app \
    file://Makefile.lib \
"

SRC_URI[md5sum] = "2c415af16bbd68440b62d71a7e9775c5"
SRC_URI[sha256sum] = "74d434b3258654cea048b20eb52d4fc627f5c87e8727ce180a1d529e3285a97e"

S = "${WORKDIR}/aws-iot-device-sdk-embedded-C-${PV}"

inherit awsiotsdk-c pkgconfig

do_configure() {
	cp -f ${WORKDIR}/awsiotsdk.pc ${S}

	# Copy and update the configuration header file.
	cp -f ${WORKDIR}/aws_iot_config.h.template ${S}/include/aws_iot_config.h
	sed -i -e "s,##AWS_IOT_MQTT_HOST##,${AWS_IOT_MQTT_HOST},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MQTT_PORT##,${AWS_IOT_MQTT_PORT},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MY_THING_NAME##,${AWS_IOT_MY_THING_NAME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_ROOT_CA_FILENAME##,${AWS_IOT_ROOT_CA_FILENAME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_CERTIFICATE_FILENAME##,${AWS_IOT_CERTIFICATE_FILENAME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_PRIVATE_KEY_FILENAME##,${AWS_IOT_PRIVATE_KEY_FILENAME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MQTT_TX_BUF_LEN##,${AWS_IOT_MQTT_TX_BUF_LEN},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MQTT_RX_BUF_LEN##,${AWS_IOT_MQTT_RX_BUF_LEN},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MQTT_NUM_SUBSCRIBE_HANDLERS##,${AWS_IOT_MQTT_NUM_SUBSCRIBE_HANDLERS},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##MAX_ACKS_TO_COMEIN_AT_ANY_GIVEN_TIME##,${MAX_ACKS_TO_COMEIN_AT_ANY_GIVEN_TIME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##MAX_THINGNAME_HANDLED_AT_ANY_GIVEN_TIME##,${MAX_THINGNAME_HANDLED_AT_ANY_GIVEN_TIME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##MAX_JSON_TOKEN_EXPECTED##,${MAX_JSON_TOKEN_EXPECTED},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##MAX_SIZE_OF_THING_NAME##,${MAX_SIZE_OF_THING_NAME},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MQTT_MIN_RECONNECT_WAIT_INTERVAL##,${AWS_IOT_MQTT_MIN_RECONNECT_WAIT_INTERVAL},g" "${S}/include/aws_iot_config.h"
	sed -i -e "s,##AWS_IOT_MQTT_MAX_RECONNECT_WAIT_INTERVAL##,${AWS_IOT_MQTT_MAX_RECONNECT_WAIT_INTERVAL},g" "${S}/include/aws_iot_config.h"

	# Remove the examples header files.
	rm -f ${S}/samples/linux/shadow_sample/aws_iot_config.h
	rm -f ${S}/samples/linux/shadow_sample_console_echo/aws_iot_config.h
	rm -f ${S}/samples/linux/subscribe_publish_sample/aws_iot_config.h

	# Copy the Makefiles.
	cp -f ${WORKDIR}/Makefile ${S}
	cp -f ${WORKDIR}/Makefile.lib ${S}/src/Makefile
	cp -f ${WORKDIR}/Makefile.app ${S}/samples/linux/shadow_sample/Makefile
	cp -f ${WORKDIR}/Makefile.app ${S}/samples/linux/shadow_sample_console_echo/Makefile
	cp -f ${WORKDIR}/Makefile.app ${S}/samples/linux/subscribe_publish_sample/Makefile
}

do_install() {
	oe_runmake DESTDIR=${D} install

	# Check if certificate variables are defined and files exist.
	if [ -z "${AWS_IOT_CERTS_DIR}" ]; then
		bberror "Undefined variable AWS_IOT_CERTS_DIR. Define it in your project 'local.conf'."
		return -1
	elif [ ! -d "${AWS_IOT_CERTS_DIR}" ]; then
		bberror "Unable to find defined AWS_IOT_CERTS_DIR ('${AWS_IOT_CERTS_DIR}')."
		return -1
	elif [ ! -f "${AWS_IOT_CERTS_DIR}/${AWS_IOT_ROOT_CA_FILENAME}" ]; then
		bberror "Unable to find defined AWS_IOT_ROOT_CA_FILENAME ('${AWS_IOT_ROOT_CA_FILENAME}') in '${AWS_IOT_CERTS_DIR}'."
		return -1
	elif [ ! -f "${AWS_IOT_CERTS_DIR}/${AWS_IOT_CERTIFICATE_FILENAME}" ]; then
		bberror "Unable to find defined AWS_IOT_CERTIFICATE_FILENAME ('${AWS_IOT_CERTIFICATE_FILENAME}') in '${AWS_IOT_CERTS_DIR}'."
		return -1
	elif [ ! -f "${AWS_IOT_CERTS_DIR}/${AWS_IOT_PRIVATE_KEY_FILENAME}" ]; then
		bberror "Unable to find defined AWS_IOT_PRIVATE_KEY_FILENAME ('${AWS_IOT_PRIVATE_KEY_FILENAME}') in '${AWS_IOT_CERTS_DIR}'."
		return -1
	fi

	# Install certificates.
	install -d ${D}${sysconfdir}/ssl/certs
	install -m 0644 ${AWS_IOT_CERTS_DIR}/${AWS_IOT_ROOT_CA_FILENAME} ${D}${sysconfdir}/ssl/certs/
	install -m 0644 ${AWS_IOT_CERTS_DIR}/${AWS_IOT_CERTIFICATE_FILENAME} ${D}${sysconfdir}/ssl/certs/
	install -m 0644 ${AWS_IOT_CERTS_DIR}/${AWS_IOT_PRIVATE_KEY_FILENAME} ${D}${sysconfdir}/ssl/certs/
}

PACKAGES =+ "${PN}-cert ${PN}-examples"

FILES_${PN}-cert = "${sysconfdir}/ssl/certs/"
FILES_${PN}-examples = "${bindir}"

RDEPENDS_${PN} = "${PN}-cert"
RDEPENDS_${PN}-examples = "${PN}-cert"

ALLOW_EMPTY_${PN} = "1"

