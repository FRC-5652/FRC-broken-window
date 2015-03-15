package org.usfirst.frc.team5652.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PneumaticArm {

	// Pneumatics
	private int PCM_CAM_ID = 2;
	private Solenoid pneumatic_valve0;
	private Solenoid pneumatic_valve1;
	private Compressor pneumatic_compressor;
	private Joystick joystick;

	public PneumaticArm(Joystick joystick) {
		this.joystick = joystick;

		/*
		 * http://crosstheroadelectronics.com/control_system.html
		 * http://www.vexrobotics.com/217-4243.html
		 * http://khengineering.github.io/RoboRio/faq/pcm/
		 * http://content.vexrobotics
		 * .com/vexpro/pdf/217-4243-PCM-Users-Guide-20141230.pdf
		 */
		pneumatic_valve0 = new Solenoid(PCM_CAM_ID, 0); // This is the pneumatic
														// object
		pneumatic_valve1 = new Solenoid(PCM_CAM_ID, 1); // This is the second
														// pneumatic valve?
														// object?

		pneumatic_valve0.set(true); // true close
		pneumatic_valve1.set(true); // true close - false means open
		pneumatic_compressor = new Compressor(PCM_CAM_ID);
		pneumatic_compressor.setClosedLoopControl(true);
	}

	public Joystick getJoystick() {
		return this.joystick;
	}

	/*
	 * Let x be pneumatic valve 0 and let y pneumatic valve 1 be y If x is open,
	 * y needs to be close If x is closed, y needs to be open open is false and
	 * true is closed (for now - remember to verify later)
	 */
	public void close_arm() {
		pneumatic_valve0.set(true);
		pneumatic_valve1.set(false);
	}

	public void open_arm() {
		pneumatic_valve0.set(false);
		pneumatic_valve1.set(true);
	}

	// Disable valve - have valves close
	public void disable_pneumaticvalves() {
		pneumatic_valve0.set(true);
		pneumatic_valve1.set(true);
	}

	// pneumatic flush - flush storage cylinders - useful after game
	public void pneumatic_flush() {
		pneumatic_valve0.set(false);
		pneumatic_valve1.set(false);
	}

	private void compressor_diagnostics() {
		// Compressor diagnostics
		// http://wpilib.screenstepslive.com/s/4485/m/13503/l/216217?data-resolve-url=true&data-manual-id=13503
		SmartDashboard.putNumber("Compressor AMPS",
				pneumatic_compressor.getCompressorCurrent());
		SmartDashboard.putBoolean("CLOSED LOOP?",
				pneumatic_compressor.getClosedLoopControl());
		SmartDashboard.putBoolean("Compressor Current Fault",
				pneumatic_compressor.getCompressorCurrentTooHighFault());
		SmartDashboard.putBoolean("Compressor missing",
				pneumatic_compressor.getCompressorNotConnectedFault());
		SmartDashboard.putBoolean("Compressor Shorted",
				pneumatic_compressor.getCompressorShortedFault());
		SmartDashboard.putBoolean("Pressure switch too low",
				pneumatic_compressor.getPressureSwitchValue());

		SmartDashboard.putBoolean("Solenoid voltage fault",
				pneumatic_valve0.getPCMSolenoidVoltageFault());
		SmartDashboard.putNumber("Solenoid bit faults",
				pneumatic_valve0.getPCMSolenoidBlackList());
		SmartDashboard.putNumber("Solenoid bit status",
				pneumatic_valve0.getAll());

	}
}
