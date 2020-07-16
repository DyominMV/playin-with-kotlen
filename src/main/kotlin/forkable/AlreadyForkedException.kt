package forkable

class AlreayForkedException: 
  Exception("Cannot call forkable object's methods after it was forked")