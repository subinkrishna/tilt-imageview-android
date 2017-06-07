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

package com.github.nisrulz.sensey;

import com.github.nisrulz.sensey.FlipDetector.FlipListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.nisrulz.sensey.SensorUtils.testAccelerometerEvent;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FlipDetectorTest {

  @Mock private FlipListener mockListener;
  private FlipDetector testFlipDetector;

  @Before
  public void setUp() {
    testFlipDetector = new FlipDetector(mockListener);
  }

  @Test
  public void detectFlipWithMiddleFaceUpValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, 9.5f }));
    verify(mockListener, only()).onFaceUp();
  }

  @Test
  public void notDetectFlipWithMinFaceUpValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, 9 }));
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void notDetectFlipWithMaxFaceUpValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, 10 }));
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void detectFlipWithMiddleFaceDownValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, -9.5f }));
    verify(mockListener, only()).onFaceDown();
  }

  @Test
  public void notDetectFlipWithMinFaceDownValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, -10 }));
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void notDetectFlipWithMaxFaceDownValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, -9 }));
    verifyNoMoreInteractions(mockListener);
  }

  @Test
  public void notDetectFlipWithOtherValue() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0, 0 }));
    verifyNoMoreInteractions(mockListener);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void exceptionWithArrayLessThenThreeElements() {
    testFlipDetector.onSensorChanged(testAccelerometerEvent(new float[] { 0, 0 }));
    verifyNoMoreInteractions(mockListener);
  }
}
