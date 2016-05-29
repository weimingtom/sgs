package org.sjtu.transformers.catchme.player.cmd;

import org.sjtu.transformers.catchme.core.ClientListenerImpl;
import org.sjtu.transformers.catchme.core.Game;
import org.sjtu.transformers.catchme.player.PlayerObject;

import com.sun.sgs.app.ClientSession;

public class PlayerCommandContext {
	private final ClientListenerImpl clientListener;
	private final ClientSession clientSession;
	private final Game game;
	private final PlayerObject playerObj;

	public PlayerCommandContext(ClientListenerImpl clientListener, ClientSession clientSession, Game game, PlayerObject playerObj) {
		this.clientListener = clientListener;
		this.clientSession = clientSession;
		this.game = game;
		this.playerObj = playerObj;
	}

	public ClientListenerImpl getClientListener() {
		return clientListener;
	}

	public ClientSession getClientSession() {
		return clientSession;
	}

	public Game getGame() {
		return game;
	}

	public PlayerObject getPlayerObject() {
		return playerObj;
	}

}
