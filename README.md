# marc-terminal
The next MARC trains, on your terminal.

This program parses [GTFS](https://gtfs.org/) files to find all the departures and arrivals between two stops.

Although it's designed for the MARC, a commuter train that bridges Baltimore and DC, the code is potentially adaptable to other transit systems with GTFS support.

Running `Trains` without any arguments will print the upcoming trains today:
```
======================================
🦀 TRAINS TO BALTIMORE PENN STATION 🦀
======================================
[10:25 AM----1h3m---->11:28 AM]
 12:30 PM----58m----->01:28 PM
 02:05 PM----1h5m---->03:10 PM
 04:30 PM----59m----->05:29 PM
 05:30 PM----59m----->06:29 PM
 07:30 PM----1h7m---->08:37 PM
================================
🏛 TRAINS TO DC UNION STATION 🏛
================================
[08:55 AM----1h------>09:55 AM]
 11:00 AM----1h2m---->12:02 PM
 12:40 PM----1h------>01:40 PM
 02:00 PM----1h1m---->03:01 PM
 04:05 PM----1h------>05:05 PM
 06:00 PM----1h5m---->07:05 PM
```
You can also pass `tmr` (or `tomorrow`), `<weekday>`, or `mm/dd` as arguments to see the trains on that day.

**This runs entirely offline.** But to open up the mta's live schedules, you can pass `live` or for example `saturday live`.

To customize stops, emojis, and header, edit options.txt in the files folder.

**NOTE:** I have only tested this with the MARC-Penn line between Union Station and Penn Station. I am not liable for any consequences of a missed train on account of using this program. Please don't sue me if you miss your cousin's wedding.

## Quick Install (Mac/Unix)

### Preliminaries

You will need `java 22`. To determine your java version, open up your Terminal and enter `java --version`. Update or install [here](https://www.oracle.com/java/technologies/downloads/).

### Test run:

Open Terminal and type

`chmod +x ` with the space

Drag `run.sh` into the window, then hit enter. Then open `run.sh` with Terminal.

### Run normally
`java Trains <args>`

### Shortcuts

I find this program most useful if I can just type something like `trains tomorrow` or `trains friday` in Terminal.

To set this up:
Open `trains.sh` in a text editor, and change the following line:

```cd ~/Downloads/marc-terminal-main/src```

Edit what comes after `cd` to wherever the src folder is stored (`Cmd+option+c`to copy the file path in mac).

You won't have to do the above steps if you unzipped into your Downloads folder and plan to keep this there.

Next, in Terminal enter,

`cd /usr/local/bin`

then

`open .`

Drag `trains.sh` into this folder.

Enter

`chmod +x /usr/local/bin/trains.sh`

The final step depends on if you're running zsh or bash as your Terminal shell. If you don't know what this means just do both.

If zsh then enter

`nano ~/.zshrc`

If bash then

`nano ~/.bash_profile`

This will launch a text editor. Add the line 
`alias trains=/usr/local/bin/trains.sh`

to the end, save with `Control+X`. Restart Terminal. Enter `trains` to ensure it works. Done!

## How it works

The Maryland Transit Adminstration [publishes](https://www.mta.maryland.gov/developer-resources) trains schedules in GTFS format, which standardizes public transit information. This program parses the data, constructs the desired schedules from it, and compares them against the current time and date. That's basically it.

## Areas for improvement
### Caching
On start up, the program parses all the GTFS file afresh, with no caching of any kind. This is computationally wasteful, but since it only takes a few seconds, I wasn't motivated to implement some kind of serialization. However, it shouldn't be a massive undertaking to do so and would improve performance. This could be desired if more resourcing taxing features are added, such as live data.

### Live Data
The [MTA](https://www.mta.maryland.gov/developer-resources) also offers real time transit infromation using GTFS-RT. This could be availed of to alert users of delays and schedule changes. I was originally going to add this but got lazy, and settled with the URL launcher for now.
