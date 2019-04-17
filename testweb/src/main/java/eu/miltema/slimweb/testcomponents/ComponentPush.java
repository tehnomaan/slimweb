package eu.miltema.slimweb.testcomponents;

import java.util.*;
import java.util.stream.Collectors;

import eu.miltema.slimweb.annot.*;
import eu.miltema.slimweb.push.*;

/**
 * This component sends an int array back to push client 50ms after connection start 
 * @author Margus
 */
@Component(requireSession = false)
public class ComponentPush implements ServerPush {
	@Override
	public void pushStarted(PushHandle pushHandle, Map<String, String> parameters) throws Exception {
		new Thread(() -> {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			pushHandle.pushObject(parameters.values().stream().sorted().collect(Collectors.joining("-")));
		}).start();
	}

	@Override
	public void pushTerminated(PushHandle pushHandle) throws Exception {
	}

	public void get() {
	}
}
