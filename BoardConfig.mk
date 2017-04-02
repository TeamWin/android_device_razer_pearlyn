# Copyright (C) 2016 The Sayanogen Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


TARGET_OTA_ASSERT_DEVICE := pearlyn

# ADDED THE FOLLOWING TOWARDS A SUCCESSFUL BUILD
TARGET_ARCH := arm
TARGET_CPU_ABI := armeabi-v7a
TARGET_ARCH_VARIANT := armv7-a
TARGET_CPU_VARIANT := krait
TARGET_CPU_ABI2 := armeabi
ENABLE_CPUSETS := true
TARGET_NO_BOOTLOADER := true
TARGET_NO_RECOVERY := true
TARGET_NO_RADIOIMAGE := true
TARGET_BOARD_PLATFORM := apq8084
TARGET_BOARD_PLATFORM_GPU := qcom-adreno420
HAVE_ADRENO_SOURCE:= false

PEARLYN_PATH := device/razer/pearlyn

# Kernel
TARGET_KERNEL_VARIANT_CONFIG := pearlyn_defconfig
BOARD_CUSTOM_BOOTIMG_MK := $(PEARLYN_PATH)/mkbootimg.mk
BOARD_KERNEL_BASE := 0x00000000
BOARD_KERNEL_CMDLINE := console=null androidboot.hardware=qcom enforcing=0 androidboot.selinux=permissive user_debug=31 msm_rtb.filter=0x37 dwc3_msm.cpu_to_affin=1
BOARD_KERNEL_PAGESIZE := 4096
BOARD_KERNEL_SEPARATED_DT := true
BOARD_MKBOOTIMG_ARGS := --ramdisk_offset 0x02200000 --tags_offset 0x02000000 --second_offset 0x00f00000
TARGET_KERNEL_CONFIG := pearlyn_defconfig
TARGET_KERNEL_SOURCE := kernel/razer/pearlyn
KERNEL_TOOLCHAIN := $(ANDROID_BUILD_TOP)/prebuilts/gcc/$(HOST_OS)-x86/arm/arm-eabi-4.8/bin
TARGET_KERNEL_CROSS_COMPILE_PREFIX := arm-eabi-

WLAN_MODULES:
	mkdir -p $(KERNEL_MODULES_OUT)/qca_cld
	mv $(KERNEL_MODULES_OUT)/wlan.ko $(KERNEL_MODULES_OUT)/qca_cld/qca_cld_wlan.ko
	ln -sf /system/lib/modules/qca_cld/qca_cld_wlan.ko $(TARGET_OUT)/lib/modules/wlan.ko
  
TARGET_KERNEL_MODULES += WLAN_MODULES

# Partitions
BOARD_BOOTIMAGE_PARTITION_SIZE := 15309209
#BOARD_RECOVERYIMAGE_PARTITION_SIZE := 16148071 
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 1073741824
#BOARD_USERDATAIMAGE_PARTITION_SIZE := 3859718144
BOARD_FLASH_BLOCK_SIZE := 262144
TARGET_USERIMAGES_USE_EXT4 := true
TARGET_USERIMAGES_USE_F2FS := true

# HDMI
TARGET_HAVE_HDMI_OUT := true
TARGET_USE_HDMI_AS_PRIMARY := true

# Vold
#TARGET_HW_DISK_ENCRYPTION := true
BOARD_VOLD_DISC_HAS_MULTIPLE_MAJORS := true
BOARD_VOLD_EMMC_SHARES_DEV_MAJOR := true
BOARD_VOLD_MAX_PARTITIONS := 40

# inherit from the proprietary version
-include vendor/razer/pearlyn/BoardConfigVendor.mk

# inherit from the proprietary version
#-include vendor/google/atv/BoardConfigVendor.mk

# Bluetooth
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(PEARLYN_PATH)/bluetooth
BOARD_HAVE_BLUETOOTH_QCOM := true
BOARD_HAS_QCA_BT_ROME := true

# Audio
BOARD_USES_ALSA_AUDIO := true
BOARD_USES_TINY_ALSA_AUDIO := true
AUDIO_FEATURE_ENABLED_FLUENCE := true

# EGL
BOARD_EGL_CFG := $(PEARLYN_PATH)/egl/egl.cfg

# Power
TARGET_POWERHAL_VARIANT := qcom

# Media
TARGET_USES_ION := true

# Wifi
BOARD_HAS_QCOM_WLAN := true
BOARD_HAS_QCOM_WLAN_SDK := true
BOARD_WLAN_DEVICE := qcwcn
BOARD_HOSTAPD_DRIVER := NL80211
BOARD_HOSTAPD_PRIVATE_LIB := lib_driver_cmd_$(BOARD_WLAN_DEVICE)
BOARD_WPA_SUPPLICANT_DRIVER := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_$(BOARD_WLAN_DEVICE)
WIFI_DRIVER_MODULE_NAME := "wlan"
WIFI_DRIVER_MODULE_PATH := "/system/lib/modules/wlan.ko"
WPA_SUPPLICANT_VERSION := VER_0_8_X

# No NFC and CAMERA
BOARD_HAVE_NFC := false
BOARD_HAVE_CAMERA := false

# Graphics
USE_OPENGL_RENDERER := true

# Time
BOARD_USES_QC_TIME_SERVICES := true

# SELinux
-include device/qcom/sepolicy/sepolicy.mk

# Build a deodexed rom
DISABLE_DEXPREOPT=true
WITH_DEXPREOPT=false

# Ensure f2fstools are built
TARGET_USERIMAGES_USE_F2FS := true
