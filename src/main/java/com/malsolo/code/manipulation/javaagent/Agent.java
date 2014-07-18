package com.malsolo.code.manipulation.javaagent;

import java.lang.instrument.Instrumentation;

/**
 * VM Arguments:
 * -javaagent:/Users/jbeneito/Documents/workspace-sts-3.5.0.RELEASE/manipulation/target/manipulation-0.0.1-SNAPSHOT.jar
 */
public class Agent {
	
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("Starting the agent");
		inst.addTransformer(new ImportantLogTransformer());

	}

}
