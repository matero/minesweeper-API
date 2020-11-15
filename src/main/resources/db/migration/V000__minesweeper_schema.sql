CREATE SCHEMA IF NOT EXISTS minesweeper AUTHORIZATION ${flyway:user};

COMMENT ON SCHEMA minesweeper IS $$Schema to hold _ALL_ **minesweeper** definitions (`tables`, `views`, `stored procedure`, etc.).$$;
