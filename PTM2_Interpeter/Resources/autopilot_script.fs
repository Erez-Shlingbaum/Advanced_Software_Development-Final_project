// Opening a server to receive updates from the simulator
openDataServer 5400 10
// Connecting to flight gear to enable the ability to send commands to the plane
connect 127.0.0.1 5402

// Binding script variables to simulator variables in flight gear
var breaks = bind /controls/flight/speedbrake
var throttle = bind /controls/engines/current-engine/throttle
var heading = bind /instrumentation/heading-indicator/indicated-heading-deg
var airspeed = bind /instrumentation/airspeed-indicator/indicated-speed-kt
var roll = bind /instrumentation/attitude-indicator/indicated-roll-deg
var pitch = bind /instrumentation/attitude-indicator/internal-pitch-deg
var rudder = bind /controls/flight/rudder
var aileron = bind /controls/flight/aileron
var elevator = bind /controls/flight/elevator
var alt = bind /instrumentation/altimeter/indicated-altitude-ft

// Pausing to let the user continue the script when flight gear is up and running
pause

// Turning on the engine and freeing the breaks
breaks = 0
throttle = 1

// h0 holds the direction we want to fly to, on a runway it is the current direction (straight)
var h0 = heading

// the plane has a deviation to the left, so this is an experiment to fix it
rudder = 0.1
// sleeping to allow the plane to gain non-trivial velocity before we change flight controls
sleep 5000

// stebalizing the plane and taking off into the air
while alt < 1000 {
// originally we divided by 20, but it results in movements that are not smooth.
rudder = (h0 - heading)/140
aileron = - roll / 70
elevator = pitch / 50
print alt
sleep 150
}
print "done"