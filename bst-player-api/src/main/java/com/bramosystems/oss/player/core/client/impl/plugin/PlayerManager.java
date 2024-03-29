/*
 * Copyright 2011 Sikirulai Braheem <sbraheem at bramosystems.com>.
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
package com.bramosystems.oss.player.core.client.impl.plugin;

import com.bramosystems.oss.player.core.client.*;
import com.bramosystems.oss.player.core.client.spi.PlayerProviderFactory;
import com.google.gwt.core.client.GWT;
import java.util.*;

/**
 *
 * @author Sikirulai Braheem <sbraheem at bramosystems.com>
 */
public abstract class PlayerManager {

    private static PlayerManager instance;
    private HashSet<PlayerInfo> matrixSupports = new HashSet<PlayerInfo>(), playlistSupports = new HashSet<PlayerInfo>(),
            allPlayers = new HashSet<PlayerInfo>();
    private HashMap<String, String> mimeTypes;

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = GWT.create(PlayerManager.class);
            instance.init();
        }
        return instance;
    }

    private void init() {
        mimeTypes = new HashMap<String, String>();
        initMimeTypes(mimeTypes);

        Iterator<String> it = getProviders().iterator();
        while (it.hasNext()) {
            String prov = it.next();
            Iterator<String> pns = getPlayerNames(prov).iterator();
            while (pns.hasNext()) {
                String pn = pns.next();
                try {
                    PlayerInfo pi = getPlayerInfo(prov, pn);
                    pi.setDetectedPluginInfo(getProviderFactory(prov).getDetectedPluginInfo(pn));
                    pi.getRegisteredProtocols().addAll(getProviderFactory(prov).getPermittedMediaProtocols(pn, pi.getDetectedPluginVersion()));
                    Set<String> mimes = getProviderFactory(prov).getPermittedMimeTypes(pn, pi.getDetectedPluginVersion());
                    for (String mime : mimes) {
                        String exts = mimeTypes.get(mime.trim());
                        if (exts != null) {
                            pi.getRegisteredExtensions().addAll(Arrays.asList(exts.split(",")));
                        }
                    }

                    allPlayers.add(pi);
                    if (pi.isHasMatrixSupport()) {
                        matrixSupports.add(pi);
                    }
                    if (pi.isHasPlaylistSupport()) {
                        playlistSupports.add(pi);
                    }
                } catch (PluginNotFoundException ex) {
                }
            }
        }
    }

    public HashMap<String, String> getMimeTypes() {
        return mimeTypes;
    }

    public AbstractMediaPlayer getPlayer(Plugin plugin, String mediaURL,
            boolean autoplay, String height, String width) throws PluginVersionException, PluginNotFoundException {
        PlayerInfo pi = getSupportedPlayer(plugin, mediaURL);
        return getProviderFactory(pi.getProviderName()).getPlayer(pi.getPlayerName(), mediaURL, autoplay, height, width);
    }

    public AbstractMediaPlayer getPlayer(Plugin plugin, String mediaURL,
            boolean autoplay) throws PluginVersionException, PluginNotFoundException {
        PlayerInfo pi = getSupportedPlayer(plugin, mediaURL);
        return getProviderFactory(pi.getProviderName()).getPlayer(pi.getPlayerName(), mediaURL, autoplay);
    }

    /**
     * TODO: provide support for property file based sorting ...
     *
     * @param plugin
     * @param mediaURL
     * @return
     */
    private PlayerInfo getSupportedPlayer(Plugin plugin, String mediaURL) {
        String protocol = extractProtocol(mediaURL);
        String ext = extractExt(mediaURL);
        HashSet<PlayerInfo> pnames = new HashSet<PlayerInfo>();
        PlayerInfo pname = null;
        boolean isSpecificPlug = false;

        switch (plugin) {
            case MatrixSupport:
                pnames = matrixSupports;
                break;
            case PlaylistSupport:
                pnames = playlistSupports;
                break;
            case Auto:
                pnames = allPlayers;
                break;
            default:
                isSpecificPlug = true;
        }

        if (isSpecificPlug) {
            return getPlayerInfo("core", plugin.name());
        }

        Iterator<PlayerInfo> it = pnames.iterator();
        while (it.hasNext()) {
            PlayerInfo pn = it.next();
            if (canHandleMedia(pn, protocol, ext)) {
                pname = pn;
                break;
            }
        }
        return pname;
    }

    protected final boolean canHandleMedia(String playerProvider, String playerName, String protocol, String ext) {
        return canHandleMedia(getPlayerInfo(playerProvider, playerName), protocol, ext);
    }

    protected final boolean canHandleMedia(PlayerInfo pif, String protocol, String ext) {
        if (pif.getDetectedPluginVersion().compareTo(pif.getRequiredPluginVersion()) >= 0) {   // req plugin found...
            // check for streaming protocol & extension ...
            Set<String> types = pif.getRegisteredExtensions();
            Set<String> prots = pif.getRegisteredProtocols();
            return ((protocol != null) && (prots != null) && prots.contains(protocol.toLowerCase()))
                    || ((ext != null) && (types != null) && types.contains(ext.toLowerCase()));
        }
        return false;
    }

    protected final String extractExt(String mediaURL) {
        String _mediaURL = mediaURL.replaceAll("[;#].*$", "");
        return _mediaURL.substring(_mediaURL.lastIndexOf(".") + 1);
    }

    protected final String extractProtocol(String mediaURL) {
        if (mediaURL.contains("://")) {
            return mediaURL.substring(0, mediaURL.indexOf("://"));
        } else {
            return null;
        }
    }

    protected final PlayerInfo getApiPlayerInfoForPlugin(String playerName) {
        PlayerInfo pi = null;
        Plugin plug = Plugin.valueOf(playerName);
        switch (plug) {
            case Auto:
            case PlaylistSupport:
            case MatrixSupport:
                pi = new PlayerInfo("api", plug.name(), PluginVersion.get(0, 0, 0), false, false);
                break;
            case DivXPlayer:
            case FlashPlayer:
            case Native:
            case QuickTimePlayer:
            case VLCPlayer:
            case WinMediaPlayer:
                pi = getPlayerInfo("core", playerName);
                break;
            default:
                throw new IllegalArgumentException("Unknown player name - " + playerName);
        }
        return pi;
    }

    public abstract Set<String> getPlayerNames(String providerName);

    public abstract Set<String> getProviders();

    public abstract PlayerInfo getPlayerInfo(String providerName, String playerName);

    public abstract PlayerProviderFactory getProviderFactory(String provider);

    /**
     * Called by the constructor method to populate all known audio/video mime
     * types.
     *
     * @param mimeTypes the mimeType map to be filled. The map is filled as
     * (mimeType,file-extension) value pairs.
     */
    protected abstract void initMimeTypes(HashMap<String, String> mimeTypes);
}
