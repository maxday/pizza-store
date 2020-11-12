variable "project_id" {
    type  = string
    default = "techday-pizza-store"
}

variable "token" {
  type    = string
  default = "ya29.A0AfH6SMAqtzqFkqzc_t9FTP29P-fzA_SLFD628q1qEDjrKRp3uDsIpQLGgIYeYag9DHZqNGEvrWm3Wxlq2ofmPsHOW2Yisql5kWN8Sll6hI7zbWIp8o4RRZQxQt8iptohzTxsL4Fr4zR1jofQQnopOj7ZnBhE3xSCGYPN5VohdJub"
}

variable "service_account" {
  type    = string
  default = "ewogICJ0eXBlIjogInNlcnZpY2VfYWNjb3VudCIsCiAgInByb2plY3RfaWQiOiAidGVjaGRheS1waXp6YS1zdG9yZSIsCiAgInByaXZhdGVfa2V5X2lkIjogIjQwYmIwMjhmODMwNzE2OTNmMTFmMWZhMGJlYTMzNDBhMzdlZjg3NWUiLAogICJwcml2YXRlX2tleSI6ICItLS0tLUJFR0lOIFBSSVZBVEUgS0VZLS0tLS1cbk1JSUV2Z0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktnd2dnU2tBZ0VBQW9JQkFRQzVOWXU5b0tyMmZTMm5cbjY4Q0NHVVVXaW0zZndHbFdIMnhmT1NWMDNuVVUreDVKZGxqdk9UbWJEVU1jbnY4S0czMVY3dGVnd3VMdlNCYXVcbkV1eVdMRzZjby80UWlSa0FYTkxPdVBKeHNYbEJWbWRQM25RaFZ3LzRLYmRjZ29uRUUxR0VuR1RpQ3c4YzlrUXZcbjFxVEhJMU1ZMDE5b201UUNOM29hQThRMmtRWVZ6RGxvZTV3OWNBaHV5cGJpQ2VoRjZTSEdqWEYyWTA0REs4c3Zcbjdxa3g4cVloc2dDVzRtdGVzMFRZSTBZRkNSbURXU3VQdWlKb3RGWWhteHdVZkdVaUdJZXFGWlMyUjdneW05c3dcblRqdHArcG1EZjZodzNHUjd5RmQxSkFPL2J0bEtDYi9ZZEsrVUoxSWVrZEsvVzhZeGVUREhTSHEvRnlBcTZTOU9cbjkwK3NEWnEzQWdNQkFBRUNnZ0VBUnlyek1uK3RraC9Sem1mQnhud3RWeGNFVjJsWlVMV3NiLzdHak56NmdvQm9cblIvUkpBa3VyL2crZmpUenZaWlBnbW9lM3BkZWRXak1LUEFPYms2MHBKOW5BUTl4YWxoQVZGVWh1S1EvNE53b2JcbmtzNDVNNkoveVcxR1B5OE4veUVwUHRyVnpTSlF3V0ZFSEZuUFlaSUtXVXpIcWF1MnVROVdSTzdUaytBd1JXbFhcbjdmOEFjWk0wdVNhLys5SCs3Q0hyL25lRkVnM2ZMN0dmR2VsOFNwVE5DRXdyOGpqVjk4alIzWGhwTnRZa29IcmRcbkdKN1BRYyttQ0gvZGpzdHRTbDcyQ05TU24zWHVncUE1cGlwMUtmMXpQNHVGZE1CODJSY1RhL1lTaHJwejhjMVJcbkUraTZkbWJUMWgrRmUvUjN5NWNOTjdWN05pWE4yQnk1bkl5cjN2VGs4UUtCZ1FEZ2dqVDROZmN5TmVUUkRoRmJcbmtwdkprUzBaMFFURUp6MlB5ZmJzOGRDQlBxRHg0TTljMmI4UHN5MDBnVDNxOElaelhBWVNGMGhYU3JVY1F2bHNcblFNSGlFalpVZ1hCRlpaT1FyVm40d202S3JoaEZKWXloc2VpSUl5SHRZazJleFlSZ3JYZVZHNVBSdmtXN0JnNUVcbm93OXJWZ2pnVDcyRjBJOCtXMWs3M0tRMzd3S0JnUURUTUNaa0dpTzgxY3h3VWNmSnMrbmtzV2JGajRZbXY4RG5cbjMxYkFRK2RrRTBVUDZDSExmY2Q0NzR4ajRsSGhwT215dFNjbVRNYnkxUmNKbWVWVlk1NDZrbktZclZ2eWJ6SlFcbkZBdm05TkhWQlo4Z010Sy9vYUVrMm16aHowb2d5VFVMWkJuV2RqN01vNDh0NjY4VDgwbXd6R3FmOVdualoxanFcbjFFT1JvaXJCdVFLQmdGV2FzRkhZV2VwR1RIbytEL1pIdjZrcTl2UjZFNFFycGppV2ZOVzZHcE85YXFidjNvRjdcbjhYTXVFZFdNVml5b0M4d2UwWjNDVE80ajZVemVhTGFUTnBwWjhXZGxkNGF1aFliTDRwdU1uNU8zY0QrbFlxc2dcbjZCWkl0eDRKdHFrTWpUNUR4bTRQZHR5cDNYTERpMXMyaGFHYXk0V2tRaEt0QklGMVdXQU84SHIzQW9HQkFLQkJcbjlhQVl4ZTNTUCtVcjZmUUI5d2Q0SjRqYnRPMUtyZFJIQXNtN3ZhZVBoK1RlUG1idzk2R3FCbHIxcnpVUHM5Zk1cbjUveUVYcVIvVjVBTm5KNERqbHJjTHVIMUM4VVk1SkVuNVRCSnI4RjdGcG1VZDZDN3dsRDQvNDhMZ2pFRy9wMjZcblJIRWVJZUdnYkZKb2V0OGt5MUxDakZiK0lIQlVTSUZLdWt1VlIrVGhBb0dCQUlKZTF5bnVuRS8veDFMdThyRjFcbkRqT2Y0cUhuNE14YmoyaklXdWo4SWQ2VFRkc0h4QkFrVzNTUUVTeUlDM3hlUFYzNjltRC9ybGZTbTZncXBvT1hcbks2UUhBVjJLUW1ScVpUZm1HdWg0OUp0ZS9ndmVmM2w4UVhsRmZtd2g2MzVoUXY0ZzJPV0RNQVBzS2Z0VStKckhcbkhsMHZId0ZiQ1NGZWtvZ3R5Y0RqV0dIRFxuLS0tLS1FTkQgUFJJVkFURSBLRVktLS0tLVxuIiwKICAiY2xpZW50X2VtYWlsIjogIjIzNzMxODc4NjE1NS1jb21wdXRlQGRldmVsb3Blci5nc2VydmljZWFjY291bnQuY29tIiwKICAiY2xpZW50X2lkIjogIjEwOTA4Mjc5NDQ5MTA4NjQyMzgwNyIsCiAgImF1dGhfdXJpIjogImh0dHBzOi8vYWNjb3VudHMuZ29vZ2xlLmNvbS9vL29hdXRoMi9hdXRoIiwKICAidG9rZW5fdXJpIjogImh0dHBzOi8vb2F1dGgyLmdvb2dsZWFwaXMuY29tL3Rva2VuIiwKICAiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjogImh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL29hdXRoMi92MS9jZXJ0cyIsCiAgImNsaWVudF94NTA5X2NlcnRfdXJsIjogImh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL3JvYm90L3YxL21ldGFkYXRhL3g1MDkvMjM3MzE4Nzg2MTU1LWNvbXB1dGUlNDBkZXZlbG9wZXIuZ3NlcnZpY2VhY2NvdW50LmNvbSIKfQo="
}

variable "mongo_connexion_string" {
    type  = string
    default = "mongodb://googleCloundRunUser:25yjZFC0MkgF2iXv@article-shard-00-00.smmzv.mongodb.net:27017,article-shard-00-01.smmzv.mongodb.net:27017,article-shard-00-02.smmzv.mongodb.net:27017/pizzaOrder?ssl=true&replicaSet=atlas-pf7b4z-shard-0&authSource=admin&retryWrites=true&w=majority"
}

variable "pubsub_topic" {
    type  = string
    default = "/v1/projects/techday-pizza-store/topics/pizza-store:publish"
}