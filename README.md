This will be an app that allows two users to declare their intent to hangout and the app will then find activities within a certain drive time radius

API List:
ArcGis
Firebase
Google Play Location

Upon starting the app, it calls several set up functions and tries to initialize location stuff.
TODO next:
(IMPORTANT) get this running on both API level 23 (Android 6.0.0) as well as lower API levels, I think it only works on 6.0.0 right now
Confirm location actually works
Send location of partner device over Firebase
Draw markers on said locations
Display drive time areas, display union of said drive time areas
<--if you can get here I'll be highly impressed-->
Figure out how to find points of interest in said union
Display those

SETUP INSTRUCTIONS
Install android studio
install SDKs (will prompt you do to this when you first try building the project)
pull from this repo
go to android studio file->new->import project->stuffLocator
<--make sure you find the actual stuffLocator project and not YoloSwagMhacks-->
install genymotion/literally any emulator, try running it on your phone


