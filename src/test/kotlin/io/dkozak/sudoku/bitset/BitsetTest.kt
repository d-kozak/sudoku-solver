package io.dkozak.sudoku.bitset

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.util.*

/**
 * just to test if bitsets work the way we want them to
 */
class BitsetTest {

    @Test
    fun `set flip cardinality`() {
        val b = BitSet()
        assertThat(b.cardinality()).isEqualTo(0)
        assertThat(b[0]).isFalse()
        b.set(0)
        assertThat(b[0]).isTrue()
        assertThat(b.cardinality()).isEqualTo(1)
        assertThat(b[3]).isFalse()
        b.set(3)
        assertThat(b[3]).isTrue()
        assertThat(b.cardinality()).isEqualTo(2)
        b.flip(3)
        assertThat(b[3]).isFalse()
        assertThat(b.cardinality()).isEqualTo(1)
    }
}