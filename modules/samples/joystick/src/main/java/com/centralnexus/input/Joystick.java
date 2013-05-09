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

Copyright 2000-2001 George Rhoten and others.

*/

package com.centralnexus.input;

import java.io.IOException;

/**
 * <p>
 * Device driver to a Windows joystick. 
 * This handles at least an x,y motion joystick. A joystick can be plugged
 * in as any id, and the first joystick id may or may not be plugged in.
 * In order to create a Joystick, you must use one of createInstance functions.
 * </p>
 * <p>
 * There are two ways to update the axis and button values. You can either:
 * <ol>
 *   <li>Add a JoystickListener to the joystick with addJoystickListener().
 *   </li>
 *   <li>Use the poll() function from you own thread.
 *   </li>
 * </ol>
 * </p>
 *
 * @see JoystickListener
 * @author George Rhoten
 * @since June 10, 2000
 */
public class Joystick {

// Joystick driver capabilites
// These are equivalent to JOYCAPS in windows.

    /** Does this joystick have a z-axis capability? */
    public static final int HAS_Z       = 0x0001;

    /** Does this joystick have a r-axis capability? */
    public static final int HAS_R       = 0x0002;

    /** Does this joystick have a u-axis capability? */
    public static final int HAS_U       = 0x0004;

    /** Does this joystick have a v-axis capability? */
    public static final int HAS_V       = 0x0008;

    /** Does this joystick have a point-of-view control capability? */
    public static final int HAS_POV     = 0x0010;

    /**
     * Does this joystick point-of-view support discrete values
     * capability (centered, forward, backward, left, and right)?
     */
    public static final int HAS_POV4DIR = 0x0020;

    /**
     * Does this joystick point-of-view support continuous degree bearings
     * capability?
     */
    public static final int HAS_POVCONT = 0x0040;

    /**
     * Point-of-view hat is in the neutral position. The value -1 means the
     * point-of-view hat has no angle to report.
     */
    public static final float POV_CENTERED     = -1.0f/100.0f;

    /**
     * Point-of-view hat is pressed forward. The value 0 represents an
     * orientation of 0.00 degrees (straight ahead).
     */
    public static final float POV_FORWARD      = 0.0f;

    /**
     * Point-of-view hat is pressed to the right. The value 9,000 represents
     * an orientation of 90.00 degrees (to the right).
     */
    public static final float POV_RIGHT        = 90.00f;

    /**
     * Point-of-view hat is pressed backward. The value 18,000 represents
     * an orientation of 180.00 degrees (to the rear).
     */
    public static final float POV_BACKWARD     = 180.00f;

    /**
     * Point-of-view hat is being pressed to the left. The value 27,000
     * represents an orientation of 270.00 degrees (90.00 degrees to the left).
     */
    public static final float POV_LEFT         = 270.00f;

    /** These are the standard buttons. */
    public static final int BUTTON1 = 0x0001;
    public static final int BUTTON2 = 0x0002;
    public static final int BUTTON3 = 0x0004;
    public static final int BUTTON4 = 0x0008;   // Last button
    /** These are the extended buttons */
    public static final int BUTTON5  = 0x00000010;
    public static final int BUTTON6  = 0x00000020;
    public static final int BUTTON7  = 0x00000040;
    public static final int BUTTON8  = 0x00000080;
    public static final int BUTTON9  = 0x00000100;
    public static final int BUTTON10 = 0x00000200;
    public static final int BUTTON11 = 0x00000400;
    public static final int BUTTON12 = 0x00000800;
    public static final int BUTTON13 = 0x00001000;
    public static final int BUTTON14 = 0x00002000;
    public static final int BUTTON15 = 0x00004000;
    public static final int BUTTON16 = 0x00008000;
    public static final int BUTTON17 = 0x00010000;
    public static final int BUTTON18 = 0x00020000;
    public static final int BUTTON19 = 0x00040000;
    public static final int BUTTON20 = 0x00080000;
    public static final int BUTTON21 = 0x00100000;
    public static final int BUTTON22 = 0x00200000;
    public static final int BUTTON23 = 0x00400000;
    public static final int BUTTON24 = 0x00800000;
    public static final int BUTTON25 = 0x01000000;
    public static final int BUTTON26 = 0x02000000;
    public static final int BUTTON27 = 0x04000000;
    public static final int BUTTON28 = 0x08000000;
    public static final int BUTTON29 = 0x10000000;
    public static final int BUTTON30 = 0x20000000;
    public static final int BUTTON31 = 0x40000000;
    public static final int BUTTON32 = 0x80000000;

/*  //Not known to be used.  Maybe used for a joystick listener.
    public static final int BUTTON1CHG = 0x0100;
    public static final int BUTTON2CHG = 0x0200;
    public static final int BUTTON3CHG = 0x0400;
    public static final int BUTTON4CHG = 0x0800;
*/

    /** Constant for axisValues */
    static final int AXIS_TOTAL = 7;
    /** Constant for axisValues */
    private static final int AXIS_X     = 0;
    /** Constant for axisValues */
    private static final int AXIS_Y     = 1;
    /** Constant for axisValues */
    private static final int AXIS_Z     = 2;
    /** Constant for axisValues */
    private static final int AXIS_R     = 3;
    /** Constant for axisValues */
    private static final int AXIS_U     = 4;
    /** Constant for axisValues */
    private static final int AXIS_V     = 5;
    /** Constant for axisValues */
    private static final int AXIS_POV   = AXIS_TOTAL - 1;

    /** Singleton JoystickNotifiers that creates joystick events */
    private static JoystickNotifier joyNotifier[];

    /** The joystick id. Typically 0 or 1 */
    private int id = 0;
    /** The joystick id. Typically 0 or 1 */
    private float deadZone = 0.0f;
    /** The cache of capabilities bits */
    private int capabilities = 0;
    /** The cache of current axis values */
    float axisValues[];
    /** This is who notifies me */
    JoystickNotifier myJoyNotifier;

    static {
        try {
            new jjstick();
            joyNotifier = new JoystickNotifier[getNumDevices()];
        }
        catch (UnsatisfiedLinkError e) {
            e.fillInStackTrace();
            throw e;
        }
    }

    /**
     * Returns the number of joysticks supported by the joystick driver or zero
     * when no driver is present.
     */
    public native static final int getNumDevices();

    /**
     * Returns true when the joystick is plugged into the computer, false
     * otherwise.  This function may do some initialization to get the
     * joystick working.
     * @param id The ID of the joystick where 0 <= id < getNumDevs().
     */
    public native synchronized static boolean isPluggedIn(int id);

    native synchronized static int poll(int id, float axisValues[]);
    private native static int getCapabilities(int id);

    private native static int getNumButtons(int id);
    private native static int getNumAxes(int id);
    private native static String toString(int id);

    Joystick(int id) throws IOException {
        if (isPluggedIn(id)) {  // Must be called before anything else!
            this.id = id;
            if (joyNotifier[id] == null)
            {
                joyNotifier[id] = new JoystickNotifier(this);
            }
            myJoyNotifier = joyNotifier[id];
            axisValues = joyNotifier[id].axisValues;
            capabilities = getCapabilities(id);
            myJoyNotifier.buttonValues = poll(id, axisValues);
        }
        else {
            throw new IOException("Invalid joystick ID: " + id);
        }
    }

    /**
     * Start using the first available joystick.
     * @throws IOException Thrown when a joystick
     *          is not plugged into the computer.
     */
    public static Joystick createInstance() throws IOException {
        int maxID = getNumDevices();
        int id = 0;

        while (id < maxID && !isPluggedIn(id)) {
            id++;
        }
        if (id >= maxID) {
            throw new IOException("Joystick not found.");
        }
        return createInstance(id);
    }

    /**
     * Start using a joystick with a specific id. This should be used when
     * you know a specific joystick is plugged into the computer.
     *
     * @param id The joystick id to get joystick information from.
     * @throws IOException Thrown when the joystick for the id
     *          is not plugged into the computer.
     */
    public static Joystick createInstance(int id) throws IOException {
        if (id < 0 || getNumDevices() <= id) {
            throw new IOException("Invalid joystick ID: " + id);
        }
        int capabilities = getCapabilities(id);
        Joystick newJoystick;
        if (capabilities == 0 || capabilities == HAS_Z) {
            newJoystick = new Joystick(id);
        }
        else {
            newJoystick = new ExtendedJoystick(id);
        }
        return newJoystick;
    }

    /**
     * The joystick id for the joystick connected to the computer.
     * The ID numbers have a range of 0 <= id < getNumDevices()
     * @see #getNumDevices()
     */
    public final int getID() {
        return id;
    }

    /**
     * This polls (updates) the joystick for its values. This must be called
     * after the owner is done with the old values.
     */
    public void poll() {
        myJoyNotifier.buttonValues = poll(id, axisValues);
    }

    /**
     * Get the Capability bits. These bits can be ORed together.
     * @see #getCapability(int)
     */
    public int getCapabilities() {
        return capabilities;
    }

    /**
     * Is a certain capability bit turned on?
     * @see #HAS_Z
     * @see #HAS_R
     * @see #HAS_U
     * @see #HAS_V
     * @see #HAS_POV
     * @see #HAS_POV4DIR
     * @see #HAS_POVCONT
     */
    public final boolean getCapability(int capability) {
        return (capabilities & capability) == capability;
    }

    /**
     * Returns the axis value, or 0 if the axis value is inside the deadZone.
     */
    final float removeDeadZone(float axis) {
        return (axis <= -deadZone || deadZone <= axis) ? axis : 0.0f;
    }

    /**
     * The x value of a joystick has a range from -1 to 1.
     */
    public synchronized float getX() {
        return removeDeadZone(axisValues[AXIS_X]);
    }

    /**
     * The y value of a joystick has a range from -1 to 1.
     */
    public synchronized float getY() {
        return removeDeadZone(axisValues[AXIS_Y]);
    }

    /**
     * The z value of a joystick has a range from -1 to 1.
     * @see #getCapabilities()
     */
    public synchronized float getZ() {
        if ((capabilities & HAS_Z) == HAS_Z) {
            return removeDeadZone(axisValues[AXIS_Z]);
        }
        return 0.0f;
    }

    /**
     * The r value of a joystick has a range from -1 to 1.
     * @return the rudder value (4th axis of movement)
     * @see #getCapabilities()
     */
    public synchronized float getR() {
        if ((capabilities & HAS_R) == HAS_R) {
            return removeDeadZone(axisValues[AXIS_R]);
        }
        return 0.0f;
    }

    /**
     * The u value of a joystick has a range from -1 to 1.
     * @return the u value (5th axis of movement)
     * @see #getCapabilities()
     */
    public synchronized float getU() {
        if ((capabilities & HAS_U) == HAS_U) {
            return removeDeadZone(axisValues[AXIS_U]);
        }
        return 0.0f;
    }

    /**
     * The v value of a joystick has a range from -1 to 1.
     * @return the v value (6th axis of movement)
     * @see #getCapabilities()
     */
    public synchronized float getV() {
        if ((capabilities & HAS_V) == HAS_V) {
            return removeDeadZone(axisValues[AXIS_V]);
        }
        return 0.0f;
    }

    /**
     * Current position of the point-of-view control. Values for this member
     * are in the range 0 through 359.00. These values represent the angle,
     * in degrees.
     * @return the point of view
     * @see #getCapabilities()
     */
    public synchronized float getPOV() {
        if ((capabilities & HAS_POV) == HAS_POV) {
            return axisValues[AXIS_POV];
        }
        return 0;
    }

    /**
     * Current state of joystick buttons. To see which buttons are pressed,
     * "&" the result with one of the BUTTON constants.
     * @return the bits representing each button.
     * @see Joystick#BUTTON1
     * @see Joystick#BUTTON2
     * @see Joystick#BUTTON3
     * @see Joystick#BUTTON4
     */
    public int getButtons() {
        return myJoyNotifier.buttonValues;
    }

    /**
     * Current state of a specific joystick button.
     * @param button can be BUTTON1, BUTTON2, BUTTON3 and so on.
     * @return true if the button is being pressed, false otherwise.
     * @see Joystick#BUTTON1
     * @see Joystick#BUTTON2
     * @see Joystick#BUTTON3
     * @see Joystick#BUTTON4
     */
    public boolean isButtonDown(int button) {
        return (myJoyNotifier.buttonValues & button) == button;
    }

    /** Number of buttons on the joystick. */
    public int getNumButtons() {
        return getNumButtons(id);
    }

    /** Number of axes currently in use by the joystick. */
    public int getNumAxes() {
        return getNumAxes(id);
    }

    /** Size of the dead zone. The default value is 0.0. */
    public final float getDeadZone() {
        return deadZone;
    }

    /**
     * Size of the dead zone. The dead zone is the range of values of each
     * axis that returns 0. For example, when the deadZone = 0.1 and
     * joystick(x, y, z) = (-0.09, 0.5, 0.1), then
     * joystick(x, y, z) = (0.0, 0.5, 0.0).
     * @throws IllegalArgumentException when deadZone is out of the range
     *          0 <= deadZone <= 1.0.
     */
    public final void setDeadZone(float deadZoneVal) {
        if (0.0f <= deadZoneVal && deadZoneVal <= 1.0f) {
            deadZone = deadZoneVal;
        }
        else {
            throw new IllegalArgumentException("Dead Zone needs to be in the range 0 and 1: "
             + deadZoneVal);
        }
    }

    /**
     * Synonym for setDeadZone(float)
     */
    public final void setDeadZone(double deadZoneVal) {
        setDeadZone((float)deadZoneVal);
    }

    /**
     * Adds the specified joystick listener to receive joystick events
     * from this joystick. If l is null, no exception is thrown
     * and no action is performed.
     * @param l The joystick listener
     * @param notifyOnChangeOnly Notify the listener of changes only.
     *        Setting this parameter to false will poll the joystick
     *        at a regular interval.
     */
    public void addJoystickListener(JoystickListener l) {
        myJoyNotifier.addJoystickListener(this, l);
    }

    /**
     * Removes the specified joystick listener so that it no longer
     * receives joystick events from this joystick. If l is null,
     * no exception is thrown and no action is performed.
     * @param l The joystick listener
     */
    public void removeJoystickListener(JoystickListener l) {
        myJoyNotifier.removeJoystickListener(this, l);
    }

    /**
     * Set the time in milliseconds that the JoystickListeners get notified of 
     * this joystick events.
     */
    public void setPollInterval(int pollMillis) {
        myJoyNotifier.setPollInterval(pollMillis);
    }

    /**
     * Get the time in milliseconds that the JoystickListeners get notified of 
     * this joystick events.
     */
    public int getPollInterval() {
        return myJoyNotifier.getPollInterval();
    }

    /** Text description of this joystick without the axis values */
    public String toString() {
        return toString(id) + " [id=" + id + "]";
    }
}

/** 
 * This is used only for JDKs that only allow you to load a library 
 * with the same class name.
 */
class jjstick {
    static {
        try {
            System.loadLibrary("jjstick");
        }
        catch (UnsatisfiedLinkError e) {
            e.fillInStackTrace();
            throw e;
        }
    }
}
