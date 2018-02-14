package org.odk.collect.android.location;

public class PointReadOnlyDraggableTest extends PointReadOnlyTest {

    // isDraggable being true shouldn't effect ReadOnly:
    @Override
    protected boolean isDraggable() {
        return true;
    }
}
