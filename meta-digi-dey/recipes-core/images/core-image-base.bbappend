#
# Copyright (C) 2016 Digi International.
#

IMAGE_FEATURES += " \
    dey-network \
    eclipse-debug \
    package-management \
    ssh-server-dropbear \
    ${@bb.utils.contains('MACHINE_FEATURES', 'accel-video', 'dey-gstreamer', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'alsa', 'dey-audio', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'dey-bluetooth', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'wifi', 'dey-wireless', '', d)} \
"

CORE_IMAGE_BASE_INSTALL += "dey-examples-digiapix"

# SDK features (for toolchains generated from an image with populate_sdk)
SDKIMAGE_FEATURES ?= "dev-pkgs dbg-pkgs staticdev-pkgs"

# Add our dey-image tweaks to the final image (like /etc/build info)
inherit dey-image

# Do not install udev-cache
BAD_RECOMMENDATIONS += "udev-cache"
