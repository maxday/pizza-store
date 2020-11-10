provider "google" {
  project = "myeventpic" # replace with your project ID
}

resource "google_project_service" "run" {
  service = "run.googleapis.com"
}

resource "google_cloud_run_service" "orders" {
  name     = "orders"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/myeventpic/quarkus/orders-service@sha256:08528b96d160641ef4820e364495377f8ac7024e2af610d6a00cf0b2d766dd6c"
        env {
          name = "QUARKUS_HTTP_PORT"
          value = 8080
        }
        env {
          name = "QUARKUS_MONGODB_CONNECTION_STRING"
          value = "mongodb://googleCloundRunUser:25yjZFC0MkgF2iXv@article-shard-00-00.smmzv.mongodb.net:27017,article-shard-00-01.smmzv.mongodb.net:27017,article-shard-00-02.smmzv.mongodb.net:27017/pizzaOrder?ssl=true&replicaSet=atlas-pf7b4z-shard-0&authSource=admin&retryWrites=true&w=majority"
        }
        env {
          name = "GCP_PUBSUB_TOPIC_PUBLISH_URL"
          value = "/v1/projects/myeventpic/topics/pizza-store:publish"
        }
        env {
          name = "GCP_API_TOKEN"
          value = "ya29.a0AfH6SMDzoBHK94k_jf2xNKVZhC3eDANGXRU8E48jwa_6R-isc9U9iiViHpLSPpk6p2NxJNJ0-Bl-XgeiDMnViZ2awj0fulJwMErQ8B5vt8oYTqFWErSvE_688UzU4iwVHut6suX8JjTHc_4g1anWZztUUgwp0CWlwtU"
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  depends_on = [google_project_service.run]
}

resource "google_pubsub_topic" "pizza-store" {
  name = "pizza-store"
}

resource "google_pubsub_subscription" "pizza-store-push-sub" {
  name  = "pizza-store-push-sub"
  topic = google_pubsub_topic.pizza-store.name

  ack_deadline_seconds = 20

  retry_policy {
    minimum_backoff = "10s"
  }

  push_config {
    push_endpoint = "${google_cloud_run_service.orders.status[0].url}/orders"
  }

  filter = "attributes.eventId = \"PIZZA_ORDER_REQUEST\" OR attributes.eventId = \"PIZZA_BAKING_REQUEST\""
}

resource "google_cloud_run_service_iam_member" "allUsers" {
  service  = google_cloud_run_service.orders.name
  location = google_cloud_run_service.orders.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}

output "url" {
  value = google_cloud_run_service.orders.status[0].url
}