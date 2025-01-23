export interface Trip {
  id: string
  userId: string
  startLocation: string
  endLocation: string
  date: string
  distance: number
  passengers: number
}

export interface TripCreate {
  startLocation: string
  endLocation: string
  date: string
  distance: number
  passengers: number
}

export interface TripStats {
  trips: {
    id: string
    distance: number
    date: string
    name: string
    drivers: {
      name: string
    }[]
  }[]
  totalKilometers: number
}

export interface ErrorResponse {
  message: string
}
