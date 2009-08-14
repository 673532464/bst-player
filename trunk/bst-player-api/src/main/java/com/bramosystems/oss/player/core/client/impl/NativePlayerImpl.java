/*
 * Copyright 2009 Sikirulai Braheem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bramosystems.oss.player.core.client.impl;

import com.bramosystems.oss.player.core.client.MediaInfo;
import com.bramosystems.oss.player.core.client.ui.NativePlayer;
import com.bramosystems.oss.player.core.client.ui.VLCPlayer;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Native implementation of the VLCPlayer class. It is not recommended to
 * interact with this class directly.
 *
 * @author Sikirulai Braheem
 * @see VLCPlayer
 */
public class NativePlayerImpl extends JavaScriptObject {

    public static native NativePlayerImpl getPlayer(String playerId) /*-{
    return $doc.getElementById(playerId);
    }-*/;

    public final native String canPlayType(String mimeType) /*-{
    return this.canPlayType(mimeType);
    }-*/;

    protected NativePlayerImpl() {
    }

    public final native int getNetworkState() /*-{
    return this.networkState;
    }-*/;

    public final native int getReadyState() /*-{
    return this.readyState;
    }-*/;

    public final native int getErrorState() /*-{
    var _err = this.error;
    if(_err) {
    return _err.code
    } else {
    return 0;
    }
    }-*/;

    public final native void play() /*-{
    this.play();
    }-*/;

    public final native void pause() /*-{
    this.pause();
    }-*/;

    public final native boolean isPaused() /*-{
    return this.paused;
    }-*/;

    public final native boolean isEnded() /*-{
    return this.ended;
    }-*/;

    public final native void setMediaURL(String mediaURL) /*-{
    this.src = mediaURL;
    }-*/;

    public final native String getMediaURL() /*-{
    return this.currentSrc;
    }-*/;

    public final native boolean isLooping() /*-{
    return this.loop;
    }-*/;

    public final native void setLooping(boolean looping) /*-{
    this.loop = looping;
    }-*/;

    public final native double getTime() /*-{
    try {
    return this.currentTime;
    } catch(e) {
    return 0;
    }
    }-*/;

    public final native void setTime(double time) /*-{
    try {
    this.currentTime = time;
    } catch(e) {
    return 0;
    }
    }-*/;

    public final native double getDuration() /*-{
    try {
    return parseFloat(this.duration);
    } catch(e) {
    return 0;
    }
    }-*/;

    public final native double getVolume() /*-{
    try{
    return this.volume;
    } catch(e){
    return 0;
    }
    }-*/;

    public final native void setVolume(double volume) /*-{
    try{
    this.volume = volume;
    } catch(e){}
    }-*/;

    public final native boolean isMute() /*-{
    try{
    return this.muted;
    } catch(e){
    return false;
    }
    }-*/;

    public final native void setMute(boolean mute) /*-{
    try{
    this.muted = mute;
    } catch(e){}
    }-*/;

    public final native boolean isControlsVisible() /*-{
    return this.controls;
    }-*/;

    public final native void setControlsVisible(boolean visible) /*-{
    this.controls = visible;
    }-*/;

    public final native double getRate() /*-{
    return this.playbackRate;
    }-*/;

    public final native void setRate(double rate) /*-{
    this.playbackRate = rate;
    }-*/;

    public final native String getVideoWidth() /*-{
    return this.videoWidth;
    }-*/;

    public final native String getVideoHeight() /*-{
    return this.videoHeight;
    }-*/;

    public final native String getPoster() /*-{
    return this.poster;
    }-*/;

    public final native void setPoster(String _poster) /*-{
    this.poster = _poster;
    }-*/;

    public final native void fillMediaInfo(MediaInfo id3) /*-{
    try {
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::year = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::albumTitle = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::artists = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::comment = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::title = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::contentProviders = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::copyright = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::hardwareSoftwareRequirements = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::publisher =;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::genre = ;
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::internetStationOwner = '';
    //    id3.@com.bramosystems.oss.player.core.client.MediaInfo::internetStationName = '';
    id3.@com.bramosystems.oss.player.core.client.MediaInfo::duration = this.duration;
    id3.@com.bramosystems.oss.player.core.client.MediaInfo::videoWidth = String(this.videoWidth);
    id3.@com.bramosystems.oss.player.core.client.MediaInfo::videoHeight = String(this.videoHeight);
    $wnd.alert('Duration : ' + this.duration);
     } catch(e) {
    $wnd.alert(e);
    }
    }-*/;

    public final native void registerMediaStateHandlers(NativePlayer _player) /*-{
    this.addEventListener('progress', function(){  // plugin init complete
    //    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireProgressChanged()();
    }, false);
    this.addEventListener('play', function(){  // play started
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(1);
    }, false);
    this.addEventListener('pause', function(){  // play paused
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(2);
    }, false);
    this.addEventListener('ended', function(){  // play started
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(3);
    }, false);
    this.addEventListener('waiting', function(){  // buffering
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(4);
    }, false);
    this.addEventListener('playing', function(){  // playing again, buffering stopped
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(5);
    }, false);
    this.addEventListener('loadedmetadata', function(){  // metadata available
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(6);
    }, false);
    this.addEventListener('volumechange', function(){  // volume changed
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(7);
    }, false);
    this.addEventListener('error', function(){  // error
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(8);
    }, false);
    this.addEventListener('abort', function(){  // loading aborted
    _player.@com.bramosystems.oss.player.core.client.ui.NativePlayer::fireStateChanged(I)(9);
    }, false);
    }-*/;
}