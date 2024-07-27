# marc-terminal
The next MARC trains, on your terminal.
Launching the main application loads the upcoming trains today:
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
You can also pass `tmr` (or `tomorrow`), `<weekday>`, or `7/27` as arguments to see the trains on that day.

To open up the mta's live schedules, you can pass `live` or for example `saturday live`.

To customize stops, emojis, and header, edit options.txt in the files folder.

To test it out, open `run.sh` or (`run.bat` on Windows).

NOTE: I have only tested this with the MARC-Penn line between Union Station and Penn Station. I am not liable for any consequences of a missed train on account of using this program. Please don't sue me if you miss your son's wedding.

## Quick Install (Mac/Unix)

FIRST: You will need `java 22`. To determine your java version, open up your Terminal and enter `java --version`. Update [here](https://www.oracle.com/java/technologies/downloads/).

I find this program most useful if I can just type something like `trains tomorrow` or `trains friday` without having to find the file each time.

To set this up:
Open `trains.sh` in a text editor, and change the following line:
```cd ~/marc-terminal-main/src```

Edit what comes after `cd` to wherever the src folder is stored (`Cmd+option+C`to copy the file path in mac).

You won't have to do this if you unzipped into your user folder ("JohnSmith").

Next, in Terminal enter,
`cd /usr/local/bin`

then

`open .`

Drag `trains.sh` into this folder.

Next, if you are running zsh then enter
`nano ~/.zshrc`
If bash then
`nano ~/.bash_profile`
If you're not sure just do both.

Add the line `alias trains=/usr/local/bin/trains.sh` to the end, save and quit. Done!
