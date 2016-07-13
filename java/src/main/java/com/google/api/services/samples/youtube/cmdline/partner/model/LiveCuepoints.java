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
 * A liveCuepoint resource starts an ad break in the broadcast video stream.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the YouTube Data API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class LiveCuepoints extends com.google.api.client.json.GenericJson {
  /**
   * The ID that YouTube assigns to uniquely identify the stream.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * Identifies what kind of resource this is. Value: the fixed string "youtubePartner#liveCuepoint".
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String kind;
  
  /**
   * The ID that YouTube assigns to uniquely identify the broadcast into which the cuepoint is being inserted.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String broadcastId;

  /**
   * The settings object defines the cuepoint's settings.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private LiveCuepointsSettings settings;

  /**
   * The ID that YouTube assigns to uniquely identify the stream.
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * The ID that YouTube assigns to uniquely identify the stream.
   * @param id id or {@code null} for none
   */
  public LiveCuepoints setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifies what kind of resource this is. Value: the fixed string "youtubePartner#liveCuepoint".
   * @return value or {@code null} for none
   */
  public java.lang.String getKind() {
    return kind;
  }

  /**
   * Identifies what kind of resource this is. Value: the fixed string "youtubePartner#liveCuepoint".
   * @param kind kind or {@code null} for none
   */
  public LiveCuepoints setKind(java.lang.String kind) {
    this.kind = kind;
    return this;
  }

  /**
   * The ID that YouTube assigns to uniquely identify the broadcast into which the cuepoint is being inserted.
   * @return value or {@code null} for none
   */
  public java.lang.String getBroadcastId() {
    return broadcastId;
  }

  /**
   * The ID that YouTube assigns to uniquely identify the broadcast into which the cuepoint is being inserted.
   * @param broadcastId broadcastId or {@code null} for none
   */
  public LiveCuepoints setBroadcastId(java.lang.String broadcastId) {
    this.broadcastId = broadcastId;
    return this;
  }

  /**  
   * The settings object contains live cuepoint's information.
   * @return value or {@code null} for none
   */
  public LiveCuepointsSettings getSettings() {
    return settings;
  }

  /**
   * The settings object contains live cuepoint's information.
   * @param settings settings or {@code null} for none
   */
  public LiveCuepoints setSettings(LiveCuepointsSettings settings) {
    this.settings = settings;
    return this;
  }

  @Override
  public LiveCuepoints set(String fieldName, Object value) {
    return (LiveCuepoints) super.set(fieldName, value);
  }

  @Override
  public LiveCuepoints clone() {
    return (LiveCuepoints) super.clone();
  }

}
