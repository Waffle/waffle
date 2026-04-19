/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.jaas;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class GroupPrincipalTest.
 */
class GroupPrincipalTest {

    /** The group principal. */
    private GroupPrincipal groupPrincipal;

    /**
     * Equals_other object.
     */
    @Test
    void equals_otherObject() {
        Assertions.assertNotEquals("", this.groupPrincipal);
    }

    /**
     * Equals_same object.
     */
    @Test
    void equals_sameObject() {
        Assertions.assertEquals(this.groupPrincipal, this.groupPrincipal);
    }

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.groupPrincipal = new GroupPrincipal("localhost\\Administrator");
    }

    /**
     * Test equals_ symmetric.
     */
    @Test
    void testEquals_Symmetric() {
        final GroupPrincipal x = new GroupPrincipal("localhost\\Administrator");
        final GroupPrincipal y = new GroupPrincipal("localhost\\Administrator");
        Assertions.assertEquals(x, y);
        Assertions.assertEquals(x.hashCode(), y.hashCode());
    }

    /**
     * Test is serializable.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.groupPrincipal);
        }
        assertThat(out.toByteArray()).isNotEmpty();
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final GroupPrincipal copy = (GroupPrincipal) ois.readObject();
        // test
        Assertions.assertEquals(this.groupPrincipal, copy);
        Assertions.assertEquals(this.groupPrincipal.getName(), copy.getName());
    }

    /**
     * Test add member.
     */
    @Test
    void testAddMember() {
        final UserPrincipal member = new UserPrincipal("localhost\\user1");
        // addMember returns true if the user was already a member, false if it was just added
        Assertions.assertFalse(this.groupPrincipal.addMember(member));
        // Second add returns true (user was already a member)
        Assertions.assertTrue(this.groupPrincipal.addMember(member));
    }

    /**
     * Test is member.
     */
    @Test
    void testIsMember() {
        final UserPrincipal member = new UserPrincipal("localhost\\user1");
        Assertions.assertFalse(this.groupPrincipal.isMember(member));
        this.groupPrincipal.addMember(member);
        Assertions.assertTrue(this.groupPrincipal.isMember(member));
    }

    /**
     * Test is member via nested group.
     */
    @Test
    void testIsMemberNestedGroup() {
        final UserPrincipal member = new UserPrincipal("localhost\\user2");
        final GroupPrincipal nested = new GroupPrincipal("localhost\\nested");
        nested.addMember(member);
        this.groupPrincipal.addMember(nested);
        // member of nested group counts as member of parent
        Assertions.assertTrue(this.groupPrincipal.isMember(member));
        // a non-member is still not a member
        Assertions.assertFalse(this.groupPrincipal.isMember(new UserPrincipal("localhost\\nobody")));
    }

    /**
     * Test members enumeration.
     */
    @Test
    void testMembers() {
        Assertions.assertFalse(this.groupPrincipal.members().hasMoreElements());
        final UserPrincipal member = new UserPrincipal("localhost\\user1");
        this.groupPrincipal.addMember(member);
        Assertions.assertTrue(this.groupPrincipal.members().hasMoreElements());
    }

    /**
     * Test remove member.
     */
    @Test
    void testRemoveMember() {
        final UserPrincipal member = new UserPrincipal("localhost\\user1");
        // Removing non-existent member returns false
        Assertions.assertFalse(this.groupPrincipal.removeMember(member));
        this.groupPrincipal.addMember(member);
        // Removing existing member returns true
        Assertions.assertTrue(this.groupPrincipal.removeMember(member));
        // No longer a member
        Assertions.assertFalse(this.groupPrincipal.isMember(member));
    }

    /**
     * Test to string with members.
     */
    @Test
    void testToStringWithMembers() {
        final UserPrincipal member = new UserPrincipal("localhost\\user1");
        this.groupPrincipal.addMember(member);
        final String str = this.groupPrincipal.toString();
        Assertions.assertNotNull(str);
        Assertions.assertTrue(str.startsWith("localhost\\Administrator"));
        Assertions.assertTrue(str.contains("members:"));
    }

    /**
     * Test to string with no members.
     */
    @Test
    void testToStringWithNoMembers() {
        // GroupPrincipal with no members: toString replaces trailing comma with ')'
        // but when empty the format is "name(members:)" - setCharAt on '(' -> ')'
        final GroupPrincipal empty = new GroupPrincipal("emptyGroup");
        final String str = empty.toString();
        Assertions.assertNotNull(str);
        Assertions.assertTrue(str.startsWith("emptyGroup"));
    }

}
