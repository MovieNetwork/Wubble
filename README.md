# Wubble  [![Build Status](https://travis-ci.org/MovieNetwork/Wubble.svg?branch=master)](https://travis-ci.org/MovieNetwork/Wubble)

Wubble is a movie themed social network built for Android

<a href="https://play.google.com/store/apps/details?id=com.proxima.wubble_deneme">
<img alt="Get it on Google Play"
       src="https://cloud.githubusercontent.com/assets/12447257/8147861/0b474efe-1280-11e5-9f09-d6fb1ebaf954.png" />
</a>

We have built this app previous year, but we don't intend to develop it any further, 
so we wanted to provide it as a social network template to the open source community.
Code quality is far from pretty, so feel free to contribute.

##How it works

<img src="https://github.com/MovieNetwork/Wubble/blob/master/gifs/Feed.gif" width="350">
<img src="https://github.com/MovieNetwork/Wubble/blob/master/gifs/Movie.gif" width="350">


Wubble has a twitter-like user interface, where you can follow other users, send Wubbles, and read their Wubbles. 
You can share your thoughts about a movie ie. wubble.
You can follow people, read, like, dislike their wubbles.
You can start a discussion about a movie by commenting one of your friends wubbles.

##Installation

Just clone the repository and open the project in Android Studio. It uses Android Build Tools 21.1.1.

###Api Keys

Wubble uses [Parse](https://parse.com/) api to to store its data. 
[Rotten Tomatoes](http://developer.rottentomatoes.com/), [TMDb](https://www.themoviedb.org/documentation/api), [OMDb](http://www.omdbapi.com/)
apis are used for getting various movie information. 

Example api keys are provided in [Constants](https://github.com/MovieNetwork/Wubble/blob/master/app/src/main/java/com/proxima/Wubble/Constants.java#L89) file, 
if you want to create your own app, you should change them.

##Authors
* Barış Özkuşlar - [@Epokhe](https://github.com/Epokhe) 
* Emre Yiğit Alparslan - [@EmreYigitAlparslan](https://github.com/EmreYigitAlparslan)
* Mustafa Nacar - [@mstfnacar](https://github.com/mstfnacar)
