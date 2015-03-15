package org.usfirst.frc.team5652.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class DriveSystem {

	/* Constants */
	private final double DEFAULT_DRIVE_PWR = 0.5;
	private final double DEFAULT_ROTATE_PWD = 0.5;
	
	/* Private local variables */
	private RobotDrive drivetrain;
	private double drive_power = 0;
	private double sensitivity = 1;
	
	public double getDrive_power() {
		return drive_power;
	}

	public void setDrive_power(double drive_power) {
		this.drive_power = drive_power;
	}

	private final Joystick joystick;
	

	/* Constructor must have a joystick number */
	public DriveSystem(Joystick joystick) {
		// We have 2 motors per wheel
		drivetrain = new RobotDrive(0, 1);
		
		// 100 ms should be enough for any weird errors
		drivetrain.setExpiration(0.1);
		
		// Setting default power
		drive_power = DEFAULT_DRIVE_PWR;
		/* The this is because i re-used the joystick variable name twice.
		 * and don't want to mix them up. 
		 */
		
		this.joystick = joystick;
		sensitivity = 1;
	}
	
	/* call this when you want this object to wake up 
	 * and use the joystick values to drive the drive train.
	 */
	public void checkInputs(){
		drivetrain.arcadeDrive(sensitivity * joystick.getY(), 
				-1 * DEFAULT_ROTATE_PWD * joystick.getX());
	}
	
	
	/* In case the user needs to do low level work */
	public RobotDrive getRobotDrive(){
		return drivetrain;
	}

	/*
	 * Stop the robot drive system
	 */
	public void stop() {
		drivetrain.drive(0, 0.0);
	}

	/*
	 * For autonomous Depends the auto_drive_power
	 */
	public void forward(double seconds) {
		drivetrain.drive(-1 * drive_power, 0.0);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Set your own power
	 */
	public void forward(double power, double seconds) {
		drivetrain.drive(-1 * power, 0.0);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Depends the auto_drive_power
	 */
	public void backwards(double seconds) {
		drivetrain.drive(drive_power, 0.0);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Set your own power
	 */
	public void backwards(double power, double seconds) {
		drivetrain.drive(power, 0.0);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Set your own power
	 */
	public void rotate_left(double power, double seconds) {
		drivetrain.drive(power, 1);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Set your own power
	 */
	public void rotate_left(double seconds) {
		drivetrain.drive(drive_power, 1);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Set your own power
	 */
	public void rotate_right(double seconds) {
		drivetrain.drive(drive_power, -1);
		Timer.delay(seconds);
		this.stop();
	}

	/*
	 * For autonomous Set your own power
	 */
	public void rotate_right(double power, double seconds) {
		drivetrain.drive(power, -1);
		Timer.delay(seconds);
		this.stop();
	}

}
