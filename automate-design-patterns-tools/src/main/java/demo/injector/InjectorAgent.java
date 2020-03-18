package demo.injector;

import java.lang.instrument.Instrumentation;

public class InjectorAgent {

	public static void premain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new InjectorClassFileTransformer());
	}
}
