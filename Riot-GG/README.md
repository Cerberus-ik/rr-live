# GG – Game Gatherer 
We need real life games to get real life data. And our ``GG`` helps us getting a big amount of them
in a short amount of time. 

### How does it work?
- We get every game from a summoner with the ``getMatchlist`` api and filter them for:
    - Being played after Runes Reforged got implemented
    - Not already being parsed
- We only save the important information from a game: Runes, Patch, Time in a separate file.

### Why separated files for each game?
We don't have a server that could handle the amount of games we get. We want to switch to a database
in the future since it has a much smaller overhead. With such small file sizes we talk about a 50% file
size overhead. But this is not a problem with big hard drives. We could just smash every game into a single
file but this would slow down ``GG`` dramatically. We would have to save the file every time we make changes
to it since we don't want to make unnecessary calls. And checking if a game has already been downloaded is
quite slow as well. Reading through such a big file instead of just checking if a file is present in a certain
folder is significantly slower.

#### Step 1 load the summoners
Our ``SG`` gives us the baseline for getting games with ``GG``. We started of with ``5000`` summoners
but since the improvements we did with the ``SG`` we could get games from ~ ``92000`` summoners.

#### Step 2 get the match list from a summoner
Next we get the ``MatchList`` from the summoner with the ``getMatchlist`` api and filter 
out the games we don't already have parsed and got played after RR got implemented. 
Next we create a ``Runnable`` for each game a summoner has played and that fulfils our criteria. 

#### Step 3 get the games
Do avoid duplicated games we only multi thread the process of getting all the games with the ``getMatch``
api. This takes only a few milliseconds depending on the amount of games a summoner has played since the
last check.  

#### Step 4 parse the games
We only need around 5% of the data that is returned with the ``getMatch`` api. That's why we get the data
and save them in a separate file. Without all the file overhead one game takes up around 1.5kb of space.
A full game on the other hand takes up around 28kb of space. We save around 94.6% off space.

#### Step 5 save the game
Since part 4 and 5 are booth multi threaded we are not to worried about performance her. But we still want
to get a server in the future so we longer have to deal with thousands of files in a single folder.
But to get the best performance out of our current setup we use the [BufferedWriter](https://docs.oracle.com/javase/7/docs/api/java/io/BufferedWriter.html) for
decent performance.

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
