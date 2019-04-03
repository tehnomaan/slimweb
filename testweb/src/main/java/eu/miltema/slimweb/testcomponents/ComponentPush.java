package eu.miltema.slimweb.testcomponents;

import java.util.*;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.annot.SessionNotRequired;
import eu.miltema.slimweb.push.*;

/**
 * This component sends an int array back to push client 50ms after connection start 
 * @author Margus
 */
@Component
@SessionNotRequired
public class ComponentPush implements ServerPush {
	@Override
	public void pushStarted(PushHandle pushHandle, Map<String, List<String>> parameters) throws Exception {
		new Thread(() -> {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			pushHandle.pushObject(new int[] {3, 5});
		}).start();
	}

	@Override
	public void pushTerminated(PushHandle pushHandle) throws Exception {
	}

	public void get() {
	}
}
