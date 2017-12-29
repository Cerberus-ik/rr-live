# RR-live
This is our entry for the riot api dev challenge. Our goal is to provide an easy to use and appealing way to see the 
current "cool" and "meta" Runes from Runes-Reforged and how their popularity shifts over time. Everything is placed on 
an interactive map with the ability to scroll through the recent Patches and days.

### What we learned from this project and what we plan for the future
Everything went together quite quickly and we had a prototype up and running in 3-5 hours. The biggest challenge we 
faced was to store the data in a smart and effective way. We explain our thought process for the first prototypes more 
detailed in later parts of this documentation. Lets just say we should have planned our backend a bit more future proof.
Throughout the developing process we were sure that we would have a polished prototype running before the deadline ends.
That was until our database broke down 2 days before the deadline and our new database had corrupted data in it. But we 
managed to fix those problems rather quickly with our new ``v2 Programs``.  
No matter how well we do in the Api challenge we plan on continuing our support for this project. We think we have 
something on our hands that could be successful in the future as a standalone website. 

### What we expected from our results
In the first days we expected a strong tendency to pick already known runes. 
- Electrocute aka Thunder Lord is a big player favorite so it will probably rise in popularity and fall off after 
  players discover the new runes.
- To be honest the arcane comet was probably a bit over buffed in 7.23 but after the recent nerves we expect a fall off 
  in its pick rate for later games.
- We think that more complicated runes like the unsealed spell book will see a rise in the next weeks. With time players 
  understand what makes a rune strong and figure out new ways to play it. This is especially true for utility runes.

### What we learned from the results
- ``Press the Attack`` is by far the most picked rune with pick rates over 50%! That is incredible.
  Even tho the nerves in ``7.24`` helped bringing more variety to the bot lane.
- Yep Electrocute is pretty popular in the mid lane
- Top lane has the most variety in runes with no clear winner after the first few weeks. 
- What needs some improvements:
    - There are no clear jungle runes. This means that most keystones have little to no effect in the jungle. The 
    current top 3 runes do absolutely nothing in the jungle.
    - We have many runes that see almost no play even tho they might be useful. Every rune tree has at least one rune 
    that almost nobody plays. Maybe a slight over buff can help to show players what a certain rune can do.
### First conceptual design
![Image](https://github.com/Cerberus-ik/rr-live/blob/master/resources/images/rr-live.png)

### Why are 80% of this project written in java?
Almost the whole backend is written in java, but why tho? Since we splitted the project into the front end ``Garry`` and
the backend and data aggregation ``(Cerberus-ik)`` both of us used technologies we are familiar with. I ``(Cerberus-ik)`` 
mainly work with java and I knew that java could handle every single task we threw at it. ``Garry`` on the other hand 
worked a lot with [MDL](https://getmdl.io/index.html) in the past so it was the obvious choice. We still experimented with new stuff
like our backend framework: [Spark](http://sparkjava.com/). We have a small time windows for the whole project and decided that it would
be best to stick with known technologies. But we might switch over to new frameworks in the future if they would give us
significant improvements over our current solutions. Because java is not the solution for everything.

### What are all of those legacy projects about?
We cover how we learned that a file based system was a ticking time bomb in the ``legacy`` projects. We always knew that
this approach would lead to problems in the future. But we did not expected to hit a wall so soon. One reason why we hit
the barrier sooner than expected was the generous api key ``Riot Git Gene`` provided us. Thank you again for the key. It
improved the accuracy of the whole project ``:)``. With the move to a database it speed up the conversion process by 
27000% no joke.

### So how does it work?
If you want an in deep explanation for each Part/Program of this project 
refer to their respected folders.

![Image](https://github.com/Cerberus-ik/rr-live/blob/master/resources/images/schematic.png)

##### Step 0: Get some summoners to base the data on
We started with 5000 Summoners from EUW that are Platinum or higher. 
The data did not come from us. A friendly Member of the Riot-Api Discord provided 
us with the necessary data. This sped up the development process but we don't want to rely on the data forever and so 
we created our own solution: the [SG](https://github.com/Cerberus-ik/rr-live/tree/master/Riot-SG) short for Summoner gatherer. It basically starts with one summoner and gets every single 
player from his/her League. Afterwards [SG](https://github.com/Cerberus-ik/rr-live/tree/master/Riot-SG) searches through every single ranked game from them that they played this season. 
Afterwards we filter for players that are plat or higher and save their ranked leagues temporarily. 
And finally we just get the summoners from every single league and save some basic information like their ``id and region`` 
in a single json file. This is down once but we consider doing it again and doing some more intense filtering. Because some players are inactive and 
basically useless for further data gathering. 

##### Step 1: Get the actual data
Next up we use a program called [GG](https://github.com/Cerberus-ik/rr-live/tree/master/Riot-GG) which is short for Game-Gather. It reads out the before mentioned ``summoners.json`` to
gather data. We get every ranked game from a summoner after our cut off date which is ``1511568000000`` which is 
the 25.11.2017 0:00:00 (11.25.2017 for all of you americans out there ^^) This way we can be sure that every game contains the
new Runes Reforged system. From every game we get we save around 5% of it in a mysql database.
We only save so lille of the game since we only need a few parameters from each game. Take a look in the [GG](https://github.com/Cerberus-ik/rr-live/tree/master/Riot-GG) folder
if you want to know more about this step. 

##### Step 2: Convert the data
This step takes the thousand of games we got from the api and puts them in a nice and compact format for our second database to store.  
That's why the program is called [GTAD](https://github.com/Cerberus-ik/rr-live/tree/master/Games-To-Ajax-Data) (games-to-ajax-data). "All" the program basically does is read out the afford mentioned 
database and count out the runes. Take a look in the [GTAD](https://github.com/Cerberus-ik/rr-live/tree/master/Games-To-Ajax-Data) directory for a more detailed explanation. 

##### Step 3: Store the data
We use a different MySql database to store our formatted data. [GTAD](https://github.com/Cerberus-ik/rr-live/tree/master/Games-To-Ajax-Data) does the storing process by itself. 
The table gets wiped out every single time we update the data to prevent unwanted incompatibilities. Since we can easily change
the steps we gather and split up data. We choose 12h as a start so we don't need quite as many games as for example 6 or even 
3 hours.

##### Step 4: The backend api
Our [backend-api](https://github.com/Cerberus-ik/rr-live/tree/master/Backend-Api) is a plain REST api. Not fancy but it it's job.
The frontend is almost completely build in javascript. This means we need a way to protect our database from unwanted access. 
We chose [Spark](http://sparkjava.com/) a simple java based solution to build our own REST Api on top of it. 
In hindsight all of this is a bit over engineered but it gives us the flexibility we need and want.

##### Step 5: The frontend
As you might have noticed we went full material design here. We wanted to create an appealing and informative project 
and material fits our needs pretty well. We choose [MDL](https://getmdl.io/index.html) from google. A light weight approach of
implementing material design on your website. Take a look in the [rr-live](https://github.com/Cerberus-ik/rr-live/tree/master/rr-live) folder if you 
are interested in the code that went into the project.
The frontend calls our own backend api to get the data in a json format and displays them to the user.

### Installation
If you want to can take any of the programs and make your own changes to them or just try them out for fun.
You need to create a config.json tho. Put it in a resource folder that is located in the main directory. All programs
use this config file and share one api key. This project was written under a strict deadline. We did not focus on
usability for different environments if you want to copy one or more projects from this repository do it but you have
to put some work into it to get it working.
Nevertheless the config should look somewhat like this:
```json
{
  "key":"project-api-key",
  "sg-config": {
    "summoner-region": "EUW1",
    "summoner-name": "summonerName",
    "summonerLimit": 10000,
    "coolDownBetweenCalls": 0
  },
  "gg-config": {
    "coolDownBetweenCalls": 0,
    "summonerBulk": 25,
    "database": {
      "host": "host",
      "db": "db",
      "db_pw": "pw",
      "db_user": "user"
    }
  },
  "gtad-config": {
    "sourceDatabase": {
      "host": "host",
      "db": "db",
      "db_pw": "pw",
      "db_user": "user"
    },
    "targetDatabase": {
      "host": "host",
      "db": "db",
      "db_pw": "pw",
      "db_user": "user"
    }
  }
}
```

### TODO list
- mobile support
- 100% 4k support

### Whats up with 4k and mobile support?
Yes we definitely want to implement those two features. Since even riot does not have good 4k support it was not a top 
priority for us. But we already started implementing 4k support. Lets say it is in an early ``BETA`` stage.
Mobile support on the other hand is a bit more complicated. The site would look somewhat odd on such small screens. So 
full mobile support is still somewhat far away. 

### Disclaimer
RR-Live isn't endorsed by Riot Games and does not reflect the views or opinions of Riot Games or anyone officially 
involved in producing or managing League of Legends. League of Legends and Riot Games are trademarks or registered 
trademarks of Riot Games, Inc. League of Legends © Riot Games, Inc.