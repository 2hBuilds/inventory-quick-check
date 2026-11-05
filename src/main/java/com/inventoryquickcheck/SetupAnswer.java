package com.inventoryquickcheck;

/** What asking Inventory Setups for the open setup gave. Immutable. */
public final class SetupAnswer
{
	/** NOT_RUNNING: no answer came; NO_SETUP: it answered with no setup open; READY: a setup came with it. */
	public enum State
	{
		NOT_RUNNING,
		NO_SETUP,
		READY
	}

	private final State state;
	private final String name;
	private final SavedSetup setup;

	SetupAnswer(State state, String name, SavedSetup setup)
	{
		this.state = state;
		this.name = name;
		this.setup = setup;
	}

	public State state()
	{
		return state;
	}

	/** The name of the setup; null unless READY. */
	public String name()
	{
		return name;
	}

	/** The setup; null unless READY. */
	public SavedSetup setup()
	{
		return setup;
	}
}
