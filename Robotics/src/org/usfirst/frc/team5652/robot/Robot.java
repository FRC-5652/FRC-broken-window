package org.usfirst.frc.team5652.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The
 */
public class Robot extends SampleRobot {
	// Robot drive settings (tank drive)
	private DriveSystem drivetrain;

	// Pneumatic arm code
	private PneumaticArm armature;

	// Joystick code
	private Joystick drive_stick;
	private Joystick forklift_stick;

	private ForkLift forklift;

	// Joystick buttons
	private Button btn_lift_up, btn_lift_down, btn_pneu_close, btn_pneu_open;

	// Change to false to use image processing code.
	static private boolean IS_VISION_SIMPLE = true;

	private CameraServer camserver;

	public Robot() {

		// Joystick init
		/*
		 * https://wpilib.screenstepslive.com/s/3120/m/7912/l/133053-joysticks
		 */
		drive_stick = new Joystick(0);
		drivetrain = new DriveSystem(drive_stick);

		/* Forklift joystick */

		forklift_stick = new Joystick(1);

		armature = new PneumaticArm(drive_stick);
		try {
			forklift = new ForkLift(forklift_stick);

			/* Button configuration */
			/* TODO: Explain numbering scheme */
			btn_lift_up = new JoystickButton(forklift_stick, 1); // Lift up
			btn_lift_down = new JoystickButton(forklift_stick, 2); // Lift down

			btn_pneu_close = new JoystickButton(forklift_stick, 4); // pneumatic
																	// close
			btn_pneu_open = new JoystickButton(forklift_stick, 6); // pneumatic
																	// open
			SmartDashboard.putString("forklift_stick",
					"Forklift stick activated");

		} catch (Exception e) {
			forklift = null;
			btn_lift_up = new JoystickButton(drive_stick, 1); // Lift up
			btn_lift_down = new JoystickButton(drive_stick, 2); // Lift down

			btn_pneu_close = new JoystickButton(drive_stick, 4); // pneumatic
																	// close
			btn_pneu_open = new JoystickButton(drive_stick, 6); // pneumatic
																// open
			SmartDashboard.putString("forklift_stick",
					"Forklift stick disabled, single joystick mode");

		}
		// For practice, we don't need complicated.
		camserver = CameraServer.getInstance();
		camserver.setQuality(10);
		camserver.setSize(2);
		// the camera name (ex "cam0") can be found through the roborio web
		// interface
		camserver.startAutomaticCapture("cam0");
		SmartDashboard.putString("CAMERA", "AUTOMATIC MODE");
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	public void autonomous() {
		// Drive forward for 3 seconds.
		// Enough to bring tote into auto zone.
		drivetrain.forward(0.35, 3);
		drivetrain.stop();

	}

	/*
	 * The joystick button logic goes here. I decided not to move it to another
	 * class because it doesn't seem that it would really be worth it. The logic
	 * should be succinct and shouldn't need its own class.
	 */
	private void forklift_controls() {
		if (forklift_stick == null) {
			// lifts fork lift up
			if (btn_lift_up.get() == true && btn_lift_down.get() == false) {
				forklift.up();
			}
			// brings fork lift down
			else if (btn_lift_down.get() == true && btn_lift_up.get() == false) {
				forklift.down();
			} else {
				forklift.stop();
			}
		} else {
			double powah = forklift_stick.getY();
			// I'm putting a dead zone for ~5%
			if (powah > 0.05) {
				forklift.up(powah);
			} else if (powah < -0.05) {
				forklift.down(powah);
			} else { // Dead zone
				forklift.stop();
			}

			if (btn_pneu_close.get() == true && btn_pneu_open.get() == false) {
				armature.close_arm();
			} else if (btn_pneu_open.get() == true
					&& btn_pneu_close.get() == false) {
				armature.open_arm();
			}
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		while (isOperatorControl() && isEnabled()) {

			forklift_controls();
			drivetrain.run();

			// This doesn't really do much
			// It makes the controls a bit laggy.
			Timer.delay(0.001); // wait for a motor update time
		}

	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}
}
