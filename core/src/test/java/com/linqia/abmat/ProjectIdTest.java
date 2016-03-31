package com.linqia.abmat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ProjectIdTest {

    @Test
    public void simpleParse() {
        ProjectId id = ProjectId
                .parse("com.linqia.abmat:abmat-core:1.0-SNAPSHOT");
        assertThat(id.getGroupId()).isEqualTo("com.linqia.abmat");
        assertThat(id.getArtifactId()).isEqualTo("abmat-core");
        assertThat(id.getVersion()).isEqualTo("1.0-SNAPSHOT");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failedParse() {
        ProjectId.parse("com.linqia.abmat:abmat-core");
    }

    @Test
    public void testEquals() {
        ProjectId equal1 = ProjectId
                .parse("com.linqia.abmat:abmat-core:1.0-SNAPSHOT");
        ProjectId equal2 = ProjectId
                .parse("com.linqia.abmat:abmat-core:1.0-SNAPSHOT");
        assertThat(equal1.equals(equal2)).isTrue();
        assertThat(equal1.hashCode()).isEqualTo(equal2.hashCode());
        assertThat(equal1.sameIgnoringVersion(equal2)).isTrue();

        ProjectId differentVersion = ProjectId
                .parse("com.linqia.abmat:abmat-core:1.1-SNAPSHOT");
        assertThat(equal1.equals(differentVersion)).isFalse();
        assertThat(equal1.hashCode()).isNotEqualTo(differentVersion.hashCode());
        assertThat(equal1.sameIgnoringVersion(differentVersion)).isTrue();
    }

    @Test
    public void createStrings() {
        ProjectId id = new ProjectId("com.linqia.abmat", "abmat-core", "1.0");
        assertThat(id.getVersionlessString()).isEqualTo(
                "com.linqia.abmat:abmat-core");
        assertThat(id.getVersionedString()).isEqualTo(
                "com.linqia.abmat:abmat-core:1.0");
        assertThat(id.toString()).isEqualTo("com.linqia.abmat:abmat-core:1.0");
    }
}
