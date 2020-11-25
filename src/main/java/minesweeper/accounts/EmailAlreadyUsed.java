package minesweeper.accounts;

import minesweeper.MinesweeperException;

public final class EmailAlreadyUsed extends MinesweeperException
{
  EmailAlreadyUsed(final String email) { super("The email '" + email + "' is already used."); }
}
