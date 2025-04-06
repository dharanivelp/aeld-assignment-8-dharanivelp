DESCRIPTION = "AESD LDD modules"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git@github.com/cu-ecen-aeld/assignment-7-dharanivelp.git;protocol=ssh;branch=main"

PV = "1.0+git${SRCPV}"
SRCREV = "b4bb8ceace573abd92c236656852728b05640eba"

S = "${WORKDIR}/git"

KERNEL_MODULE_PATH = "/lib/modules/${KERNEL_VERSION}"

inherit module
inherit update-rc.d

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} += "S98modules"

RPROVIDES:${PN} += "kernel-module-aesd-ldd"

EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"
EXTRA_OEMAKE += "CFLAGS='${CFLAGS} -I${S}/include'"

do_configure() {
    :
}

do_compile() {
    oe_runmake -C ${S}/misc-modules modules
    oe_runmake -C ${S}/scull modules
}

do_install() {

    install -d ${D}${KERNEL_MODULE_PATH}/extra/
    oe_runmake -C ${STAGING_KERNEL_DIR} M=${S}/misc-modules modules_install INSTALL_MOD_PATH=${D}
    oe_runmake -C ${STAGING_KERNEL_DIR} M=${S}/scull modules_install INSTALL_MOD_PATH=${D}

    install -d ${D}${base_bindir}
    install -d ${D}${sysconfdir}/init.d

    install -m 0755 ${S}/misc-modules/module_load ${D}${base_bindir}
    install -m 0755 ${S}/misc-modules/module_unload ${D}${base_bindir}
#    install -m 0755 ${S}/misc-modules/hello.ko ${D}${base_bindir}

    install -m 0755 ${S}/scull/scull_load ${D}${base_bindir}
    install -m 0755 ${S}/scull/scull_unload ${D}${base_bindir}
#    install -m 0755 ${S}/scull/scull.ko ${D}${base_bindir}

    install -m 0755 ${S}/init ${D}${sysconfdir}/init.d/S98modules

    depmod -a -b ${D} -r ${KERNEL_VERSION}
}

# Include all installed files in the main package
#FILES:${PN} += "${base_bindir}/module_load"
#FILES:${PN} += "${base_bindir}/module_unload"
#FILES:${PN} += "${base_bindir}/scull_load"
FILES:${PN} += "${base_bindir}/*"
FILES:${PN}-modules += "${KERNEL_MODULE_PATH}/extra/*.ko"
FILES:${PN} += "${KERNEL_MODULE_PATH}/modules.*"
FILES:${PN} += "${sysconfdir}/init.d/S98modules"
