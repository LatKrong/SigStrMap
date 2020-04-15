# Signal Strength Map

## What is it?

Signal Strength Map is an Android app that's intended to show the signal strength of a WiFi in a
heat map.

## How is it done?

WiFi signal strength is captured along with the geolocation, a heat map is then generated from those
information.

## Where is it at?

Turns out GPS accuracy is pretty bad, with 4 meter accuracy. Unfortunately, this app will not work
properly without super precise location information, as the heat map should only cover a very small
area.

## Is this project dead then?

This is my starter Android project, hence I am still experiencing with different things, though the
end product is probably not something useful.

## What has been done?

* Created a custom view for displaying the heat map.
* Draw heat map on Canvas.
* Getting location and WiFI signal strength information.
* Handling permissions properly.
* A simple settings view.
* Save instance state.
* Save heat map to disk.
* Listing saved heat map in the main view.
* Ability to load an existing heat map from the main view.
* Ability to add a new heat map through main view.
* Ability to delete a heat map.

## What's next?

* Polish UI.