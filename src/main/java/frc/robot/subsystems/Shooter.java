/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import bjorg.sim.WPI_CANSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {

  // the flywheel motor
  private final WPI_CANSparkMax m_shooterMaster;
  private final WPI_CANSparkMax m_shooterSlave;

  private final CANEncoder m_shooterEncoder;

  // controls the angle of hood
  private final WPI_CANSparkMax m_shooterAngleMotor;

  private final CANEncoder m_shooterAngleEncoder;

  double currentList[] = new double[5];
  int positionInList = 0;

  //Hood PID
  private final CANPIDController m_hoodPID;
  private final static double kHoodP = 0.0;
  private final static double kHoodI = 0.0;
  private final static double kHoodD = 0.0;
  private final static double kHoodIz = 0.0;
  private final static double kHoodFF = 0.0;

  //TODO Flywheel PID
  private final CANPIDController m_flywheelPID;
  private final static double kFlyP = 0.0;
  private final static double kFlyI = 0.0;
  private final static double kFlyD = 0.0;
  private final static double kFlyIz = 0.0;
  private final static double kFlyFF = 0.0;

  //PID Debugging for Flywheel
  private double goalSpeed = 0.0;

  /**
   * Creates a new Shooter.
   */
  public Shooter() {
    setName("Shooter");

    m_shooterMaster = new WPI_CANSparkMax(Constants.kShooterNeoMaster, MotorType.kBrushless);
    initSparkMax(m_shooterMaster);
    m_shooterMaster.setInverted(Constants.kShooterInverted);
    addChild("Shooter Master", m_shooterMaster);

    m_shooterSlave = new WPI_CANSparkMax(Constants.kShooterNeoSlave, MotorType.kBrushless);
    initSparkMax(m_shooterSlave);
    m_shooterSlave.follow(m_shooterMaster, true);
    addChild("Shooter Slave", m_shooterSlave);

    m_shooterEncoder = m_shooterMaster.getEncoder();

    m_shooterAngleMotor = new WPI_CANSparkMax(Constants.kShooterNeoAngle, MotorType.kBrushless);
    initSparkMax(m_shooterAngleMotor);
    m_shooterAngleMotor.setInverted(Constants.kShooterAngleInverted);
    m_shooterAngleMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    addChild("Shooter Angle Motor", m_shooterAngleMotor);
    
    m_shooterAngleEncoder = m_shooterAngleMotor.getEncoder();
    m_shooterAngleEncoder.setPositionConversionFactor(Constants.kShooterAngleScale);
    //42 counts per revolution
    //addChild("Shooter Angle Encoder", m_shooterAngleEncoder);

    m_flywheelPID = new CANPIDController(m_shooterMaster);
    m_hoodPID = new CANPIDController(m_shooterAngleMotor);

    //set PID coefficeints of hood
    m_hoodPID.setP(kHoodP);
    m_hoodPID.setI(kHoodI);
    m_hoodPID.setD(kHoodD);
    m_hoodPID.setIZone(kHoodIz);
    m_hoodPID.setFF(kHoodFF);
    
    //set PID coefficeint of flywheel
    m_flywheelPID.setP(kFlyP);
    m_flywheelPID.setI(kFlyI);
    m_flywheelPID.setD(kFlyD);
    m_flywheelPID.setIZone(kFlyIz);
    m_flywheelPID.setFF(kFlyFF);
  }

  private void initSparkMax(CANSparkMax spark){
    spark.restoreFactoryDefaults();
    spark.setSmartCurrentLimit(40); // 40 amps
  }

  /**
   * Start the shooter motor spinning
   * @param percentOutput Motor Speed [ -1.0 to 1.0 ] - Positive Numbers Shoot
   */
  public void shoot(double percentOutput) {
    m_shooterMaster.set(percentOutput);
  }

/**
 * Set goal 
 * @param goalRPM goal speed/velocity in rotation per min.
 */
  public void shootClosedLoop(double goalRPM){
    m_flywheelPID.setReference(goalRPM, ControlType.kVelocity);
    goalSpeed = goalRPM;
  }
/**
 * Sets goal angle for the Hood using PID
 * @param angle goal angle of hood in degrees
 */
  public void setAngleMotorDegrees(double goalAngle) {
    m_hoodPID.setReference(goalAngle, ControlType.kPosition);

  }

  public void setAngleMotorSpeed(double percentOutput){
    m_shooterAngleMotor.set(percentOutput);
  }
/**
 * Determines whether hood is at home position
 */
  public boolean isHoodPosition(){
    //calculates the average
    double average = 0;
    for(int i = 0; i<5; i++) {
      average = average+currentList[i];
    }
    average = average/5;
    
    if (average>Constants.kCurrentThreshold) {
      return true;
    }
    else {
      return false;
    }
  }

  public double getAngle(){
    double position = m_shooterAngleEncoder.getPosition();
    return position;

  }

  public double getSpeed(){
    double speed = m_shooterEncoder.getVelocity();
    return speed;
  }

  public double getSpeedError(){
    double currentSpeed = getSpeed();
    double error = goalSpeed - currentSpeed;
    return error;
  }

  public void initEncoder(){
      m_shooterAngleEncoder.setPosition(0);
  }

  /**
   * Stop the shooter motor
   */
  public void stop() {
    m_shooterMaster.stopMotor();
    m_shooterAngleMotor.stopMotor();
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    double newCurrent = m_shooterAngleMotor.getOutputCurrent();
    currentList[positionInList%5] = newCurrent;
    positionInList++;
    SmartDashboard.putNumber("Shooter RPM", getSpeed());
  }
}
