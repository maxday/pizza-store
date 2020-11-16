/* resource "google_cloud_run_domain_mapping" "pizza_maximedavid" {
  location = "us-central1"
  name     = "pizza.maximedavid.fr"

  metadata {
    namespace = var.project_id
    annotations = {
      "run.googleapis.com/launch-stage" : "BETA"
    }
  }

  spec {
    route_name = google_cloud_run_service.client-frontend.name
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
    route_name = google_cloud_run_service.manager-frontend.name
  }
} */
