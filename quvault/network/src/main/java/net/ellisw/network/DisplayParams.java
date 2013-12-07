package net.ellisw.network;

import java.util.ArrayList;
import java.util.List;

public class DisplayParams {
	public DisplayNodes displayNodes = DisplayNodes.All;
	public DisplayCurrents displayCurrents = DisplayCurrents.All;
	
	/**
	 * If displayNodes == DisplayNodes.Include, these are the nodes to display
	 * If displayNodes == DisplayNodes.Exclude, these are the nodes to hide
	 */
	public final List<String> nodes = new ArrayList<String>();
	/**
	 * If displayCurrents == DisplayCurrents.Include, these are the currents to display
	 * If displayCurrents == DisplayCurrents.Exclude, these are the currents to hide
	 */
	public final List<String> currents = new ArrayList<String>();
}
