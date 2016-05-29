package org.sjtu.transformers.sgs.boot;

import com.sun.sgs.system.Boot;

public class Booter {
	public void execute() throws Exception {
		Boot.main(new String[] { "dist/sjtu-darkstar.boot.properties" });
	}
	
	
	public final static void main(String[] args) throws Exception{
		new Booter().execute();
	}
}

