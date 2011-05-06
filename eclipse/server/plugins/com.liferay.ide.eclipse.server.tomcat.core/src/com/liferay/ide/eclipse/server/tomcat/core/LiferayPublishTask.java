package com.liferay.ide.eclipse.server.tomcat.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.server.tomcat.core.internal.PublishTask;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;


@SuppressWarnings("restriction")
public class LiferayPublishTask extends PublishTask {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PublishOperation[] getTasks(IServer server, int kind, List modules, List kindList) {
		if (modules == null) {
			return null;
		}

		LiferayTomcatServerBehavior tomcatServer =
			(LiferayTomcatServerBehavior) server.loadAdapter(LiferayTomcatServerBehavior.class, null);

		List tasks = new ArrayList();
		int size = modules.size();
		for (int i = 0; i < size; i++) {
			IModule[] module = (IModule[]) modules.get(i);
			Integer in = (Integer) kindList.get(i);
			tasks.add(new LiferayPublishOperation(tomcatServer, kind, module, in.intValue()));
		}

		return (PublishOperation[]) tasks.toArray(new PublishOperation[tasks.size()]);
	}
}
