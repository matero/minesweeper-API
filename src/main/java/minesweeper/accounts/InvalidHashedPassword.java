package minesweeper.accounts;

import minesweeper.MinesweeperException;

final class InvalidHashedPassword extends MinesweeperException
{
  private InvalidHashedPassword() { super("Hashed password is invalid."); }

  static final class Unique
  {
    static final InvalidHashedPassword INSTANCE = new InvalidHashedPassword();
  }
}
