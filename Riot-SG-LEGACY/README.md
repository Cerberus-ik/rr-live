# SG – Summoner Gatherer - LEGACY
This part of rr-live feeds the other data gathering programs with summoners.
We wanted a scalable solution that's why we wrote this program.

After a first test run we managed to get ``92423`` summoners that are platinum or higher.

### v2 is done
Checkout the [SG](https://github.com/Cerberus-ik/rr-live/tree/master/Riot-SG) folder for more information about v2.

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
The result should look somewhat like this. We plan to save all the data in a database and update it occasionally. 
But since summoners usually stick around in their current league or managed to get promoted this data is a good
baseline for further processing. To reduce the amount of api calls and speed up the ``GG`` we plan to filter
the summoners further. For example we only include players that played in the last 30 days to reduce "dead" calls
for new games from them. Or we filter for players that play a reasonable amount of games in a given time like
5 or more games in 30 days.
````json
{
  "data": [
  {
    "platformId": "EUW1",
    "summonerId": 12345678,
    "rank": "I",
    "tier": "PLATINUM"
  },
  {
    "platformId": "EUW1",
    "summonerId": 12345678,
    "rank": "I",
    "tier": "DIAMOND"
  },
  {
    "platformId": "EUW1",
    "summonerId": 12345678,
    "rank": "III",
    "tier": "PLATINUM"
  }
  ]
}
````
