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

Copyright 2001-2002 George Rhoten and others.

*/

package com.centralnexus.input;

/**
 * Notifies the joystick listeners of Joystick events.
 * Only one of these notifiers should be created per joystick.
 * 
 * @author George Rhoten
 * @since July 8, 2001
 */
class JoystickNotifier implements Runnable
{
    /** A joystick used for polling. */
    private Joystick jstick;

    /** The thread used to poll one joystick. */
    private Thread pollingThread = null;

    /** polling interval for this joystick */
    private int interval = 50;

    /** Listeners for this joystick */
    private JoystickListener joyListeners[] = new JoystickListener[0];

    /**
     * Joysticks for the listeners. Multiple joysticks may be created,
     * but they all have the same data values. Keeping this array
     * makes it easier to use the operator== in Java.
     */
    private Joystick joysticks[] = new Joystick[0];

    /** axis values for all axis values on all joysticks with the same ID. */
    float axisValues[] = new float[Joystick.AXIS_TOTAL];

    /** Old axis values for all axis values. */
    private float axisValuesOld[] = new float[Joystick.AXIS_TOTAL];

    /** The cache of current button values */
    int buttonValues = 0;

    /** The Old cache of current button values */
    private int buttonValuesOld = 0;

    JoystickNotifier(Joystick js) {
        jstick = js;
    }

    /** Start polling the joystick. */
    public void start() {
        pollingThread.start();
    }

    /**
     * Add a listener to this joystick
     */
    public synchronized void addJoystickListener(Joystick j, JoystickListener l) {
        if (l != null) {
            JoystickListener newListeners[] = new JoystickListener[joyListeners.length + 1];
            Joystick newJoystickArr[] = new Joystick[joyListeners.length + 1];

            System.arraycopy(joyListeners, 0, newListeners, 0, joyListeners.length);
            System.arraycopy(joysticks, 0, newJoystickArr, 0, joysticks.length);

            newListeners[joyListeners.length] = l;
            newJoystickArr[joysticks.length] = j;

            joyListeners = newListeners;
            joysticks = newJoystickArr;

//            System.out.println("adding " + j);
            // This new listener needs to now the current state.
            joyListeners[joyListeners.length - 1].joystickAxisChanged(joysticks[joyListeners.length - 1]);
            joyListeners[joyListeners.length - 1].joystickButtonChanged(joysticks[joyListeners.length - 1]);

            if (pollingThread == null) {
                pollingThread = new Thread(this);
                pollingThread.start();
//                System.out.println("starting");
            }
        }
    }

    /**
     * Remove a listener to this joystick
     */
    public synchronized void removeJoystickListener(Joystick j, JoystickListener l) {
        if (l != null && joyListeners.length > 0) {
            JoystickListener newListeners[] = new JoystickListener[joyListeners.length - 1];
            Joystick newJoystickArr[] = new Joystick[joyListeners.length - 1];
            int idx;

            for (idx = 0; idx < newListeners.length
             && joyListeners[idx] != l && joysticks[idx] != j; idx++)
            {
                newListeners[idx] = joyListeners[idx];
                newJoystickArr[idx] = joysticks[idx];
            }
//            System.out.println(idx + " removed");
            if (idx < joyListeners.length) {
                if (0 < newListeners.length) {
                    System.arraycopy(joyListeners, idx + 1, newListeners, idx, newListeners.length - idx);
                    System.arraycopy(joysticks, idx + 1, newJoystickArr, idx, newJoystickArr.length - idx);
                }
                joyListeners = newListeners;
                joysticks = newJoystickArr;
            }
        }
    }

    public synchronized final void notifyJoystickListeners() {
        jstick.poll();

        boolean axisChanged = false;
        for (int idx = 0; idx < axisValues.length; idx++) {
            if (axisValues[idx] != axisValuesOld[idx]) {
                axisChanged = true;
                break;
            }
        }

        for (int listenNum = joyListeners.length - 1; listenNum >= 0; listenNum--)
        {
            if (axisChanged) {
//                System.out.println(jstick + " " + listenNum + " axis changed");
                joyListeners[listenNum].joystickAxisChanged(joysticks[listenNum]);
            }
            if (buttonValues != buttonValuesOld) {
//                System.out.println(jstick + " " + listenNum +" button changed");
                joyListeners[listenNum].joystickButtonChanged(joysticks[listenNum]);
            }
        }
        buttonValuesOld = buttonValues;
        System.arraycopy(axisValues, 0, axisValuesOld, 0, axisValues.length);
    }

    public void setPollInterval(int pollMillis) {
        interval = pollMillis;
    }

    public int getPollInterval() {
        return interval;
    }

    public void run()
    {
        while (joyListeners.length > 0) {
            notifyJoystickListeners();
            //System.out.println(jstick.toString());

            try {
                Thread.sleep(interval);
            }
            catch (InterruptedException e) {
                pollingThread = null;
                return;
            }
        }
        pollingThread = null;
    }
}
