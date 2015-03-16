package org.usfirst.frc.team5652.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;

public class ForkLift {

	private Joystick joystick;

	// Lift controls power
	private double lift_power_down = 0.45;
	private double lift_power_up = 1.0;
	private double lift_power_stop = 0.00;
	private double sensitivity = 1;

	// Limit switches for fork lift.
	private final boolean digital_io_enabled = false;
	private DigitalInput upperLimitSwitch = new LimitSwitch(0);
	private DigitalInput lowerLimitSwitch = new LimitSwitch(1);

	// Motors
	private Victor motor_5, motor_6, motor_7, motor_8;

	public ForkLift(Joystick joystick) {
		this.joystick = joystick;
		/*
		 * Setup the victors objects
		 * http://content.vexrobotics.com/docs/217-2769-Victor888UserManual.pdf
		 */
		motor_5 = new Victor(2);
		motor_6 = new Victor(3);
		motor_7 = new Victor(4);
		motor_8 = new Victor(5);
	}

	/*
	 * Code to lift the fork up
	 * 
	 * First it checks if the limit switch is triggered, If so, it won't go up
	 * anymore to protect the hardware else It will move up depending on the
	 * sensitivity mode and the amount of tween'd power At the end, it checks if
	 * it needs to stop the forklift because it hit a limit switch. TODO? Make
	 * it an interrupt, not a poll
	 */
	@SuppressWarnings("unused")
	public void up(double power) {
		// Check limit switch if digitalio enabled.
		if (!upperLimitSwitch.get() || !digital_io_enabled) {
			motor_5.set(lift_power_up * power);
			motor_6.set(sensitivity * -1 * lift_power_up * power);
			motor_7.set(sensitivity * lift_power_up * power);
			motor_8.set(sensitivity * -1 * lift_power_up * power);
		}

		if (upperLimitSwitch.get() && digital_io_enabled) {
			stop();
		}
	}

	public void up() {
		this.up(1);
	}

	/*
	 * Code to lift the fork down
	 * 
	 * First it checks if the limit switch is triggered, If so, it won't go down
	 * anymore to protect the hardware else It will move up depending on the
	 * sensitivity mode and the amount of tween'd power
	 * 
	 * At the end, it checks if it needs to stop the forklift because it hit a
	 * limit switch.
	 * 
	 * TODO? Make it an interrupt, not a poll
	 */
	@SuppressWarnings("unused")
	public void down(double current_power) {
		// Check limit switch only if dio enabled.
		if (!lowerLimitSwitch.get() || !digital_io_enabled) {
			motor_5.set(-1 * lift_power_down * current_power);
			motor_6.set(lift_power_down);
			motor_7.set(-1 * lift_power_down * current_power);
			motor_8.set(lift_power_down);
		}
		// Probably need this just in case
		if (lowerLimitSwitch.get() && digital_io_enabled) {
			stop();
		}
	}

	public void down() {
		this.down(1);
	}

	public void stop() {
		motor_5.set(lift_power_stop);
		motor_6.set(lift_power_stop);
		motor_7.set(lift_power_stop);
		motor_8.set(lift_power_stop);
	}
}
