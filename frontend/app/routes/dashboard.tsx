import * as T from 'effect/Effect'

import { Remix } from '~/runtime/Remix'
import { api } from '../services/api'

import { useAuth } from '~/contexts/AuthContext'
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

export const loader = Remix.unwrapLoader(
  T.gen(function* () {
    yield* T.logInfo('Fetching total stats')
    const totalStats = yield* api.getTotalStats()

    return T.succeed({ totalStats })
  })
)

export default function Dashboard({ loaderData: { totalStats } }: Route.ComponentProps) {
  // const { totalStats } = useLoaderData<typeof loader>()
  const auth = useAuth()

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="py-8">
        <h2 className="text-2xl font-semibold text-gray-900 mb-8">
          Statistiques Globales
        </h2>
        {auth.isAuthenticated && (
          <div
            className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4"
            role="alert"
          >
            <strong className="font-bold">Connecté !</strong>
            <span>{' '}</span>
            <span className="block sm:inline">
              Vous êtes connecté en tant que {auth.user}
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
      </div>
    </div>
  )
}
