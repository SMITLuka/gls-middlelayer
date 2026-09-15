package hr.smit.gls.client;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class GlsPasswordEncoderTest {

    @Test
    void hashIsSha512LengthAndDeterministic() {
        int[] first = GlsPasswordEncoder.toUnsignedByteArray("test-password");
        int[] second = GlsPasswordEncoder.toUnsignedByteArray("test-password");

        assertThat(first).hasSize(64);
        assertThat(first).isEqualTo(second);
        assertThat(IntStream.of(first).allMatch(b -> b >= 0 && b <= 255)).isTrue();
    }

    @Test
    void differentPasswordsHashDifferently() {
        int[] a = GlsPasswordEncoder.toUnsignedByteArray("password-a");
        int[] b = GlsPasswordEncoder.toUnsignedByteArray("password-b");

        assertThat(a).isNotEqualTo(b);
    }
}
