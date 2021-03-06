package com.jivesoftware.android.mobile.sdk.entity.value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Commonly used verbs.
 */
@ParametersAreNonnullByDefault
public enum JiveCoreVerb implements JiveCoreVerbValue {

    applied("jive:applied"),

    bookmarked("jive:bookmarked"),

    created("jive:created"),

    commented("jive:commented"),

    completed("jive:completed"),

    connected("jive:connected"),

    correct_answer_set("jive:correct_answer_set"),

    followed("jive:followed"),

    helped("jive:helped"),

    installed("jive:installed"),

    joined("jive:joined"),

    liked("jive:liked"),

    mentioned("jive:mentioned"),

    modified("jive:modified"),

    moved("jive:moved"),

    notification("jive:notification"),

    outcome_set("jive:outcome_set"),

    outlook_install_client("uri:jiveName:outlook_install_client"),

    promoted("jive:promoted"),

    rated("jive:rated"),

    read("jive:read"),

    replied("jive:replied"),

    repost("jive:repost"),

    shared("jive:shared"),

    unconnected("jive:unconnected"),

    unfollowed("jive:unfollowed"),

    unjoined("jive:unjoined"),

    unliked("jive:unliked"),

    unread("jive:unread"),

    updatedStatus("jive:updatedStatus"),

    viewed("jive:viewed"),

    voted("jive:voted");


    private static final Map<String, JiveCoreVerb> BY_REPRESENTATION;
    static {
        Map<String, JiveCoreVerb> map = new HashMap<String, JiveCoreVerb>();
        for (JiveCoreVerb verb : values()) {
            map.put(verb.representation, verb);
        }
        BY_REPRESENTATION = Collections.unmodifiableMap(map);
    }

    @Nonnull
    private final String representation;

    JiveCoreVerb(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Nullable
    public static JiveCoreVerb getByRepresentation(String stringForm) {
        return BY_REPRESENTATION.get(stringForm);
    }

}

