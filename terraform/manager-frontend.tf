
resource "google_cloud_run_service" "manager-frontend" {
  name     = "manager-frontend"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/manager-frontend"
        env {
          name = "ORDERS_ENDPOINT"
          value = google_cloud_run_service.manager-orders-api.status[0].url
        }
        resources {
          limits = {
            memory        = "128Mi"
          }
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