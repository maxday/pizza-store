provider "google" {
  project = "myeventpic" # replace with your project ID
}

resource "google_project_service" "run" {
  service = "run.googleapis.com"
}

resource "google_cloud_run_service" "clientorders" {
  name     = "clientorders"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/myeventpic/client-orders"
        env {
          name = "QUARKUS_HTTP_PORT"
          value = 8080
        }
        env {
          name = "GCP_PUBSUB_TOPIC_PUBLISH_URL"
          value = "/v1/projects/myeventpic/topics/pizza-store:publish"
        }
        env {
          name = "GCP_API_TOKEN"
          value = "ya29.A0AfH6SMCkj-1Rk6bb2Sc6r5Z2Ui6TyolyooeJSjQjPLTNseM7xrsu5yoHOvFu4_b89853cYY62r7F0E0lTB-9h8fjXI76Y4sgG2KOxrzV-sFwKI-1Z6IdiJMuKKIV7t8Mx3DmlNl8q4hwt_dL7cs4MmThXK3MwSw2IjEJiz_rbClh"
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
          value = "ya29.A0AfH6SMCkj-1Rk6bb2Sc6r5Z2Ui6TyolyooeJSjQjPLTNseM7xrsu5yoHOvFu4_b89853cYY62r7F0E0lTB-9h8fjXI76Y4sgG2KOxrzV-sFwKI-1Z6IdiJMuKKIV7t8Mx3DmlNl8q4hwt_dL7cs4MmThXK3MwSw2IjEJiz_rbClh"
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

resource "google_cloud_run_service" "clientfrontend" {
  name     = "clientfrontend"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/myeventpic/client-frontend"
        env {
          name = "EVENTS_ENDPOINT"
          value = google_cloud_run_service.clientssehandler.status[0].url
        }
        env {
          name = "ORDERS_ENDPOINT"
          value = google_cloud_run_service.clientorders.status[0].url
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  metadata {
    namespace = "myeventpic"
  }

  depends_on = [google_project_service.run]
}

resource "google_cloud_run_service" "clientssehandler" {
  name     = "clientssehandler"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/myeventpic/client-sse-handler"
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  metadata {
    namespace = "myeventpic"
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

resource "google_cloud_run_service_iam_binding" "auth_orders" {
  location = google_cloud_run_service.orders.location
  project = google_cloud_run_service.orders.project
  service = google_cloud_run_service.orders.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth_client_frontend" {
  location = google_cloud_run_service.clientfrontend.location
  project = google_cloud_run_service.clientfrontend.project
  service = google_cloud_run_service.clientfrontend.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth_client_orders" {
  location    = google_cloud_run_service.clientorders.location
  project     = google_cloud_run_service.clientorders.project
  service     = google_cloud_run_service.clientorders.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_service_iam_binding" "auth_client_sse_handler" {
  location    = google_cloud_run_service.clientssehandler.location
  project     = google_cloud_run_service.clientssehandler.project
  service     = google_cloud_run_service.clientssehandler.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_domain_mapping" "default" {
  location = "us-central1"
  name     = "pizza.maximedavid.fr"

  metadata {
    namespace = "myeventpic"
    annotations = {
      "run.googleapis.com/launch-stage" : "BETA"
    }
  }

  spec {
    route_name = google_cloud_run_service.clientfrontend.name
  }
}

output "url" {
  value = google_cloud_run_service.orders.status[0].url
}