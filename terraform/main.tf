provider "google" {
  project = var.project_id # replace with your project ID
}

resource "google_project_service" "run" {
  service = "run.googleapis.com"
}


output "client-frontend_url" {
  value = google_cloud_run_service.client-frontend.status[0].url
}

output "manager-frontend_url" {
  value = google_cloud_run_service.manager-frontend.status[0].url
}
