package net.povstalec.sgjourney.common.factions;

public abstract class Faction
{
	// TODO
	// Faction Name
	// Faction Leader
	// Faction Territory
	// Faction Strength
	
	public Faction(String factionName)
	{
		
	}
	
	public abstract void onLeaderDeath();
	
	/*
	 * When the Faction Leader dies, the following can happen to the Faction:
	 * 1) Faction gets a new Leader
	 * 2) Faction falls apart
	 * 3) Faction gets absorbed into another Faction
	 */
}
