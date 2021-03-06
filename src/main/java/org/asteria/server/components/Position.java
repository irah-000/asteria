package org.asteria.server.components;

import org.asteria.utils.Vec3d;
import org.entityflow.component.ComponentBase;

/**
 * Position in a (solar) system.
 */
public final class Position extends ComponentBase {

    /**
     * Position of the entity in the current system.
     */
    public final Vec3d pos = new Vec3d();

}
