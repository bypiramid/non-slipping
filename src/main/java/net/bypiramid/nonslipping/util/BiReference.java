package net.bypiramid.nonslipping.util;

import com.google.common.collect.Sets;

import java.util.Set;

public class BiReference<A, B> {

    private A refA;
    private B refB;

    public BiReference() {
        this(null, null);
    }

    public BiReference(A refA, B refB) {
        this.refA = refA;
        this.refB = refB;
    }

    public A getRefA() {
        return refA;
    }

    public boolean hasRefA() {
        return refA != null;
    }

    public void setRefA(A refA) {
        this.refA = refA;
    }

    public B getRefB() {
        return refB;
    }

    public boolean hasRefB() {
        return refB != null;
    }

    public void setRefB(B refB) {
        this.refB = refB;
    }

    public Set<?> asSet() {
        return Sets.newHashSet(refA, refB);
    }
}
