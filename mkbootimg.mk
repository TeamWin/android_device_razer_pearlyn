LOCAL_PATH := $(call my-dir)

# Compress recovery ramdisk using XZ
KERNEL_IMAGE := $(PRODUCT_OUT)/kernel

BUILT_RAMDISK_CPIO := $(PRODUCT_OUT)/ramdisk-recovery.cpio
COMPRESS_COMMAND := lzma --force -9e

INSTALLED_RECOVERYIMAGE_TARGET := $(PRODUCT_OUT)/recovery.img
$(INSTALLED_RECOVERYIMAGE_TARGET): $(recovery_ramdisk)
	@echo "------- Compressing recovery ramdisk -------"
	$(hide) $(COMPRESS_COMMAND) "$(BUILT_RAMDISK_CPIO)"
	@echo "------- Making recovery image -------"
	$(hide) $(MKBOOTIMG) \
		--kernel $(KERNEL_IMAGE) \
		--ramdisk $(BUILT_RAMDISK_CPIO).lzma \
		--cmdline "$(BOARD_KERNEL_CMDLINE)" \
		--base $(BOARD_KERNEL_BASE) \
		--pagesize $(BOARD_KERNEL_PAGESIZE) \
		$(BOARD_MKBOOTIMG_ARGS) \
		-o $(INSTALLED_RECOVERYIMAGE_TARGET)
		$(hide) $(call assert-max-image-size,$@,$(BOARD_RECOVERYIMAGE_PARTITION_SIZE),raw)
	@echo "------- Made recovery image: $@ -------"

# Compress kernel ramdisk using LZMA LehKeda Edit
LZMA_BIN := $(shell which lzma)
LZMA_BOOT_RAMDISK := $(PRODUCT_OUT)/ramdisk-lzma.img

$(LZMA_BOOT_RAMDISK): $(BUILT_RAMDISK_TARGET)
	gunzip -f < $(BUILT_RAMDISK_TARGET) | lzma -e > $@

$(INSTALLED_BOOTIMAGE_TARGET): $(MKBOOTIMG) $(INTERNAL_BOOTIMAGE_FILES) $(LZMA_BOOT_RAMDISK)
	$(call pretty,"Target boot image: $@")
	$(hide) $(MKBOOTIMG) $(INTERNAL_BOOTIMAGE_ARGS) $(BOARD_MKBOOTIMG_ARGS) --kernel $(KERNEL_IMAGE) --output $@ --ramdisk $(LZMA_BOOT_RAMDISK)
	$(hide) $(call assert-max-image-size,$@,$(BOARD_BOOTIMAGE_PARTITION_SIZE),raw)
	@echo -e ${CL_CYN}"Made boot image: $@"${CL_RST}
