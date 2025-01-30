import * as T from 'effect/Effect'

import { Remix } from '~/runtime/Remix'
import { api } from '../services/api'

import { CookieSessionStorage } from '~/runtime/CookieSessionStorage'

import { stringify } from 'effect/FastCheck'
import { NotFound } from '~/runtime/ServerResponse'

import { useEffect, useState } from 'react'
import { TripCreate } from '~/types/api'
// eslint-disable-next-line import/no-unresolved
import { Route } from './+types/dashboard'

function StatsCard({ title, value }: { title: string; value: string | number }) {
  return (
    <div className="bg-white overflow-hidden shadow rounded-lg">
      <div className="px-4 py-5 sm:p-6">
        <dt className="text-sm font-medium text-gray-500 truncate">{title}</dt>
        <dd className="mt-1 text-3xl font-semibold text-gray-900">{value}</dd>
      </div>
    </div>
  )
}

export const loader = Remix.loader(
  T.gen(function* () {
    const cookieSession = yield* CookieSessionStorage
    const user = yield* cookieSession.getUserName()

    yield* T.logInfo('Fetching total stats')
    const totalStats = yield* api.getTotalStats()

    return { totalStats, user }
  }).pipe(T.catchAll(error => T.fail(new NotFound({ message: stringify(error) }))))
)

export default function Dashboard({ loaderData: { totalStats, user } }: Route.ComponentProps) {
  const [trips, setTrips] = useState<TripCreate[]>([])

  useEffect(() => {
    // Fetch trips data from API or any other source
    // For demonstration, using static data
    const fetchTrips = async () => {
      const data: TripCreate[] = [
        {
          name: 'Trip to Paris',
          date: new Date('2023-10-01'),
          distance: 300,
          drivers: ['John Doe', 'Jane Smith']
        },
        {
          name: 'Trip to Berlin',
          date: new Date('2023-10-05'),
          distance: 500,
          drivers: ['Alice Johnson']
        }
      ]
      setTrips(data)
    }

    fetchTrips()
  }, [])

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="py-8">
        <h2 className="text-2xl font-semibold text-gray-900 mb-8">
          Statistiques Globales
        </h2>
        {user && (
          <div
            className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4"
            role="alert"
          >
            <strong className="font-bold">Connecté !</strong>
            <span>{' '}</span>
            <span className="block sm:inline">
              Vous êtes connecté en tant que {user}
            </span>
          </div>
        )}
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          <StatsCard
            title="Nombre total de trajets"
            value={totalStats.trips.length}
          />
          <StatsCard
            title="Distance totale (km)"
            value={Math.round(totalStats.totalKilometers)}
          />
          <StatsCard
            title="Passagers transportés"
            value={totalStats.trips.reduce((acc, trip) => acc + trip.drivers.length, 0)}
          />
          <StatsCard
            title="Distance moyenne (km)"
            value={totalStats.trips.length === 0 ?
              0 :
              Math.round(totalStats.totalKilometers / totalStats.trips.length)}
          />
        </div>
        <div className="mt-8 grid grid-cols-1 gap-4">
          {trips.map((trip, index) => (
            <div key={index} className="bg-white shadow-md rounded-lg p-4">
              <div className="flex justify-between items-center">
                <div>
                  <h3 className="text-xl font-semibold">{trip.name}</h3>
                  <p className="text-gray-700">Date: {trip.date.toDateString()}</p>
                </div>
                <div className="text-right">
                  <p className="text-gray-700">Distance: {trip.distance} km</p>
                  <div className="flex gap-2 items-center">
                    <span className="text-gray-700">Personnes:</span>
                    <span className="text-gray-700">
                      {trip.drivers.join(', ')}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
