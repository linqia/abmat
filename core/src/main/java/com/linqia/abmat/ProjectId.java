package com.linqia.abmat;

import com.google.common.base.Strings;

/**
 * Represents the maven id triple.
 */
public final class ProjectId {

    public static ProjectId parse(String identifier) {
        if (Strings.isNullOrEmpty(identifier)) {
            throw new IllegalArgumentException();
        }
        String[] split = identifier.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException(
                    "Could not split identifier properly");
        }
        return new ProjectId(split[0], split[1], split[2]);
    }

    private final String m_groupId;
    private final String m_artifactId;
    private final String m_version;
    private final String m_triple;
    private final String m_double;

    public ProjectId(String groupId, String artifactId, String version) {
        if (Strings.isNullOrEmpty(groupId) || Strings.isNullOrEmpty(artifactId)
                || Strings.isNullOrEmpty(version)) {
            throw new IllegalArgumentException();
        }
        m_groupId = groupId;
        m_artifactId = artifactId;
        m_version = version;
        m_triple = m_groupId + ":" + m_artifactId + ":" + m_version;
        m_double = m_groupId + ":" + m_artifactId;
    }

    public String getGroupId() {
        return m_groupId;
    }

    public String getArtifactId() {
        return m_artifactId;
    }

    public String getVersion() {
        return m_version;
    }

    public String getVersionedString() {
        return m_triple;
    }

    public String getVersionlessString() {
        return m_double;
    }

    public boolean sameIgnoringVersion(ProjectId other) {
        return m_groupId.equals(other.m_groupId)
                && m_artifactId.equals(other.m_artifactId);
    }

    @Override
    public int hashCode() {
        return m_triple.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return m_triple.equals(((ProjectId) obj).m_triple);
    }

    @Override
    public String toString() {
        return m_triple;
    }
}
