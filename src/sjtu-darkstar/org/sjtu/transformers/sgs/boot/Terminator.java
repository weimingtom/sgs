package org.sjtu.transformers.sgs.boot;

import com.sun.sgs.system.stop.Stop;

public class Terminator {
	public void execute() throws Exception {
		Stop.main(new String[]{"dist/CatchMe.boot"});
	}
}
