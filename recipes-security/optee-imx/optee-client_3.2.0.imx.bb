# Copyright (C) 2017-2018 NXP

SUMMARY = "OPTEE Client libs"
HOMEPAGE = "http://www.optee.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

inherit python3native systemd

SRCBRANCH = "lf-5.4.y"
OPTEE_CLIENT_SRC ?= "git://source.codeaurora.org/external/imx/imx-optee-client.git;protocol=https"
SRC_URI = "${OPTEE_CLIENT_SRC};branch=${SRCBRANCH}"

SRCREV = "71a9bef78fff2d5d4db8a2307d3b91e2aa671dc9"

SRC_URI += "file://tee-supplicant.service \
            file://0001-flags-do-not-override-CFLAGS-from-host.patch \
"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE_${PN} = "tee-supplicant.service"

OPTEE_ARCH ?= "arm32"
OPTEE_ARCH_armv7a = "arm32"
OPTEE_ARCH_aarch64 = "arm64"

EXTRA_OEMAKE = "ARCH=${OPTEE_ARCH}"

do_install () {
	oe_runmake install

	install -D -p -m0644 ${S}/out/export/lib/libteec.so.1.0 ${D}${libdir}/libteec.so.1.0
	ln -sf libteec.so.1.0 ${D}${libdir}/libteec.so
	ln -sf libteec.so.1.0 ${D}${libdir}/libteec.so.1

	install -D -p -m0755 ${S}/out/export/bin/tee-supplicant ${D}${bindir}/tee-supplicant

	cp -a ${S}/out/export/include ${D}/usr/

	sed -i -e s:/etc:${sysconfdir}:g -e s:/usr/bin:${bindir}:g ${WORKDIR}/tee-supplicant.service
	install -D -p -m0644 ${WORKDIR}/tee-supplicant.service ${D}${systemd_system_unitdir}/tee-supplicant.service
}

PACKAGES += "tee-supplicant"
FILES_${PN} += "${libdir}/* ${includedir}/*"
FILES_tee-supplicant += "${bindir}/tee-supplicant"

INSANE_SKIP_${PN} = "ldflags dev-elf"
INSANE_SKIP_${PN}-dev = "ldflags dev-elf"
INSANE_SKIP_tee-supplicant = "ldflags"

COMPATIBLE_MACHINE = "(mx6|mx7|mx8)"
