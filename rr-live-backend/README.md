# Backend-Api
No fancy name this time. ``:(``
This is basically our whole backend api. It might me slightly outdated because it's currently hosted on Heroku and we
use their git integration. I will try my best to keep this project up to date.

### What is it build on?
We used [Spark](http://sparkjava.com/) a very very simple framework.
We chose [Spark](http://sparkjava.com/) over other solutions like Spring because we simply don't need 99% of the features
they offer. [Spark](http://sparkjava.com/) is small and light weighted. This makes the overall footprint of the api
much smaller. It does everything we want and need it to do. There is no good reason for us to switch to a different solution.

### How does it work?
Mostly it just accesses the database and pulls data out. But we already have more complicated functions implemented that
take up a lot of database power. So we decided on implementing a caching system. Every caching value has it's own 
caching time attached to it. This means that extremely large operations only get re cached every 12 or even 24 hours.

### The caching system
We build our very own custom caching system. This is why the website tends to load much longer if no one visits the site
for a while. Every single cache value has to be updated again and those actions usually take quite a while. This makes
the site run faster if more people visit it on a regular base. The most database intensive calls need around 500-2000ms
to cache. Reading the cache only takes around 2ms. It is planned that the cache refreshes itself if it is outdated. So 
no user has to wait if he/she wants to visit the site.