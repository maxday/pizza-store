variable "project_id" {
    type  = string
    default = "techday-pizza-store"
}

variable "token" {
  type    = string
  default = "ya29.A0AfH6SMAF9aQYIUyrDTteYftVxAsNQeGtS-NRmHsnET9FvLiGQH5hOp4DT8AI4hYfxOmCrBGOci_RIaJvYv9TgPjfKbUHggr9jadvSpeVzBjqoYgbqKelHhUEAaIwnpJ6PfP3nhuyHJQc2gK6qRytBGIqFbQoK6MkCU1Ra-r0pvhi"
}

variable "mongo_connexion_string" {
    type  = string
    default = "mongodb://googleCloundRunUser:25yjZFC0MkgF2iXv@article-shard-00-00.smmzv.mongodb.net:27017,article-shard-00-01.smmzv.mongodb.net:27017,article-shard-00-02.smmzv.mongodb.net:27017/pizzaOrder?ssl=true&replicaSet=atlas-pf7b4z-shard-0&authSource=admin&retryWrites=true&w=majority"
}

variable "pubsub_topic" {
    type  = string
    default = "/v1/projects/techday-pizza-store/topics/pizza-store:publish"
}