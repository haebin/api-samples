/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.youtube.cmdline.partner.model;

/**
 * Brief description of the live cuepoints settings.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the YouTube Data API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class LiveCuepointsSettings extends com.google.api.client.json.GenericJson {
	
  /**
   * This value specifies a point in time in the video when viewers should see an ad or in-stream slate. 
   * The property value identifies a time offset, in milliseconds, from the beginning of the monitor stream.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long offsetTimeMs;	
  
  /**
   * This value specifies the wall clock time at which the cuepoint should be inserted. 
   * The value is specified in ISO 8601 (YYYY-MM-DDThh:mm:ss.sssZ) format.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private com.google.api.client.util.DateTime walltime;
  
  /**
   * The cuepoint's type. 'ad' for ads.
   */
  @com.google.api.client.util.Key
  private java.lang.String cueType;	
 
  /**
   * The cuepoint's duration, in seconds. 
   * This value must be specified if the cueType is ad and is ignored otherwise.
   */
  @com.google.api.client.util.Key
  private  java.lang.Long durationSecs;	

  /**
   * This value specifies a point in time in the video when viewers should see an ad or in-stream slate.
   * @return value or {@code null} for none
   */
  public java.lang.Long getOffsetTimeMs() {
    return offsetTimeMs;
  }

  /**
   * This value specifies a point in time in the video when viewers should see an ad or in-stream slate.
   * @param offsetTimeMs or {@code null} for none
   */
  public LiveCuepointsSettings setOffsetTimeMs(java.lang.Long offsetTimeMs) {
    this.offsetTimeMs = offsetTimeMs;
    return this;
  }

  /**
   * This value specifies the wall clock time at which the cuepoint should be inserted.
   * @return value or {@code null} for none
   */
  public com.google.api.client.util.DateTime getWalltime() {
    return walltime;
  }

  /**
   * This value specifies the wall clock time at which the cuepoint should be inserted.
   * @param walltime
   */
  public LiveCuepointsSettings setWalltime(com.google.api.client.util.DateTime walltime) {
    this.walltime = walltime;
    return this;
  }  
  
  /**
   * The cuepoint's type.
   * @return value
   */
  public java.lang.String getCueType() {
    return cueType;
  }

  /**
   * The cuepoint's type.
   * @param cueType cue point type. "ad"
   */
  public LiveCuepointsSettings setCueType(java.lang.String cueType) {
    this.cueType = cueType;
    return this;
  }
    
  /**
   * The cuepoint's duration, in seconds. 
   * @return value
   */
  public java.lang.Long getDurationSecs() {
    return durationSecs;
  }

  /**
   * The cuepoint's duration, in seconds. 
   * @param durationSecs duration seconds for ads slot.
   */
  public LiveCuepointsSettings setDurationSecs(java.lang.Long durationSecs) {
    this.durationSecs = durationSecs;
    return this;
  }  
  
  @Override
  public LiveCuepointsSettings set(String fieldName, Object value) {
    return (LiveCuepointsSettings) super.set(fieldName, value);
  }

  @Override
  public LiveCuepointsSettings clone() {
    return (LiveCuepointsSettings) super.clone();
  }

}
