/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.tiltimageview.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * The type Sensey.
 */
public class Sensey {

  /**
   * The constant SAMPLING_PERIOD_FASTEST.
   */
  public static final int SAMPLING_PERIOD_FASTEST = SensorManager.SENSOR_DELAY_FASTEST;
  /**
   * The constant SAMPLING_PERIOD_GAME.
   */
  public static final int SAMPLING_PERIOD_GAME = SensorManager.SENSOR_DELAY_GAME;
  /**
   * The constant SAMPLING_PERIOD_NORMAL.
   */
  public static final int SAMPLING_PERIOD_NORMAL = SensorManager.SENSOR_DELAY_NORMAL;
  /**
   * The constant SAMPLING_PERIOD_UI.
   */
  public static final int SAMPLING_PERIOD_UI = SensorManager.SENSOR_DELAY_UI;
  /**
   * Map from any of default listeners (
   * to SensorDetectors created by those listeners.
   *
   * This map is needed to hold reference to all started detections <strong>NOT</strong>
   * through {@link Sensey#startSensorDetection(SensorDetector)}, because the last one
   * passes task to hold reference of {@link SensorDetector sensorDetector} to the client
   */

  private final Map<Object, SensorDetector> defaultSensorsMap = new HashMap<>();
  private SensorManager sensorManager;
  private TouchTypeDetector touchTypeDetector;
  private PinchScaleDetector pinchScaleDetector;
  private SoundLevelDetector soundLevelDetector;
  private int samplingPeriod = SAMPLING_PERIOD_NORMAL;

  private Sensey() {
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static Sensey getInstance() {
    return LazyHolder.INSTANCE;
  }

  /**
   * Init the lib
   *
   * @param context
   *     the context
   * @param samplingPeriod
   *     the sampling period
   */
  public void init(Context context, int samplingPeriod) {
    init(context);
    this.samplingPeriod = samplingPeriod;
  }

  /**
   * Init the lib
   *
   * @param context
   *     the context
   */
  public void init(Context context) {
    this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
  }

  /**
   * Stop.
   */
  public void stop() {
    this.sensorManager = null;
  }

  /**
   * Start shake detection.
   *
   * @param shakeListener
   *     the shake listener
   */
  public void startShakeDetection(ShakeDetector.ShakeListener shakeListener) {
    startLibrarySensorDetection(new ShakeDetector(shakeListener), shakeListener);
  }

  private void startLibrarySensorDetection(SensorDetector detector, Object clientListener) {
    if (!defaultSensorsMap.containsKey(clientListener)) {
      defaultSensorsMap.put(clientListener, detector);
      startSensorDetection(detector);
    }
  }

  private void startSensorDetection(SensorDetector detector) {
    final Iterable<Sensor> sensors = convertTypesToSensors(detector.getSensorTypes());
    if (areAllSensorsValid(sensors)) {
      registerDetectorForAllSensors(detector, sensors);
    }
  }

  private Iterable<Sensor> convertTypesToSensors(int... sensorTypes) {
    Collection<Sensor> sensors = new ArrayList<>();
    if (sensorManager != null) {
      for (int sensorType : sensorTypes) {
        sensors.add(sensorManager.getDefaultSensor(sensorType));
      }
    }
    return sensors;
  }

  private boolean areAllSensorsValid(Iterable<Sensor> sensors) {
    for (Sensor sensor : sensors) {
      if (sensor == null) {
        return false;
      }
    }

    return true;
  }

  private void registerDetectorForAllSensors(SensorDetector detector, Iterable<Sensor> sensors) {
    for (Sensor sensor : sensors) {
      sensorManager.registerListener(detector, sensor, samplingPeriod);
    }
  }

  /**
   * Start shake detection.
   *
   * @param threshold
   *     the threshold
   * @param timeBeforeDeclaringShakeStopped
   *     the time before declaring shake stopped
   * @param shakeListener
   *     the shake listener
   */
  public void startShakeDetection(float threshold, long timeBeforeDeclaringShakeStopped,
      ShakeDetector.ShakeListener shakeListener) {
    startLibrarySensorDetection(
        new ShakeDetector(threshold, timeBeforeDeclaringShakeStopped, shakeListener),
        shakeListener);
  }

  /**
   * Stop shake detection.
   *
   * @param shakeListener
   *     the shake listener
   */
  public void stopShakeDetection(ShakeDetector.ShakeListener shakeListener) {
    stopLibrarySensorDetection(shakeListener);
  }

  private void stopLibrarySensorDetection(Object clientListener) {
    SensorDetector detector = defaultSensorsMap.remove(clientListener);
    stopSensorDetection(detector);
  }

  private void stopSensorDetection(SensorDetector detector) {
    if (detector != null && sensorManager != null) {
      sensorManager.unregisterListener(detector);
    }
  }

  /**
   * Start movement detection.
   *
   * @param movementListener
   *     the movement listener
   */
  public void startMovementDetection(MovementDetector.MovementListener movementListener) {
    startLibrarySensorDetection(new MovementDetector(movementListener), movementListener);
  }

  /**
   * Start movement detection.
   *
   * @param threshold
   *     the threshold
   * @param timeBeforeDeclaringStationary
   *     the time before declaring stationary
   * @param movementListener
   *     the movement listener
   */
  public void startMovementDetection(float threshold, long timeBeforeDeclaringStationary,
      MovementDetector.MovementListener movementListener) {
    startLibrarySensorDetection(
        new MovementDetector(threshold, timeBeforeDeclaringStationary, movementListener),
        movementListener);
  }

  /**
   * Stop movement detection.
   *
   * @param movementListener
   *     the movement listener
   */
  public void stopMovementDetection(MovementDetector.MovementListener movementListener) {
    stopLibrarySensorDetection(movementListener);
  }

  /**
   * Start chop detection.
   *
   * @param chopListener
   *     the chop listener
   */
  public void startChopDetection(ChopDetector.ChopListener chopListener) {
    startLibrarySensorDetection(new ChopDetector(chopListener), chopListener);
  }

  /**
   * Start chop detection.
   *
   * @param threshold
   *     the threshold
   * @param timeForChopGesture
   *     the time for chop gesture
   * @param chopListener
   *     the chop listener
   */
  public void startChopDetection(float threshold, long timeForChopGesture,
      ChopDetector.ChopListener chopListener) {
    startLibrarySensorDetection(new ChopDetector(threshold, timeForChopGesture, chopListener),
        chopListener);
  }

  /**
   * Stop chop detection.
   *
   * @param chopListener
   *     the chop listener
   */
  public void stopChopDetection(ChopDetector.ChopListener chopListener) {
    stopLibrarySensorDetection(chopListener);
  }

  /**
   * Start wrist twist detection.
   *
   * @param wristTwistListener
   *     the wrist twist listener
   */
  public void startWristTwistDetection(WristTwistDetector.WristTwistListener wristTwistListener) {
    startLibrarySensorDetection(new WristTwistDetector(wristTwistListener), wristTwistListener);
  }

  /**
   * Start wrist twist detection.
   *
   * @param threshold
   *     the threshold
   * @param timeForWristTwistGesture
   *     the time for wrist twist gesture
   * @param wristTwistListener
   *     the wrist twist listener
   */
  public void startWristTwistDetection(float threshold, long timeForWristTwistGesture,
      WristTwistDetector.WristTwistListener wristTwistListener) {
    startLibrarySensorDetection(
        new WristTwistDetector(threshold, timeForWristTwistGesture, wristTwistListener),
        wristTwistListener);
  }

  /**
   * Stop wrist twist detection.
   *
   * @param wristTwistListener
   *     the wrist twist listener
   */
  public void stopWristTwistDetection(WristTwistDetector.WristTwistListener wristTwistListener) {
    stopLibrarySensorDetection(wristTwistListener);
  }

  /**
   * Start light detection.
   *
   * @param lightListener
   *     the light listener
   */
  public void startLightDetection(LightDetector.LightListener lightListener) {
    startLibrarySensorDetection(new LightDetector(lightListener), lightListener);
  }

  /**
   * Start light detection.
   *
   * @param threshold
   *     the threshold
   * @param lightListener
   *     the light listener
   */
  public void startLightDetection(float threshold, LightDetector.LightListener lightListener) {
    startLibrarySensorDetection(new LightDetector(threshold, lightListener), lightListener);
  }

  /**
   * Stop light detection.
   *
   * @param lightListener
   *     the light listener
   */
  public void stopLightDetection(LightDetector.LightListener lightListener) {
    stopLibrarySensorDetection(lightListener);
  }

  /**
   * Start flip detection.
   *
   * @param flipListener
   *     the flip listener
   */
  public void startFlipDetection(FlipDetector.FlipListener flipListener) {
    startLibrarySensorDetection(new FlipDetector(flipListener), flipListener);
  }

  /**
   * Stop flip detection.
   *
   * @param flipListener
   *     the flip listener
   */
  public void stopFlipDetection(FlipDetector.FlipListener flipListener) {
    stopLibrarySensorDetection(flipListener);
  }

  /**
   * Start rotation angle detection.
   *
   * @param rotationAngleListener
   *     the rotation angle listener
   */
  public void startRotationAngleDetection(RotationAngleDetector.RotationAngleListener rotationAngleListener) {
    startLibrarySensorDetection(new RotationAngleDetector(rotationAngleListener),
        rotationAngleListener);
  }

  /**
   * Stop rotation angle detection.
   *
   * @param rotationAngleListener
   *     the rotation angle listener
   */
  public void stopRotationAngleDetection(RotationAngleDetector.RotationAngleListener rotationAngleListener) {
    stopLibrarySensorDetection(rotationAngleListener);
  }

  /**
   * Start tilt direction detection.
   *
   * @param tiltDirectionListener
   *     the tilt direction listener
   */
  public void startTiltDirectionDetection(TiltDirectionDetector.TiltDirectionListener tiltDirectionListener) {
    startLibrarySensorDetection(new TiltDirectionDetector(tiltDirectionListener),
        tiltDirectionListener);
  }

  /**
   * Stop tilt direction detection.
   *
   * @param tiltDirectionListener
   *     the tilt direction listener
   */
  public void stopTiltDirectionDetection(TiltDirectionDetector.TiltDirectionListener tiltDirectionListener) {
    stopLibrarySensorDetection(tiltDirectionListener);
  }

  /**
   * Start orientation detection.
   *
   * @param orientationListener
   *     the orientation listener
   */
  public void startOrientationDetection(OrientationDetector.OrientationListener orientationListener) {
    startLibrarySensorDetection(new OrientationDetector(orientationListener), orientationListener);
  }

  /**
   * Start orientation detection.
   *
   * @param smoothness
   *     the smoothness
   * @param orientationListener
   *     the orientation listener
   */
  public void startOrientationDetection(int smoothness, OrientationDetector.OrientationListener orientationListener) {
    startLibrarySensorDetection(new OrientationDetector(smoothness, orientationListener),
        orientationListener);
  }

  /**
   * Stop orientation detection.
   *
   * @param orientationListener
   *     the orientation listener
   */
  public void stopOrientationDetection(OrientationDetector.OrientationListener orientationListener) {
    stopLibrarySensorDetection(orientationListener);
  }

  /**
   * Start proximity detection.
   *
   * @param proximityListener
   *     the proximity listener
   */
  public void startProximityDetection(ProximityDetector.ProximityListener proximityListener) {
    startLibrarySensorDetection(new ProximityDetector(proximityListener), proximityListener);
  }

  /**
   * Stop proximity detection.
   *
   * @param proximityListener
   *     the proximity listener
   */
  public void stopProximityDetection(ProximityDetector.ProximityListener proximityListener) {
    stopLibrarySensorDetection(proximityListener);
  }

  /**
   * Start wave detection.
   *
   * @param waveListener
   *     the wave listener
   */
  public void startWaveDetection(WaveDetector.WaveListener waveListener) {
    startLibrarySensorDetection(new WaveDetector(waveListener), waveListener);
  }

  /**
   * Start wave detection.
   *
   * @param threshold
   *     the threshold
   * @param waveListener
   *     the wave listener
   */
  public void startWaveDetection(float threshold, WaveDetector.WaveListener waveListener) {
    startLibrarySensorDetection(new WaveDetector(threshold, waveListener), waveListener);
  }

  /**
   * Stop wave detection.
   *
   * @param waveListener
   *     the wave listener
   */
  public void stopWaveDetection(WaveDetector.WaveListener waveListener) {
    stopLibrarySensorDetection(waveListener);
  }

  /**
   * Start sound level detection.
   *
   * @param soundLevelListener
   *     the sound level listener
   */
  public void startSoundLevelDetection(SoundLevelDetector.SoundLevelListener soundLevelListener) {
    if (soundLevelListener != null) {
      soundLevelDetector = new SoundLevelDetector(soundLevelListener);
      soundLevelDetector.start();
    }
  }

  /**
   * Stop sound level detection.
   */
  public void stopSoundLevelDetection() {
    if (soundLevelDetector != null) {
      soundLevelDetector.stop();
    }
    soundLevelDetector = null;
  }

  /**
   * Start pinch scale detection.
   *
   * @param context
   *     the context
   * @param pinchScaleListener
   *     the pinch scale listener
   */
  public void startPinchScaleDetection(Context context, PinchScaleDetector.PinchScaleListener pinchScaleListener) {
    if (pinchScaleListener != null) {
      pinchScaleDetector = new PinchScaleDetector(context, pinchScaleListener);
    }
  }

  /**
   * Stop pinch scale detection.
   */
  public void stopPinchScaleDetection() {
    pinchScaleDetector = null;
  }

  /**
   * Start touch type detection.
   *
   * @param context
   *     the context
   * @param touchTypListener
   *     the touch typ listener
   */
  public void startTouchTypeDetection(Context context, TouchTypeDetector.TouchTypListener touchTypListener) {
    if (touchTypListener != null) {
      touchTypeDetector = new TouchTypeDetector(context, touchTypListener);
    }
  }

  /**
   * Stop touch type detection.
   */
  public void stopTouchTypeDetection() {
    touchTypeDetector = null;
  }

  /**
   * Sets dispatch touch event.
   *
   * @param event
   *     the event
   */
  public void setupDispatchTouchEvent(MotionEvent event) {
    if (touchTypeDetector != null) {
      touchTypeDetector.onTouchEvent(event);
    }

    if (pinchScaleDetector != null) {
      pinchScaleDetector.onTouchEvent(event);
    }
  }

  private static class LazyHolder {
    private static final Sensey INSTANCE = new Sensey();
  }
}
