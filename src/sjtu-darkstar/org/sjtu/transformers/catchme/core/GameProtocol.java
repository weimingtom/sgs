package org.sjtu.transformers.catchme.core;

public interface GameProtocol {
	public static final String MSG_UTF = "UTF-8";
    final byte VERSION  = 0x01;
    final Short RoleMOVE = 0x02;
    final Short RoleLeav = 0x03;
}
