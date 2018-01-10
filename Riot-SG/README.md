# SG – Summoner Gatherer v2
This part of rr-live feeds the other data gathering programs with summoners.
We wanted a scalable solution that's why we wrote this program.

After a first test run we managed to get ``92423`` summoners that are platinum or higher.

### Changes in v2
- Summoners get stored in a database
- Much better logging
- Multi region support
- Summoners that are no longer platinum or higher will get removed 

### How does it work?

#### Step 0 Set the base summoner
``SG`` starts with a single summoner that fulfils the requirements set by ourselves. 
We use the summoner api to save the response in memory temporarily.

#### Step 1 Get match participants
Next we get the ``MatchList`` from the summoner and filter for the current season so we 
get a reasonable response size. The matches in the ``MatchList`` already contain the summoner 
ids so we can save them for later after we filtered them for duplicates.

#### Step 2 Get high elo leagues
Now we have a significant amount of players but we want more without making much more api calls.
That's why we get the leagues the summoner currently is in with the ``GetLeaguePositions`` api
and filter them for Platinum+. 

#### Step 3 Get the actual summoners
After all the above mentioned steps we should have a few hundred leagues. Depending on the amount
of games the base summoner played. The only thing left to do is to get the actual ``League`` with
the League api and get all the summoners in it. We always filter for duplicates since a player could
be Platinum or higher in multiple queues (FlexQ and SoloQ).

### The result
The result should look somewhat like this.
 
| platformId   | summonerId  | rank  | tier      | playerId  |
| ------------ |:-----------:|:-----:|:---------:|----------:|
| EUW1         | 12345678    | I     | PLATINUM  | 1         |
| EUW1         | 12345678    | V     | DIAMOND   | 2         | 
| NA1          | 12345678    | III   | MASTER    | 3         | 
 

We save all the data in a database and update it occasionally. 
To reduce the amount of api calls and speed up the ``GG`` we plan to filter
the summoners further. For example we only include players that played in the last 30 days to reduce "dead" calls
for new games from them. Or we filter for players that play a reasonable amount of games in a given time like
5 or more games in 30 days.
