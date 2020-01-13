package com.linagora.android.linshare.utils

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.FileSize
import com.linagora.android.linshare.util.FileSize.SizeFormat.LONG
import com.linagora.android.linshare.util.FileSize.SizeFormat.SHORT
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class FileSizeTest {

    @Nested
    inner class ValidateSize {

        private val VALID_SIZES = listOf(0L, 1L, 871L, 999L, 99999999L, 999999999999999999L, Long.MAX_VALUE)

        private val INVALID_SIZES = listOf(-1L, -9L, -999999999999999999L, Long.MIN_VALUE)

        @Test
        fun validateFileSize() {
            for (size in VALID_SIZES) {
                try {
                    FileSize(size)
                } catch (throwable: Throwable) {
                    fail(throwable)
                }
            }

            for (invalid_size in INVALID_SIZES) {
                assertThrows<IllegalArgumentException> {
                    FileSize(invalid_size)
                }
            }
        }
    }

    @Nested
    inner class PetabyteInLong {

        @Test
        fun formatSizeShouldReturnSizeFormatWithMaxValueOfLong() {
            assertThat(FileSize(Long.MAX_VALUE)
                    .format(SHORT))
                .isEqualTo("9223.4 PB")

            assertThat(FileSize(Long.MAX_VALUE)
                    .format(LONG))
                .isEqualTo("9223.37 PB")
        }

        @Test
        fun formatSizeShouldReturnALongFormat() {
            assertThat(FileSize(6000000000000001)
                    .format(LONG))
                .isEqualTo("6.00 PB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5914907503145698)
                    .format(LONG))
                .isEqualTo("5.91 PB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5917907503698145)
                    .format(LONG))
                .isEqualTo("5.92 PB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInTBAndGreaterThanThreshold() {
            assertThat(FileSize(998907503498341)
                    .format(LONG))
                .isEqualTo("1.00 PB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInTBAndLessThanThreshold() {
            assertThat(FileSize(901707503976518)
                    .format(LONG))
                .isEqualTo("0.90 PB")
        }
    }

    @Nested
    inner class PetabyteInShort {
        @Test
        fun formatSizeShouldReturnAShortFormat() {
            assertThat(FileSize(6000000000987999)
                    .format(SHORT))
                .isEqualTo("6.0 PB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5947907503999999)
                    .format(SHORT))
                .isEqualTo("5.9 PB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5967907503000000)
                    .format(SHORT))
                .isEqualTo("6.0 PB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInTBAndGreaterThanThreshold() {
            assertThat(FileSize(998907503000999)
                    .format(SHORT))
                .isEqualTo("1.0 PB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInTBAndLessThanThreshold() {
            assertThat(FileSize(901707503999999)
                    .format(SHORT))
                .isEqualTo("0.9 PB")
        }
    }

    @Nested
    inner class TerabyteInLong {
        @Test
        fun formatSizeShouldReturnALongFormat() {
            assertThat(FileSize(6000000000100)
                    .format(LONG))
                .isEqualTo("6.00 TB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5914907503987)
                    .format(LONG))
                .isEqualTo("5.91 TB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5917907503987)
                    .format(LONG))
                .isEqualTo("5.92 TB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInGBAndGreaterThanThreshold() {
            assertThat(FileSize(998907503654)
                    .format(LONG))
                .isEqualTo("1.00 TB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInGBAndLessThanThreshold() {
            assertThat(FileSize(901707503123)
                    .format(LONG))
                .isEqualTo("0.90 TB")
        }
    }

    @Nested
    inner class TerabyteInShort {
        @Test
        fun formatSizeShouldReturnAShortFormat() {
            assertThat(FileSize(6000000000987)
                    .format(SHORT))
                .isEqualTo("6.0 TB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5947907503999)
                    .format(SHORT))
                .isEqualTo("5.9 TB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5967907503000)
                    .format(SHORT))
                .isEqualTo("6.0 TB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInGBAndGreaterThanThreshold() {
            assertThat(FileSize(998907503000)
                    .format(SHORT))
                .isEqualTo("1.0 TB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInGBAndLessThanThreshold() {
            assertThat(FileSize(901707503999)
                    .format(SHORT))
                .isEqualTo("0.9 TB")
        }
    }

    @Nested
    inner class GigabyteInShort {

        @Test
        fun formatSizeShouldReturnAShortFormat() {
            assertThat(FileSize(6000000000)
                    .format(SHORT))
                .isEqualTo("6.0 GB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5947907503)
                    .format(SHORT))
                .isEqualTo("5.9 GB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5967907503)
                    .format(SHORT))
                .isEqualTo("6.0 GB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInMBAndGreaterThanThreshold() {
            assertThat(FileSize(998907503)
                    .format(SHORT))
                .isEqualTo("1.0 GB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInMBAndLessThanThreshold() {
            assertThat(FileSize(901707503)
                    .format(SHORT))
                .isEqualTo("0.9 GB")
        }
    }

    @Nested
    inner class GigabyteInLong {

        @Test
        fun formatSizeShouldReturnALongFormat() {
            assertThat(FileSize(6000000000)
                    .format(LONG))
                .isEqualTo("6.00 GB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5914907503)
                    .format(LONG))
                .isEqualTo("5.91 GB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5917907503)
                    .format(LONG))
                .isEqualTo("5.92 GB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInMBAndGreaterThanThreshold() {
            assertThat(FileSize(998907503)
                    .format(LONG))
                .isEqualTo("1.00 GB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInMBAndLessThanThreshold() {
            assertThat(FileSize(901707503)
                    .format(LONG))
                .isEqualTo("0.90 GB")
        }
    }

    @Nested
    inner class MegabyteInShort {
        @Test
        fun formatSizeShouldReturnAShortFormat() {
            assertThat(FileSize(6000000)
                    .format(SHORT))
                .isEqualTo("6.0 MB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5947907)
                    .format(SHORT))
                .isEqualTo("5.9 MB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5967907)
                    .format(SHORT))
                .isEqualTo("6.0 MB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInKBAndGreaterThanThreshold() {
            assertThat(FileSize(998907)
                    .format(SHORT))
                .isEqualTo("1.0 MB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInKBAndLessThanThreshold() {
            assertThat(FileSize(901707)
                    .format(SHORT))
                .isEqualTo("0.9 MB")
        }
    }

    @Nested
    inner class MegabyteInLong {

        @Test
        fun formatSizeShouldReturnALongFormat() {
            assertThat(FileSize(6000000)
                    .format(LONG))
                .isEqualTo("6.00 MB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5914907)
                    .format(LONG))
                .isEqualTo("5.91 MB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5917907)
                    .format(LONG))
                .isEqualTo("5.92 MB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInKBAndGreaterThanThreshold() {
            assertThat(FileSize(998907)
                    .format(LONG))
                .isEqualTo("1.00 MB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInKBAndLessThanThreshold() {
            assertThat(FileSize(901707)
                    .format(LONG))
                .isEqualTo("0.90 MB")
        }
    }

    @Nested
    inner class KilobyteInShort {

        @Test
        fun formatSizeShouldReturnAShortFormat() {
            assertThat(FileSize(6000)
                    .format(SHORT))
                .isEqualTo("6.0 KB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5947)
                    .format(SHORT))
                .isEqualTo("5.9 KB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5967)
                    .format(SHORT))
                .isEqualTo("6.0 KB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInBAndGreaterThanThreshold() {
            assertThat(FileSize(998)
                    .format(SHORT))
                .isEqualTo("1.0 KB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInBAndLessThanThreshold() {
            assertThat(FileSize(901)
                    .format(SHORT))
                .isEqualTo("0.9 KB")
        }
    }

    @Nested
    inner class KilobyteInLong {
        @Test
        fun formatSizeShouldReturnALongFormat() {
            assertThat(FileSize(6000)
                    .format(LONG))
                .isEqualTo("6.00 KB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatFloorWhileSizeNeedRound() {
            assertThat(FileSize(5914)
                    .format(LONG))
                .isEqualTo("5.91 KB")
        }

        @Test
        fun formatSizeShouldReturnALongFormatCeilWhileSizeNeedRound() {
            assertThat(FileSize(5917)
                    .format(LONG))
                .isEqualTo("5.92 KB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWhileSizeInBAndGreaterThanThreshold() {
            assertThat(FileSize(998)
                    .format(LONG))
                .isEqualTo("1.00 KB")
        }

        @Test
        fun formatSizeShouldReturnAShortFormatWithWhileSizeInBAndLessThanThreshold() {
            assertThat(FileSize(901)
                    .format(LONG))
                .isEqualTo("0.90 KB")
        }
    }

    @Nested
    inner class ByteFormat {

        @Test
        fun formatSizeShouldReturnSameFormat() {
            assertThat(FileSize(800)
                    .format(LONG))
                .isEqualTo("800 B")

            assertThat(FileSize(800)
                    .format(SHORT))
                .isEqualTo("800 B")
        }

        @Test
        fun formatSizeShouldReturnSameFormatInByteWhenSizeUnderThreshold() {
            assertThat(FileSize(871)
                    .format(SHORT))
                .isEqualTo("871 B")

            assertThat(FileSize(871)
                    .format(LONG))
                .isEqualTo("871 B")
        }

        @Test
        fun formatSizeShouldReturnShortFormatInKilobyteWhenSizeOverThreshold() {
            assertThat(FileSize(971)
                    .format(SHORT))
                .isEqualTo("1.0 KB")
        }

        @Test
        fun formatSizeShouldReturnLongFormatInKilobyteWhenSizeOverThreshold() {
            assertThat(FileSize(971)
                        .format(LONG))
                .isEqualTo("0.97 KB")
        }
    }
}
