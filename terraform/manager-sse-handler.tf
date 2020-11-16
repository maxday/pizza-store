resource "google_cloud_run_service" "manager-sse-handler" {
  name     = "manager-sse-handler"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/sse-handler"
        env {
          name = "TOPIC_NAME"
          value = "pizza-store-manager"
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