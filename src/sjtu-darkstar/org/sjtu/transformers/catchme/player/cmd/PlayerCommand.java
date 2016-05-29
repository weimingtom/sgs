package org.sjtu.transformers.catchme.player.cmd;

public interface PlayerCommand {
	public void execute(PlayerCommandContext context, String[] args);
}

