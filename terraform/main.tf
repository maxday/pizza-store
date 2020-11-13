provider "google" {
  project = var.project_id # replace with your project ID
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
        image = "gcr.io/${var.project_id}/client-orders"
        env {
          name = "QUARKUS_HTTP_PORT"
          value = 8080
        }
        env {
          name = "GCP_PUBSUB_TOPIC_PUBLISH_URL"
          value = var.pubsub_topic
        }
        env {
          name = "QUARKUS_MONGODB_CONNECTION_STRING"
          value = var.mongo_connexion_string
        }
        env {
          name = "QUARKES_TOKEN_MACHINE_AUDIENCE"
          value = var.audience
        }
        env {
          name = "QUARKES_TOKEN_MACHINE_SERVICE_ACCOUNT"
          value = var.service_account
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
        image = "gcr.io/${var.project_id}/orders-service"
        env {
          name = "QUARKUS_HTTP_PORT"
          value = 8080
        }
        env {
          name = "QUARKUS_MONGODB_CONNECTION_STRING"
          value = var.mongo_connexion_string
        }
        env {
          name = "GCP_PUBSUB_TOPIC_PUBLISH_URL"
          value = var.pubsub_topic
        }
        env {
          name = "QUARKES_TOKEN_MACHINE_AUDIENCE"
          value = var.audience
        }
        env {
          name = "QUARKES_TOKEN_MACHINE_SERVICE_ACCOUNT"
          value = var.service_account
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

resource "google_cloud_run_service" "manager" {
  name     = "manager"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/manager-service"
        env {
          name = "QUARKUS_HTTP_PORT"
          value = 8080
        }
        env {
          name = "QUARKUS_MONGODB_CONNECTION_STRING"
          value = var.mongo_connexion_string
        }
        env {
          name = "GCP_PUBSUB_TOPIC_PUBLISH_URL"
          value = var.pubsub_topic
        }
        env {
          name = "QUARKES_TOKEN_MACHINE_AUDIENCE"
          value = var.audience
        }
        env {
          name = "QUARKES_TOKEN_MACHINE_SERVICE_ACCOUNT"
          value = var.service_account
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
        image = "gcr.io/${var.project_id}/client-frontend"
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
    namespace = var.project_id
  }

  depends_on = [google_project_service.run]
}

resource "google_cloud_run_service" "managerfrontend" {
  name     = "managerfrontend"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/manager-frontend"
        env {
          name = "ORDERS_ENDPOINT"
          value = google_cloud_run_service.manager.status[0].url
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  metadata {
    namespace = var.project_id
  }

  depends_on = [google_project_service.run]
}

resource "google_cloud_run_service" "clientssehandler" {
  name     = "clientssehandler"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/client-sse-handler"
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  metadata {
    namespace = var.project_id
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

  filter = "attributes.eventId = \"PIZZA_ORDER_REQUEST\" OR attributes.eventId = \"PIZZA_PREPARED_REQUEST\" OR attributes.eventId = \"PIZZA_BAKED_REQUEST\" OR attributes.eventId = \"PIZZA_LEFT_STORE_REQUEST\" OR attributes.eventId = \"PIZZA_DELIVERED_REQUEST\""
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

resource "google_cloud_run_service_iam_binding" "auth_manager_frontend" {
  location = google_cloud_run_service.managerfrontend.location
  project = google_cloud_run_service.managerfrontend.project
  service = google_cloud_run_service.managerfrontend.name
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

resource "google_cloud_run_service_iam_binding" "auth_manager_service" {
  location    = google_cloud_run_service.manager.location
  project     = google_cloud_run_service.manager.project
  service     = google_cloud_run_service.manager.name
  role = "roles/run.invoker"
  members = [
    "allUsers",
  ]
}

resource "google_cloud_run_domain_mapping" "pizza_maximedavid" {
  location = "us-central1"
  name     = "pizza.maximedavid.fr"

  metadata {
    namespace = var.project_id
    annotations = {
      "run.googleapis.com/launch-stage" : "BETA"
    }
  }

  spec {
    route_name = google_cloud_run_service.clientfrontend.name
  }
}

resource "google_cloud_run_domain_mapping" "orders_maximedavid" {
  location = "us-central1"
  name     = "orders.maximedavid.fr"

  metadata {
    namespace = var.project_id
    annotations = {
      "run.googleapis.com/launch-stage" : "BETA"
    }
  }

  spec {
    route_name = google_cloud_run_service.managerfrontend.name
  }
}

output "client_front_end_url" {
  value = google_cloud_run_service.clientfrontend.status[0].url
}

output "manager_front_end_url" {
  value = google_cloud_run_service.managerfrontend.status[0].url
}