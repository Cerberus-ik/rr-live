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