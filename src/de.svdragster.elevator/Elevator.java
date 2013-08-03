package de.svdragster.elevator;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;

public class Elevator extends Plugin {

  @Override
	public void disable() {

	}

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(new ElevatorListener(), this);
		return true;
	}

}
