package de.svdragster.elevator;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;

public class Elevator extends Plugin {

	@Override
	public void disable() {
		//Nothing needed here
	}

	@Override
	public boolean enable() {
		new ElevatorListener().createFiles();
		new ElevatorListener().getProps();
		Canary.hooks().registerListener(new ElevatorListener(), this);
		return true;
	}

}
