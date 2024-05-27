INSTALL AND EDIT THE CONFIG AFTER THE FIRST LAUNCH. 
FALLBACK SERVER IS NOT SUPPORTED YET, WIP
Remove everything from 
try = [
]
- in velocity.toml to kick the player when server stops/crashes instead of moving them to an active server
  
Leave
try = [
"SERVERNAME"
] 
- intact to keep a fallback functionality
