/*
 * Copyright 2012 sbraheem.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bramosystems.oss.player.provider.vimeo.client;

import com.bramosystems.oss.player.core.client.*;
import com.bramosystems.oss.player.core.client.spi.Player;
import com.bramosystems.oss.player.core.client.ui.SWFWidget;
import com.bramosystems.oss.player.core.event.client.PlayStateEvent.State;
import com.bramosystems.oss.player.core.event.client.PlayerStateEvent;
import com.bramosystems.oss.player.provider.vimeo.client.impl.VimeoPlayerAFImpl;
import com.bramosystems.oss.player.provider.vimeo.client.impl.VimeoPlayerProvider;

/**
 *
 * @author sbraheem
 */
@Player(name = "FlashPlayer", minPluginVersion = "10.0.0", providerFactory = VimeoPlayerProvider.class)
public class VimeoFlashPlayer extends AbstractMediaPlayer {

    private SWFWidget swf;
    private VimeoPlayerAFImpl impl;
    private VimeoPlayerProvider provider;
    private RepeatMode repeatMode;

    private VimeoFlashPlayer() {
        provider = ((VimeoPlayerProvider) getWidgetFactory(VimeoPlayerProvider.PROVIDER_NAME));
        repeatMode = RepeatMode.REPEAT_OFF;
    }

    public VimeoFlashPlayer(String clipId, boolean autoplay, String width, String height) throws PluginNotFoundException, PluginVersionException {
        this();
        swf = new SWFWidget("http://vimeo.com/moogaloop.swf?server=vimeo.com&clip_id=" + clipId, width, height, PluginVersion.get(10, 0, 0));
        swf.addProperty("allowScriptAccess", "always");
        swf.addProperty("allowFullScreen", "true");
        swf.setFlashVar("autoplay", autoplay ? 1 : 0);
        swf.setFlashVar("api", 1);
        swf.setFlashVar("player_id", swf.getId());
        swf.setFlashVar("api_ready", provider.getInitFunctionRef(swf.getId()));
        swf.commitFlashVars();
        initWidget(swf);
        
        provider.initHandlers(swf.getId(), new VimeoPlayerProvider.EventHandler() {

            @Override
            public void onMsg(String msg) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onInit() {
                impl = VimeoPlayerAFImpl.getPlayerImpl(swf.getId());
                impl.registerHandlers(provider.getEvtFunctionBaseName(swf.getId()));
                firePlayerStateEvent(PlayerStateEvent.State.Ready);
            }

            @Override
            public void onLoadingPrgress(double progress) {
                fireLoadingProgress(progress);
            }

            @Override
            public void onPlayingProgress() {
            }

            @Override
            public void onPlay() {
                firePlayStateEvent(State.Started, 1);
            }

            @Override
            public void onFinish() {
                firePlayStateEvent(State.Finished, 1);
            }

            @Override
            public void onPause() {
                firePlayStateEvent(State.Paused, 1);
            }

            @Override
            public void onSeek() {
            }
        });
    }

    @Override
    public void loadMedia(String mediaURL) throws LoadException {
        if (isPlayerOnPage(swf.getId())) {
//            impl.
        }
    }

    @Override
    public void playMedia() throws PlayException {
        checkAvailable();
        impl.play();
    }

    @Override
    public void stopMedia() {
        checkAvailable();
        impl.stop();
    }

    @Override
    public void pauseMedia() {
        checkAvailable();
        impl.pause();
    }

    @Override
    public long getMediaDuration() {
        checkAvailable();
        return (long) impl.getDuration();
    }

    @Override
    public double getPlayPosition() {
        checkAvailable();
        return impl.getCurrentTime();
    }

    @Override
    public void setPlayPosition(double position) {
        checkAvailable();
        impl.seekTo(position);
    }

    @Override
    public double getVolume() {
        checkAvailable();
        return impl.getVolume();
    }

    @Override
    public void setVolume(double volume) {
        checkAvailable();
        impl.setVolume(volume);
    }

    @Override
    public void setRepeatMode(RepeatMode mode) {
        switch (mode) {
            case REPEAT_ALL:
                if (isPlayerOnPage(swf.getId())) {
                    impl.setLoop(true);
                } else {
                    swf.setFlashVar("loop", 1);
                }
                repeatMode = mode;
                break;
            case REPEAT_OFF:
                if (isPlayerOnPage(swf.getId())) {
                    impl.setLoop(false);
                } else {
                    swf.setFlashVar("loop", 0);
                }
                repeatMode = mode;
        }
    }

    @Override
    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    @Override
    public int getVideoHeight() {
        checkAvailable();
        return impl.getVideoHeight();
    }

    @Override
    public int getVideoWidth() {
        checkAvailable();
        return impl.getVideoWidth();
    }

    @Override
    public boolean isControllerVisible() {
        return true;
    }

    @Override
    public <T> void setConfigParameter(ConfigParameter param, T value) {
        super.setConfigParameter(param, value);
        if (param instanceof DefaultConfigParameter) {
            switch (DefaultConfigParameter.valueOf(param.getName())) {
                case TransparencyMode:
                    swf.addProperty("wmode", ((TransparencyMode) value).name().toLowerCase());
                    break;
                case BackgroundColor:
                    swf.addProperty("bgcolor", (String) value);
            }
        } else if (param instanceof VimeoConfigParameters) {
            switch (VimeoConfigParameters.valueOf(param.getName())) {
                case ShowByline:
                    swf.setFlashVar("byline", (Boolean) value ? 1 : 0);
                    break;
                case ShowPortrait:
                    swf.setFlashVar("portrait", (Boolean) value ? 1 : 0);
                    break;
                case ShowTitle:
                    swf.setFlashVar("title", (Boolean) value ? 1 : 0);
                    break;
                case EnableFullscreen:
                    swf.setFlashVar("fullscreen", (Boolean) value ? 1 : 0);
                    break;
            }
            swf.commitFlashVars();
        }
    }

    private void checkAvailable() {
        if (!isPlayerOnPage(swf.getId())) {
            String message = "Player not available, create an instance";
            fireDebug(message);
            throw new IllegalStateException(message);
        }
    }
}
