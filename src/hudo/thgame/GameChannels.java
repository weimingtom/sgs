/*
 * Copyright 2007-2008 Sun Microsystems, Inc.
 *
 * This file is part of Project Darkstar Server.
 *
 * Project Darkstar Server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Project Darkstar Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package thgame;

import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedReference;

public class GameChannels implements Serializable, AppListener
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(GameChannels.class.getName());
    static final String CHANNEL_1_NAME = "channel_1";
    static final String CHANNEL_2_NAME = "channel_2";
    
    private ManagedReference<Channel> channel1 = null;

    /**
     * 这里的初始化需要清除数据库
     */
    public void initialize(Properties props) {
        ChannelManager channelMgr = AppContext.getChannelManager();
        
        Channel c1 = channelMgr.createChannel(CHANNEL_1_NAME, new THChannelsChannelListener(),Delivery.RELIABLE);
        channel1 = AppContext.getDataManager().createReference(c1);
        channelMgr.createChannel(CHANNEL_2_NAME, new THChannelsChannelListener(), Delivery.RELIABLE);
        
        logger.info("thServer Game Initialized");
    }

    public ClientSessionListener loggedIn(ClientSession session) {
        logger.log(Level.INFO, "User {0} has logged in", session.getName());
        return new THClientSessionListener(session, channel1);
    }
}
