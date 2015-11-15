# Wubble

Wubble is a movie themed social network built for Android

We have built this app previous year, but we don't intend to develop it any further, so we wanted to provide it as a social network template to the open source community.

##How it works

![Feed](https://github.com/MovieNetwork/Wubble/blob/master/gifs/Feed.gif)
![Movie](https://github.com/MovieNetwork/Wubble/blob/master/gifs/Movie.gif)


Wubble has a twitter-like user interface, where you can follow other users, send Wubbles, and read their Wubbles. 
You can share your thoughts about a movie ie. wubble.
You can follow people, read, like, dislike their wubbles.
You can start a discussion about a movie by commenting one of your friends wubbles.

##Installation

Just clone the repository and open the project in Android Studio.

###Api Keys

Wubble uses [Parse](https://parse.com/) api to to store its data. 
[Rotten Tomatoes](http://developer.rottentomatoes.com/), [TMDb](https://www.themoviedb.org/documentation/api), [OMDb](http://www.omdbapi.com/)
apis are used for getting various movie information. 

Example api keys are provided in [Constants](https://github.com/MovieNetwork/Wubble/blob/master/app/src/main/java/com/proxima/Wubble/Constants.java#L89) file, 
if you want to create your own app, you should change them.

