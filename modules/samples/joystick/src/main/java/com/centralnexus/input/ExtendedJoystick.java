/*
THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. NEITHER RECIPIENT NOR
ANY CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING WITHOUT
LIMITATION LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THE PROGRAM
OR THE EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGES.

The name of the Copyright Holder may not be used to endorse or promote
products derived from this software without specific prior written permission.

Copyright 2000-2002 George Rhoten and others.

*/

package com.centralnexus.input;

import java.io.IOException;

/**
 * Device driver to a Windows extended joystick.
 * Windows only supports two joysticks even though the standard driver
 * can handle more.  Unless you need to use a strange joystick, use the
 * simpler and slightly faster Joystick class that this class is derived
 * from. This joystick can handle up to 32 buttons.  It can also handle
 * the x, y, z, r, u, v, and POV values.  You can consider the r, u, v
 * values as rotation values around any given axis, but this is only a guess.
 * A POV value is a point-of-view value.  Some joysticks handle four points
 * of view, while others handle a wide range of values from 0.00-359.00
 * degrees.
 *
 * @author George Rhoten
 * @since June 10, 2000
 */
class ExtendedJoystick extends Joystick {

/* Special internal flags for returning the specific axes
    public static final int RETURNX     = 0x00000001;
    public static final int RETURNY     = 0x00000002;
    public static final int RETURNZ     = 0x00000004;
    public static final int RETURNR     = 0x00000008;
    public static final int RETURNU     = 0x00000010;   // axis 5
    public static final int RETURNV     = 0x00000020;   // axis 6
    public static final int RETURNPOV       = 0x00000040;
    public static final int RETURNBUTTONS   = 0x00000080;
    public static final int RETURNRAWDATA   = 0x00000100;
    public static final int RETURNPOVCTS    = 0x00000200;
    public static final int RETURNCENTERED  = 0x00000400;
    public static final int USEDEADZONE     = 0x00000800;
    public static final int RETURNALL       = RETURNX | RETURNY | RETURNZ
                 | RETURNR | RETURNU | RETURNV
                 | RETURNPOV | RETURNBUTTONS;
*/

    /**
     * Returns true when the joystick is plugged into the computer, false
     * otherwise.
     * @param id The ID of the joystick where 0 <= id < getNumDevs().
     */
    public native synchronized static boolean isPluggedIn(int id);

    native synchronized static int poll(int id, float axisValues[]);

    /**
     * Start using a joystick with a specific id. This should be used when
     * you know a specific joystick is plugged into the computer.
     *
     * @param id The joystick id to get joystick information from.
     * @throws IOException Thrown when the joystick for the id
     *          is not plugged into the computer.
     */
    ExtendedJoystick(int id) throws IOException {
        super(id);
    }

    /**
     * This polls (updates) the joystick for its values. This must be called
     * after the owner is done with the old values.
     */
    public void poll() {
        myJoyNotifier.buttonValues = poll(getID(), axisValues);
    }

}
