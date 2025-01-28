import { DateTime, pipe, Schema as Sc } from 'effect'
import { format, formatIsoDateUtc } from 'effect/DateTime'

export interface Trip {
  id: string
  userId: string
  startLocation: string
  endLocation: string
  date: string
  distance: number
  passengers: number
}
const formatter = new Intl.DateTimeFormat('en-GB', {
  year: 'numeric',
  day: '2-digit',
  month: '2-digit'
})

const LocalDate = Sc.transform(
  // Source schema: "on" or "off"

  Sc.String,
  // Target schema: boolean

  Sc.DateFromSelf,
  {
    // optional but you get better error messages from TypeScript

    strict: true,

    // Transformation to convert the output of the

    // source schema ("on" | "off") into the input of the

    // target schema (boolean)

    decode: x => Sc.decodeSync(Sc.DateFromString)(x), // Always succeeds here

    // Reverse transformation

    encode: date => {
      console.log(pipe(
        date,
        DateTime.unsafeFromDate,
        format({
          year: 'numeric',
          day: '2-digit',
          month: '2-digit'
        })
      ))

      return pipe(
        date,
        DateTime.unsafeFromDate,
        formatIsoDateUtc
        // formatIso({
        //   year: 'numeric',
        //   day: '2-digit',
        //   month: '2-digit'
        // })
      )
    }
  }
)

export const TripCreate = Sc.Struct({
  name: Sc.String,
  date: LocalDate,
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
