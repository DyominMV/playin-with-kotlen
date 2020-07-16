package forkable

class AlreadyForkedException: 
  Exception("Cannot call forkable object's methods after it was forked")