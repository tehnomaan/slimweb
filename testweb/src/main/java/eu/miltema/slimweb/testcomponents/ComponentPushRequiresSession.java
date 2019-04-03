package eu.miltema.slimweb.testcomponents;

import java.util.*;

import eu.miltema.slimweb.annot.Component;
import eu.miltema.slimweb.push.*;

@Component
public class ComponentPushRequiresSession implements ServerPush {

	@Override
	public void pushStarted(PushHandle pushHandle, Map<String, String> parameters) throws Exception {
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

}
