package minesweeper.security;

import minesweeper.MinesweeperException;

public final class BadCredentialsProvided extends MinesweeperException
{
  private BadCredentialsProvided() { super("Bad credentials."); }

  static final class Unique
  {
    static final BadCredentialsProvided INSTANCE = new BadCredentialsProvided();
  }
}
