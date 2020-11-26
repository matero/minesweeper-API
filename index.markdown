# [Deviget - Minesweeper challenge](https://matero-minesweeper.herokuapp.com)

I used the challenge to force me to try out some good practices that I could not use in my last professional steps.
Package-by-feature, immutability, last spring-boot versions, using plain SQL with stored procedures, limit abstraction
visibility and object creation.

It seemed easy while I was thinking about it, but it was harder than expected.

Maybe using common practices and tools (which are already in my toolbox) this could be done quicker, but I needed to try
it out :), it was a challenge after all, so make it a complete challenge.

## Architecture 
The application can be seen in 3 tiers:

1. **`persistence`**: `JdbcTemplate` based repositories which interacts with postgresql db.

2. **`business logic`**: services and domain objects that process the users actions to work with **games** and
                         **accounts**, using `repositories` to persist changes or fetch data.

3. **`web api`**: controllers implementing the `REST API` to play _Minesweeper_.

## Game constraints

- To start playing you must register an Account at `/register`. Once you have registered the account, you will
automatically have a JWT token to interact with the REST API. Only the email is validated, the strength of the password
is not checked, I could use something like [passay](http://www.passay.org/) and create a validator... but I think it
exceeds the challenge, for the same reason no registration confirmation is done. 

- Once you have a JWT token (you are logged in, or just created your account) you can create games.

- At that moment, the game is not considered as being played, but created. Once you take an action on it (flag or reveal
 cell, it would track the time spent in the game).

- Then you can `PAUSE` the game, or keep playing until you `WON` or `LOOSE` the game.

- Every time you take an action on the game, the api gives you the complete status of the game, with revealed cells and
unrevealed ones as a matrix, time played, columns, rows, mines in the board, etc.

- at any time you can query for all your games, but you never has access to other accounts games.

## Design Principles

### Immutability
No entity in the system should be mutable unless an explicit requirement exists. This, I think, give us a code easier to
read, maintain and evolve.

For example, `Game` which is a key concept on this challenge is immutable, this forced the definition of a game builder,
game changes, service and repository which work with games changes instead of complete game states.

### Package by feature
Each package correspond to important, high-level aspects of the challenge. In this case, we have:

- `minesweeper`: global definitions, common to the system.
- `minesweeper.accounts`: definitions related to Account to play games.
- `minesweeper.games`: Games related definitions.
- `minesweeper.games`: authentication and authorization definitons, using JWT.

This give us:

- *Modularity* packages have high cohesion, high modularity, and low coupling between packages.

- *Easier Code Navigation* Maintenance programmers need to do a lot less searching for items, since all items needed for
a given task are usually in the same directory.

- *Higher Level of Abstraction* It makes it easier to think about a problem, and emphasizes fundamental services over
implementation details. As a direct benefit of being at a high level of abstraction, the application becomes more
self-documenting: the overall size of the application is communicated by the number of packages, and the basic features
are communicated by the package names.

- *Separates Both Features and Layers* The package-by-feature style still honors the idea of separating layers, but that
separation is implemented using separate classes (`GamesService` and `GamesRepository`).

- *Minimizes Scope* Allows some classes to decrease their scope from public to package-private. This is a significant
change, and will help to minimize ripple effects.

- *Better Growth Style* The number of classes within each package remains limited to the items related to a specific
feature. If a package becomes too large, it may be refactored in a natural way into two or more packages.

## Security
JWT is used to authenticate and authorize the application endpoints.
No roles where defined other than `user`, again because I thought it would exceed the scope of the challenge. No
administration requirements exists, so, why bother?

## Details

### Persistence details
Avoided the use of `ORM`s as hibernate or JPA, mainly because the board of the game, which is basically a matrix and
would make the mapping and operations like a horror movie. So the simpler but yet powerful [Spring JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html)

Also, to avoid doing multiple related calls (like when updating the state of the game and tracking the time played), I
preferred to use postgresql stored procedures which seemed like a simpler way.
JdbcTemplate and its exception translation provides an easier way to report key duplication (email already used) or
element not found errors.

To describe database schema, [postgresql comments](https://www.postgresql.org/docs/12/sql-comment.html) **+**
[SchemmaSpy](http://schemaspy.org/) **+** [exec-maven-plugin](https://www.mojohaus.org/exec-maven-plugin/) were used to
generate HTML pages that can be accessed at [minesweeper db-docs](https://matero-minesweeper.herokuapp.com/db-docs/index.html).

Finally, but not less important, [flyway](https://flywaydb.org/) is the tool selected to carry on the DB migrations.

The database used is, as already stated, postgres in its version `12`, because thats the current version usable at
[heroku](https://www.heroku.com/postgres). Used the opportunity to play with postgres enums, which were at my radar from
long time ago :).

### Business logic details
At its core, not all related logic is solve at Service classes. Some logic is easier to read and evolve if its defined
in the domain objects (like the case of `minesweeper.Game`) and in such cases the service opens the transactions,
checks preconditions create the according results.

As Java still doesn't have something like scala or kotlin `object`, many times were something like a singleton appears
in the design, and enums can't be used, I use the recommended java way: an inner class with a class constant
representing the singleton. Like for example:

```java
public final class BadCredentialsProvided extends MinesweeperException
{
  private BadCredentialsProvided() { super("Bad credentials."); }

  static final class Unique
  {
    static final BadCredentialsProvided INSTANCE = new BadCredentialsProvided();
  }
}
```

**NEVER** a service has knowledge of what happens in a repository or who is his caller, it only deals with transactions,
precondiotions, and business related logic (minesweeper game and accounts creation this case).
**IT CAN** assume that certain patterns corresponds to a model, like it daes to check that an email represents a game
owner, in:

```java
Game getGameWithId(final int gameId, final String gameOwner)
{
  final var game = get(gameId);
  if (!game.owner.equals(gameOwner)) {
    throw new AccessDeniedException("You don't own this game.");
  }
  // some more code
}
```

### REST API details

The rest api is implemented using spring `@Controllers`, it is the simple tool for the task.

No DELETE actions where implemented, and no integration tests on them were defined.
Mostly because I thought it exceeded the scope of the challenge and preferred to use swagger to see the correct behavior
of the controllers.

In a team development, under a professional project, I would prefer to use integration tests to check the behavior of
controllers, security, etc.

It was a challenge to configure correctly `springfox + jwt`, when doing an authentication in swagge-ui you **MUST** set
the token with `"Bearer "` at the beginning, so if your token is `blablabla`, instead of set `blablabla` in the input
box, you must put `Bearer blablabla`. Seems to be a springfox issue, I spent too much time on that, and by now its is
awkward, but it can be used.

## Development environment

Used JDK 15, maven wrapper, with maven version 3.6.3, docker, and alpine Postgres image to simulate the persistence
layer when working locally.
For testing, an in-memory Postgres DB is used (see [zonky](https://github.com/zonkyio/embedded-postgres)).

To install Java 15, you can use [SDKMAN!](https://sdkman.io/) or whatever you prefer, to install docker it depends on
your SO.
To start the postgres container, you can use the script defined at `docker/start_pg` and to stop
it `docker/stop_pg` (this to scripts are for unix shells if you are working on linux, adapt them it should be easy).

To generate db and solution documentation (this doc as HTML), just run: `./mvnw clean package -PgenerateDocs` (in *nix
platforms) or `mvnw.cmd clean package -PgenerateDocs` (in windows platforms).
After this command is executed, you will have a database description at `/db-docs/index.html` and this document
available at `root path` or at `/index.html`.

