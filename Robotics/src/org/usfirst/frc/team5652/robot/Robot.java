package org.usfirst.frc.team5652.robot;

import java.util.concurrent.atomic.AtomicBoolean;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	// Robot drive settings (tank drive)
	private DriveSystem drivetrain;
	
	//Pneumatic arm code
	private PneumaticArm armature;
	
	
	// Joystick code
	private Joystick drive_stick;
	private Joystick forklift_stick;
	
	// Joystick buttons
	private Button btn_lift_up, btn_lift_down, btn_pneu_close, btn_pneu_open, btn_soft_mode;
	private AtomicBoolean soft_touch_mode = new AtomicBoolean(false);
	
	// Limit switches for fork lift.
	private final boolean digital_io_enabled = false;
	private DigitalInput upperLimitSwitch = new LimitSwitch (0);
	private DigitalInput lowerLimitSwitch = new LimitSwitch (1);
	
	// Autonomous drive power. 
	// 1.0 is FULL SPEED. 
	// Change this if you need to power
	private double auto_drive_power = 0.5; 
	
	static private double MAX_SENSITIVITY = 1.0; // DO NOT EDIT
	
	// Modify this value to change sensitivity of the controls. 
	private double MIN_SENSITIVITY = 0.75; 
	

	private double sensitivity = MIN_SENSITIVITY;
	
	// Change to false to use image processing code.
	static private boolean IS_VISION_SIMPLE = true;
	
	// Power distribution module
	private PowerDistributionPanel pdp;
	
	// Motors
	private Victor motor_5, motor_6,motor_7, motor_8;
	
	
	
	// Lift controls p
	private double lift_power_down = 0.45;
	private double lift_power_up = 1.0;
	private double lift_power_stop = 0.00;
	private Integer loop_count = 0;

	private long profiler_start;
	private long profiler_end;

	// Camera variables
	private Vision vision;
	private Thread thread;
	private CameraServer camserver;
	
	public Robot() {
		
		
		// PDP setup
		pdp = new PowerDistributionPanel();
		/* 
		 * Setup the victors objects
		 * http://content.vexrobotics.com/docs/217-2769-Victor888UserManual.pdf
		 * 
		 */
		motor_5 = new Victor(2);
		motor_6 = new Victor(3);
		motor_7 = new Victor(4);
		motor_8 = new Victor(5);
		
		

		// Joystick init
		/*
		 * https://wpilib.screenstepslive.com/s/3120/m/7912/l/133053-joysticks
		 * 
		 */
		drive_stick = new Joystick(0);
		drivetrain = new DriveSystem(drive_stick);

		btn_lift_up = new JoystickButton(drive_stick, 1); // Lift up
		btn_lift_down = new JoystickButton(drive_stick, 2); // Lift down

		btn_pneu_close = new JoystickButton(drive_stick, 4); // pneumatic close
		btn_pneu_open = new JoystickButton(drive_stick, 6); // pneumatic open
		
		btn_soft_mode = new JoystickButton(drive_stick,  12); // Soft touch mode
		
		/* Forklift joystick */	
		try {
			forklift_stick = new Joystick(1);
		}
		catch(Exception e) {
			forklift_stick = null;
			SmartDashboard.putString("forklift_stick", 
					"EPIC FAILURE, NO STICK FOUND");
			
		}
		if (forklift_stick != null){
			armature = new PneumaticArm(drive_stick);
		}

		SmartDashboard.putString("forklift_stick", "Forklift stick activated");
		
		// Create vision object and thread
		/*
		 * http://khengineering.github.io/RoboRio/vision/cameratest/
		 * 
		 */
		if (IS_VISION_SIMPLE == false) {
			vision = new Vision();
			thread = new Thread(vision);
			thread.start();
			SmartDashboard.putString("CAMERA", "MANUAL MODE");
		}
		else {
			// For practice, we don't need complicated.
			camserver = CameraServer.getInstance();
			camserver.setQuality(10);
			camserver.setSize(2);
		     //the camera name (ex "cam0") can be found through the roborio web interface
			camserver.startAutomaticCapture("cam0");
			SmartDashboard.putString("CAMERA", "AUTOMATIC MODE");
		}
		
		// SmartDashboard defaults
		SmartDashboard.putNumber("AUTO_DRIVE_POWER", auto_drive_power);
		SmartDashboard.putNumber("AUTO_DRIVE_POWER", sensitivity);
		
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
	 * Code to lift the fork up
	 * 
	 * First it checks if the limit switch is triggered,
	 * 		If so, it won't go up anymore to protect the hardware
	 * else
	 * 		It will move up depending on the sensitivity mode and
	 * 		the amount of tween'd power
	 * At the end, it checks if it needs to stop the forklift
	 * because it hit a limit switch.
	 * TODO? Make it an interrupt, not a poll
	 */
	@SuppressWarnings("unused")
	public void forklift_up(double power) {
		// Check limit switch if digitalio enabled.
		if (!upperLimitSwitch.get() || 
				!digital_io_enabled) {
			motor_5.set( lift_power_up * power);
			motor_6.set(sensitivity * -1 * lift_power_up * power);
			motor_7.set(sensitivity * lift_power_up * power);
			motor_8.set(sensitivity * -1 * lift_power_up * power);
		}
		
		if (upperLimitSwitch.get() && 
				digital_io_enabled) {
			forklift_stop();
		}
	}

	/*
	 * Code to lift the fork down
	 * 
	 * First it checks if the limit switch is triggered,
	 * 		If so, it won't go down anymore to protect the hardware
	 * else
	 * 		It will move up depending on the sensitivity mode and
	 * 		the amount of tween'd power
	 * 
	 * At the end, it checks if it needs to stop the forklift
	 * because it hit a limit switch.
	 * 
	 * TODO? Make it an interrupt, not a poll
	 */
	@SuppressWarnings("unused")
	public void forklift_down(double current_power) {
		// Check limit switch only if dio enabled.
		if (!lowerLimitSwitch.get() ||
				!digital_io_enabled) {
			motor_5.set( -1 * lift_power_down * current_power);
			motor_6.set( lift_power_down);
			motor_7.set( -1 * lift_power_down * current_power);
			motor_8.set( lift_power_down);
		}
		// Probably need this just in case
		if (lowerLimitSwitch.get() && digital_io_enabled) {
			forklift_stop();
		}
	}

	public void forklift_stop() {
		motor_5.set(lift_power_stop);
		motor_6.set(lift_power_stop);
		motor_7.set(lift_power_stop);
		motor_8.set(lift_power_stop);
	}

	
	private void soft_touch_logic(){
		// Hold the soft touch button to force sensitive controls.
		soft_touch_mode.set(btn_soft_mode.get());
		if (soft_touch_mode.get() == true) {
			sensitivity = MIN_SENSITIVITY;
		} else {
			sensitivity = MAX_SENSITIVITY;
		}
	}
	
	private void forklift_logic() {
		if (forklift_stick != null){
			// lifts fork lift up
			if (btn_lift_up.get() == true && btn_lift_down.get() == false) {
				forklift_up(1);
			}
			// brings fork lift down
			else if (btn_lift_down.get() == true && btn_lift_up.get() == false) {
				forklift_down(1);
			} else {
				forklift_stop();
			}
		} else {
			double powah = forklift_stick.getY();
			// I'm putting a dead zone for ~5%
			if (powah > 0.05) {
				forklift_up(powah);
			}
			else if (powah < -0.05) {
				forklift_down(powah);
			}
			else { // Dead zone
				forklift_stop();
			}

			if (btn_pneu_close.get() == true && btn_pneu_open.get() == false) {
				armature.close_arm();
			} else if (btn_pneu_open.get() == true && btn_pneu_close.get() == false) {
				armature.open_arm();
			}
		}
		}
	
	private void soft_touch_diagnostics() {
		if(soft_touch_mode.get() == true){
			SmartDashboard.putString("SOFT_TOUCH", "ENABLED");
		}
		else {
			SmartDashboard.putString("SOFT_TOUCH", "DISABLED");
		}
		sensitivity = SmartDashboard.getNumber("SENSITIVITY");
	}
	

	
	private void interval_logic() {
		// 1/0.005 s = 5 ms
		// 200 * 0.005 = 1000 = 1 sec
		if ((loop_count++ % 100) == 0) {

			// Profiler code, don't edit
			profiler_end = System.currentTimeMillis();
			SmartDashboard.putNumber("profiler_drive_ms", profiler_end
					- profiler_start);
			SmartDashboard.putString("ERROR", "NONE");
			profiler_start = System.currentTimeMillis();
			
			// ADD LOGIC HERE FOR DIAGNOSTICS
			soft_touch_diagnostics();
			

			// If we want to do image processing. 
			if (IS_VISION_SIMPLE == false){
				vision.set_vision_send_image();
			}

			// DON't EDIT, PROFILER CODE
			profiler_end = System.currentTimeMillis();
			SmartDashboard.putNumber("profiler_loop_ms", profiler_end
					- profiler_start);
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		while (isOperatorControl() && isEnabled()) {
			profiler_start = System.currentTimeMillis();
			
			// This needs to run all the time
			soft_touch_logic();
			forklift_logic();
			drivetrain.run();
			
			// The following runs occasionally.
			interval_logic();

			
			Timer.delay(0.005); // wait for a motor update time
		}

	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}
}
