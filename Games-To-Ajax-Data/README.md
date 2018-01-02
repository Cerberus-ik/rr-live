# GTAD v2
### Why v2?
We abandoned trying to save every game in a separate file. It worked. For a while.
But eventually the file overhead became to big and process of analyzing and converting the 
data to slow. GTAD no longer relies on files. It basically gets the data from one database,
converts it and stores it in a second database. (our backend database) 
We already updated our [GG](https://github.com/Cerberus-ik/rr-live/tree/master/Riot-GG) and did the same
to this program.

### Did it work?
Yes! GTAD used to run for about 90 minutes straight. Now? 20 seconds. Incredible right? We had to read every 
file at least once and keep a bunch of stuff in memory. This slowed down the process tremendously. 
So we are happy that we moved to a database sooner than later. 