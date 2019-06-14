openDataServer 5400 10
connect 127.0.0.1 5402
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
pause
breaks = 0
throttle = 1
var h0 = heading
// sleeping is necessary to allow the plane to stabilize its direction and get a good starting speed before changing the rudder,aileron,elevator..
sleep 5000
while alt < 1000 {
// originally we divided by 20, but it results in movements that are not smooth.
rudder = (h0 - heading)/180
aileron = - roll / 70
elevator = pitch / 50
print alt
sleep 150
}
print "done"