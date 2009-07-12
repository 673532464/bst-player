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
package com.bramosystems.oss.player.core.client.ui;

import com.bramosystems.oss.player.core.event.client.*;
import com.bramosystems.oss.player.core.client.*;
import com.bramosystems.oss.player.core.client.impl.VLCPlayerImpl;
import com.bramosystems.oss.player.core.client.skin.FlatCustomControl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import java.util.ArrayList;

/**
 * Widget to embed VLC Player plugin.
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 * <code><pre>
 * SimplePanel panel = new SimplePanel();   // create panel to hold the player
 * Widget player = null;
 * try {
 *      // create the player
 *      player = new VLCPlayer("www.example.com/mediafile.vob");
 * } catch(LoadException e) {
 *      // catch loading exception and alert user
 *      Window.alert("An error occured while loading");
 * } catch(PluginVersionException e) {
 *      // catch plugin version exception and alert user, possibly providing a link
 *      // to the plugin download page.
 *      player = new HTML(".. some nice message telling the user to download plugin first ..");
 * } catch(PluginNotFoundException e) {
 *      // catch PluginNotFoundException and tell user to download plugin, possibly providing
 *      // a link to the plugin download page.
 *      player = new HTML(".. another kind of message telling the user to download plugin..");
 * }
 *
 * panel.setWidget(player); // add player to panel.
 * </pre></code>
 *
 * @author Sikirulai Braheem
 */
public final class VLCPlayer extends AbstractMediaPlayer implements PlaylistSupport {

    private static VLCPlayerImpl impl;
    private String playerId,  mediaUrl;
    private HTML playerDiv;
    private Logger logger;
    private boolean isEmbedded,  autoplay;
//    private MediaStateListener _onInitListListener;
    private HandlerRegistration initListHandler;
    private ArrayList<MRL> _playlistCache;
    private FlatCustomControl control;

    VLCPlayer() throws PluginNotFoundException, PluginVersionException {
        PluginVersion v = PlayerUtil.getVLCPlayerPluginVersion();
        if (v.compareTo(0, 8, 6) < 0) {
            throw new PluginVersionException("0.8.6", v.toString());
        }

        if (impl == null) {
            impl = GWT.create(VLCPlayerImpl.class);
        }

        _playlistCache = new ArrayList<MRL>();
        playerId = DOM.createUniqueId().replace("-", "");
    }

    /**
     * Constructs <code>VLCPlayer</code> with the specified {@code height} and
     * {@code width} to playback media located at {@code mediaURL}. Media playback
     * begins automatically if {@code autoplay} is {@code true}.
     *
     * <p> {@code height} and {@code width} are specified as CSS units. A value of {@code null}
     * for {@code height} or {@code width} puts the player in embedded mode.  When in embedded mode,
     * the player is made invisible on the page and media state events are propagated to registered 
     * listeners only.  This is desired especially when used with custom sound controls.  For custom
     * video control, specify valid CSS values for {@code height} and {@code width} but hide the
     * player controls with {@code setControllerVisible(false)}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     * @param height the height of the player
     * @param width the width of the player.
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required VLCPlayer plugin version is not installed on the client.
     * @throws PluginNotFoundException if the VLCPlayer plugin is not installed on the client.
     */
    public VLCPlayer(String mediaURL, boolean autoplay, String height, String width)
            throws LoadException, PluginVersionException, PluginNotFoundException {
        this();

        mediaUrl = mediaURL;
        this.autoplay = autoplay;

        impl.init(playerId, new MediaStateListenerAdapter(), this);
        /*
        impl.init(playerId, new MediaStateListener() {

        public void onPlayFinished() {
        firePlayFinished();
        }

        public void onLoadingComplete() {
        fireLoadingComplete();
        }

        public void onError(String description) {
        fireError(description);
        }

        public void onDebug(String message) {
        fireDebug(message);
        }

        public void onLoadingProgress(double progress) {
        fireLoadingProgress(progress);
        }

        public void onPlayStarted() {
        firePlayStarted();
        }

        public void onPlayerReady() {
        firePlayerReady();
        }

        public void onMediaInfoAvailable(MediaInfo info) {
        fireMediaInfoAvailable(info);
        }

        public void onPlayStarted(int index) {
        firePlayStarted(index);
        }

        public void onPlayFinished(int index) {
        firePlayFinished(index);
        }

        public void onBuffering(boolean buffering) {
        fireBuffering(buffering);
        }
        });
         */
        DockPanel dp = new DockPanel();
        initWidget(dp);

        isEmbedded = (height == null) || (width == null);
        if (!isEmbedded) {
            logger = new Logger();
            logger.setVisible(false);
            dp.add(logger, DockPanel.SOUTH);

            control = new FlatCustomControl(this);
            dp.add(control, DockPanel.SOUTH);

            /*
            addMediaStateListener(new MediaStateListenerAdapter() {

            @Override
            public void onError(String description) {
            Window.alert(description);
            logger.log(description, false);
            }

            @Override
            public void onDebug(String message) {
            logger.log(message, false);
            }

            @Override
            public void onMediaInfoAvailable(MediaInfo info) {
            logger.log(info.asHTMLString(), true);
            }
            });
             */
            addDebugHandler(new DebugHandler() {

                public void onDebug(DebugEvent event) {
                    switch (event.getMessageType()) {
                        case Error:
                            Window.alert(event.getMessage());
                        case Info:
                            logger.log(event.getMessage(), false);
                    }
                }
            });
            addMediaInfoHandler(new MediaInfoHandler() {

                public void onMediaInfoAvailable(MediaInfoEvent event) {
                    logger.log(event.getMediaInfo().asHTMLString(), true);
                }
            });

        } else {
            height = "0px";
            width = "0px";
        }

        playerDiv = new HTML();
        playerDiv.setStyleName("");
        playerDiv.setHorizontalAlignment(HTML.ALIGN_CENTER);
        playerDiv.setHeight(height);
        dp.add(playerDiv, DockPanel.CENTER);

        setWidth(width);
    }

    /**
     * Constructs <code>VLCPlayer</code> to automatically playback media located at
     * {@code mediaURL} using the default height of 20px and width of 100%.
     *
     * @param mediaURL the URL of the media to playback
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required VLCPlayer plugin version is not installed on the client.
     * @throws PluginNotFoundException if the VLCPlayer plugin is not installed on the client.
     *
     */
    public VLCPlayer(String mediaURL) throws LoadException, PluginVersionException,
            PluginNotFoundException {
        this(mediaURL, true, "0px", "100%");
    }

    /**
     * Constructs <code>VLCPlayer</code> to playback media located at {@code mediaURL}
     * using the default height of 20px and width of 100%. Media playback begins
     * automatically if {@code autoplay} is {@code true}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required VLCPlayer plugin version is not installed on the client.
     * @throws PluginNotFoundException if the VLCPlayer plugin is not installed on the client.
     */
    public VLCPlayer(String mediaURL, boolean autoplay) throws LoadException,
            PluginVersionException, PluginNotFoundException {
        this(mediaURL, autoplay, "0px", "100%");
    }

    /**
     * Overridden to register player for plugin DOM events
     */
    @Override
    protected final void onLoad() {
        Timer t = new Timer() {

            @Override
            public void run() {
                playerDiv.setHTML(impl.getPlayerScript(playerId,
                        playerDiv.getOffsetHeight(), playerDiv.getOffsetWidth()));
                impl.initPlayer(playerId, mediaUrl, autoplay,
                        playerDiv.getOffsetHeight(), playerDiv.getOffsetWidth());
            }
        };
        t.schedule(500);            // IE workarround...
    }

    /**
     * Subclasses that override this method should call <code>super.onUnload()</code>
     * to ensure the player is properly removed from the browser's DOM.
     *
     * Overridden to remove player from browsers' DOM.
     */
    @Override
    protected void onUnload() {
        impl.close(playerId);
        playerDiv.getElement().setInnerText("");
    }

    public void loadMedia(String mediaURL) throws LoadException {
        checkAvailable();
        impl.load(playerId, mediaURL);
    }

    public void playMedia() throws PlayException {
        checkAvailable();
        impl.play(playerId);
    }

    public void stopMedia() {
        checkAvailable();
        impl.stop(playerId);
    }

    public void pauseMedia() {
        checkAvailable();
        impl.pause(playerId);
    }

    public void close() {
        impl.close(playerId);
    }

    public long getMediaDuration() {
        checkAvailable();
        return (long) impl.getDuration(playerId);
    }

    public double getPlayPosition() {
        checkAvailable();
        return impl.getTime(playerId);
    }

    public void setPlayPosition(double position) {
        checkAvailable();
        impl.setTime(playerId, position);
    }

    public double getVolume() {
        checkAvailable();
        return impl.getVolume(playerId);
    }

    public void setVolume(double volume) {
        checkAvailable();
        impl.setVolume(playerId, volume);
        fireDebug("Volume set to " + (volume * 100) + "%");
    }

    private void checkAvailable() {
        if (!impl.isPlayerAvailable(playerId)) {
            String message = "Player closed already, create another instance";
            fireDebug(message);
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void showLogger(boolean enable) {
        if (!isEmbedded) {
            logger.setVisible(enable);
        }
    }

    @Override
    public void setControllerVisible(boolean show) {
        if (!isEmbedded) {
            control.setVisible(show);
        }
    }

    @Override
    public boolean isControllerVisible() {
        return control.isVisible();
    }

    @Override
    public int getLoopCount() {
        checkAvailable();
        return impl.getLoopCount(playerId);
    }

    /**
     * Sets the number of times the current media file should repeat playback before stopping.
     *
     * <p>As of version 1.0, if this player is not available on the panel, this method
     * call is added to the command-queue for later execution.
     */
    @Override
    public void setLoopCount(final int loop) {
        if (impl.isPlayerAvailable(playerId)) {
            impl.setLoopCount(playerId, loop);
        } else {
            addToPlayerReadyCommandQueue("loopcount", new Command() {

                public void execute() {
                    impl.setLoopCount(playerId, loop);
                }
            });
        }
    }

    @Override
    public void addToPlaylist(String mediaURL) {
        if (impl.isPlayerAvailable(playerId)) {
            impl.addToPlaylist(playerId, mediaURL, null);
        } else {
            if (initListHandler == null) {
                initListHandler = addPlayerStateHandler(new PlayerStateHandler() {

                    public void onPlayerStateChanged(PlayerStateEvent event) {
                        switch (event.getPlayerState()) {
                            case Ready:
                                for (MRL mrl : _playlistCache) {
                                    impl.addToPlaylist(playerId, mrl.getUrl(), mrl.getOption());
                                }
                                break;
                        }
                        initListHandler.removeHandler();
                    }
                });
            }
            /*
            if (!containsMediaStateListener(_onInitListListener)) {
            _onInitListListener = new MediaStateListenerAdapter() {

            @Override
            public void onPlayerReady() {
            for (MRL mrl : _playlistCache) {
            //                        for (int i = 0; i < _playlistCache.size(); i++) {
            //                            MRL mrl = _playlistCache.get(i);
            impl.addToPlaylist(playerId, mrl.getUrl(), mrl.getOption());
            }
            //                        removeMediaStateListener(_onInitListListener);
            }
            };
            addMediaStateListener(_onInitListListener);
            }
             */
            _playlistCache.add(new MRL(mediaURL, null));
        }
    }

    @Override
    public boolean isShuffleEnabled() {
        checkAvailable();
//        return impl.isShuffleEnabled(playerId);
        return false;
    }

    @Override
    public void removeFromPlaylist(int index) {
        checkAvailable();
        impl.removeFromPlaylist(playerId, index);
    }

    @Override
    public void setShuffleEnabled(final boolean enable) {
/*        if (impl.isPlayerAvailable(playerId)) {
            impl.addToPlaylist(playerId, GWT.getModuleBaseURL() + "silence.mp3",
                    enable ? " --loop " : " --no-loop ");
        } else {
            _playlistCache.add(new MRL(GWT.getModuleBaseURL() + "silence.mp3",
                    enable ? " --loop " : " --no-loop "));
        /*            addToPlayerReadyCommandQueue("shuffle", new Command() {

        public void execute() {
        impl.addToPlaylist(playerId, "", enable ? "--random" : "--no-random");
        }
        });
         *
        }
*/    }

    public void clearPlaylist() {
        checkAvailable();
        impl.clearPlaylist(playerId);
    }

    public int getPlaylistSize() {
        checkAvailable();
        return impl.getPlaylistCount(playerId);
    }

    public void play(int index) throws IndexOutOfBoundsException {
        checkAvailable();
        impl.playMedia(playerId, index);
    }

    public void playNext() throws PlayException {
        checkAvailable();
        if (!impl.playNext(playerId)) {
            throw new PlayException("No more entries in playlist");
        }
    }

    public void playPrevious() throws PlayException {
        checkAvailable();
        if (!impl.playPrevious(playerId)) {
            throw new PlayException("Beginning of playlist reached");
        }
    }

    public void setAudioChannelMode(AudioChannelMode mode) {
        checkAvailable();
        impl.setAudioChannelMode(playerId, mode.ordinal());
    }

    public AudioChannelMode getAudioChannelMode() {
        checkAvailable();
        return AudioChannelMode.values()[impl.getAudioChannelMode(playerId)];
    }

    public static enum AudioChannelMode {

        Disabled, Stereo, ReverseStereo, Left, Right, Dolby
    }

    private class MRL {

        private String url,  option;

        public MRL(String url, String option) {
            this.url = url;
            this.option = option;
        }

        public String getUrl() {
            return url;
        }

        public String getOption() {
            return option;
        }
    }
}
