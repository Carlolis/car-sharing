import { Schema as Sc } from 'effect'

export interface Trip {
  id: string
  userId: string
  startLocation: string
  endLocation: string
  date: string
  distance: number
  passengers: number
}
export const TripCreate = Sc.Struct({
  name: Sc.String,
  date: Sc.DateFromString,
  distance: Sc.NumberFromString,
  drivers: Sc.Array(Sc.String)
})

export type TripCreate = Sc.Schema.Type<typeof TripCreate>

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
