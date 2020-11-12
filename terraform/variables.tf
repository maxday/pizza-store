variable "project_id" {
    type  = string
    default = "techday-pizza-store"
}

variable "token" {
  type    = string
  default = "ya29.A0AfH6SMC3TV_gXww53oKDBBTAxs8XDWmwSFtK9erkdJ-hWtqJz2RcG0mCmJ4cwlE3rcoC5MlsjuerTUZBTKfeSf1dF9hb2zSu2KxILai3_9DMmckoMXxS3fIsrxmxadL6xH86qMNUrzQZNLbrgo6wPKtEnYK9-CfOcsn-Oxu_C6CU"
}

variable "mongo_connexion_string" {
    type  = string
    default = "mongodb://googleCloundRunUser:25yjZFC0MkgF2iXv@article-shard-00-00.smmzv.mongodb.net:27017,article-shard-00-01.smmzv.mongodb.net:27017,article-shard-00-02.smmzv.mongodb.net:27017/pizzaOrder?ssl=true&replicaSet=atlas-pf7b4z-shard-0&authSource=admin&retryWrites=true&w=majority"
}

variable "pubsub_topic" {
    type  = string
    default = "/v1/projects/${var.project_id}/topics/pizza-store:publish"
}