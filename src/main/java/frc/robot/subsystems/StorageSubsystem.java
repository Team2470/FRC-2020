/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class StorageSubsystem extends SubsystemBase {

  private final WPI_TalonSRX m_conveyorMotor;
  private final WPI_VictorSPX m_outputMotor;
  private final DigitalInput m_ballAtInput;  
  private final DigitalInput m_ballAtOutput;


  /**
   * Creates a new StorageSubsystem.
   */
  public StorageSubsystem() {
    setName("Storage");
    m_ballAtInput = new DigitalInput(Constants.kStorageBallInputChannel);
    m_ballAtOutput = new DigitalInput(Constants.kStorageBallOutputChannel);
    addChild("Ball At Input", m_ballAtInput);
    addChild("Ball At Output", m_ballAtOutput);
    
    m_conveyorMotor = new WPI_TalonSRX(Constants.kStorageMotorTalonID);
    m_conveyorMotor.setInverted(Constants.kStorageMotorInverted);
    addChild("Conveyor Motor", m_conveyorMotor);

    m_outputMotor = new WPI_VictorSPX(Constants.kStorageOutputVictorID);
    m_outputMotor.setInverted(Constants.kStorageOutputInverted);
    addChild("Output Motor", m_outputMotor);
  }

  public void setCoveyorMotor(double MotorSpeed) 
  {
    m_conveyorMotor.set(ControlMode.PercentOutput, MotorSpeed);
  }

  public void setOutputMotor(double MotorSpeed)
  {
    m_outputMotor.set(ControlMode.PercentOutput, MotorSpeed);
  }

  public boolean isBallAtInput() {
    return m_ballAtInput.get();
  }

  public boolean isBallAtOutput() {
    return m_ballAtOutput.get();
  }

  public void stopMotors() {
    m_conveyorMotor.setNeutralMode(NeutralMode.Coast);
    m_outputMotor.setNeutralMode(NeutralMode.Coast);
  }
 
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putBoolean("Conveyor Ball at input", isBallAtInput());
    SmartDashboard.putBoolean("Conveyor Bal at output", isBallAtOutput());
  }
}
