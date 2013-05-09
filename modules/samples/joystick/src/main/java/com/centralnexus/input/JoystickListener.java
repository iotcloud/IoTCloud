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
 * The listener interface for receiving joystick events. A Joystick
 * will periodically notify the implementor of this listener of changes
 * to the attached joystick. It is <b>not</b> neccessary to call the
 * poll function on the joystick when this interface is implemented.
 *
 * @see Joystick
 * @author George Rhoten
 * @author Ed Burns <edburns@acm.org>
 * @since July 8, 2001
 */
public interface JoystickListener
{
    /**
     * Implement this function to get periodically notified that
     * a joystick changed one of its axis values.
     *
     * @param j The joystick that was recently polled.
     */
    public void joystickAxisChanged(Joystick j);

    /**
     * Implement this function to get periodically notified that
     * a joystick button changed one of its values.
     *
     * @param j The joystick that was recently polled.
     */
    public void joystickButtonChanged(Joystick j);
}
