# RR-live
This is my entry for the riot api dev challenge. My goal is to provide an easy to use and appealing way to see the 
current "cool" and "meta" Runes from Runes-Reforged. Everything will be placed on an interactive map with the ability to 
scroll through the recent Patches and days.

### First conceptual design
![Image](https://github.com/Cerberus-ik/rr-live/blob/master/rr-live.png)

### So how does it work?
If you want an in deep explanation for each Part/Program of this project 
refer to their respected folders.

##### Step 0: Get some summoners to base the data on
We started with 5000 Summoners from EUW that are Platinum or higher. 
The data did not come from us. A friendly Member of the Riot-Api Discord provided 
us with the necessary data. This sped up the development process but we don't want to rely on the data forever and so 
we created our own solution: the ``SG`` short for Summoner gatherer. It basically starts with one summoner and gets every single 
player from his/her League. Afterwards ``SG`` searches through every single ranked game from them that they played this season. 
Afterwards we filter for players that are plat or higher and save their ranked leagues temporarily. 
And finally we just get the summoners from every single league and save some basic information like their ``id and region`` 
in a single json file. This is down once but we consider doing it again and doing some more intense filtering. Because some players are inactive and 
basically useless for further data gathering. 

##### Step 1: Get the actual data
Next up we use a program called ``GG`` which is short for Game-Gather. It reads out the before mentioned ``summoners.json`` to
gather data. We get every ranked game from a summoner after our cut off date which is ``1511568000000`` which is 
the 25.11.2017 0:00:00 (11.25.2017 for all of you americans out there ^^) This way we can be sure that every game contains the
new Runes Reforged system. Every game gets saved in its entirety. Why the entire game? We wanted to make sure that we can add
more data and information in the development process. Which already happened a few times.

##### Step 2: Convert the data
This step takes the thousand of files and games and puts them in a nice and compact format for our database to store.  
That's why the program is called ``GTAD`` (games-to-ajax-data). "All" the program basically does is to read out all the files 
from a given directory and count out the runes. Take a look in the Games-To-Ajax-Data directory for a more detailed explanation. 

##### Step 3: Store the data
We use a simple MySql database to store our formatted data. ``GTAD`` does the storing process by itself. 
The table gets wiped out every single time we update the data to prevent unwanted incompatibilities. Since we can easily change
the steps we gather and split up data. We choose 12h as a start so we don't need quite as many games as for example 6 or even 
3 hours.

##### Step 4: The backend api
The frontend is almost completely build in javascript. This means we need a way to protect our database from unwanted access. 
We choose [Spark](http://sparkjava.com/) a simple java based solution to build our own REST Api on top of it. 
In hindsight all of this is a bit over engineered but it gives us the flexibility we need and want.

##### Step 5: The frontend
As you might have noticed we went full material design here. We wanted to create an appealing and informative project and
material fits our needs pretty well. We choose [MDL](https://getmdl.io/index.html) from google. A light weight approach of
implementing material design on your website. 
The frontend calls our own backend api to get the data in a json format and displays them to the user.
