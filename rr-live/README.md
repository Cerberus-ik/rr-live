# RR-live
This is our new material frontend. It uses [MDL](https://getmdl.io/index.html) and [Google Charts](https://developers.google.com/chart/) 
as it's frameworks. 

### Icons and rune names
We have our own icons laying on the server so we can load them quickly. The rune names on the other hand get cached by
the DDragon api. We reuse them later for multiple features like: displaying them on the map or in the all time records.

### AJAX
We use quite a lot of ajax. Much of the gets loaded while you see the loading circle rotating. The rune names and some
basic stats like the total analyzed runes all get loaded during this loading screen. The actual statistics on the other
hand all load while the user is on the site. We can specify the amount of so called ids(steps in which runes get 
analyzed, currently 12h apart) that we load at once. We experimented with a few options how we want to load the data.
- load more data if the user gets closer to the end of the current cache
- load all at once
- load more data as soon the last backend api call finished
We settled with the last option because we wanted to show a diagram  with the amount of games we have cached and we
generated with the backend api data. Otherwise the diagram would load only if the user looks through all of the ids on
the map.