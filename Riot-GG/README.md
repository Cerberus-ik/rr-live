# GG – Game Gatherer v2
We need real life games to get real life data. And our ``GG`` helps us getting a big amount of them
in a short amount of time. 

### How does it work?
- We get every game from a summoner with the ``getMatchlist`` api and filter them for:
    - Being played after Runes Reforged got implemented
    - Not already being parsed
- We only save the important information from a game: Runes, Patch and the timestamp in a database

### Why we no longer use separate files for games.
The way we saved our games made it impossible to load games in small chunks. We always had to read every single
game into memory and process it. If the program crashed we had to begin at 0. Due to the inefficiency in loading
so many files it took around 1 hour to process all of our games and bring them in our ajax format. We switched
to a local database until we have our own server. Which benefits do we get by using a database?
- smaller file footprints
- faster storing
- faster access
- more flexibility
- faster processing

### Why we used files at the start of this project
We always had the intention to switch over to a database but until we had a big and powerful one we wanted to store
them locally. This meant that we could easily write a program that would convert our local files and stores them in
a database. We underestimated the file overhead in windows and the amount of games we would actually get.

#### Step 1 load the summoners
Our ``SG`` gives us the baseline for getting games with ``GG``. We started of with ``5000`` summoners
but since the improvements we did with the ``SG`` we could get games from ~ ``92000`` summoners.

#### Step 2 get the match list from a summoner
Next we get the ``MatchList`` from the summoner with the ``getMatchlist`` api and filter 
out the games we don't already have parsed and got played after RR got implemented. 
Next we create a ``Runnable`` for each game a summoner has played and that fulfils our criteria. 

#### Step 3 get the games
To avoid duplicated games we only multi thread the process of getting all the games with the ``getMatch``
api. This takes only a few milliseconds depending on the amount of games a summoner has played since the
last check.  

#### Step 4 parse the games
We only need around 5% of the data that is returned with the ``getMatch`` api. That's why we get the data
and grab the values we need to store them in a database. One game takes up around 1.5kb of space.
A full game on the other hand takes up around 28kb of space. With this process we save around 94.6% off space.

#### Step 5 save the game
We currently store all games in a local mysql database.
Our current database looks somewhat like this:

| gameId      | region    | game             |timestamp      |
| ----------- |:---------:| ----------------:|--------------:|
| 123         | EUW1      | {"json":"data"}  | 1511718058731 |
| 234         | NA1       | {"json":"data"}  | 1511751558731 | 
| 345         | EUW1      | {"json":"data"}  | 1532311054731 | 

### The result
Our games look all somewhat like this. We have our ``game`` object for the ``GTAD``. So it knows what to do
with all the data. The index at the start of every single value is our player/participant id. It is consistent
throughout the file and gives us the information we need like: Which role and lane did he/she play on, which runes
did he/she pick and so on. In our example our number 1 would be the ADC how strangely enough picked the arcane comet. 
Maybe you noticed we not only save the keystone but every single rune from a player. This gives us room to expand 
the site in the future. But we don't use them currently. 
````json
{
   "game":{
      "patch":"7.24.212.5337",
      "region":"EUW1",
      "timestamp":1513937595294
   },
   "1-role": "DUO_CARRY",
   "1-lane": "BOTTOM",
   "1-perk0": 8229,
   "1-perk1": 8226,
   "1-perk2": 8234,
   "1-perk3": 8237,
   "1-perk4": 9111,
   "1-perk5": 8136,
   "2-role": "DUO_SUPPORT",
   "...": "..."   
}
````
