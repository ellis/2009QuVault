package net.ellisw.quvault.vault;

import net.ellisw.network.Network;


public class QuProblemElec extends QuProblem {
	public QuProblemElec(QuObject parent) {
		super(parent);
		setInstructionKeywords("ee");
	}
	
	public Network createNetwork() {
		return new Network();
	}
}
